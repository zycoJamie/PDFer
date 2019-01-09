package com.jamie.zyco.pdfer

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.os.Environment
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import com.jamie.zyco.pdfer.base.BaseActivity
import com.jamie.zyco.pdfer.base.Constants
import com.jamie.zyco.pdfer.databinding.ActivityMainBinding
import com.jamie.zyco.pdfer.listener.clickhandler.MainActivityClickHandler
import com.jamie.zyco.pdfer.model.entity.db.PdfDocument
import com.jamie.zyco.pdfer.ui.adapter.MainViewPagerAdapter
import com.jamie.zyco.pdfer.ui.adapter.PdfListAdapter
import com.jamie.zyco.pdfer.utils.SPUtils
import com.jamie.zyco.pdfer.utils.Zog
import com.jamie.zyco.pdfer.viewmodel.MainActivityViewModel
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.Permission
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_view_pager.*
import kotlinx.android.synthetic.main.none_pdf.*
import kotlinx.android.synthetic.main.progress_bar.*


class MainActivity : BaseActivity<ActivityMainBinding>(), MainActivityClickHandler {

    private var isFirstSearch = true
    private var mViewList: ArrayList<View>? = ArrayList()
    private var mTitleList: ArrayList<String>? = ArrayList()

    private val viewModel: MainActivityViewModel by lazy {
        obtainViewModel(this, MainActivityViewModel::class.java)
    }

    companion object {
        val TAG = MainActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding.click = this
        initView()
        initData()
    }

    override fun initView() {
        Constants.gPdfCount = SPUtils(Constants.SP_NAME).getInt(Constants.PDF_COUNT, 0)
        Constants.gDirCount = SPUtils(Constants.SP_NAME).getInt(Constants.DIR_COUNT, 0)
        if (Constants.gPdfCount == 0) {
            Zog.log(0, "none pdf")
            mViewStub.visibility = View.VISIBLE //viewStub延迟加载布局，当加载一次过后，就会被替换掉，因此不能多次加载(Visible)，否则会报IllegalStateException
            mFindPDF.setOnClickListener {
                findPDF()
            }
            mViewPager.visibility = View.GONE
            mBottomLl.visibility = View.GONE
        } else {
            Zog.log(0, "exist pdf")
            mViewStub.visibility = View.GONE
            mViewPager.visibility = View.VISIBLE
            showTab()
            mBottomLl.visibility = View.VISIBLE
        }
    }

    override fun initData() {
        lifecycle.addObserver(viewModel.lifecycleWatcher)
        viewModel.isScanOver.observe(this@MainActivity, Observer {
            if (it!!) {
                mProgressBarLayout.visibility = View.GONE
                mDrawer.setIntercepted(false)
                Zog.log(1, "${System.currentTimeMillis()}")
            }
        })
        updatePdfList()
    }

    private fun updatePdfList() {
        viewModel.mPdfList.observe(this@MainActivity, Observer {
            if (it != null) {
                (mPdfRv.adapter as PdfListAdapter).data = it
                mPdfRv.adapter?.notifyDataSetChanged()
                if (mFrameContainer != null && mPdfRv.adapter!!.itemCount > 0) {
                    Zog.log(0, "hide none pdf layout")
                    mFrameContainer.visibility = View.GONE
                    mViewPager.visibility = View.VISIBLE
                    mBottomLl.visibility = View.VISIBLE
                    showTab()
                    viewModel.save2Database()
                }
            }
        })
    }

    private fun showTab() {
        for (i in 0 until Constants.gDirCount) {
            mViewList?.add(mPdfRv)
        }
        mTitleList!!.add("我的文件")
        mTitleList!!.add("最近")
        mViewPager.adapter = MainViewPagerAdapter(mViewList!!)
        mTabLayout.setViewPager(mViewPager, mTitleList!!.toArray(arrayOfNulls(mTitleList!!.size)))
        viewModel.mPdfList.value = MutableList(0) {
            PdfDocument("null", 0f, null, null)
        }
        mPdfRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mPdfRv.adapter = PdfListAdapter(R.layout.item_pdf_list, viewModel.mPdfList.value!!)

    }

    override fun getLayoutId() = R.layout.activity_main

    /** MainActivityClickHandler **/

    override fun add(view: View) {
        Zog.log(0, "main-add")
    }

    override fun switchList() {
        Zog.log(0, "main-switchList")
    }

    override fun findPDF() {
        AndPermission.with(this)
                .runtime()
                .permission(Permission.Group.STORAGE)
                .onGranted {
                    //sendBroadcast(Intent(Intent.ACTION_MEDIA_MOUNTED)) android 4.4以前可发送此广播通知扫描文件，4.4以上则只能是系统app才能发送此广播
                    if (isFirstSearch) {
                        mViewStubProgressBar.visibility = View.VISIBLE
                        isFirstSearch = false
                    } else {
                        mProgressBarLayout.visibility = View.VISIBLE
                    }
                    mDrawer.setIntercepted(true)
                    Zog.log(1, "${System.currentTimeMillis()}")
                    viewModel.scheduleScanPdf(Environment.getExternalStorageDirectory().path)
                }
                .onDenied {
                    Toast.makeText(this@MainActivity, "授予相应的权限，APP才能正常使用哦~", Toast.LENGTH_SHORT).show()
                }
                .start()
    }
}
