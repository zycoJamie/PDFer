package com.jamie.zyco.pdfer.worker

import android.arch.lifecycle.ViewModel
import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.jamie.zyco.pdfer.base.Constants
import com.jamie.zyco.pdfer.utils.Zog
import com.jamie.zyco.pdfer.viewmodel.MainActivityViewModel

class ScanWorker {
    class ScanPdfWorker(context: Context, workerParameters: WorkerParameters) : Worker(context, workerParameters) {
        companion object {
            var model: ViewModel? = null
        }

        override fun doWork(): Result {
            if (model is MainActivityViewModel) {
                val modelTemp = model as MainActivityViewModel
                modelTemp.scanPdf(inputData.getString(Constants.SCAN_PATH)!!, true)
            }
            return Result.success()
        }
    }

    class ScanPdfOverWorker(context: Context, workerParameters: WorkerParameters) : Worker(context, workerParameters) {
        companion object {
            var model: ViewModel? = null
        }

        override fun doWork(): Result {
            if (model is MainActivityViewModel) {
                val modelTemp = model as MainActivityViewModel
                Zog.log(0, "ScanPdfOverWorker isScanOver")
                modelTemp.isScanOver.postValue(true)
                if (modelTemp.tempPdfList.size > 0) {
                    modelTemp.save2Database()
                }
            }
            ScanPdfOverWorker.model = null
            ScanPdfWorker.model = null
            Zog.log(0, "ScanPdfOverWorker")
            return Result.success()
        }
    }
}