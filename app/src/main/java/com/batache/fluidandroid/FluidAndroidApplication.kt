package com.batache.fluidandroid

import android.app.Application
import io.alterac.blurkit.BlurKit

class FluidAndroidApplication : Application() {
  override fun onCreate() {
    super.onCreate()

    BlurKit.init(this)
  }
}