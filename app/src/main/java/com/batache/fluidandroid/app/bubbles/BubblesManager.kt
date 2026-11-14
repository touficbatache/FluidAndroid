package com.batache.fluidandroid.app.bubbles

import android.content.Context
import android.graphics.Color
import android.util.Log
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

class BubblesManager(
  private val context: Context,
  private val containerView: ViewGroup,
  private val model: BubbleModel,
  private var data: List<BubbleData>? = null,
  private var categories: List<CategoryData>? = null
) {

  private val panRecognizer by lazy { PanRecognizer(::onPan, context) }

//  private val categoriesMap: MutableMap<Int, List<String>> = mutableMapOf()

  private val bubbles: MutableList<GeneratedBubble> = ArrayList()

  private var centerX: Float = 0f
  private var centerY: Float = 0f
  private var offsetX: Float = 0f
  private var offsetY: Float = 0f

  init {
    containerView.doOnLayout { invalidate() }
    containerView.setOnTouchListener(panRecognizer)
  }

  private fun invalidate() {
    data?.let {
      centerX = containerView.measuredWidth / 2f
      centerY = containerView.measuredHeight / 2f

      var x = PADDINGX / 2 + RADIUS
      var y = 0f

//      categoriesMap.clear()
      val originalCategoriesMap: Map<String, Int> =
        it.map { item -> item.category }.groupingBy { category -> category }.eachCount()
      val categoriesMap: MutableMap<String, Int> =
        it.map { item -> item.category }.groupingBy { category -> category }.eachCount()
          .toMutableMap()
//      val (horizontalCount, verticalCount) = getCount(it.size)

//      for (item in it) {
//        val itemsOfCategoryInLine =
//          floor(sqrt(it.size.toDouble()) / sqrt(originalCategoriesMap[item.category]!!.toDouble()))
//        bubbles.add(
//          GeneratedBubble(
//            x = x,
//            y = y,
//            data = item
//          )
//        )
//        x += RADIUS * 2 + PADDINGX // increase x for the next circle
//      }


      var line = 0
      var categoryIndex: Int? = null

      val itemsAllowedInLine = ceil(sqrt(it.size.toDouble())).toInt()
      var itemsOnCurrentLine = 0

      for (i in it.indices) {
        if (categoryIndex == null) {
          val catIndex = findFirstNonEmptyCategory(categoriesMap) ?: return
          categoryIndex = catIndex
        }

        val categoryNameAtIndex = categories?.get(categoryIndex)?.category!!

        val items = getItemsForCategory(categoryNameAtIndex)
        val ind = originalCategoriesMap[categoryNameAtIndex]!! - categoriesMap[categoryNameAtIndex]!!
        if (ind < items!!.size) {
          bubbles.add(
            GeneratedBubble(
              x = x,
              y = y,
              data = items[ind]
            )
          )
          x += RADIUS * 2 + PADDINGX // increase x for the next circle
          itemsOnCurrentLine++
        }

        categoriesMap[categoryNameAtIndex] = categoriesMap[categoryNameAtIndex]!! - 1

        val categoryItemsAllowedInLine =
          floor(sqrt(originalCategoriesMap[categoryNameAtIndex]!!.toDouble())).toInt()
        if ((originalCategoriesMap[categoryNameAtIndex]!! - categoriesMap[categoryNameAtIndex]!!) % categoryItemsAllowedInLine == 0) {
          categoryIndex++
          if (categoryIndex < categories!!.size) {
            val nextCategoryName = categories?.get(categoryIndex)?.category!!
            val nextCategoryAllowance =
              floor(sqrt(originalCategoriesMap[nextCategoryName]!!.toDouble())).toInt()
            if (itemsOnCurrentLine + nextCategoryAllowance > itemsAllowedInLine) {
              line++
              x = if (line % 2 == 0) PADDINGX / 2 + RADIUS else 0f
              y += RADIUS * 2 + PADDINGY // Increase y for the next circle row.
              itemsOnCurrentLine = 0
              categoryIndex = null
            }
          }
        }

        if (itemsOnCurrentLine >= itemsAllowedInLine) {
          line++
          x = if (line % 2 == 0) PADDINGX / 2 + RADIUS else 0f
          y += RADIUS * 2 + PADDINGY // Increase y for the next circle row.
          itemsOnCurrentLine = 0
          categoryIndex = null
        }
      }


//      val (horizontalCount, verticalCount) = getCount(it.size)
//      offsetX = -((RADIUS * 2 + PADDINGX) * (horizontalCount - 1)) / 2
//      offsetY = -((RADIUS * 2 + PADDINGY) * (verticalCount - 1)) / 2
//
//      var x = PADDINGX / 2 + RADIUS
//      var y = 0f
//
//      var index = 0
//      for (i in 0 until verticalCount) {
//        for (j in 0 until horizontalCount) {
//          if (index >= it.size) {
//            break
//          }
//
//          if (i % 2 == 0 && j == horizontalCount) {
//            break
//          }
//
//          bubbles.add(
//            GeneratedBubble(
//              x = x,
//              y = y,
//              data = it[index]
//            )
//          )
//          x += RADIUS * 2 + PADDINGX // increase x for the next circle
//
//          index++
//        }
//
//        /**
//         * Instead of resetting x to 0 before going into a new loop, we check if the row is pair or not.
//         * If it is, we reset it to 0. If not, we move it slightly to the right.
//         */
//        x = if (i % 2 == 0) 0f else PADDINGX / 2 + RADIUS
//
//        if (i == verticalCount - 2) {
//          x += (horizontalCount - (it.size - index)) / 2f * (RADIUS * 2 + PADDINGX)
//        }
//
//        y += RADIUS * 2 + PADDINGY // Increase y for the next circle row.
//      }

      draw()
    }
  }

  private fun findFirstNonEmptyCategory(categoriesMap: MutableMap<String, Int>): Int? {
    categories?.let {
      for ((i, category) in it.withIndex()) {
        if (categoriesMap[category.category]!! > 0) {
          return i
        }
      }
    }
    return null
  }

  private fun getItemsForCategory(category: String): List<BubbleData>? {
    return data?.filter { it.category == category }
  }

  fun update(data: List<BubbleData>, categories: List<CategoryData>) {
    this.data = data
    this.categories = categories
    invalidate()
  }

  private fun getCount(listSize: Int): Pair<Int, Int> {
    val sqrtListSize = sqrt(listSize.toDouble())
    val supposedCount = round(sqrtListSize).toInt()
    val verticalCount = if (supposedCount < sqrtListSize) supposedCount + 1 else supposedCount
    return Pair(verticalCount, supposedCount)
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

  private fun getScaleFromDistance(bubble: GeneratedBubble): Float {
    val dx = bubble.x + offsetX
    val dy = bubble.y + offsetY
    val dist = sqrt(dx.pow(2) + dy.pow(2))
    return (1f - dist / SCALE_FACTOR).coerceAtLeast(0f)
  }

  private fun getColor(bubble: GeneratedBubble): Int? {
    return categories?.find { it.category == bubble.data.category }?.backgroundColor
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

  data class BubbleData(
    val text: String,
    // We could add (text position) or (when to show text) customizations here if needed.
    val image: String,
    val category: String
  )

  private data class GeneratedBubble(
    val x: Float,
    val y: Float,
    val data: BubbleData
  )

  data class CategoryData(
    val category: String,
    val title: String,
    val backgroundColor: Int
  )
}