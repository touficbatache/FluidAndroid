package com.batache.fluidandroid.old

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updateLayoutParams
import androidx.dynamicanimation.animation.*
import com.batache.fluidandroid.R
import com.batache.fluidandroid.dp
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.ShapeAppearanceModel
import jp.wasabeef.blurry.Blurry
import kotlinx.android.synthetic.main.activity_old_main.*
import kotlin.math.max
import kotlin.math.min

class OldMainActivity : AppCompatActivity() {

  private val velocityTracker = VelocityTracker.obtain()
  private val velocitiesY: MutableList<String> = ArrayList()

  private val durationMultiplier = 1

  private var initialWidth = 0
  private var initialHeight = 0

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
    setContentView(R.layout.activity_old_main)

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
//
//      val transform = MaterialContainerTransform().apply {
//        startView = appIcon
//        endView = appPage
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
//      appIcon.visibility = View.GONE
//      appPage.visibility = View.VISIBLE


//      Blurry.with(this)
//        .radius(34)
//        .async()
//        .animate(150 * durationMultiplier)
//        .onto(homeScreen)

        isAppCollapsed = false
      }
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
    if (!isAppCollapsed) {
      animAppIconX.apply {
        cancel()
        spring.finalPosition = initialIconX
        start()
      }

      animAppIconY.apply {
        cancel()
        spring.finalPosition = initialIconY
        start()
      }

      animAppIconWidth.apply {
        cancel()
        spring.finalPosition = initialIconWidth.toFloat()
        start()
      }

      animAppIconHeight.apply {
        cancel()
        spring.finalPosition = initialIconHeight.toFloat()
        start()
      }

      animAppIconAlpha.apply {
        cancel()
        spring.finalPosition = 1f
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

//    val transform = MaterialContainerTransform().apply {
//      startView = appPage
//      endView = appIcon
//
//      addTarget(endView as ShapeableImageView)
//
////        setPathMotion(MaterialArcMotion())
//
//      ShapeAppearanceModel()
//
//      scrimColor = Color.TRANSPARENT
//
//      duration = 400L * durationMultiplier
//    }
//
//    TransitionManager.beginDelayedTransition(container, transform)
//    appPage.visibility = View.GONE
//    appIcon.visibility = View.VISIBLE

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

  private fun openingForce(force: SpringForce) {
    force.apply {
      stiffness = SpringForce.STIFFNESS_LOW
      dampingRatio = SpringForce.DAMPING_RATIO_NO_BOUNCY
    }
  }
}