package com.jamie.zyco.pdfer.pdfcustom

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.scroll.ScrollHandle
import com.jamie.zyco.pdfer.R
import com.jamie.zyco.pdfer.utils.ConvertUtils

class MyHorScrollHandle : ScrollHandle, RelativeLayout {

    lateinit var mPdfView: PDFView
    lateinit var mCurrentPageTv: TextView
    lateinit var mSumPageTv: TextView
    lateinit var mContainer: LinearLayout
    lateinit var mDivider: View
    private val mLarge: Int = ConvertUtils.px2dp(80f)
    private val mShort: Int = ConvertUtils.px2dp(50f)
    private val mDividerHeight: Int = ConvertUtils.px2dp(1f)
    private val mDividerWidth: Int = ConvertUtils.px2dp(40f)
    private val mPageTextSize: Int = ConvertUtils.px2sp(12f)
    private val mPageTextColor: Int = Color.WHITE

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context, attributeSet: AttributeSet?, defStyle: Int) : super(context, attributeSet, defStyle)

    override fun setPageNum(pageNum: Int) {

    }

    override fun destroyLayout() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setScroll(position: Float) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun shown(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun hide() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun show() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun hideDelayed() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setupLayout(pdfView: PDFView?) {
        initView()
        mPdfView = pdfView!!
        background = context.getDrawable(R.drawable.pdf_page)!!
        mSumPageTv.text = pdfView.pageCount.toString()
        if (!mPdfView.isSwipeVertical) {


        }
        addView(mCurrentPageTv)
        addView(mSumPageTv)

    }

    private fun initView() {
        mSumPageTv = TextView(context)
        mSumPageTv.apply {
            textSize = mPageTextSize.toFloat()
            setTextColor(mPageTextColor)
        }
        mCurrentPageTv = TextView(context)
        mCurrentPageTv.apply {
            textSize = mPageTextSize.toFloat()
            setTextColor(mPageTextColor)
        }
        mDivider = View(context)
        mDivider.apply {
            layoutParams = LinearLayout.LayoutParams(mDividerWidth, mDividerHeight)
            setBackgroundColor(mPageTextColor)
        }
        mContainer = LinearLayout(context)
        mContainer.addView(mCurrentPageTv)
        mContainer.addView(mDivider)
        mContainer.addView(mSumPageTv)
    }

}