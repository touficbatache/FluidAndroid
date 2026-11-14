package com.batache.fluidandroid.app

import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnLayout
import com.batache.fluidandroid.R
import io.alterac.blurkit.BlurKit
import kotlinx.android.synthetic.main.activity_player.*

class PlayerActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_player)

    firstBackgroundIv.doOnLayout {
      firstBackgroundIv.setImageBitmap(BlurKit.getInstance().fastBlur(firstBackgroundIv, 25, .1f))
      firstBackgroundIv.rotateInfinitely(true)
//      firstBackgroundIv.shapeAppearanceModel = null
    }

    secondBackgroundIv.doOnLayout {
      secondBackgroundIv.setImageBitmap(BlurKit.getInstance().fastBlur(secondBackgroundIv, 25, .1f))
      secondBackgroundIv.rotateInfinitely(false)
    }
  }

  private fun ImageView.rotateInfinitely(clockwise: Boolean){
    val rotateAnimation = RotateAnimation(
      if (clockwise) 0f else 360f, if (clockwise) 360f else 0f,
      Animation.RELATIVE_TO_SELF, 0.5f,
      Animation.RELATIVE_TO_SELF, 0.5f

    )
    rotateAnimation.duration = 35_000
    rotateAnimation.repeatCount = Animation.INFINITE
    this.startAnimation(rotateAnimation)
  }

}