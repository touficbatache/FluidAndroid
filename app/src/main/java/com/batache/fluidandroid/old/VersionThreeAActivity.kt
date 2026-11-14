package com.batache.fluidandroid.old

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.doOnLayout
import androidx.core.view.updateLayoutParams
import androidx.dynamicanimation.animation.*
import com.batache.fluidandroid.R
import com.batache.fluidandroid.cancelAndAnimateTo
import com.batache.fluidandroid.dp
import com.batache.fluidandroid.f
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.ShapeAppearanceModel
import jp.wasabeef.blurry.Blurry
import kotlinx.android.synthetic.main.activity_version_three_a.*
import kotlin.math.max
import kotlin.math.min

class VersionThreeAActivity : AppCompatActivity(), View.OnTouchListener {

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

  private lateinit var gestureDetector: GestureDetectorCompat

  private var dY = 0f
  private var onTouchDownAppWidth = 0
  private var onTouchDownAppHeight = 0

  private val velocityTracker = VelocityTracker.obtain()
  private val velocitiesY: MutableList<String> = ArrayList()

  private var isAppCollapsed: Boolean = true
  private var isAppInOverview: Boolean = false

  @SuppressLint("ClickableViewAccessibility")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_version_three_a)

    measureViews {
      setupAnimations()
      appContainer.setOnClickListener { animateAppLaunchOrExpand() }
      gestureBar.setOnTouchListener(this)
    }

    gestureDetector =
      GestureDetectorCompat(this, object : GestureDetector.SimpleOnGestureListener() {
        override fun onFling(
          e1: MotionEvent,
          e2: MotionEvent?,
          velocityX: Float,
          velocityY: Float
        ): Boolean {
          if (isAppInOverview || !shouldShowOverview(e1.rawY + dY)) {
            animateAppRetract(velocityX, velocityY)
          }
          return true
        }
      })
  }

  private fun measureViews(callback: () -> Unit) {
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

        callback.invoke()
      }
    }
  }

  private fun setupAnimations() {
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
//        .async()
//        .animate(150)
        .onto(homeScreen)
    }

    isAppCollapsed = false
    isAppInOverview = false
  }

  override fun onTouch(view: View?, event: MotionEvent?): Boolean {
    if (isAppCollapsed) {
      return false
    }

//    velocityTracker2.addMovement(event)

    gestureDetector.onTouchEvent(event)

    when (event?.action) {
      MotionEvent.ACTION_DOWN -> {
        dY = event.rawY
        onTouchDownAppWidth = appContainer.measuredWidth
        onTouchDownAppHeight = appContainer.measuredHeight
      }

      MotionEvent.ACTION_MOVE -> {
        velocityTracker.addMovement(event)
        velocityTracker.computeCurrentVelocity(10000)
        velocitiesY.add(velocityTracker.yVelocity.toString())

        val scale =
          1 - min((containerHeight / 4f), max(0F, dY - event.rawY)) / (containerHeight / 2)
//        updateAppInOverview(scale, false)
      }

      MotionEvent.ACTION_UP -> {
//        var hasFlinged = false
//
//        // A fling must travel the minimum tap distance
//        val pointerId = event.getPointerId(0)
//        velocityTracker2.computeCurrentVelocity(1000, ViewConfiguration.get(this).scaledMaximumFlingVelocity.f);
//        val velocityY = velocityTracker2.getYVelocity(pointerId);
//        if ((abs(velocityY) > ViewConfiguration.get(this).scaledMinimumFlingVelocity)) {
//          hasFlinged = true
//        }
//        firstTv.text = "flinged? = $hasFlinged"
//        secondTv.text = "velocityY = $velocityY"

        if (!isAppInOverview && shouldShowOverview(event.rawY + dY)) {
          val scale = 1 - min((containerHeight / 4f), 200F) / (containerHeight / 2)
          updateAppInOverview(scale, true)
        }
//        else {
//          animateAppRetract()
//        }

        velocitiesY.clear()
      }

      else -> return false
    }
    return true
  }

  private fun shouldShowOverview(dY: Float): Boolean {
    val minVelocity = velocitiesY.map { it.toDouble() }.minOrNull() ?: 0.0

    val maxVelocitySinceMin = velocitiesY.subList(
      velocitiesY.indexOf("$minVelocity") + 1,
      velocitiesY.size
    ).map { it.toDouble() }.maxOrNull()

    return -dY < (containerHeight / 4f) && minVelocity > -50000 && maxVelocitySinceMin != null
  }

  private fun updateAppInOverview(scale: Float, animate: Boolean) {
    if (animate) {
      animAppX.applyOverviewForce()
        .cancelAndAnimateTo((containerWidth - (containerWidth * scale)) / 2)
      animAppY.applyOverviewForce()
        .cancelAndAnimateTo((containerHeight - (containerHeight * scale)) / 2)
      animAppWidth.applyOverviewForce().cancelAndAnimateTo(containerWidth * scale)
      animAppHeight.applyOverviewForce().cancelAndAnimateTo(containerHeight * scale)

      isAppInOverview = true
    } else {
      appContainer.updateLayoutParams<ViewGroup.LayoutParams> {
        width = (onTouchDownAppWidth * scale).toInt()
        height = (onTouchDownAppHeight * scale).toInt()
      }

      appContainer.x = (containerWidth - (onTouchDownAppWidth * scale)) / 2
      appContainer.y = (containerHeight - (onTouchDownAppHeight * scale)) / 2
    }
  }

  private fun animateAppRetract(velocityX: Float, velocityY: Float) {
//    val velocity = velocityTracker.yVelocity / 10000
    if (!isAppCollapsed) {
      animAppX.applyRetractForce().cancelAndAnimateTo(initialAppX)
      animAppY.applyRetractForce().cancelAndAnimateTo(initialAppY)
      animAppWidth.applyRetractForce().cancelAndAnimateTo(initialAppWidth.f)
      animAppHeight.applyRetractForce().cancelAndAnimateTo(initialAppHeight.f)

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