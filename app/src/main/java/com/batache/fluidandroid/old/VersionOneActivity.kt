package com.batache.fluidandroid.old

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updateLayoutParams
import androidx.dynamicanimation.animation.*
import androidx.transition.TransitionManager
import com.batache.fluidandroid.R
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.ShapeAppearanceModel
import com.google.android.material.transition.MaterialContainerTransform
import jp.wasabeef.blurry.Blurry
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_version_one.*
import kotlin.math.max
import kotlin.math.min

class VersionOneActivity : AppCompatActivity() {

  private val velocityTracker = VelocityTracker.obtain()
  private val velocitiesY: MutableList<String> = ArrayList()

  private val durationMultiplier = 1

  private var initialWidth = 0
  private var initialHeight = 0

  private var lastWidth = 0
  private var lastHeight = 0

  private var dY = 0f

  private var scale = 0f

  private var isAppInOverview: Boolean = false

  @SuppressLint("ClickableViewAccessibility")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_version_one)

    appPage.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
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
        initialWidth = container.measuredWidth
        initialHeight = container.measuredHeight
        appPage.removeOnLayoutChangeListener(this)
      }
    })

    appIcon.setOnClickListener {
      if (initialHeight > 0 && initialWidth > 0) {
        setAppFullScreen(false)
      }

      val transform = MaterialContainerTransform().apply {
        startView = appIcon
        endView = appPage

        addTarget(endView as ShapeableImageView)

//        setPathMotion(MaterialArcMotion())

        scrimColor = Color.TRANSPARENT

        duration = 400L * durationMultiplier
      }

      TransitionManager.beginDelayedTransition(container, transform)
      appIcon.visibility = View.GONE
      appPage.visibility = View.VISIBLE


      Blurry.with(this)
        .radius(34)
        .async()
        .animate(150 * durationMultiplier)
        .onto(homeScreen)
    }

//    gestureBar.setOnClickListener {
//      val transform = MaterialContainerTransform().apply {
//        startView = appPage
//        endView = appIcon
//
//        addTarget(endView as ShapeableImageView)
//
////        setPathMotion(MaterialArcMotion())
//
//        scrimColor = Color.TRANSPARENT
//
//        duration = 400L * durationMultiplier
//      }
//
//      TransitionManager.beginDelayedTransition(container, transform)
//      appPage.visibility = View.GONE
//      appIcon.visibility = View.VISIBLE
//
//      Blurry.delete(homeScreen)
//    }

    appPage.setOnClickListener {
      setAppFullScreen(true)
    }

    val flingAnimationY: FlingAnimation by lazy(LazyThreadSafetyMode.NONE) {
      FlingAnimation(appPage, DynamicAnimation.Y).setFriction(1.1f)
    }

    val gestureListener = object : GestureDetector.SimpleOnGestureListener() {

      override fun onDown(e: MotionEvent?): Boolean {
        return true
      }

      override fun onFling(
        e1: MotionEvent,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
      ): Boolean {
        flingAnimationY.setStartVelocity(velocityY)
        flingAnimationY.start()

        scale =
          1 - min((initialHeight / 4f), max(0F, -(e1.rawY + dY))) / (initialHeight / 2)

        appPage.x = (initialWidth - (initialWidth * scale)) / 2
//          appPage.y = (initialHeight - (initialHeight * scale)) / 2

        appPage.updateLayoutParams<ViewGroup.LayoutParams> {
          height = (initialHeight * scale).toInt()
          width = (initialWidth * scale).toInt()
        }

//        dyTv.text = "dY = ${abs(event.rawY + dY)}"

        return true
      }
    }

    val gestureDetector = GestureDetector(this, gestureListener)

    gestureBar.setOnTouchListener { view, event ->
//      mDragHelper.processTouchEvent(event)

//      gestureDetector.onTouchEvent(event)

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

          scale = 1 - min((initialHeight / 4f), max(0F, dY - event.rawY)) / (initialHeight / 2)
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
            scale = 1 - min((initialHeight / 4f), 200F) / (initialHeight / 2)
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
    val transform = MaterialContainerTransform().apply {
      startView = appPage
      endView = appIcon

      addTarget(endView as ShapeableImageView)

//        setPathMotion(MaterialArcMotion())

      ShapeAppearanceModel()

      scrimColor = Color.TRANSPARENT

      duration = 400L * durationMultiplier
    }

    TransitionManager.beginDelayedTransition(container, transform)
    appPage.visibility = View.GONE
    appIcon.visibility = View.VISIBLE

    Blurry.delete(homeScreen)

    isAppInOverview = false
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
        initialWidth.toFloat()
      ).apply(::startFullScreenAnimation)

      springAnimationOf(
        {
          appPage.updateLayoutParams<ViewGroup.LayoutParams> {
            height = it.toInt()
          }
        },
        { return@springAnimationOf appPage.measuredHeight.toFloat() },
        initialHeight.toFloat()
      ).apply(::startFullScreenAnimation)
    } else {
      appPage.x = 0f
      appPage.y = 0f
      appPage.updateLayoutParams<ViewGroup.LayoutParams> {
        height = initialHeight
        width = initialWidth
      }
    }

    isAppInOverview = false
  }

  private fun updateAppInOverview(scale: Float, animate: Boolean) {
    if (animate) {
      springAnimationOf(
        { appPage.x = it },
        { return@springAnimationOf appPage.x },
        (initialWidth - (initialWidth * scale)) / 2
      ).apply(::startAnimation)

      springAnimationOf(
        { appPage.y = it },
        { return@springAnimationOf appPage.y },
        (initialHeight - (initialHeight * scale)) / 2
      ).apply(::startAnimation)

      springAnimationOf(
        {
          appPage.updateLayoutParams<ViewGroup.LayoutParams> {
            height = it.toInt()
          }
        },
        { return@springAnimationOf appPage.measuredHeight.toFloat() },
        initialHeight * scale
      ).apply(::startAnimation)

      springAnimationOf(
        {
          appPage.updateLayoutParams<ViewGroup.LayoutParams> {
            width = it.toInt()
          }
        },
        { return@springAnimationOf appPage.measuredWidth.toFloat() },
        initialWidth * scale
      ).apply(::startAnimation)

      isAppInOverview = true
    } else {
      appPage.x = (initialWidth - (lastWidth * scale)) / 2
      appPage.y = (initialHeight - (lastHeight * scale)) / 2

      appPage.updateLayoutParams<ViewGroup.LayoutParams> {
        width = (lastWidth * scale).toInt()
        height = (lastHeight * scale).toInt()
      }
    }
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

    return -dY < (initialHeight / 4f) && minVelocity > -50000 && maxVelocitySinceMin != null
  }
}