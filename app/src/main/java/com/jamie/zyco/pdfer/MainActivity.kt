package com.jamie.zyco.pdfer

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.jamie.zyco.pdfer.base.BaseActivity
import com.jamie.zyco.pdfer.base.Constants
import com.jamie.zyco.pdfer.databinding.ActivityMainBinding
import com.jamie.zyco.pdfer.listener.clickhandler.MainActivityClickHandler
import com.jamie.zyco.pdfer.ui.activity.PdfViewActivity
import com.jamie.zyco.pdfer.ui.adapter.HeaderWrapperAdapter
import com.jamie.zyco.pdfer.ui.adapter.MainViewPagerAdapter
import com.jamie.zyco.pdfer.ui.adapter.PdfListAdapter
import com.jamie.zyco.pdfer.ui.decoration.ListDecoration
import com.jamie.zyco.pdfer.utils.Zog
import com.jamie.zyco.pdfer.viewmodel.MainActivityViewModel
import com.jamie.zyco.pdfer.widget.MyRecyclerView
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.Permission
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.none_pdf.*
import kotlinx.android.synthetic.main.progress_bar.*


class MainActivity : BaseActivity<ActivityMainBinding>(), MainActivityClickHandler {

    private var mViewList: ArrayList<View>? = ArrayList()
    private var mTitleList: ArrayList<String>? = ArrayList()
    private var mCurrentView: MyRecyclerView? = null

    private val viewModel: MainActivityViewModel by lazy {
        obtainViewModel(this, MainActivityViewModel::class.java)
    }

    companion object {
        val TAG = MainActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding.click = this
        lifecycle.addObserver(viewModel.lifecycleWatcher)
        initView()
        initData()
    }

    override fun initView() {
        viewModel.mPdfCountLiveData.observe(this@MainActivity, Observer {
            when (it!!) {
                0 -> nonePdfLayout()
                else -> hasPdfLayout()
            }
        })
    }

    override fun initData() {
        viewModel.isScanOver.observe(this@MainActivity, Observer {
            if (it!!) {
                mProgressBarLayout.visibility = View.GONE
                mDrawer.setIntercepted(false)
                Zog.log(1, "${System.currentTimeMillis()}")
            }
        })
        updatePdfList()
        viewModel.get4Database()
    }

    private fun nonePdfLayout() {
        viewModel.mPdfListLiveData.observe(this@MainActivity, Observer {
            if (it!!.isEmpty()) {
                Zog.log(0, "none pdf")
                mViewStub.visibility = View.VISIBLE //viewStub延迟加载布局，当加载一次过后，就会被替换掉，因此不能多次加载(Visible)，否则会报IllegalStateException
                mFindPDF.setOnClickListener {
                    findPDF()
                }
                mViewPager.visibility = View.GONE
                mBottomLl.visibility = View.GONE
            }
        })
    }

    private fun hasPdfLayout() {
        Zog.log(0, "exist pdf")
        mViewStub?.visibility = View.GONE
        mViewPager.visibility = View.VISIBLE
        mBottomLl.visibility = View.VISIBLE
    }

    private fun updatePdfList() {
        viewModel.mPdfListLiveData.observe(this@MainActivity, Observer {
            Zog.log(0, "hide none pdf layout")
            showTab()
            ((mCurrentView?.adapter as HeaderWrapperAdapter).mInnerAdapter as PdfListAdapter).data = it!!
            mFrameContainer?.visibility = View.GONE
            mViewPager.visibility = View.VISIBLE
            mBottomLl.visibility = View.VISIBLE
        })
    }

    private fun showTab() {
        mTitleList!!.clear()
        mViewList?.clear()
        for (i in 0 until Constants.gDirCount) {
            mViewList?.add(LayoutInflater.from(this).inflate(R.layout.item_view_pager, mViewPager, false))
        }
        mTitleList!!.add("我的文件")
        mTitleList!!.add("最近")
        mViewPager.adapter = MainViewPagerAdapter(mViewList!!)
        mTabLayout.setViewPager(mViewPager, mTitleList!!.toArray(arrayOfNulls(mTitleList!!.size)))
        val containerView = mViewList!![mViewPager.currentItem]
        mCurrentView = containerView.findViewById(R.id.mPdfRv) as MyRecyclerView
        mCurrentView?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mCurrentView?.adapter = HeaderWrapperAdapter(PdfListAdapter(R.layout.item_pdf_list, context = this@MainActivity))
        mCurrentView?.addItemDecoration(ListDecoration())
        val firstRecyclerView = mViewList!![0].findViewById(R.id.mPdfRv) as MyRecyclerView
        val headerView = createHeaderView(R.layout.rv_header, firstRecyclerView)
        if (firstRecyclerView.adapter != null) {
            (firstRecyclerView.adapter as HeaderWrapperAdapter).addHeaders(headerView)
        }

    }

    private fun createHeaderView(resId: Int, container: ViewGroup): View {
        val headerView = LayoutInflater.from(this@MainActivity).inflate(resId, container, false)
        headerView.layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, 0)
        return headerView
    }

    private fun showProgressBar() {
        if (mProgressBarLayout == null) {
            mViewStubProgressBar.visibility = View.VISIBLE
        } else {
            mProgressBarLayout.visibility = View.VISIBLE
        }
    }

    private fun indexHeaderAutoBack() {
        if (!mViewList!!.isEmpty()) {
            val firstRecyclerView = mViewList!![0].findViewById(R.id.mPdfRv) as MyRecyclerView
            firstRecyclerView.autoBack()
            firstRecyclerView.isExtension = false
        }
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
                    showProgressBar()
                    indexHeaderAutoBack()
                    mDrawer.setIntercepted(true)
                    Zog.log(1, "${System.currentTimeMillis()}")
                    viewModel.scheduleScanPdf(Environment.getExternalStorageDirectory().path)
                }
                .onDenied {
                    Toast.makeText(this@MainActivity, "授予相应的权限，APP才能正常使用哦~", Toast.LENGTH_SHORT).show()
                }
                .start()
    }

    override fun quicklyFound() {
        Zog.log(0, "main-quicklyFound")
        showProgressBar()
        indexHeaderAutoBack()
        mDrawer.setIntercepted(true)
        viewModel.getData4Database()
        viewModel.mPdfPathLiveData.observe(this@MainActivity, Observer {
            Zog.log(0, "***quicklyScanPdf***")
            viewModel.quicklyScanPdf(it?.toList())
        })
    }

    override fun viewPdf(path: String, page: Int) {
        Zog.log(0, "main-viewPdf")
        val intent = Intent(this@MainActivity, PdfViewActivity::class.java)
        intent.putExtra(Constants.ITEM_PDF_PATH, path)
        intent.putExtra(Constants.DEFAULT_PAGE, page)
        go2Activity(intent)

    }
}
