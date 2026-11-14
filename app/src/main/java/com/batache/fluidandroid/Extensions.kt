package com.batache.fluidandroid

import android.content.res.Resources
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import androidx.dynamicanimation.animation.SpringAnimation
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sign

val Int.dp: Int get() = (this * Resources.getSystem().displayMetrics.density).toInt()
val Int.px: Int get() = (this / Resources.getSystem().displayMetrics.density).toInt()
val Double.dp: Double get() = this * Resources.getSystem().displayMetrics.density
val Double.px: Double get() = this / Resources.getSystem().displayMetrics.density
val Float.dp: Float get() = this * Resources.getSystem().displayMetrics.density
val Float.px: Float get() = this / Resources.getSystem().displayMetrics.density
val Int.f: Float get() = this.toFloat()
val Double.f: Float get() = this.toFloat()

fun View.setMargins(
  left: Int? = null,
  top: Int? = null,
  right: Int? = null,
  bottom: Int? = null
) {
  val lp = layoutParams as? ViewGroup.MarginLayoutParams
    ?: return

  lp.setMargins(
    left ?: lp.leftMargin,
    top ?: lp.topMargin,
    right ?: lp.rightMargin,
    bottom ?: lp.bottomMargin
  )

  layoutParams = lp
}

fun SpringAnimation.cancelAndAnimateTo(finalPosition: Float): SpringAnimation {
  cancel()
  spring.finalPosition = finalPosition
  start()
  return this
}

fun runIn(delay: Long, r: Runnable) {
  Handler(Looper.getMainLooper()).postDelayed(r, delay)
}

fun mapValues(x1: Float, y1: Float, x2: Float, y2: Float, x3: Float): Float {
  return (x3 - x1) / (x2 - x1) * (y2 - y1) + y1
}

fun Float.powMaintainSign(x: Float): Float = abs(abs(this).pow(x)) * sign