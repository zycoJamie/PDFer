package com.jamie.zyco.pdfer.viewmodel

import android.arch.lifecycle.*
import com.jamie.zyco.pdfer.base.Constants
import com.jamie.zyco.pdfer.model.database.factory.DAOFactory
import com.jamie.zyco.pdfer.model.entity.db.PdfDocument
import com.jamie.zyco.pdfer.utils.SPUtils
import com.jamie.zyco.pdfer.utils.Zog
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class MainActivityViewModel : ViewModel() {

    private var executor: ThreadPoolExecutor? = null
    val isScanOver: MutableLiveData<Boolean> = MutableLiveData()
    var mPdfListLiveData: MutableLiveData<MutableList<PdfDocument>> = MutableLiveData()
    private val tempPdfList: ArrayList<PdfDocument> by lazy {
        //第一次扫描文件时用到的临时list
        ArrayList<PdfDocument>()
    }
    private var mObserveDatabaseLiveData: LiveData<MutableList<PdfDocument>>? = null
    private var mObserver: Observer<MutableList<PdfDocument>> = Observer { it -> mPdfListLiveData.value = it!! }
    val mPdfCountLiveData: MutableLiveData<Int> = MutableLiveData()
    val mDirCountLiveData: MutableLiveData<Int> = MutableLiveData()

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

    private fun scanPdf(folder: String) {
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
                    return
                }
                scanPdf(f.path)
            }
        } else if (file.path.endsWith(".pdf", true)) {
            Zog.log(1, file.path)
            val size = (Math.floor((file.length() * 100 / (1024f * 1024f)).toDouble())) / 100f
            val document = PdfDocument(file.path, size.toFloat(), null, null)
            tempPdfList.add(document)
        }

    }

    fun scheduleScanPdf(folder: String) {
        isScanOver.value = false
        val file = File(folder)
        if (file.listFiles().isEmpty()) {
            return
        }
        executor = ThreadPoolExecutor(Constants.CORE, Constants.MAX, 10L, TimeUnit.SECONDS,
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
                        mPdfListLiveData.postValue(tempPdfList.toMutableList())
                    }
                    break
                }
            }
        }).start()
    }

    fun save2Database() {
        Thread(Runnable {
            tempPdfList.forEach {
                DAOFactory.getPdfDocumentDAO().insertAll(it)
            }
        }).start()
    }

    fun get4Database() {
        Thread(Runnable {
            mObserveDatabaseLiveData = DAOFactory.getPdfDocumentDAO().queryAll()
            mObserveDatabaseLiveData?.observeForever(mObserver)
        }).start()
    }

    fun savePdfCount2SP() {
        SPUtils(Constants.SP_NAME).putInt(Constants.PDF_COUNT, tempPdfList.size)
    }

}