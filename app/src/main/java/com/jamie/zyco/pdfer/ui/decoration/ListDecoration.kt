package com.jamie.zyco.pdfer.ui.decoration

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.jamie.zyco.pdfer.ui.adapter.HeaderWrapperAdapter
import com.jamie.zyco.pdfer.ui.adapter.PdfListAdapter

class ListDecoration : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val adapter = parent.adapter as HeaderWrapperAdapter
        val layoutManager = parent.layoutManager
        if (layoutManager is LinearLayoutManager) {
            if (adapter.mInnerAdapter is PdfListAdapter) {
                if (adapter.getHeaderCount() > 0 && view != adapter.getHeaderItem(0)
                        && layoutManager.getPosition(view) != adapter.itemCount - 1) {
                    outRect.bottom = 1
                }
            }
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        c.drawColor(Color.GRAY)
    }
}