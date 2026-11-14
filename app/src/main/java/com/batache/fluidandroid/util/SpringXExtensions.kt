package com.batache.fluidandroid.util

import android.widget.TextView
import androidx.dynamicanimation.animation.FloatPropertyCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.batache.fluidandroid.R
import com.batache.fluidandroid.dp
import com.batache.fluidandroid.f
import com.batache.fluidandroid.px
import com.batache.fluidandroid.util.squircle.SquircleImageView
import com.github.lcdsmao.springx.SpringAnimationConfig
import com.github.lcdsmao.springx.ViewPropertySpringAnimator
import kotlinx.android.synthetic.main.activity_synced_lyrics.*

//var ShapeableImageView.cornerRadius
//  set(value) {
//    shapeAppearanceModel = ShapeAppearanceModel.builder()
//      .setAllCorners(CornerFamily.ROUNDED, value.dp)
//      .build()
//    setTag(R.id.CORNER_RADIUS, value)
//  }
//  get() = getTag(R.id.CORNER_RADIUS) as? Float ?: 0f
//
//private val CORNER_RADIUS = object : FloatPropertyCompat<ShapeableImageView>("cornerRadius") {
//  override fun setValue(view: ShapeableImageView, value: Float) {
//    view.cornerRadius = value
//  }
//
//  override fun getValue(view: ShapeableImageView): Float {
//    return view.cornerRadius
//  }
//}
//
//fun <T : ShapeableImageView> ViewPropertySpringAnimator<T>.cornerRadius(
//  value: Float,
//  config: SpringAnimationConfig.() -> Unit = {}
//): ViewPropertySpringAnimator<T> =
//  animateProperty(
//    CORNER_RADIUS,
//    value,
//    config
//  )
var SquircleImageView.cornerRadius
  set(value) {
    setCornerRadius(value.dp)
    setTag(R.id.CORNER_RADIUS, value)
  }
  get() = getTag(R.id.CORNER_RADIUS) as? Float ?: 0f

private val CORNER_RADIUS = object : FloatPropertyCompat<SquircleImageView>("cornerRadius") {
  override fun setValue(view: SquircleImageView, value: Float) {
    view.cornerRadius = value
  }

  override fun getValue(view: SquircleImageView): Float {
    return view.cornerRadius
  }
}

fun <T : SquircleImageView> ViewPropertySpringAnimator<T>.cornerRadius(
  value: Float,
  config: SpringAnimationConfig.() -> Unit = {}
): ViewPropertySpringAnimator<T> =
  animateProperty(
    CORNER_RADIUS,
    value,
    config
  )


private val TEXT_SIZE = object : FloatPropertyCompat<TextView>("textSize") {
  override fun setValue(view: TextView, value: Float) {
    view.textSize = value
  }

  override fun getValue(view: TextView): Float {
    return view.textSize.px
  }
}

fun <T : TextView> ViewPropertySpringAnimator<T>.textSize(
  value: Float,
  config: SpringAnimationConfig.() -> Unit = {}
): ViewPropertySpringAnimator<T> =
  animateProperty(
    TEXT_SIZE,
    value,
    config
  )


var RecyclerView.scrollWay
  set(value) {
    scrollBy(0, value - scrollWay)
    setTag(R.id.SCROLL_Y, value)
  }
  get() = getTag(R.id.SCROLL_Y) as? Int ?: 0

private val SCROLL_Y = object : FloatPropertyCompat<RecyclerView>("scrollY") {
  override fun setValue(view: RecyclerView, value: Float) {
    view.scrollWay = value.toInt()
  }

  override fun getValue(view: RecyclerView): Float {
    return view.scrollWay.f
  }
}

fun <T : RecyclerView> ViewPropertySpringAnimator<T>.scrollYBy(
  value: Float,
  config: SpringAnimationConfig.() -> Unit = {}
): ViewPropertySpringAnimator<T> =
  animateProperty(
    SCROLL_Y,
    value,
    config
  )





private val SCROLL_TO_POS = object : FloatPropertyCompat<RecyclerView>("scrollToPos") {
  override fun setValue(view: RecyclerView, value: Float) {
    (view.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(value.toInt(), 32.dp)
  }

  override fun getValue(view: RecyclerView): Float {
    return view.computeVerticalScrollOffset().f
  }
}

fun <T : RecyclerView> ViewPropertySpringAnimator<T>.scrollToPos(
  value: Float,
  config: SpringAnimationConfig.() -> Unit = {}
): ViewPropertySpringAnimator<T> =
  animateProperty(
    SCROLL_TO_POS,
    value,
    config
  )