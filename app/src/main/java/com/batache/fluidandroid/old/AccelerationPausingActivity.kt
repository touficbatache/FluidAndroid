package com.batache.fluidandroid.old

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.HapticFeedbackConstants
import androidx.appcompat.app.AppCompatActivity
import com.batache.fluidandroid.R
import com.batache.fluidandroid.util.CGPoint
import com.batache.fluidandroid.util.CGPointZero
import com.batache.fluidandroid.util.PanRecognizer
import com.github.lcdsmao.springx.ViewPropertySpringAnimator
import kotlinx.android.synthetic.main.activity_acceleration_pausing.*
import kotlin.math.abs
import kotlin.math.pow

class AccelerationPausingActivity : AppCompatActivity() {

  private val accelerationViewSpring by lazy {
    ViewPropertySpringAnimator(accelerationView)
      .defaultStiffness(450f)
      .defaultDampingRatio(0.8f)
  }

  private val panRecognizer by lazy { PanRecognizer(::onPan) }

  private val verticalOffset: Float = 180f

  @SuppressLint("ClickableViewAccessibility")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_acceleration_pausing)

    accelerationView.setOnTouchListener(panRecognizer)
  }

  private var originalTouchPoint: CGPoint = CGPointZero
  private fun onPan(motionData: PanRecognizer.MotionData): Boolean {
    val touchPoint = motionData.location
    val velocity = motionData.velocity

    when (motionData.state) {
      PanRecognizer.State.BEGAN -> {
        velocities.clear()
        originalTouchPoint = touchPoint
      }

      PanRecognizer.State.CHANGED -> {
        val offset: Float = calculateOffset(touchPoint, originalTouchPoint)
        accelerationViewSpring
          .translationY(offset) { stiffness = 700f }
          .start()
        trackPause(velocity = velocity.y, offset = offset)
      }

      PanRecognizer.State.ENDED, PanRecognizer.State.CANCELLED -> {
        accelerationViewSpring
          .translationY(0f) {startVelocity = velocity.y}
          .start()
        pauseLabel.alpha = 0f
        hasPaused = false
      }
      else -> {}
    }

    return true
  }

  private fun calculateOffset(touchPoint: CGPoint, originalTouchPoint: CGPoint): Float {
    val offset = touchPoint.y - originalTouchPoint.y
    if (offset > 0) {
      return offset.pow(0.7f)
    } else if (offset < -verticalOffset * 2) {
      return -verticalOffset * 2 - (-(offset + verticalOffset * 2)).pow(0.7f)
    }
    return offset
  }

  /// The number of past velocities to track.
  private val numberOfVelocities = 7

  /// The array of past velocities.
  private var velocities: MutableList<Float> = ArrayList()

  /// Whether the view is considered paused.
  private var hasPaused = false

  /// Tracks the most recent velocity values, and determines whether the change is great enough to be paused.
  /// After calling this function, the result can be checked in the `hasPaused` property.
  private fun trackPause(velocity: Float, offset: Float) {
    // if the motion is paused, we are done
    if (hasPaused) return

    // update the array of most recent velocities
    if (velocities.size < numberOfVelocities) {
      velocities.add(velocity)
      return
    } else {
      velocities.removeFirst()
      velocities.add(velocity)
    }

    // enforce minimum velocity and offset
//    if (abs(velocity) > 5000 || abs(offset) < 50) return                //mine
    if (abs(velocity) > 100 || abs(offset) < 50) return

    val firstRecordedVelocity = velocities[0]

    // if the majority of the velocity has been lost recently, we consider the motion to be paused
    Log.d("acceleration", "tgblog firstRecordedVelocity is $firstRecordedVelocity")
    Log.d("acceleration", "tgblog velocity is $velocity")
    Log.d(
      "acceleration",
      "tgblog taux is ${abs(firstRecordedVelocity - velocity) / abs(firstRecordedVelocity)}"
    )
//    if (abs(firstRecordedVelocity - velocity) / abs(firstRecordedVelocity) > 0.7) {           //mine
    if (abs(firstRecordedVelocity - velocity) / abs(firstRecordedVelocity) > 0.9) {
      pauseLabel.alpha = 1f
      accelerationView.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
      hasPaused = true
      velocities.clear()
    }
  }
}
