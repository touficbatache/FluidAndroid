package com.batache.fluidandroid.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.dynamicanimation.animation.SpringForce
import com.batache.fluidandroid.R
import com.github.lcdsmao.springx.ViewPropertySpringAnimator
import kotlinx.android.synthetic.main.activity_testing.*

class TestingActivity : AppCompatActivity() {

  /**
   * Animation variables
   */
  private val stiff = SpringForce.STIFFNESS_VERY_LOW
  private val damp = SpringForce.DAMPING_RATIO_NO_BOUNCY
  private val animator by lazy {
    ViewPropertySpringAnimator(animatedView)
      .defaultStiffness(stiff)
      .defaultDampingRatio(damp)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_testing)

    showBtn.setOnClickListener {
      animator
        .alpha(1f)
        .start()
    }
  }
}