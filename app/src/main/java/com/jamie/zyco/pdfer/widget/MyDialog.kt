package com.jamie.zyco.pdfer.widget

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface

class MyDialog(context: Context) : Dialog(context) {
    var title:String?=null
    var layoutId:Int?=null
    var canCancel:Boolean=false
    var showListener:DialogInterface.OnShowListener?=null
    var cancelListener:DialogInterface.OnCancelListener?=null
    val dismissListener:DialogInterface.OnDismissListener?=null

    override fun onStart() {
        super.onStart()

    }
}