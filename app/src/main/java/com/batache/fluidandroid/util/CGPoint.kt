package com.batache.fluidandroid.util

import android.view.View
import kotlin.math.pow
import kotlin.math.sqrt

val CGPointZero get() = CGPoint(0f, 0f)

fun CGPoint.distance(toPoint: CGPoint): Float {
  return sqrt((toPoint.x - x).toDouble().pow(2) + (toPoint.y - y).toDouble().pow(2)).toFloat()
}

var View.coordinates: CGPoint
  get() = CGPoint(
    x = x,
    y = y
  )
  set(value) {
    x = value.x
    y = value.y
  }

var View.center: CGPoint
  get() = CGPoint(
    x = x + (measuredWidth / 2),
    y = y + (measuredHeight / 2)
  )
  set(value) {
    x = value.x - (measuredWidth / 2)
    y = value.y - (measuredHeight / 2)
  }

data class CGPoint(
  val x: Float,
  val y: Float
)