package com.jamie.zyco.pdfer.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.text.TextUtils

class PdfFileScanner :BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        if(TextUtils.equals(intent?.action,Intent.ACTION_MEDIA_SCANNER_STARTED)){

        }
    }
}