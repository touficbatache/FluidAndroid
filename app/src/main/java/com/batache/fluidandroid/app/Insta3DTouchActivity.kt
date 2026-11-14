package com.batache.fluidandroid.app

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.doOnLayout
import com.batache.fluidandroid.R
import com.batache.fluidandroid.mapValues
import com.batache.fluidandroid.util.*
import io.alterac.blurkit.BlurKit
import kotlinx.android.synthetic.main.activity_insta_3d_touch.*
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.sign

class Insta3DTouchActivity : AppCompatActivity() {

  /**
   * Animation variables
   */
  private val stiff = 250f
  private val damp = 0.85f

  private lateinit var morphAnimation: SpringMorphAnimation

  /**
   * Gesture detection variables
   */
  private val handler = Handler(Looper.getMainLooper())
  private val runnable = Runnable { start3DTouch() }

  private val panRecognizer by lazy { PanRecognizer(::onPan) }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_insta_3d_touch)

    setupAnimation()
    setupGestureDetection()
  }

  private fun setupAnimation() {
    appFakeBackground.doOnLayout {
      overlayBlur.setImageBitmap(BlurKit.getInstance().fastBlur(appFakeBackground, 5, 0.1f))
    }
    morphAnimation = SpringMorphAnimation(thumbnail, floatingCard, stiff, damp)
  }

  @SuppressLint("ClickableViewAccessibility")
  private fun setupGestureDetection() {
    thumbnail.setOnTouchListener { view, motionEvent ->
      floatingCard.dispatchTouchEvent(motionEvent)

      if (motionEvent.action == MotionEvent.ACTION_DOWN) {
        handler.postDelayed(runnable, 250)
        morphAnimation.animateSourceViewScale(0.9f)
      }
      if (motionEvent.action == MotionEvent.ACTION_UP) {
        handler.removeCallbacks(runnable)
      }

      return@setOnTouchListener true
    }
    floatingCard.setOnTouchListener(panRecognizer)
  }

  private fun start3DTouch() {
    overlay.animate().alpha(1f).setDuration(200).start()
    morphAnimation.animateForward()
  }

  private var originalTouchPoint: CGPoint = CGPointZero
  private fun onPan(motionData: PanRecognizer.MotionData): Boolean {
    val touchPoint = motionData.location

    when (motionData.state) {
      PanRecognizer.State.BEGAN -> {
        originalTouchPoint = touchPoint
      }

      PanRecognizer.State.CHANGED -> {
        if (morphAnimation.isMorphed) {
          val offset: Pair<Float, Float> = calculateOffset(touchPoint, originalTouchPoint)
          val scale = mapValues(0f, 1f, 600f, .7f, offset.second.absoluteValue).coerceAtLeast(.7f)
          morphAnimation.divertAnimation(offset.first, offset.second, scale)
        }
      }

      PanRecognizer.State.ENDED, PanRecognizer.State.CANCELLED -> {
        val startVelocity =
          if (morphAnimation.isMorphed && motionData.velocity.y > 0) Math.abs(motionData.velocity.y * 1.5f) else 0f
        end3DTouch(startVelocity)
      }
      else -> {}
    }

    return true
  }

  private fun end3DTouch(startVelocity: Float) {
    overlay.animate().alpha(0f).setDuration(200).start()
    morphAnimation.animateBackward(startVelocity)
  }

  private fun calculateOffset(touchPoint: CGPoint, originalTouchPoint: CGPoint): Pair<Float, Float> {
    // X calculation
    val castTo = 100f
    val divider = container.measuredWidth / 2f / castTo
    val originalXOffset = touchPoint.x - originalTouchPoint.x
    val x = originalXOffset / divider

    // Y calculation
    val originalYOffset = touchPoint.y - originalTouchPoint.y
    val power = if (originalYOffset > 0) 1f else 0.7f
    val y = originalYOffset.absoluteValue.pow(power) * originalYOffset.sign

    return Pair(x, y)
  }

  override fun onResume() {
    super.onResume()

    WindowCompat.setDecorFitsSystemWindows(window, false)
  }
}
