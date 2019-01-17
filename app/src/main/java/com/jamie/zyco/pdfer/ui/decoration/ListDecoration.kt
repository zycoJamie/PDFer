package com.jamie.zyco.pdfer.ui.decoration

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.jamie.zyco.pdfer.ui.adapter.HeaderWrapperAdapter
import com.jamie.zyco.pdfer.ui.adapter.PdfListAdapter

class ListDecoration : RecyclerView.ItemDecoration() {

    private val mPaintDivider: Paint = Paint()
    private val mDividerHeight = 1

    init {
        mPaintDivider.color = Color.RED
        mPaintDivider.style = Paint.Style.FILL
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val adapter = parent.adapter as HeaderWrapperAdapter
        val layoutManager = parent.layoutManager
        if (layoutManager is LinearLayoutManager) {
            if (adapter.mInnerAdapter is PdfListAdapter) {
                if (adapter.getHeaderCount() > 0 && view != adapter.getHeaderItem(0)
                        && layoutManager.getPosition(view) != adapter.itemCount - 1) {
                    outRect.bottom = mDividerHeight
                }
                if (adapter.getHeaderCount() == 0 && layoutManager.getPosition(view) != adapter.itemCount - 1) {
                    outRect.bottom = mDividerHeight
                }
            }
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight
        val count = parent.childCount
        val adapter = parent.adapter as HeaderWrapperAdapter
        val layoutManager = parent.layoutManager
        for (i in 0 until count) {
            if (layoutManager is LinearLayoutManager) {
                if (adapter.mInnerAdapter is PdfListAdapter) {
                    if (adapter.getHeaderCount() > 0 && parent.getChildAt(i) != adapter.getHeaderItem(0)
                            && i != count - 1) {
                        val top = parent.getChildAt(i).bottom
                        val bottom = top + mDividerHeight
                        c.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mPaintDivider)
                    }
                    if (adapter.getHeaderCount() == 0 && i != count - 1) {
                        val top = parent.getChildAt(i).bottom
                        val bottom = top + mDividerHeight
                        c.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mPaintDivider)
                    }
                }
            }
        }
    }
}