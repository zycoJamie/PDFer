package com.jamie.zyco.pdfer.widget

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.jamie.zyco.pdfer.ui.adapter.HeaderWrapperAdapter
import com.jamie.zyco.pdfer.utils.Zog

class MyRecyclerView : RecyclerView {
    companion object {
        val TAG = MyRecyclerView::class.java.simpleName
    }

    private val mFriction = 3f
    private var initPointY: Float = 0f

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
                        && headerWrapperAdapter.getHeaderCount() > 0) {
                    Zog.log(0, TAG, "${e.rawY}-$initPointY=${e.rawY - initPointY}")
                    val header: View? = headerWrapperAdapter.getHeaderItem(0)
                    val layoutParams = header?.layoutParams
                    layoutParams?.height = ((e.rawY - initPointY) / mFriction).toInt()
                    header?.layoutParams = layoutParams
                    smoothScrollToPosition(0)
                }
            }
        }
        return super.onTouchEvent(e)
    }

}