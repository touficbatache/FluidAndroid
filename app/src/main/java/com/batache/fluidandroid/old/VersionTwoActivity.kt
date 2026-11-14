package com.batache.fluidandroid.old

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updateLayoutParams
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import androidx.dynamicanimation.animation.springAnimationOf
import androidx.dynamicanimation.animation.withSpringForceProperties
import com.batache.fluidandroid.R
import com.batache.fluidandroid.dp
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.ShapeAppearanceModel
import jp.wasabeef.blurry.Blurry
import kotlinx.android.synthetic.main.activity_version_two.*
import kotlin.math.max
import kotlin.math.min

class VersionTwoActivity : AppCompatActivity() {

  private val velocityTracker = VelocityTracker.obtain()
  private val velocitiesY: MutableList<String> = ArrayList()

  private var containerWidth = 0
  private var containerHeight = 0

  private var lastWidth = 0
  private var lastHeight = 0

  private var initialIconX = 0f
  private var initialIconY = 0f
  private var initialIconWidth = 0
  private var initialIconHeight = 0

  private lateinit var animAppIconX: SpringAnimation
  private lateinit var animAppIconY: SpringAnimation
  private lateinit var animAppIconWidth: SpringAnimation
  private lateinit var animAppIconHeight: SpringAnimation
  private lateinit var animAppIconAlpha: SpringAnimation

  private var dY = 0f

  private var scale = 0f

  private var isAppCollapsed: Boolean = true
  private var isAppInOverview: Boolean = false

  @SuppressLint("ClickableViewAccessibility")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_version_two)

    container.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
      override fun onLayoutChange(
        p0: View?,
        p1: Int,
        p2: Int,
        p3: Int,
        p4: Int,
        p5: Int,
        p6: Int,
        p7: Int,
        p8: Int
      ) {
        containerWidth = container.measuredWidth
        containerHeight = container.measuredHeight
        container.removeOnLayoutChangeListener(this)
      }
    })

    appIcon.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
      override fun onLayoutChange(
        p0: View?,
        p1: Int,
        p2: Int,
        p3: Int,
        p4: Int,
        p5: Int,
        p6: Int,
        p7: Int,
        p8: Int
      ) {
        initialIconX = appIcon.x
        initialIconY = appIcon.y
        initialIconWidth = appIcon.measuredWidth
        initialIconHeight = appIcon.measuredHeight
        appIcon.removeOnLayoutChangeListener(this)
      }
    })

//    animAppIconX = SpringAnimation(appIcon, SpringAnimation.X).withSpringForceProperties(::openingForce)
//
//    animAppIconY = SpringAnimation(appIcon, SpringAnimation.Y).withSpringForceProperties(::openingForce)

    appPage.visibility = View.VISIBLE

    animAppIconX = springAnimationOf(
      {
        appIcon.x = it
        appPage.x = it
      },
      {
        return@springAnimationOf appPage.x
      }
    ).withSpringForceProperties(::openingForce)

    animAppIconY = springAnimationOf(
      {
        appIcon.y = it
        appPage.y = it
      },
      {
        return@springAnimationOf appPage.y
      }
    ).withSpringForceProperties(::openingForce)

    animAppIconWidth = springAnimationOf(
      {
        appIcon.updateLayoutParams<ViewGroup.LayoutParams> {
          width = it.toInt()
        }
        appPage.updateLayoutParams<ViewGroup.LayoutParams> {
          width = it.toInt()
        }
      },
      {
        return@springAnimationOf appPage.measuredWidth.toFloat()
      }
    ).withSpringForceProperties(::openingForce)

    animAppIconHeight = springAnimationOf(
      {
        appIcon.updateLayoutParams<ViewGroup.LayoutParams> {
          height = it.toInt()
        }
        appPage.updateLayoutParams<ViewGroup.LayoutParams> {
          height = it.toInt()
//          height = (container.measuredHeight * (appIcon.measuredWidth.toFloat() / container.measuredWidth.toFloat())).toInt()
        }
        val shapeAppearanceModel = ShapeAppearanceModel()
          .toBuilder()
          .setAllCorners(CornerFamily.ROUNDED, 16.dp.toFloat())
          .build()
        appIcon.shapeAppearanceModel = shapeAppearanceModel
        appPage.shapeAppearanceModel = shapeAppearanceModel
      },
      {
        return@springAnimationOf appPage.measuredHeight.toFloat()
      }
    ).withSpringForceProperties(::openingForce)

    animAppIconAlpha =
      SpringAnimation(appIcon, SpringAnimation.ALPHA).withSpringForceProperties(::openingForce)

    appPage.visibility = View.GONE

    appIcon.setOnClickListener {
      if (isAppCollapsed) {
        appPage.visibility = View.VISIBLE

        animAppIconX.apply {
          cancel()
          spring.finalPosition = 0f
          start()
        }

        animAppIconY.apply {
          cancel()
          spring.finalPosition = 0f
          start()
        }

        animAppIconWidth.apply {
          cancel()
          spring.finalPosition = container.measuredWidth.toFloat()
          start()
        }

        animAppIconHeight.apply {
          cancel()
          spring.finalPosition = container.measuredHeight.toFloat()
          start()
        }

        animAppIconAlpha.apply {
          cancel()
          spring.finalPosition = 0f
          start()
        }

        SpringAnimation(appPage, SpringAnimation.ALPHA).withSpringForceProperties(::openingForce)
          .apply {
            cancel()
            spring.finalPosition = 1f
            start()
          }

//      if (initialHeight > 0 && initialWidth > 0) {
//        setAppFullScreen(false)
//      }

//      Blurry.with(this)
//        .radius(34)
//        .async()
//        .animate(150)
//        .onto(homeScreen)

        isAppCollapsed = false
      }
    }

    appPage.setOnClickListener {
      setAppFullScreen(true)
    }

    gestureBar.setOnTouchListener { view, event ->
      when (event.action) {
        MotionEvent.ACTION_DOWN -> {
          dY = event.rawY
          lastWidth = appPage.measuredWidth
          lastHeight = appPage.measuredHeight
        }
        MotionEvent.ACTION_MOVE -> {
          velocityTracker.addMovement(event)
          velocityTracker.computeCurrentVelocity(10000)
          velocitiesY.add(velocityTracker.yVelocity.toString())
//          dyTv.text = "velocity = ${velocityTracker.yVelocity}"

          scale = 1 - min((containerHeight / 4f), max(0F, dY - event.rawY)) / (containerHeight / 2)
          dyTv.text = "scale = $scale"
          updateAppInOverview(scale, false)

//          if ((-dY) > (initialHeight / 4f)) {
//            animateAppPageIntoIcon()
//          }
        }
        MotionEvent.ACTION_UP -> {
//          val minVelocity = velocitiesY.map { it.toDouble() }.minOrNull() ?: 0.0
//          dyTv.text = "minVelocity = $minVelocity"
//          Intent(this, ChartActivity::class.java).let {
//            it.putStringArrayListExtra("velocitiesY", velocitiesY as java.util.ArrayList<String>)
//            startActivity(it)
//          }
          if (shouldShowOverview(event.rawY + dY) && !isAppInOverview) {
            scale = 1 - min((containerHeight / 4f), 200F) / (containerHeight / 2)
            updateAppInOverview(scale, true)
          } else {
            animateAppPageIntoIcon()
//            if (-(event.rawY + dY) > 60) {
//              animateAppPageIntoIcon()
//            } else {
//              setAppFullScreen(true)
//            }
          }

          velocitiesY.clear()
        }
        else -> return@setOnTouchListener false
      }
      return@setOnTouchListener true
    }
  }

  private fun animateAppPageIntoIcon() {
//    val velocity = velocityTracker.yVelocity / 10000
    if (!isAppCollapsed) {
      animAppIconX.apply {
        cancel()
        spring.finalPosition = initialIconX
//        setStartVelocity(velocity)
        start()
      }

      animAppIconY.apply {
        cancel()
        spring.finalPosition = initialIconY
//        setStartVelocity(velocity)
        start()
      }

      animAppIconWidth.apply {
        cancel()
        spring.finalPosition = initialIconWidth.toFloat()
//        setStartVelocity(velocity)
        start()
      }

      animAppIconHeight.apply {
        cancel()
        spring.finalPosition = initialIconHeight.toFloat()
//        setStartVelocity(velocity)
        start()
      }

      animAppIconAlpha.apply {
        cancel()
        spring.finalPosition = 1f
//        setStartVelocity(velocity)
        start()
      }

      SpringAnimation(appPage, SpringAnimation.ALPHA).withSpringForceProperties(::openingForce)
        .apply {
          cancel()
          spring.finalPosition = 0f
          start()
        }.addEndListener { animation, canceled, value, velocity ->
          appPage.visibility = View.GONE
          isAppCollapsed = true
        }

      Blurry.delete(homeScreen)

      isAppInOverview = false
    }
  }

  private fun setAppFullScreen(animate: Boolean) {
    if (animate) {
      springAnimationOf(
        { appPage.x = it },
        { return@springAnimationOf appPage.x },
        0f
      ).apply(::startFullScreenAnimation)

      springAnimationOf(
        { appPage.y = it },
        { return@springAnimationOf appPage.y },
        0f
      ).apply(::startFullScreenAnimation)

      springAnimationOf(
        {
          appPage.updateLayoutParams<ViewGroup.LayoutParams> {
            width = it.toInt()
          }
        },
        { return@springAnimationOf appPage.measuredWidth.toFloat() },
        containerWidth.toFloat()
      ).apply(::startFullScreenAnimation)

      springAnimationOf(
        {
          appPage.updateLayoutParams<ViewGroup.LayoutParams> {
            height = it.toInt()
          }
        },
        { return@springAnimationOf appPage.measuredHeight.toFloat() },
        containerHeight.toFloat()
      ).apply(::startFullScreenAnimation)
    } else {
      appPage.x = 0f
      appPage.y = 0f
      appPage.updateLayoutParams<ViewGroup.LayoutParams> {
        height = containerHeight
        width = containerWidth
      }
    }

    isAppInOverview = false
  }

  private fun updateAppInOverview(scale: Float, animate: Boolean) {
    if (animate) {
      springAnimationOf(
        { appPage.x = it },
        { return@springAnimationOf appPage.x },
        (containerWidth - (containerWidth * scale)) / 2
      ).apply(::startAnimation)

      springAnimationOf(
        { appPage.y = it },
        { return@springAnimationOf appPage.y },
        (containerHeight - (containerHeight * scale)) / 2
      ).apply(::startAnimation)

      springAnimationOf(
        {
          appPage.updateLayoutParams<ViewGroup.LayoutParams> {
            height = it.toInt()
          }
        },
        { return@springAnimationOf appPage.measuredHeight.toFloat() },
        containerHeight * scale
      ).apply(::startAnimation)

      springAnimationOf(
        {
          appPage.updateLayoutParams<ViewGroup.LayoutParams> {
            width = it.toInt()
          }
        },
        { return@springAnimationOf appPage.measuredWidth.toFloat() },
        containerWidth * scale
      ).apply(::startAnimation)

      isAppInOverview = true
    } else {
      appPage.x = (containerWidth - (lastWidth * scale)) / 2
      appPage.y = (containerHeight - (lastHeight * scale)) / 2

      appPage.updateLayoutParams<ViewGroup.LayoutParams> {
        width = (lastWidth * scale).toInt()
        height = (lastHeight * scale).toInt()
      }
    }

    dyTv.text = "x = ${(containerWidth - (lastWidth * scale)) / 2}"
  }

  private fun startAnimation(springAnimation: SpringAnimation) {
    springAnimation.spring.stiffness = SpringForce.STIFFNESS_LOW
    springAnimation.spring.dampingRatio = SpringForce.DAMPING_RATIO_LOW_BOUNCY
    springAnimation.start()
  }

  private fun startFullScreenAnimation(springAnimation: SpringAnimation) {
    springAnimation.spring.stiffness = 820f
    springAnimation.spring.dampingRatio = SpringForce.DAMPING_RATIO_NO_BOUNCY
    springAnimation.start()
  }

  private fun shouldShowOverview(dY: Float): Boolean {
    val minVelocity = velocitiesY.map { it.toDouble() }.minOrNull() ?: 0.0

    val maxVelocitySinceMin = velocitiesY.subList(
      velocitiesY.indexOf("$minVelocity") + 1,
      velocitiesY.size
    ).map { it.toDouble() }.maxOrNull()

    return -dY < (containerHeight / 4f) && minVelocity > -50000 && maxVelocitySinceMin != null
  }

  private fun openingForce(force: SpringForce) {
    force.apply {
      stiffness = SpringForce.STIFFNESS_LOW
      dampingRatio = SpringForce.DAMPING_RATIO_NO_BOUNCY
    }
  }
}