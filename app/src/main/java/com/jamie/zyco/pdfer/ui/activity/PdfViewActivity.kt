package com.jamie.zyco.pdfer.ui.activity

import android.os.Bundle
import com.github.barteksc.pdfviewer.link.DefaultLinkHandler
import com.github.barteksc.pdfviewer.util.FitPolicy
import com.jamie.zyco.pdfer.R
import com.jamie.zyco.pdfer.base.BaseActivity
import com.jamie.zyco.pdfer.base.Constants
import com.jamie.zyco.pdfer.databinding.ActivityPdfViewBinding
import com.jamie.zyco.pdfer.viewmodel.PdfViewActivityViewModel
import kotlinx.android.synthetic.main.activity_pdf_view.*
import java.io.File

class PdfViewActivity : BaseActivity<ActivityPdfViewBinding>() {

    private val viewModel by lazy {
        obtainViewModel(this@PdfViewActivity, PdfViewActivityViewModel::class.java)
    }

    private val mPdfPath: String by lazy {
        intent.getStringExtra(Constants.ITEM_PDF_PATH)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(viewModel.lifecycleWatcher)
        initView()
        initData()
    }

    override fun initView() {

    }

    override fun initData() {
        mPdfView.fromFile(File(mPdfPath))
                .defaultPage(0)
                .enableSwipe(true)
                .swipeHorizontal(true)
                .enableDoubletap(true)
                .enableAnnotationRendering(true)
                .enableAntialiasing(true)
                .spacing(0)
                .autoSpacing(true)
                .linkHandler(DefaultLinkHandler(mPdfView))
                .pageFitPolicy(FitPolicy.WIDTH)
                .pageFling(true)
                .pageSnap(true)
                .nightMode(false)
                .onError {
                    toast(getString(R.string.pdf_view_format_error), 0)
                }
                .load()

    }

    override fun getLayoutId() = R.layout.activity_pdf_view
}