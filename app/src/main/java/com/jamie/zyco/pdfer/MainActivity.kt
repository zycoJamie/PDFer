package com.jamie.zyco.pdfer

import android.os.Bundle
import android.view.View
import com.jamie.zyco.pdfer.base.BaseActivity
import com.jamie.zyco.pdfer.base.Constants
import com.jamie.zyco.pdfer.databinding.ActivityMainBinding
import com.jamie.zyco.pdfer.listener.clickhandler.MainActivityClickHandler
import com.jamie.zyco.pdfer.utils.LogUtils
import com.jamie.zyco.pdfer.utils.SPUtils
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
            mFindPDF.setOnClickListener {

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
        LogUtils.zLog(0, "main-add")
    }

    override fun switchList() {
        LogUtils.zLog(0, "main-switchList")
    }
}
