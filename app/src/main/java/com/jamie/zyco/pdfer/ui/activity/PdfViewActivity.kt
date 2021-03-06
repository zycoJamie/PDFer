package com.jamie.zyco.pdfer.ui.activity

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.PopupWindow
import com.github.barteksc.pdfviewer.link.DefaultLinkHandler
import com.github.barteksc.pdfviewer.util.FitPolicy
import com.jamie.zyco.pdfer.R
import com.jamie.zyco.pdfer.base.BaseActivity
import com.jamie.zyco.pdfer.base.Constants
import com.jamie.zyco.pdfer.databinding.ActivityPdfViewBinding
import com.jamie.zyco.pdfer.listener.clickhandler.PdfViewActivityClickHandler
import com.jamie.zyco.pdfer.pdfcustom.MyHorScrollHandle
import com.jamie.zyco.pdfer.utils.ConvertUtils
import com.jamie.zyco.pdfer.viewmodel.PdfViewActivityViewModel
import kotlinx.android.synthetic.main.activity_pdf_view.*
import java.io.File

class PdfViewActivity : BaseActivity<ActivityPdfViewBinding>(), PdfViewActivityClickHandler {

    private val viewModel by lazy {
        obtainViewModel(this@PdfViewActivity, PdfViewActivityViewModel::class.java)
    }

    private val mPdfPath: String by lazy {
        intent.getStringExtra(Constants.ITEM_PDF_PATH)
    }

    private val mPdfDefaultPage by lazy {
        intent.getIntExtra(Constants.DEFAULT_PAGE, 0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(viewModel.lifecycleWatcher)
        initView()
        initData()
    }

    override fun initView() {
        configuration()
    }

    override fun initData() {
        mPdfView.fromFile(File(mPdfPath))
                .defaultPage(mPdfDefaultPage)
                .enableSwipe(true) //允许使用滑动阻止更改页面
                .enableDoubletap(true)
                .enableAnnotationRendering(true)
                .enableAntialiasing(true)
                .spacing(0)
                .pageFitPolicy(FitPolicy.WIDTH)
                .nightMode(false)
                .autoSpacing(true) //类似viewpager
                .pageFling(true)
                .pageSnap(true)
                .swipeHorizontal(true)
                .linkHandler(DefaultLinkHandler(mPdfView))
                .scrollHandle(MyHorScrollHandle(this@PdfViewActivity))
                .onLongPress {
                    showPop(it)
                }
                .onError {
                    toast(getString(R.string.pdf_view_format_error), 0)
                }
                .load()

    }

    private fun configuration() {
        mPdfView.useBestQuality(true)
        mPdfView.minZoom = 1f
        mPdfView.midZoom = 1f
        mPdfView.maxZoom = 1.5f
    }

    override fun getLayoutId() = R.layout.activity_pdf_view

    /** PdfViewActivityClickHandler **/

    override fun showPop(event: MotionEvent) {
        val contentView = LayoutInflater.from(this@PdfViewActivity).inflate(R.layout.popup_pdf_tag, mPdfView, false)
        val pop = PopupWindow(contentView, ConvertUtils.px2dp(1000f), ConvertUtils.px2dp(1000f), true)
        pop.isTouchable = true
        pop.isOutsideTouchable = true
        pop.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        pop.showAsDropDown(mPdfView, event.rawX.toInt(), event.rawY.toInt())

    }
}