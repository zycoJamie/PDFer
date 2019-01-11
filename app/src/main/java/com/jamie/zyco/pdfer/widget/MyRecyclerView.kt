package com.jamie.zyco.pdfer.widget

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Scroller
import com.jamie.zyco.pdfer.utils.Zog

class MyRecyclerView : RecyclerView {
    companion object {
        val TAG = MyRecyclerView::class.java.simpleName
    }

    private var initPointY: Float = 0f
    private var mHeadView: View? = null
    private val scroller: Scroller by lazy {
        Scroller(context)
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyle: Int) : super(context, attributeSet, defStyle)

    fun setHeadView(view: View) {
        mHeadView = view
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(e: MotionEvent?): Boolean {
        when (e!!.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                initPointY = e.rawY
            }
            MotionEvent.ACTION_MOVE -> {
                val layoutManager = layoutManager as LinearLayoutManager
                if (scrollState == RecyclerView.SCROLL_STATE_DRAGGING
                        && layoutManager.findFirstCompletelyVisibleItemPosition() == 0 && e.rawY - initPointY > 0 && mHeadView != null) {
                    if (!scroller.computeScrollOffset() || scroller.isFinished) {
                        Zog.log(0, TAG, "进来啦")
                        scroller.startScroll(0, initPointY.toInt(), 0, (e.rawY-initPointY).toInt())
                        invalidate()
                    }
                    /*Zog.log(0, TAG, "move")
                    Zog.log(0, TAG, "${e.rawY}-${initPointY}滑动${e.rawY - initPointY}")
                    if (mHeadView is LinearLayout) {
                        mHeadView?.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (e.rawY - initPointY).toInt())
                    }*/
                }
            }
        }
        return super.onTouchEvent(e)
    }


    override fun onDetachedFromWindow() {
        scroller.forceFinished(true)
        super.onDetachedFromWindow()
    }
}