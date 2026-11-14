package com.batache.fluidandroid.util.squircle

import android.content.Context
import android.graphics.Matrix
import android.util.AttributeSet

class TopCropSquircleImageView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0,
) : SquircleImageView(context, attrs, defStyleAttr) {

  init {
    scaleType = ScaleType.MATRIX
  }

  override fun setFrame(l: Int, t: Int, r: Int, b: Int): Boolean {
    super.setFrame(l, t, r, b)
    val matrix = Matrix()
    val scale: Float
    val viewWidth: Int = width - paddingLeft - paddingRight
    val viewHeight: Int = height - paddingTop - paddingBottom
    val drawableWidth: Int = drawable.intrinsicWidth
    val drawableHeight: Int = drawable.intrinsicHeight
    scale = if (drawableWidth * viewHeight > drawableHeight * viewWidth) {
      viewHeight.toFloat() / drawableHeight.toFloat()
    } else {
      viewWidth.toFloat() / drawableWidth.toFloat()
    }
    matrix.setScale(scale, scale)
    imageMatrix = matrix
    return true
  }
}