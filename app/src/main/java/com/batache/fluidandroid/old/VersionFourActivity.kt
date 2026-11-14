package com.batache.fluidandroid.old

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnLayout
import androidx.core.view.updateLayoutParams
import androidx.dynamicanimation.animation.*
import com.batache.fluidandroid.R
import com.batache.fluidandroid.cancelAndAnimateTo
import com.batache.fluidandroid.dp
import com.batache.fluidandroid.f
import com.batache.fluidandroid.util.CGVector
import com.batache.fluidandroid.util.PanRecognizer
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.ShapeAppearanceModel
import jp.wasabeef.blurry.Blurry
import kotlinx.android.synthetic.main.activity_version_four.*
import kotlin.math.max
import kotlin.math.min

class VersionFourActivity : AppCompatActivity(), (PanRecognizer.MotionData) -> Boolean {

  private var containerWidth = 0
  private var containerHeight = 0

  private var initialAppX = 0f
  private var initialAppY = 0f
  private var initialAppWidth = 0
  private var initialAppHeight = 0

  private lateinit var animAppX: SpringAnimation
  private lateinit var animAppY: SpringAnimation
  private lateinit var animAppWidth: SpringAnimation
  private lateinit var animAppHeight: SpringAnimation

  private lateinit var animAppIconAlpha: SpringAnimation
  private lateinit var animAppPageAlpha: SpringAnimation

  private var dY = 0f
  private var onTouchDownAppWidth = 0
  private var onTouchDownAppHeight = 0

  private val panRecognizer by lazy { PanRecognizer(this) }

  private val velocitiesY: MutableList<String> = ArrayList()

  private var isAppCollapsed: Boolean = true
  private var isAppInOverview: Boolean = false

  @SuppressLint("ClickableViewAccessibility")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_version_four)

    measureViews()
    buildAnimations()

    appContainer.setOnClickListener { animateAppLaunchOrExpand() }

    gestureBar.setOnTouchListener(panRecognizer)
  }

  private fun measureViews() {
    container.doOnLayout {
      containerWidth = container.measuredWidth
      containerHeight = container.measuredHeight

      appContainer.doOnLayout {
        initialAppWidth = appContainer.measuredWidth
        initialAppHeight = appContainer.measuredHeight

        initialAppX = 68.dp.f
        appContainer.x = initialAppX

        initialAppY = (containerHeight - (79.25).dp - initialAppHeight).f
        appContainer.y = initialAppY
      }
    }
  }

  private fun buildAnimations() {
    animAppX = SpringAnimation(appContainer, SpringAnimation.X)
    animAppY = SpringAnimation(appContainer, SpringAnimation.Y)
    animAppWidth = springAnimationOf(
      { appContainer.updateLayoutParams<ViewGroup.LayoutParams> { width = it.toInt() } },
      { appContainer.measuredWidth.toFloat() }
    )
    animAppHeight = springAnimationOf(
      {
        appContainer.updateLayoutParams<ViewGroup.LayoutParams> { height = it.toInt() }
        val shapeAppearanceModel = ShapeAppearanceModel()
          .toBuilder()
          .setAllCorners(CornerFamily.ROUNDED, 16.dp.toFloat())
          .build()
        appContainer.shapeAppearanceModel = shapeAppearanceModel
      },
      { appContainer.measuredHeight.toFloat() }
    )

    animAppIconAlpha = SpringAnimation(appIcon, SpringAnimation.ALPHA)
    animAppPageAlpha = SpringAnimation(appPage, SpringAnimation.ALPHA)
  }

  private fun animateAppLaunchOrExpand() {
    if (!isAppCollapsed && !isAppInOverview) {
      return
    }

    appPage.visibility = View.VISIBLE

    animAppX.applyLaunchOrExpandForce().cancelAndAnimateTo(0f)
    animAppY.applyLaunchOrExpandForce().cancelAndAnimateTo(0f)
    animAppWidth.applyLaunchOrExpandForce().cancelAndAnimateTo(containerWidth.f)
    animAppHeight.applyLaunchOrExpandForce().cancelAndAnimateTo(containerHeight.f)

    if (!isAppInOverview) {
      animAppIconAlpha.applyLaunchForce().cancelAndAnimateTo(0f)
      animAppPageAlpha.applyLaunchForce().cancelAndAnimateTo(1f)
    }

    if (isAppCollapsed) {
      Blurry.with(this)
        .radius(34)
        .onto(homeScreen)
    }

    isAppCollapsed = false
    isAppInOverview = false
  }

  override fun invoke(motionData: PanRecognizer.MotionData): Boolean {
    if (isAppCollapsed) {
      return false
    }

    cancelAllAnimations()

    val touchPoint = motionData.location

    when (motionData.state) {
      PanRecognizer.State.BEGAN -> {
        velocitiesY.clear()

        dY = touchPoint.y
        onTouchDownAppWidth = appContainer.measuredWidth
        onTouchDownAppHeight = appContainer.measuredHeight
      }

      PanRecognizer.State.CHANGED -> {
        velocitiesY.add(motionData.velocity.y.toString())
        val scale =
          1 - min((containerHeight / 4f), max(0F, dY - touchPoint.y)) / (containerHeight / 2)
        updateAppInOverview(scale, false)
      }

      PanRecognizer.State.ENDED, PanRecognizer.State.CANCELLED -> {
        if (!isAppInOverview && shouldShowOverview(touchPoint.y + dY)) {
          val scale = 1 - min((containerHeight / 4f), 200F) / (containerHeight / 2)
          updateAppInOverview(scale, true)
        } else {
          val velocity = motionData.velocity
          val relativeInitialVelocity = CGVector(
            dx = relativeVelocity(
              velocity = velocity.x,
              fromValue = appPage.x,
              toValue = appIcon.x
            ),
            dy = relativeVelocity(
              velocity = velocity.y,
              fromValue = appPage.y,
              toValue = appIcon.y
            )
          )
          animateAppRetract(relativeInitialVelocity)
        }
      }
      else -> {}
    }

    return true
  }

  private fun cancelAllAnimations() {
    animAppX.cancel()
    animAppY.cancel()
    animAppWidth.cancel()
    animAppHeight.cancel()
  }

  private fun shouldShowOverview(dY: Float): Boolean {
    val minVelocity = velocitiesY.map { it.toDouble() }.minOrNull() ?: 0.0

    val maxVelocitySinceMin = velocitiesY.subList(
      velocitiesY.indexOf("$minVelocity") + 1,
      velocitiesY.size
    ).map { it.toDouble() }.maxOrNull()

    return -dY < (containerHeight / 4f) && minVelocity > -5000 && maxVelocitySinceMin != null
  }

  private fun updateAppInOverview(scale: Float, animate: Boolean) {
    if (animate) {
      animAppX
        .applyOverviewForce()
        .cancelAndAnimateTo((containerWidth - (containerWidth * scale)) / 2)
      animAppY
        .applyOverviewForce()
        .cancelAndAnimateTo((containerHeight - (containerHeight * scale)) / 2)
      animAppWidth
        .applyOverviewForce()
        .cancelAndAnimateTo(containerWidth * scale)
      animAppHeight
        .applyOverviewForce()
        .cancelAndAnimateTo(containerHeight * scale)

      isAppInOverview = true
    } else {
      val useCaseWidth = if (!isAppInOverview) containerWidth else onTouchDownAppWidth
      val useCaseHeight = if (!isAppInOverview) containerHeight else onTouchDownAppHeight

      appContainer.updateLayoutParams<ViewGroup.LayoutParams> {
        width = (useCaseWidth * scale).toInt()
        height = (useCaseHeight * scale).toInt()
      }

      appContainer.x = (containerWidth - (useCaseWidth * scale)) / 2
      appContainer.y = (containerHeight - (useCaseHeight * scale)) / 2
    }
  }

  /// Calculates the relative velocity needed for the initial velocity of the animation.
  private fun relativeVelocity(velocity: Float, fromValue: Float, toValue: Float): Float {
    if ((fromValue - toValue).equals(0f)) {
      return 0f
    }
    return velocity / (toValue - fromValue)
  }

  private fun animateAppRetract(velocity: CGVector) {
//    val velocity = velocityTracker.yVelocity / 10000
    if (!isAppCollapsed) {
      animAppX
        .setStartVelocity(velocity.dx)
        .applyRetractForce()
        .cancelAndAnimateTo(initialAppX)
      animAppY
        .setStartVelocity(velocity.dy)
        .applyRetractForce()
        .cancelAndAnimateTo(initialAppY)
      animAppWidth
        .setStartVelocity(velocity.dx)
        .applyRetractForce()
        .cancelAndAnimateTo(initialAppWidth.f)
      animAppHeight
        .setStartVelocity(velocity.dy)
        .applyRetractForce()
        .cancelAndAnimateTo(initialAppHeight.f)

      animAppIconAlpha.applyRetractForce().cancelAndAnimateTo(1f)
      animAppPageAlpha.applyRetractForce().cancelAndAnimateTo(0f).apply {
        addEndListener(object : DynamicAnimation.OnAnimationEndListener {
          override fun onAnimationEnd(
            animation: DynamicAnimation<out DynamicAnimation<*>>?,
            canceled: Boolean,
            value: Float,
            velocity: Float
          ) {
            if (!canceled) {
              appPage.visibility = View.GONE
            }
            removeEndListener(this)
          }
        })
      }

      Blurry.delete(homeScreen)

      isAppInOverview = false
      isAppCollapsed = true
    }
  }

  private fun SpringAnimation.applyLaunchForce(): SpringAnimation {
    withSpringForceProperties {
      stiffness = 400f
      dampingRatio = SpringForce.DAMPING_RATIO_NO_BOUNCY
    }
    return this
  }

  private fun SpringAnimation.applyLaunchOrExpandForce(): SpringAnimation {
    withSpringForceProperties {
      stiffness = if (!isAppInOverview) 400f else 820f
      dampingRatio = SpringForce.DAMPING_RATIO_NO_BOUNCY
    }
    return this
  }

  private fun SpringAnimation.applyOverviewForce(): SpringAnimation {
    withSpringForceProperties {
      stiffness = SpringForce.STIFFNESS_LOW
      dampingRatio = SpringForce.DAMPING_RATIO_LOW_BOUNCY
    }
    return this
  }

  private fun SpringAnimation.applyRetractForce(): SpringAnimation {
    withSpringForceProperties {
      stiffness = 400f
      dampingRatio = 0.95f
    }
    return this
  }
}