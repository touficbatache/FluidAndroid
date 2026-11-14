package com.batache.fluidandroid.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.FlingAnimation
import androidx.dynamicanimation.animation.SpringForce
import com.batache.fluidandroid.R
import com.batache.fluidandroid.util.CGPoint
import com.batache.fluidandroid.util.CGPointZero
import com.batache.fluidandroid.util.PanRecognizer
import com.github.lcdsmao.springx.ViewPropertySpringAnimator
import kotlinx.android.synthetic.main.activity_jumping.*

class JumpingActivity : AppCompatActivity() {

  /**
   * Animation variables
   */
  private val stiff = SpringForce.STIFFNESS_MEDIUM
  private val damp = SpringForce.DAMPING_RATIO_NO_BOUNCY
  private val jumpingViewSpring by lazy {
    ViewPropertySpringAnimator(jumpingView)
      .defaultStiffness(stiff)
      .defaultDampingRatio(damp)
  }
  val jumpingViewFling by lazy { FlingAnimation(jumpingView, DynamicAnimation.TRANSLATION_Y) }

  /**
   * Gesture detection variables
   */
  private val panRecognizer by lazy { PanRecognizer(::onPan) }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_jumping)

    jumpingView.setOnTouchListener(panRecognizer)
  }

  private val axes: MutableList<Pair<String, String>> = ArrayList()
  private var originalTouchPoint: CGPoint = CGPointZero
  private fun onPan(motionData: PanRecognizer.MotionData): Boolean {
    val touchPoint = motionData.location
    val velocity = motionData.velocity

    when (motionData.state) {
      PanRecognizer.State.BEGAN -> {
//        velocities.clear()
        originalTouchPoint = touchPoint
      }

      PanRecognizer.State.CHANGED -> {
////        val offset: Float = calculateOffset(touchPoint, originalTouchPoint)
//        val offset: Float = touchPoint.y - originalTouchPoint.y
//        jumpingViewSpring
//          .translationY(offset) //{ stiffness = 700f }
//          .start()

        jumpingViewSpring
          .translationY(touchPoint.y - originalTouchPoint.y) {
            stiffness = SpringForce.STIFFNESS_HIGH
          }
          .start()

//        jumpingView.translationY = touchPoint.y - originalTouchPoint.y
      }

//      PanRecognizer.State.FLING -> {
//        val decelerationRate = 0.99f
//
//        jumpingViewSpring
//          .translationY(
//            jumpingView.translationY + project(
//              initialVelocity = velocity.y,
//              decelerationRate = decelerationRate
//            )
//          ) {
//            onUpdate { spring, value, updateVelocity ->
//              velocityText.text = "$updateVelocity"
//              if (updateVelocity < -35f) {
//                jumpingViewSpring
//                  .translationY(0f) { startVelocity = velocity.y }
//                  .start()
//              }
//            }
//          }
//          .start()
//      }

      PanRecognizer.State.FLING -> {
//        jumpingViewFling
//          .setStartVelocity(velocity.y)
//          .setFriction(2f)
//          .addUpdateListener { animation, value, velocity ->
//            val time =
//              if (axes.isEmpty())
//                SystemClock.uptimeMillis()
//              else
//                SystemClock.uptimeMillis().toInt().minus(axes[0].first.toInt())
//
//            axes.add(Pair(time.toString(), velocity.toString()))
//          }
//          .addEndListener { animation, canceled, value, velocity ->
//            axes[0] = Pair("0", axes[0].second)
//            for(xy in axes) {
//              Log.d("tgblog", "x: ${xy.first} and y: ${xy.second}")
//            }
//            Intent(this, BetterChartActivity::class.java).let {
//              it.putStringArrayListExtra(
//                "axisX",
//                axes.map { xyValues -> xyValues.first }.toList() as java.util.ArrayList<String>
//              )
//              it.putStringArrayListExtra(
//                "axisY",
//                axes.map { xyValues -> xyValues.second }.toList() as java.util.ArrayList<String>
//              )
//              startActivity(it)
//            }
//            axes.clear()
//          }
//          .start()

        jumpingViewSpring
          .translationY(0f) {
            startVelocity = velocity.y
            stiffness = 20f
          }
          .start()

//        val anim = SpringAnimation(jumpingView, SpringAnimation.TRANSLATION_Y).setSpring(
//          SpringForce()
//            .setFinalPosition(0f)
//            .setDampingRatio(SpringForce.DAMPING_RATIO_NO_BOUNCY)
//            .setStiffness(SpringForce.STIFFNESS_MEDIUM)
//        )
//        anim.setStartVelocity(velocity.y).start()
      }

//      PanRecognizer.State.ENDED, PanRecognizer.State.CANCELLED -> {
//        jumpingViewSpring
//          .translationY(0f) { startVelocity = velocity.y * 2f }
//          .start()
//      }
      else -> {}
    }

    return true
  }

  private fun project(initialVelocity: Float, decelerationRate: Float): Float {
    return (initialVelocity / 1000) * decelerationRate / (1 - decelerationRate)
  }
}