package com.jamie.zyco.pdfer.widget

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import com.jamie.zyco.pdfer.R
import com.jamie.zyco.pdfer.utils.Zog
import kotlinx.android.synthetic.main.global_dialog.*

class MyDialog(context: Context) : Dialog(context, R.style.Dialog) {

    var canCancel: Boolean = false
    var mSureText: String = context.getString(R.string.dialog_sure_btn)
    var mCancelText: String = context.getString(R.string.dialog_cancel_btn)
    var sureListener: View.OnClickListener? = null
    var cancelListener: View.OnClickListener? = null
    var showListener: DialogInterface.OnShowListener? = null
    var dismissListener: DialogInterface.OnDismissListener? = null

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.global_dialog)
    }

    override fun onStart() {
        super.onStart()
        setCancelable(canCancel)
        mSureBtn.text = mSureText
        mCancelBtn.text = mCancelText
        if (sureListener != null) {
            mSureBtn.setOnClickListener(sureListener)
        }
        if (cancelListener != null) {
            mCancelBtn.setOnClickListener(cancelListener)
        }
        if (showListener != null) {
            setOnShowListener(showListener)
        }
        if (dismissListener != null) {
            setOnDismissListener(dismissListener)
        }
    }


    override fun show() {
        super.show()
        Zog.log(0, "*** global dialog showing ****")
    }

    override fun dismiss() {
        super.dismiss()
        Zog.log(0, "*** global dialog dismiss ****")
    }

    class Builder(context: Context) {
        var dialog: MyDialog? = null

        init {
            dialog = MyDialog(context)
        }

        fun sure(text: String, clickListener: View.OnClickListener) = apply {
            dialog?.mSureText = text
            dialog?.sureListener = clickListener
        }

        fun cancel(text: String, clickListener: View.OnClickListener) = apply {
            dialog?.mCancelText = text
            dialog?.cancelListener = clickListener
        }

        fun cancelableByOutside(can: Boolean) = apply {
            dialog?.canCancel = can
        }

        fun showCallback(listener: DialogInterface.OnShowListener) = apply {
            dialog?.showListener = listener
        }

        fun dismissCallback(listener: DialogInterface.OnDismissListener) = apply {
            dialog?.dismissListener = listener
        }

        fun build(): MyDialog = dialog!!

    }
}