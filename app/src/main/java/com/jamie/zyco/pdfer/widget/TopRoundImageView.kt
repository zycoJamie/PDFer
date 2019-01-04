package com.jamie.zyco.pdfer.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.util.AttributeSet
import android.widget.ImageView

class TopRoundImageView : ImageView {

    private val mClipCorner=25f
    var mWidth = 0
    var mHeight = 0
    var path: Path = Path()

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context, attributeSet: AttributeSet?, defStyle: Int) : super(context, attributeSet, defStyle)

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = width
        mHeight = height
    }

    override fun onDraw(canvas: Canvas?) {
        path.moveTo(mClipCorner, 0f)
        path.lineTo(mWidth - mClipCorner, 0f)
        path.quadTo(mWidth.toFloat(), 0f, mWidth.toFloat(), mClipCorner)
        path.lineTo(mWidth.toFloat(), mHeight.toFloat())
        path.lineTo(0f, mHeight.toFloat())
        path.lineTo(0f, mClipCorner)
        path.quadTo(0f, 0f, mClipCorner, 0f)
        canvas?.clipPath(path)
        super.onDraw(canvas)
    }
}