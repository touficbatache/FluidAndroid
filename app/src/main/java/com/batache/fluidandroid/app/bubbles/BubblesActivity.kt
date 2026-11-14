package com.batache.fluidandroid.app.bubbles

import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnLayout
import androidx.dynamicanimation.animation.flingAnimationOf
import com.batache.fluidandroid.R
import com.batache.fluidandroid.dp
import com.batache.fluidandroid.util.CGPoint
import com.batache.fluidandroid.util.CGPointZero
import com.batache.fluidandroid.util.PanRecognizer
import com.batache.fluidandroid.util.squircle.SquircleView
import kotlinx.android.synthetic.main.activity_bubbles.*
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sqrt
import kotlin.random.Random

class BubblesActivity : AppCompatActivity() {

  private val panRecognizer by lazy { PanRecognizer(::onPan, this) }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_bubbles)

    init()
    container.doOnLayout { draw() }
    container.setOnTouchListener(panRecognizer)
  }

  private val colors = arrayOf("#FF9AA2", "#FFB7B2", "#FFDAC1", "#E2F0CB", "#B5EAD7", "#C7CEEA")
  private val circles: MutableList<Circle> = ArrayList()

  private val centerX: Float by lazy { container.measuredWidth / 2f }
  private val centerY: Float by lazy { container.measuredHeight / 2f }
  private var offsetX: Float = 0f
  private var offsetY: Float = 0f

  private fun init() {
//    offsetX = (container.measuredWidth -
//        (RADIUS * 2 * HORIZONTAL_COUNT +
//            PADDINGX * (HORIZONTAL_COUNT - 1) +
//            RADIUS +
//            PADDINGX / 2)) /
//        2 +
//        RADIUS //center the circles by getting its width and calculating the leftover space
//
//    offsetY =
//      (container.measuredHeight - (RADIUS * 2 * VERTICAL_COUNT + PADDINGY * (VERTICAL_COUNT - 1))) / 2 +
//          RADIUS

    offsetX = -((RADIUS * 2 + PADDINGX) * (HORIZONTAL_COUNT - 1)) / 2
    offsetY = -((RADIUS * 2 + PADDINGY) * (VERTICAL_COUNT - 1)) / 2
//    offsetX = 0f
//    offsetY = 0f

    var x = 0f
    var y = 0f

    for (i in 0 until VERTICAL_COUNT) {
      for (j in 0 until HORIZONTAL_COUNT) {
        val randomColor =
          colors[round(Random.nextFloat() * (colors.size - 1)).toInt()] //generating a random color for the menu circle

        circles.add(
          Circle(
            x = x,
            y = y,
            color = randomColor
          )
        )
        x += RADIUS * 2 + PADDINGX //increase x for the next circle
      }

      if (i % 2 == 0) {
        x =
          PADDINGX / 2 + RADIUS //if its the second, fourth, sixth etc. row then move the row to right
      } else {
        x = 0f
      }

      y += RADIUS * 2 + PADDINGY //increase y for the next circle row
    }
  }

  private fun draw() {
    container.removeAllViews()

    for (circle in circles) {
      val scale = getScaleFromDistance(circle)
      if (scale > 0) {
        val sideSize = ((RADIUS * 2) * scale).toInt()
        val circleView = SquircleView(this)
        circleView.layoutParams = ViewGroup.LayoutParams(
          sideSize,
          sideSize
        )
//        circleView.addView(TextView(this).apply { text = "y: ${centerY - (circle.y + offsetY - sideSize / 4)}\nscale: $scale" })
        container.addView(circleView)
        circleView.setBackgroundColor(Color.parseColor(circle.color))
        circleView.setCornerRadius(CORNER_RADIUS)
        circleView.x = circle.x + centerX + offsetX - sideSize / 2
        circleView.y = circle.y + centerY + offsetY - sideSize / 2
      }
    }
  }

  private fun getScaleFromDistance(circle: Circle): Float {
    val dx = circle.x + offsetX
    val dy = circle.y + offsetY
    val dist = sqrt(dx.pow(2) + dy.pow(2))
    return (1f - dist / SCALE_FACTOR).coerceAtLeast(0f)
  }

  var flingX = flingAnimationOf(
    {
      offsetX = it
      draw()
    },
    { return@flingAnimationOf offsetX }
  )

  var flingY = flingAnimationOf(
    {
      offsetY = it
      draw()
    },
    { return@flingAnimationOf offsetY }
  )

  private var originalTouchPoint: CGPoint = CGPointZero
  private var originalOffset: CGPoint = CGPointZero
  private fun onPan(motionData: PanRecognizer.MotionData): Boolean {
    val touchPoint = motionData.location
    val velocity = motionData.velocity

    when (motionData.state) {
      PanRecognizer.State.BEGAN -> {
        flingX.cancel()
        flingY.cancel()

        originalTouchPoint = touchPoint
        originalOffset = CGPoint(offsetX, offsetY)
      }

      PanRecognizer.State.CHANGED -> {
        offsetX = originalOffset.x + touchPoint.x - originalTouchPoint.x
        offsetY = originalOffset.y + touchPoint.y - originalTouchPoint.y
        draw()
      }

      PanRecognizer.State.ENDED, PanRecognizer.State.CANCELLED -> {
        draw()
      }

      PanRecognizer.State.FLING -> {
        flingX.setStartVelocity(velocity.x).start()
        flingY.setStartVelocity(velocity.y).start()
      }
      else -> {}
    }

    return true
  }

  companion object {
    private val RADIUS = 50f.dp
    private val CORNER_RADIUS = 25f.dp
    private val PADDINGX = 5f.dp
    private val PADDINGY = 5f.dp
    private val SCALE_FACTOR = 400f.dp

    private const val HORIZONTAL_COUNT = 21
    private const val VERTICAL_COUNT = 21
  }

  data class Circle(
    val x: Float,
    val y: Float,
    val color: String
  )

}