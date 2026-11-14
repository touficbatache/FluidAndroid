package com.batache.fluidandroid.util

import android.view.View

data class CGSize(
  val width: Int,
  val height: Int
)

val View.size: CGSize
  get() = CGSize(
    width = measuredWidth,
    height = measuredHeight
  )