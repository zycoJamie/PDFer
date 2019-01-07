package com.jamie.zyco.pdfer.base

import android.app.Application
import android.arch.persistence.room.Room
import com.jamie.zyco.pdfer.model.database.AppDatabase
import com.jamie.zyco.pdfer.utils.Zog

class MyApplication : Application() {
    companion object {
        private var sInstance: MyApplication? = null
        private var database: AppDatabase? = null
        fun getInstance(): MyApplication = sInstance!!
        fun getDatabase(): AppDatabase = database!!
    }

    override fun onCreate() {
        super.onCreate()
        sInstance = this
        Zog.logSwitch = true
        database = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "pdf.db").build()
    }


}