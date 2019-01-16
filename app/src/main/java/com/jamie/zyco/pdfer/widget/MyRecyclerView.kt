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

    private val mFriction = 2.5f  //阻力系数
    private var initPointY: Float = 0f
    private var mHeaderHeight = 0
    private val mThreshold = 400  //header最大高度
    private val mAutoScrollThreshold = 150 //触发header自动滑动的最大高度
    private var isExtension = false
    private var mBackByFinger = false
    private var isFirstDragging = true
    //问题：当header处于展开状态，手指上划，滑动距离超过header高度 通过scroller动态缩小header高度时 出现了向下回弹
    // 解决思路：当最终松手时，若滑动距离超过header高度，直接修改header高度为0
    private var mBackByFingerDistance = 0
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
                isFirstDragging = true
                if (!mScroller.isFinished) {
                    mScroller.forceFinished(true) //当scroller到达final坐标后，并未立即结束
                }
            }
            MotionEvent.ACTION_MOVE -> {
                val layoutManager = layoutManager as LinearLayoutManager
                val headerWrapperAdapter = adapter as HeaderWrapperAdapter
                if (scrollState == RecyclerView.SCROLL_STATE_DRAGGING
                        && layoutManager.findFirstCompletelyVisibleItemPosition() == 0 && e.rawY - initPointY > 0
                        && mHeaderHeight <= mThreshold
                        && headerWrapperAdapter.getHeaderCount() > 0
                        && !isExtension) {
                    //当首次出现dragging时立即刷新初始Y坐标，避免一开始下拉header时，header就有高度e.rawY - initPointY
                    if (isFirstDragging) {
                        initPointY = e.rawY
                        Zog.log(0, TAG, "first dragging initPointY:$initPointY")
                        isFirstDragging = false
                    }
                    Zog.log(0, TAG, "pull")
                    Zog.log(0, TAG, "e.rawY:${e.rawY} initPointY:$initPointY")
                    move(e)
                }
                if (isExtension && e.rawY - initPointY < 0 && headerWrapperAdapter.getHeaderCount() > 0) {
                    Zog.log(0, TAG, "back")
                    mBackByFingerDistance = (e.rawY - initPointY).toInt()
                    move(e)
                }
            }
            MotionEvent.ACTION_UP -> {
                isExtension = mHeaderHeight >= mAutoScrollThreshold
                if (mBackByFinger) {
                    isExtension = false
                }
                if (mHeaderHeight in mAutoScrollThreshold until mThreshold && !mBackByFinger) {
                    Zog.log(0, TAG, "start auto pull $mHeaderHeight ****")
                    mScroller.startScroll(0, mHeaderHeight, 0, mThreshold - mHeaderHeight, 500)
                    Zog.log(0, TAG, "finalY ${mScroller.finalY}")
                    invalidate()
                }
                if (mHeaderHeight in 1 until mAutoScrollThreshold || mBackByFinger) {
                    val layoutManager = layoutManager as LinearLayoutManager
                    val headerWrapperAdapter = adapter as HeaderWrapperAdapter
                    if (mBackByFingerDistance < -mThreshold && layoutManager.findLastVisibleItemPosition() != headerWrapperAdapter.itemCount - 1) {
                        val header: View? = headerWrapperAdapter.getHeaderItem(0)
                        val layoutParams = header?.layoutParams
                        layoutParams?.height = 0
                        header?.layoutParams = layoutParams

                    } else {
                        Zog.log(0, TAG, "start auto back $mHeaderHeight ****")
                        mScroller.startScroll(0, mHeaderHeight, 0, -mHeaderHeight, 500)
                        Zog.log(0, TAG, "finalY ${mScroller.finalY}")
                        invalidate()
                    }
                    mBackByFingerDistance = 0
                }
            }
        }
        return super.onTouchEvent(e)
    }

    private fun move(e: MotionEvent) {
        val headerWrapperAdapter = adapter as HeaderWrapperAdapter
        if (!isExtension) {
            mBackByFinger = false
            mHeaderHeight = ((e.rawY - initPointY) / mFriction).toInt()
            Zog.log(0, TAG, "header height:$mHeaderHeight before")
            val header: View? = headerWrapperAdapter.getHeaderItem(0)
            val layoutParams = header?.layoutParams
            if (mHeaderHeight >= mThreshold) {
                mHeaderHeight = mThreshold
            }
            if (mHeaderHeight < 0) {
                mHeaderHeight = 0
            }
            Zog.log(0, TAG, "header height:$mHeaderHeight after")
            layoutParams?.height = mHeaderHeight
            header?.layoutParams = layoutParams
            smoothScrollToPosition(0)
        } else {
            mBackByFinger = true
        }
    }

    override fun computeScroll() {
        if (mScroller.computeScrollOffset()) {
            Zog.log(0, TAG, "computeScroll ${mScroller.isFinished}")
            val headerWrapperAdapter = adapter as HeaderWrapperAdapter
            val header: View? = headerWrapperAdapter.getHeaderItem(0)
            val layoutParams = header?.layoutParams
            val lastY = layoutParams?.height
            layoutParams?.height = mScroller.currY
            mHeaderHeight = mScroller.currY
            header?.layoutParams = layoutParams
            Zog.log(0, TAG, "${mScroller.currY}-$lastY")
            smoothScrollBy(0, mScroller.currY - lastY!!)
            postInvalidate()
        }
    }

    override fun onDetachedFromWindow() {
        mScroller.forceFinished(true)
        super.onDetachedFromWindow()
    }
}