package com.jamie.zyco.pdfer

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.jamie.zyco.pdfer.base.BaseActivity
import com.jamie.zyco.pdfer.base.Constants
import com.jamie.zyco.pdfer.databinding.ActivityMainBinding
import com.jamie.zyco.pdfer.listener.clickhandler.MainActivityClickHandler
import com.jamie.zyco.pdfer.utils.Zog
import com.jamie.zyco.pdfer.utils.SPUtils
import com.jamie.zyco.pdfer.widget.MyDialog
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.none_pdf.*

class MainActivity : BaseActivity<ActivityMainBinding>(), MainActivityClickHandler {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding.click = this
        initView()
    }

    override fun initView() {
        if (SPUtils(Constants.SP_NAME).getInt(Constants.PDF_COUNT, 0) == 0) {
            mViewStub.visibility = View.VISIBLE
            var dialog:MyDialog?=null
            mFindPDF.setOnClickListener {
                dialog=MyDialog.Builder(this)
                        .cancelableByOutside(true)
                        .sure("sure", View.OnClickListener {
                            Zog.zLog(0,"sure click")
                        })
                        .cancel("cancel",View.OnClickListener {
                            dialog?.dismiss()
                        })
                        .build()
                dialog?.show()
                //sendBroadcast(Intent(Intent.ACTION_MEDIA_MOUNTED))
            }
        }else{
            mViewStub.visibility=View.GONE
        }
    }

    override fun initData() {

    }

    override fun getLayoutId() = R.layout.activity_main

    /** MainActivityClickHandler **/

    override fun add(view: View) {
        Zog.zLog(0, "main-add")
    }

    override fun switchList() {
        Zog.zLog(0, "main-switchList")
    }
}
