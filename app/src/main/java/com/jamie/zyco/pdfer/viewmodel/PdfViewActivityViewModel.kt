package com.jamie.zyco.pdfer.viewmodel

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.ViewModel

class PdfViewActivityViewModel:ViewModel() {

    val lifecycleWatcher= object : DefaultLifecycleObserver {
        override fun onCreate(owner: LifecycleOwner) {

        }

        override fun onDestroy(owner: LifecycleOwner) {

        }
    }
}