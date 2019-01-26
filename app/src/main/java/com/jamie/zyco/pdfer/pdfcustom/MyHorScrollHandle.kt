package com.jamie.zyco.pdfer.pdfcustom

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
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
    lateinit var mCenterPageTv: TextView
    private val mLarge: Int = ConvertUtils.px2dp(1000f)
    private val mShort: Int = ConvertUtils.px2dp(900f)
    private val mDividerHeight: Int = ConvertUtils.px2dp(10f)
    private val mDividerWidth: Int = ConvertUtils.px2dp(400f)
    private val mPageTextSize: Int = ConvertUtils.px2sp(70f)
    private val mCenterPageTextSize: Int = ConvertUtils.px2sp(140f)
    private val mCenterPageViewSize: Int = ConvertUtils.px2dp(2000f)
    private val mPageTextColor: Int = Color.WHITE
    private var mAdjust: Float = 0f //每次给页码游标定位时，进行位置的调整，0->viewSize 由当前浏览pdf的进度百分比控制参数的改变
    private var mCurPage: Int = 0
    private var mKillRunnable = false
    private var mLastTouchUpTime = 0L

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context, attributeSet: AttributeSet?, defStyle: Int) : super(context, attributeSet, defStyle)

    override fun setPageNum(pageNum: Int) {
        if (pageNum.toString() != mCurrentPageTv.text) {
            mCurrentPageTv.text = pageNum.toString()
            mCenterPageTv.text = pageNum.toString()
        }
        mCurPage = pageNum
    }

    override fun destroyLayout() {
        mPdfView.removeView(this)
    }

    override fun setScroll(position: Float) {
        if (!shown() && mCurPage != mPdfView.currentPage + 1) { //mPdfView.currentPage 从0开始的
            show()
        }
        if (!mPdfView.isSwipeVertical) {
            toPosition(position * mPdfView.width)
        }
    }

    private fun toPosition(position: Float) {
        if (!mPdfView.isSwipeVertical) {
            x = position - mAdjust
            if (x < 0) {
                x = 0f
            } else if (x > mPdfView.width - width) {
                x = (mPdfView.width - width).toFloat()
            }
            mAdjust = position / mPdfView.width * width
            invalidate()
        }
    }

    override fun shown() = visibility == View.VISIBLE

    override fun hide() {
        visibility = View.GONE
    }

    override fun show() {
        visibility = View.VISIBLE
    }

    override fun hideDelayed() {
        mKillRunnable = false
        postDelayed({
            if (!mKillRunnable) {
                hide()
            }
        }, 3000)
    }

    private fun hideCenterPageDelayed() {
        postDelayed({
            mCenterPageTv.visibility = View.GONE
        }, 500)
    }

    override fun setupLayout(pdfView: PDFView?) {
        initView()
        mPdfView = pdfView!!
        background = context.getDrawable(R.drawable.pdf_page)!!
        mSumPageTv.text = pdfView.pageCount.toString()
        if (!mPdfView.isSwipeVertical) {
            mContainer.orientation = LinearLayout.VERTICAL
            val containerLp = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
            containerLp.addRule(CENTER_IN_PARENT)
            addView(mContainer, containerLp)
            val lp = RelativeLayout.LayoutParams(mShort, mLarge)
            lp.addRule(ALIGN_PARENT_BOTTOM)
            pdfView.addView(this, lp)
            val centerTvLp = RelativeLayout.LayoutParams(mCenterPageViewSize, mCenterPageViewSize)
            centerTvLp.addRule(CENTER_IN_PARENT)
            pdfView.addView(mCenterPageTv, centerTvLp)
        }

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
        mContainer.gravity = Gravity.CENTER_HORIZONTAL
        mContainer.addView(mCurrentPageTv)
        mContainer.addView(mDivider)
        mContainer.addView(mSumPageTv)
        mCenterPageTv = TextView(context)
        mCenterPageTv.apply {
            textSize = mCenterPageTextSize.toFloat()
            setTextColor(mPageTextColor)
            val padding = ConvertUtils.px2dp(100f)
            setPadding(padding, padding, padding, padding)
            gravity = Gravity.CENTER
        }
        mCenterPageTv.background = context.getDrawable(R.drawable.shape_center_circle)
        mCenterPageTv.visibility = View.GONE
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (mPdfView.pageCount <= 0) {
            return super.onTouchEvent(event)
        }
        when (event!!.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                mKillRunnable = true
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                mAdjust = event.rawX / mPdfView.width * width
                toPosition(event.rawX)
                mPdfView.positionOffset = event.rawX / mPdfView.width
                mCenterPageTv.visibility = View.VISIBLE
                return true
            }
            MotionEvent.ACTION_UP -> {
                val currentTime = System.currentTimeMillis()
                mPdfView.performPageSnap()
                hideCenterPageDelayed()
                if (currentTime - mLastTouchUpTime > 3000) {
                    hideDelayed()
                    mLastTouchUpTime = currentTime
                }
            }
        }
        return super.onTouchEvent(event)
    }

}