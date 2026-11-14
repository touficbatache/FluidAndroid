package com.batache.fluidandroid.old

import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.doOnLayout
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.FlingAnimation
import com.batache.fluidandroid.R
import kotlinx.android.synthetic.main.activity_fling_animation.*

class FlingAnimationActivity : AppCompatActivity() {

  private lateinit var gestureDetector: GestureDetectorCompat

  private var duckFlingAnimationX: FlingAnimation? = null
  private var duckFlingAnimationY: FlingAnimation? = null

  private val gestureListener = object : GestureDetector.SimpleOnGestureListener() {
    override fun onDown(event: MotionEvent): Boolean {
      return true
    }

    override fun onFling(
        event1: MotionEvent,
        event2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
      duckFlingAnimationX?.setStartVelocity(velocityX)?.start()
      duckFlingAnimationY?.setStartVelocity(velocityY)?.start()
      return true
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_fling_animation)
    title = "Fling anim"
    supportActionBar?.setDisplayHomeAsUpEnabled(true)

    gestureDetector = GestureDetectorCompat(this, gestureListener)

    duck.doOnLayout { setupFlingAnimations() }
    duck.setOnTouchListener { _, event -> gestureDetector.onTouchEvent(event) }
  }

  private fun setupFlingAnimations() {
    duckFlingAnimationX = FlingAnimation(duck, DynamicAnimation.X).apply {
      setMinValue(0f)
      setMaxValue(pond.width.toFloat() - duck.width)
      friction = 1f
    }.addEndListener { _, _, _, _ -> duckFlingAnimationY?.let { if (it.isRunning) it.cancel() } }

    duckFlingAnimationY = FlingAnimation(duck, DynamicAnimation.Y).apply {
      setMinValue(0f)
      setMaxValue(pond.height.toFloat() - duck.width)
      friction = 1f
    }.addEndListener { _, _, _, _ -> duckFlingAnimationX?.let { if (it.isRunning) it.cancel() } }
  }
}