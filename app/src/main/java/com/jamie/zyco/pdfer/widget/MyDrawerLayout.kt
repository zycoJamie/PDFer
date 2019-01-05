package com.jamie.zyco.pdfer.widget

import android.content.Context
import android.support.v4.widget.DrawerLayout
import android.util.AttributeSet
import android.view.MotionEvent

class MyDrawerLayout : DrawerLayout {

    private var isIntercepted = false

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyle: Int) : super(context, attributeSet, defStyle)

    fun setIntercepted(isIntercepted: Boolean) {
        this.isIntercepted = isIntercepted
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return if (isIntercepted) {
            true
        } else {
            super.onInterceptTouchEvent(ev)
        }
    }
}