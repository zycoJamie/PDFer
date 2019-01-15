package com.jamie.zyco.pdfer.widget

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Scroller
import com.jamie.zyco.pdfer.ui.adapter.HeaderWrapperAdapter
import com.jamie.zyco.pdfer.utils.Zog

class MyRecyclerView : RecyclerView {
    companion object {
        val TAG = MyRecyclerView::class.java.simpleName
    }

    private val mFriction = 2.4f  //阻力系数
    private var initPointY: Float = 0f
    private var mHeaderHight = 0
    private val mThreshold = 300  //header最大高度
    private val mAutoScrollThreshold = 150 //触发header自动滑动的最大高度
    private val mScroller: Scroller by lazy {
        Scroller(context)
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyle: Int) : super(context, attributeSet, defStyle)


    /**
     * 不要直接构建新的LayoutParams对象来改变header的高度
     * header.layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, ((e.rawY - initPointY) / mFriction).toInt())
     * 使用上面代码会报NullPointException 'android.support.v7.widget.RecyclerView$ViewHolder.getLayoutPosition()' on a null object reference
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(e: MotionEvent?): Boolean {
        when (e!!.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                initPointY = e.rawY
            }
            MotionEvent.ACTION_MOVE -> {
                val layoutManager = layoutManager as LinearLayoutManager
                val headerWrapperAdapter = adapter as HeaderWrapperAdapter
                if (scrollState == RecyclerView.SCROLL_STATE_DRAGGING
                        && layoutManager.findFirstCompletelyVisibleItemPosition() == 0 && e.rawY - initPointY > 0
                        && mHeaderHight <= mThreshold
                        && headerWrapperAdapter.getHeaderCount() > 0) {
                    move(e)
                }
                if (mHeaderHight == mThreshold && e.rawY - initPointY < 0) {
                    move(e)
                }
            }
            MotionEvent.ACTION_UP -> {
                if (mHeaderHight in mAutoScrollThreshold until mThreshold) {
                    mScroller.startScroll(0, mHeaderHight, 0, mThreshold - mHeaderHight, 500)
                    invalidate()
                }
                if (mHeaderHight in 1 until mAutoScrollThreshold) {
                    mScroller.startScroll(0, mHeaderHight, 0, -mHeaderHight, 500)
                    invalidate()
                }
            }
        }
        return super.onTouchEvent(e)
    }

    private fun move(e: MotionEvent) {
        val headerWrapperAdapter = adapter as HeaderWrapperAdapter
        mHeaderHight = Math.abs((e.rawY - initPointY) / mFriction).toInt()
        Zog.log(0, TAG, "header hight:$mHeaderHight")
        val header: View? = headerWrapperAdapter.getHeaderItem(0)
        val layoutParams = header?.layoutParams
        if (mHeaderHight > mThreshold) {
            mHeaderHight = mThreshold
        }
        if (mHeaderHight < 0) {
            mHeaderHight = 0
        }
        layoutParams?.height = mHeaderHight
        header?.layoutParams = layoutParams
        smoothScrollToPosition(0)
    }

    override fun computeScroll() {
        if (mScroller.computeScrollOffset()) {
            Zog.log(0, TAG, "computeScroll")
            val headerWrapperAdapter = adapter as HeaderWrapperAdapter
            val header: View? = headerWrapperAdapter.getHeaderItem(0)
            val layoutParams = header?.layoutParams
            layoutParams?.height = mScroller.currY
            header?.layoutParams = layoutParams
            Zog.log(0, TAG, "${mScroller.currY}")
            smoothScrollBy(0, mScroller.currY - scrollY)
            postInvalidate()
        }
    }

    override fun onDetachedFromWindow() {
        mScroller.forceFinished(true)
        super.onDetachedFromWindow()
    }
}