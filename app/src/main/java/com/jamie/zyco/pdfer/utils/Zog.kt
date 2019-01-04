package com.jamie.zyco.pdfer.utils

import android.util.Log

class Zog {
    companion object {
        val TAG = Zog::class.java.simpleName
        const val d = 0
        const val i = 1
        const val e = 2
        var logSwitch = true
        fun zLog(level: Int, msg: String) {
            if (logSwitch) {
                when (level) {
                    d -> Log.d(TAG, msg)
                    i -> Log.i(TAG, msg)
                    e -> Log.e(TAG, msg)
                }
            }
        }

        fun zlog(level: Int, tag: String, msg: String) {
            if (logSwitch) {
                when (level) {
                    d -> Log.d(tag, msg)
                    i -> Log.i(tag, msg)
                    e -> Log.e(tag, msg)
                }
            }
        }
    }
}