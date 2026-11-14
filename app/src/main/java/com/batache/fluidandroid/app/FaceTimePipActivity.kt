package com.batache.fluidandroid.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.dynamicanimation.animation.SpringForce
import com.batache.fluidandroid.R
import com.batache.fluidandroid.util.*
import com.github.lcdsmao.springx.ViewPropertySpringAnimator
import kotlinx.android.synthetic.main.activity_facetime_pip.*

class FaceTimePipActivity : AppCompatActivity(), (PanRecognizer.MotionData) -> Boolean {

  private val panRecognizer by lazy { PanRecognizer(this) }

  private val pipPositionViews
    get() = arrayListOf(
      topLeftView,
      topRightView,
      bottomLeftView,
      bottomRightView
    )

  private val pipSpring by lazy {
    ViewPropertySpringAnimator(pipView)
      .defaultStiffness(250f)
      .defaultDampingRatio(SpringForce.DAMPING_RATIO_NO_BOUNCY)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_facetime_pip)

    pipView.setOnTouchListener(panRecognizer)
  }

  private var initialOffset = CGPointZero

  override fun invoke(motionData: PanRecognizer.MotionData): Boolean {
    val touchPoint = motionData.location

    when (motionData.state) {
      PanRecognizer.State.BEGAN -> {
        initialOffset = CGPoint(
          x = touchPoint.x - pipView.x,
          y = touchPoint.y - pipView.y
        )
      }

      PanRecognizer.State.CHANGED -> {
        pipSpring
          .x(touchPoint.x - initialOffset.x) { stiffness = 500f }
          .y(touchPoint.y - initialOffset.y) { stiffness = 500f }
          .start()
      }

      PanRecognizer.State.ENDED, PanRecognizer.State.CANCELLED -> {
        val decelerationRate = 0.99f
        val velocity = motionData.velocity
        val projectedPosition = CGPoint(
          x = pipView.x + project(
            initialVelocity = velocity.x,
            decelerationRate = decelerationRate
          ),
          y = pipView.y + project(
            initialVelocity = velocity.y,
            decelerationRate = decelerationRate
          )
        )
        val constrainedProjectedPosition = constrainWithinScreen(touchPoint, projectedPosition)
        val nearestCornerPosition = nearestCorner(toPoint = constrainedProjectedPosition) //nearestCorner(toPoint = projectedPosition) // nearestCorner(toPoint = constrainedProjectedPosition)
        val relativeInitialVelocity = CGVector(
          dx = relativeVelocity(
            velocity = velocity.x,
            fromValue = pipView.x,
            toValue = nearestCornerPosition.x
          ),
          dy = relativeVelocity(
            velocity = velocity.y,
            fromValue = pipView.y,
            toValue = nearestCornerPosition.y
          )
        )
        pipSpring
          .x(nearestCornerPosition.x) { startVelocity = relativeInitialVelocity.dx }
          .y(nearestCornerPosition.y) { startVelocity = relativeInitialVelocity.dy }
          .start()
      }
      else -> {}
    }

    return true
  }

  private fun constrainWithinScreen(touchPoint: CGPoint, projectedPosition: CGPoint): CGPoint {
    val isXInScreen = 0 < projectedPosition.x && projectedPosition.x < screen.measuredWidth
    val isYInScreen = 0 < projectedPosition.y && projectedPosition.y < screen.measuredHeight

    if (isXInScreen && isYInScreen) {
      return projectedPosition
    }

    val a = (projectedPosition.y - touchPoint.y) / (projectedPosition.x - touchPoint.x)
    val b = touchPoint.y - a * touchPoint.x

    val directionVertical =
      if (projectedPosition.y > touchPoint.y)
        DirectionVertical.TOP_TO_BOTTOM
      else
        DirectionVertical.BOTTOM_TO_TOP

    val yIntersection =
      if (directionVertical == DirectionVertical.TOP_TO_BOTTOM) { // intersect with bottom
        screenConstraints.measuredHeight
      } else { // intersect with top
        0
      }.toFloat()

    val constrainedX = (yIntersection - b) / a

    val directionHorizontal =
      if (projectedPosition.x > touchPoint.x)
        DirectionHorizontal.START_TO_END
      else
        DirectionHorizontal.END_TO_START

    val xIntersection =
      if (directionHorizontal == DirectionHorizontal.START_TO_END) { // intersect with end
        screenConstraints.measuredWidth
      } else { // intersect with start
        0
      }.toFloat()

    val constrainedY = a * xIntersection + b

    return CGPoint(
      x = constrainedX.coerceAtLeast(0f).coerceAtMost(screenConstraints.measuredWidth.toFloat()),
      y = constrainedY.coerceAtLeast(0f).coerceAtMost(screenConstraints.measuredHeight.toFloat())
    )
  }

  private enum class DirectionHorizontal {
    START_TO_END, END_TO_START
  }

  private enum class DirectionVertical {
    TOP_TO_BOTTOM, BOTTOM_TO_TOP
  }

  /// Distance traveled after decelerating to zero velocity at a constant rate.
  private fun project(initialVelocity: Float, decelerationRate: Float): Float {
    return (initialVelocity / 500) * decelerationRate / (1 - decelerationRate)
  }

  /// Finds the position of the nearest corner to the given point.
  private fun nearestCorner(toPoint: CGPoint): CGPoint {
    var minDistance = Float.MAX_VALUE
    var closestPosition = CGPointZero
    for ((i, position) in pipPositionViews.map { it.center }.withIndex()) {
      val distance = toPoint.distance(toPoint = position)
      if (distance < minDistance) {
        closestPosition = pipPositionViews[i].coordinates
        minDistance = distance
      }
    }
    return closestPosition
  }

  /// Calculates the relative velocity needed for the initial velocity of the animation.
  private fun relativeVelocity(velocity: Float, fromValue: Float, toValue: Float): Float {
    if ((fromValue - toValue).equals(0f)) {
      return 0f
    }
    return velocity / (toValue - fromValue)
  }
}
