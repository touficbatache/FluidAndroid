package com.batache.fluidandroid.util

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import androidx.core.view.GestureDetectorCompat

class PanRecognizer(
  private val callback: (motionData: MotionData) -> Boolean,
  context: Context? = null
) : View.OnTouchListener {

  private val velocityTracker: VelocityTracker = VelocityTracker.obtain()

  private val gestureDetector =
    GestureDetectorCompat(context, object : GestureDetector.OnGestureListener {
      override fun onDown(p0: MotionEvent?): Boolean = false

      override fun onShowPress(p0: MotionEvent?) {

      }

      override fun onSingleTapUp(p0: MotionEvent?): Boolean = false

      override fun onScroll(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean =
        false

      override fun onLongPress(p0: MotionEvent?) {

      }

      override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent?,
        velocityX: Float,
        velocityY: Float
      ): Boolean =
        callback.invoke(
          MotionData(
            state = State.FLING,
            location = CGPointZero,
            velocity = Velocity(velocityX, velocityY)
          )
        )
    })

  override fun onTouch(view: View, event: MotionEvent): Boolean {
    gestureDetector.onTouchEvent(event)

    val state = when (event.action) {
      MotionEvent.ACTION_DOWN -> State.BEGAN
      MotionEvent.ACTION_MOVE -> State.CHANGED
      MotionEvent.ACTION_UP -> State.ENDED
      MotionEvent.ACTION_CANCEL -> State.CANCELLED
      else -> null
    }

    if (event.action == MotionEvent.ACTION_DOWN) {
      velocityTracker.clear()
    }

    val mockEvent = arrayListOf(event)
    mockEvent[0].setLocation(event.rawX, event.rawY)
    velocityTracker.addMovement(mockEvent[0])
    velocityTracker.computeCurrentVelocity(1000)

//    if (event.action == MotionEvent.ACTION_UP) {
//      velocityTracker.computeCurrentVelocity(1000)
//    }

    return callback.invoke(
      MotionData(
        state = state,
        location = CGPoint(
          x = event.rawX,
          y = event.rawY
        ),
        velocity = Velocity(velocityTracker.xVelocity, velocityTracker.yVelocity)
      )
    )
  }

  data class MotionData(
    val state: State?,
    val location: CGPoint,
    val velocity: Velocity
  )

  enum class State {
    BEGAN, CHANGED, ENDED, CANCELLED, FLING
  }

  data class Velocity(
    val x: Float,
    val y: Float
  )
}


