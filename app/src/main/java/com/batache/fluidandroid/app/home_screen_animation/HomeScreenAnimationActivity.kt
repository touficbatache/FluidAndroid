package com.batache.fluidandroid.app.home_screen_animation

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.doOnLayout
import androidx.dynamicanimation.animation.SpringForce
import com.batache.fluidandroid.R
import com.batache.fluidandroid.util.CGPoint
import com.batache.fluidandroid.util.CGPointZero
import com.batache.fluidandroid.util.PanRecognizer
import io.alterac.blurkit.BlurKit
import kotlinx.android.synthetic.main.activity_home_screen_animation.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class HomeScreenAnimationActivity : AppCompatActivity() {

  /**
   * Animation variables
   */

  lateinit var homeScreenAnimation: HomeScreenAnimation

  /**
   * Gesture detection variables
   */

  private val panRecognizer by lazy { PanRecognizer(::onPan) }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_home_screen_animation)

    setupAnimation()
    setupGestureDetection()
  }

  private fun setupAnimation() {
    appFakeBackground.doOnLayout {
      overlayBlur.setImageBitmap(BlurKit.getInstance().fastBlur(appFakeBackground, 7, 0.1f))
    }
    homeScreenAnimation = HomeScreenAnimation(
      iconView = icon,
      appView = app,
      containerView = homeScreen,
      debugTextView = yValue,
      animationStiffness = 200f,
      animationDampingRatio = SpringForce.DAMPING_RATIO_NO_BOUNCY
    )
  }

  private fun setupGestureDetection() {
    icon.setOnClickListener {
      blurBackground(true)
      homeScreenAnimation.animateForward()
    }
    gestureBar.setOnTouchListener(panRecognizer)
  }

  private fun blurBackground(blur: Boolean, duration: Long = 150) {
    val alpha = if (blur) 1f else 0f
    overlay.animate().alpha(alpha).setDuration(duration).start()
  }

  var mainVelocity: Float? = null
  var done = false

  private var originalTouchPoint: CGPoint = CGPointZero
  private var lastOffset = Pair(0f, 0f)
  private fun onPan(motionData: PanRecognizer.MotionData): Boolean {
    val touchPoint = motionData.location
    val velocity = motionData.velocity

    when (motionData.state) {
      PanRecognizer.State.BEGAN -> {
        hasPaused = false
        velocities.clear()
        originalTouchPoint = touchPoint
      }

      PanRecognizer.State.CHANGED -> {
        val offsetX = touchPoint.x - originalTouchPoint.x
        val offsetY = touchPoint.y - originalTouchPoint.y
        lastOffset = Pair(offsetX, offsetY)

        trackPause(velocity = velocity.y, offset = offsetY)

        if (homeScreenAnimation.currentAnimationState == HomeScreenAnimation.AnimationState.EXPANDED || homeScreenAnimation.currentAnimationState == HomeScreenAnimation.AnimationState.SWITCHER_RESTING) {
          homeScreenAnimation.animateDrag(offsetX = offsetX, offsetY = offsetY)
        } else if (homeScreenAnimation.currentAnimationState == HomeScreenAnimation.AnimationState.SWITCHER_PEEKING) {
          homeScreenAnimation.animatePeeking(offsetX = offsetX, offsetY = offsetY)
        }
      }

      PanRecognizer.State.ENDED, PanRecognizer.State.CANCELLED -> {
        if (!hasPaused || homeScreenAnimation.skipOverview) {
//          homeScreenAnimation.animateFling(
//            offsetX = lastOffset.first,
//            offsetY = max(
//              min(lastOffset.second * abs(velocity.y / 10_000f), lastOffset.second),
//              -1280f
//            ),
//            listener = { velocityy ->
//              if (done) {
                homeScreenAnimation.animateBackward(velocity.y)
                Log.d("tgblog velY: ", "${velocity.y}")
                blurBackground(false, 400)
                homeScreenAnimation.skipOverview = false
//
//                done = false
//                mainVelocity = null
//              } else {
//                if (mainVelocity == null || mainVelocity == 0f) {
//                  mainVelocity = velocityy
//                } else {
//                  mainVelocity?.let {
//                    if (velocityy <= it * 0.05) {
//                      done = true
//                    }
//                  }
//                }
//              }
//            }
//          )

          lastOffset = Pair(0f, 0f)

//          runIn(50) {
//            homeScreenAnimation.animateBackward()
//            blurBackground(false, 400)
//            homeScreenAnimation.skipOverview = false
//          }
        } else {
          homeScreenAnimation.animateSwitcherResting()
        }
      }
      else -> {}
    }

    return true
  }

  private var velocities: MutableList<Float> = ArrayList()

  private var hasPaused = false
    set(value) {
      field = value
      if (value) {
        blurBackground(value, 150)
        if (homeScreenAnimation.currentAnimationState == HomeScreenAnimation.AnimationState.RETRACTED) {
//          homeScreenAnimation.cancel()
          homeScreenAnimation.prepareSwitcherPeeking()
        }
      }
    }

  private fun trackPause(velocity: Float, offset: Float) {
    if (hasPaused) return

    if (velocities.size < 7) {
      velocities.add(velocity)
      return
    } else {
      velocities.removeFirst()
      velocities.add(velocity)
    }

    // enforce maximum velocity and minimum offset
    if (abs(velocity) > 5000 || abs(offset) < 50) return

    val firstRecordedVelocity = velocities[0]

    // if the majority of the velocity has been lost recently, we consider the motion to be paused
    if (abs(firstRecordedVelocity - velocity) / abs(firstRecordedVelocity) > 0.5) {
      hasPaused = true
      velocities.clear()
    }
  }

  override fun onResume() {
    super.onResume()

    WindowCompat.setDecorFitsSystemWindows(window, false)
  }
}
