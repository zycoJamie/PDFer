package com.jamie.zyco.pdfer.viewmodel

import android.arch.lifecycle.*
import com.jamie.zyco.pdfer.base.Constants
import com.jamie.zyco.pdfer.utils.Zog
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class MainActivityViewModel : ViewModel() {

    private var executor: ThreadPoolExecutor? = null
    val isScanOver: MutableLiveData<Boolean> = MutableLiveData()

    //观察MainActivity的生命周期，当Activity进入销毁阶段时，执行资源回收逻辑
    val lifecycleWatcher = object : DefaultLifecycleObserver {
        override fun onDestroy(owner: LifecycleOwner) {
            executor?.shutdownNow()
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
                    break
                }
            }
        }).start()
    }

}