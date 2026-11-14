package com.batache.fluidandroid.app.bubbles

import android.content.Context
import android.graphics.Color
import android.view.ViewGroup
import androidx.core.view.doOnLayout
import androidx.dynamicanimation.animation.flingAnimationOf
import com.batache.fluidandroid.dp
import com.batache.fluidandroid.util.CGPoint
import com.batache.fluidandroid.util.CGPointZero
import com.batache.fluidandroid.util.PanRecognizer
import com.batache.fluidandroid.util.squircle.SquircleView
import kotlinx.android.synthetic.main.activity_bubbles.*
import kotlin.math.*

class BubblesManagerBroken<T : Enum<T>>(
  private val context: Context,
  private val containerView: ViewGroup,
  private val model: BubbleModel,
  private val data: List<BubbleData<T>>,
  private val categories: List<CategoryData<T>>
) {

  private val panRecognizer by lazy { PanRecognizer(::onPan, context) }

  private val bubbles: MutableList<GeneratedBubble<T>> = ArrayList()

  private var centerX: Float = 0f
  private var centerY: Float = 0f
  private var offsetX: Float = 0f
  private var offsetY: Float = 0f

  init {
    containerView.doOnLayout {
      centerX = containerView.measuredWidth / 2f
      centerY = containerView.measuredHeight / 2f

      val (horizontalCount, verticalCount) = getCount(data.size)
      val iterationHorizontalCount =
        horizontalCount + (if (horizontalCount.toDouble().pow(2).toInt() - data.size == -1) 1 else 0)
      offsetX = -((RADIUS * 2 + PADDINGX) * (horizontalCount - 1)) / 2
      offsetY = -((RADIUS * 2 + PADDINGY) * (verticalCount - 1)) / 2

      var x = PADDINGX / 2 + RADIUS
      var y = 0f

//      for ((i, bubble) in data.withIndex()) {
//        val line = i / horizontalCount // Integer division : we only want the result, no rest.
//        val column = i % horizontalCount // The rest.
//      }

      var index = 0
      for (i in 0 until verticalCount) {
        for (j in 0 until iterationHorizontalCount) {
          if (index >= data.size) {
            break
          }

          if (i % 2 == 0 && j == horizontalCount) {
            break
          }

          bubbles.add(
            GeneratedBubble(
              x = x,
              y = y,
              data = data[index]
            )
          )
          x += RADIUS * 2 + PADDINGX // increase x for the next circle

          index++
        }

        /**
         * Instead of resetting x to 0 before going into a new loop, we check if the row is pair or not.
         * If it is, we reset it to 0. If not, we move it slightly to the right.
         */
        x = if (i % 2 == 0 && horizontalCount != iterationHorizontalCount) 0f else PADDINGX / 2 + RADIUS

        if (i == verticalCount - 2) {
          x += (horizontalCount - (data.size - index)) / 2f * (RADIUS * 2 + PADDINGX)
        }

        y += RADIUS * 2 + PADDINGY // Increase y for the next circle row.
      }
    }

    containerView.doOnLayout { draw() }
    containerView.setOnTouchListener(panRecognizer)
  }

  private fun getCount(listSize: Int): Pair<Int, Int> {
    val supposedCount = round(sqrt(listSize + 1.0)).toInt()
    return Pair(supposedCount, supposedCount)
  }

  private fun draw() {
    containerView.removeAllViews()

    for (bubble in bubbles) {
      val scale = getScaleFromDistance(bubble)
      if (scale > 0) {
        val sideSize = ((RADIUS * 2) * scale).toInt()
        val circleView = SquircleView(context)
        circleView.layoutParams = ViewGroup.LayoutParams(
          sideSize,
          sideSize
        )
//        circleView.addView(TextView(this).apply { text = "y: ${centerY - (circle.y + offsetY - sideSize / 4)}\nscale: $scale" })
        containerView.addView(circleView)
        circleView.setBackgroundColor(getColor(bubble) ?: Color.parseColor("#f5f5f5"))
        circleView.setCornerRadius(model.cornerRadius)
        circleView.x = bubble.x + centerX + offsetX - sideSize / 2
        circleView.y = bubble.y + centerY + offsetY - sideSize / 2
      }
    }
  }

  private fun getScaleFromDistance(bubble: GeneratedBubble<T>): Float {
    val dx = bubble.x + offsetX
    val dy = bubble.y + offsetY
    val dist = sqrt(dx.pow(2) + dy.pow(2))
    return (1f - dist / SCALE_FACTOR).coerceAtLeast(0f)
  }

  private fun getColor(bubble: GeneratedBubble<T>): Int? {
    return categories.find { it.category == bubble.data.category }?.backgroundColor
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

    //    private val CORNER_RADIUS = 25f.dp
    private val PADDINGX = 5f.dp
    private val PADDINGY = 5f.dp
    private val SCALE_FACTOR = 400f.dp
  }

  data class BubbleModel(
    val cornerRadius: Float
  )

  data class BubbleData<T>(
    val text: String,
    // We could add (text position) or (when to show text) customizations here if needed.
    val image: String,
    val category: T
  )

  private data class GeneratedBubble<T>(
    val x: Float,
    val y: Float,
    val data: BubbleData<T>
  )

  data class CategoryData<T>(
    val category: T,
    val backgroundColor: Int
  )
}