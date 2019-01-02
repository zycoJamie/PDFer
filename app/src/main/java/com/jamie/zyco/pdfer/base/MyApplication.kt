package com.jamie.zyco.pdfer.base

import android.app.Application

class MyApplication : Application() {
    companion object {
        private var sInstance: MyApplication? = null
        fun getInstance(): MyApplication {
            return sInstance!!
        }
    }

    override fun onCreate() {
        super.onCreate()
        sInstance = this
    }


}