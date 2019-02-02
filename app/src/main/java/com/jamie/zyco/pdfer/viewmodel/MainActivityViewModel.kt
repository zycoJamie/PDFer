package com.jamie.zyco.pdfer.viewmodel

import android.annotation.SuppressLint
import android.arch.lifecycle.*
import android.util.ArraySet
import androidx.work.*
import com.jamie.zyco.pdfer.base.Constants
import com.jamie.zyco.pdfer.model.database.factory.DAOFactory
import com.jamie.zyco.pdfer.model.entity.db.PdfDocument
import com.jamie.zyco.pdfer.utils.SPUtils
import com.jamie.zyco.pdfer.utils.Zog
import com.jamie.zyco.pdfer.worker.ScanWorker
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class MainActivityViewModel : ViewModel() {

    private var executor: ThreadPoolExecutor? = null
    val isScanOver: MutableLiveData<Boolean> = MutableLiveData()
    var mPdfListLiveData: MutableLiveData<MutableList<PdfDocument>> = MutableLiveData()
    val tempPdfList: ArrayList<PdfDocument> by lazy {
        ArrayList<PdfDocument>()
    }
    private var mObserveDatabaseLiveData: LiveData<MutableList<PdfDocument>>? = null
    private var mObserver: Observer<MutableList<PdfDocument>> = Observer { it -> mPdfListLiveData.value = it!! }
    val mPdfCountLiveData: MutableLiveData<Int> = MutableLiveData()
    val mDirCountLiveData: MutableLiveData<Int> = MutableLiveData()
    val mPdfPathLiveData: MutableLiveData<ArrayList<String>> = MutableLiveData()
    private var mPdfPathList: ArrayList<String> = ArrayList()

    //观察MainActivity的生命周期，当Activity进入销毁阶段时，执行资源回收逻辑
    val lifecycleWatcher = object : DefaultLifecycleObserver {

        override fun onCreate(owner: LifecycleOwner) {
            Constants.gPdfCount = SPUtils(Constants.SP_NAME).getInt(Constants.PDF_COUNT, 0)
            Constants.gDirCount = SPUtils(Constants.SP_NAME).getInt(Constants.DIR_COUNT, 2)
            mPdfCountLiveData.value = Constants.gPdfCount
            mDirCountLiveData.value = Constants.gDirCount
        }

        override fun onDestroy(owner: LifecycleOwner) {
            executor?.shutdownNow()
            mObserveDatabaseLiveData?.removeObserver(mObserver)
        }
    }

    fun scanPdf(folder: String, scanCurrentDir: Boolean = false) {
        val file = File(folder)
        //判断是否是空文件夹
        if (file.isDirectory && (file.listFiles() == null || file.listFiles().isEmpty())) {
            //Zog.log(0, "file.listFiles() == null")
            return
        }
        //非空文件夹就递归扫描文件，若是文件的话，就判断是否是PDF文档
        if (file.isDirectory) {
            for (f in file.listFiles()) {
                if (f == null) {
                    //Zog.log(0, "file == null")
                    continue
                }
                if (scanCurrentDir) {
                    if (f.isFile && f.path.endsWith(".pdf", true)) {
                        Zog.log(1, "quickly scan ${f.path}")
                        val size = (Math.floor((f.length() * 100 / (1024f * 1024f)).toDouble())) / 100f
                        val document = PdfDocument(f.path, size.toFloat(), null, 0, null)
                        tempPdfList.add(document)
                    }
                } else {
                    scanPdf(f.path)
                }
            }
        } else if (file.path.endsWith(".pdf", true)) {
            Zog.log(1, file.path)
            val size = (Math.floor((file.length() * 100 / (1024f * 1024f)).toDouble())) / 100f
            val document = PdfDocument(file.path, size.toFloat(), null, 0, null)
            tempPdfList.add(document)
        }

    }

    fun scheduleScanPdf(folder: String) {
        isScanOver.value = false
        tempPdfList.clear()
        val file = File(folder)
        if (file.listFiles().isEmpty()) {
            isScanOver.value = true
            return
        }
        executor = ThreadPoolExecutor(Constants.CORE, Constants.MAX, 2L, TimeUnit.SECONDS,
                LinkedBlockingDeque(),
                Executors.defaultThreadFactory(),
                ThreadPoolExecutor.DiscardOldestPolicy())
        for (i in 0 until file.listFiles().size) {
            val work = Runnable {
                Zog.log(0, file.listFiles()[i].path)
                scanPdf(file.listFiles()[i].path)
            }
            executor?.execute(work)
        }
        executor?.shutdown()
        Thread(Runnable {
            while (true) {
                if (executor!!.isTerminated) {
                    isScanOver.postValue(true)
                    if (tempPdfList.size > 0) {
                        save2Database()
                        mPdfListLiveData.postValue(tempPdfList.toMutableList())
                        savePdfCount2SP()
                    }
                    break
                }
            }
        }).start()
    }

    fun save2Database() {
        Thread(Runnable {
            DAOFactory.getPdfDocumentDAO().insertAll(*tempPdfList.toTypedArray()) //展开array
        }).start()
    }

    fun get4Database() {
        Thread(Runnable {
            mObserveDatabaseLiveData = DAOFactory.getPdfDocumentDAO().queryAll()
            mObserveDatabaseLiveData?.observeForever(mObserver)
        }).start()
    }

    fun getData4Database() {
        Thread(Runnable {
            val list = DAOFactory.getPdfDocumentDAO().queryPath()
            list.forEach {
                Zog.log(0, it)
            }
            mPdfPathList.clear()
            mPdfPathList.addAll(list)
            mPdfPathLiveData.postValue(mPdfPathList)
        }).start()
    }

    @SuppressLint("EnqueueWork")
    fun quicklyScanPdf(pathList: List<String>?) {
        tempPdfList.clear()
        val pathSet: ArraySet<String> = ArraySet()
        val newPathList = getPdfPath(pathList)
        newPathList.forEach {
            Zog.log(0, "pathSet $it")
            pathSet.add(it)
        }
        mPdfPathList.clear()
        mPdfPathList.addAll(pathSet)
        val chain = ArrayList<WorkContinuation>()
        ScanWorker.ScanPdfWorker.model = this
        ScanWorker.ScanPdfOverWorker.model = this
        for (i in 0 until mPdfPathList.size step 2) {
            val workManager = WorkManager.getInstance()
            var workContinuation: WorkContinuation
            val worker = OneTimeWorkRequestBuilder<ScanWorker.ScanPdfWorker>()
                    .setInputData(Data.Builder().putString(Constants.SCAN_PATH, mPdfPathList[i]).build())
                    .build()
            var worker2: OneTimeWorkRequest
            workContinuation = workManager.beginWith(worker)
            if (i + 1 < mPdfPathList.size) {
                worker2 = OneTimeWorkRequestBuilder<ScanWorker.ScanPdfWorker>()
                        .setInputData(Data.Builder().putString(Constants.SCAN_PATH, mPdfPathList[i + 1]).build())
                        .build()
                workContinuation.then(worker2)
            }
            chain.add(workContinuation)
        }
        WorkContinuation.combine(chain)
                .then(OneTimeWorkRequestBuilder<ScanWorker.ScanPdfOverWorker>().build())
                .enqueue()
    }

    fun savePdfCount2SP() {
        if (mPdfListLiveData.value != null) {
            SPUtils(Constants.SP_NAME).putInt(Constants.PDF_COUNT, mPdfListLiveData.value!!.size)
        }
    }

    fun getPdfPath(pathList: List<String>?): ArrayList<String> {
        val newPathList = ArrayList<String>()
        pathList?.forEach { it ->
            newPathList.add(it.substring(0, it.indexOfLast {
                it == '/'
            } + 1))
        }
        return newPathList
    }

}