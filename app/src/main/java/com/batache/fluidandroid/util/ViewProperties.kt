package com.batache.fluidandroid.util

import android.view.View

data class ViewProperties(
  val coordinates: CGPoint,
  val center: CGPoint,
  val size: CGSize
)

val View.properties: ViewProperties
  get() = ViewProperties(
    coordinates = coordinates,
    center = center,
    size = size
  )