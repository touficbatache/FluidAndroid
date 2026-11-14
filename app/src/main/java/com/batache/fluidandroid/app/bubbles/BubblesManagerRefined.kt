package com.batache.fluidandroid.app.bubbles

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.doOnLayout
import androidx.dynamicanimation.animation.flingAnimationOf
import com.batache.fluidandroid.dp
import com.batache.fluidandroid.util.CGPoint
import com.batache.fluidandroid.util.CGPointZero
import com.batache.fluidandroid.util.PanRecognizer
import com.batache.fluidandroid.util.squircle.SquircleView
import kotlinx.android.synthetic.main.activity_bubbles.*
import kotlin.math.*

class BubblesManagerRefined(
  private val context: Context,
  private val containerView: ViewGroup,
  private val model: BubbleModel,
  private var data: List<BubbleData>? = null,
  private var categories: List<CategoryData>? = null
) {

  private val panRecognizer by lazy { PanRecognizer(::onPan, context) }

  private lateinit var sortedData: Array<Array<BubbleData?>>

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
    data?.let { data ->
      centerX = containerView.measuredWidth / 2f
      centerY = containerView.measuredHeight / 2f

      val matrixSize = getMatrixSize(data.size)
      sortedData = Array(matrixSize) { Array(matrixSize) { null } }

      categories?.let { categories ->
        for (category in categories) {
          val itemsInCategory = data.filter { item -> item.category == category.category }

          val coordinates = findPlaceFor(itemsInCategory.size)
          for ((i, item) in itemsInCategory.withIndex()) {
            if (i < coordinates.size) { // TODO: Fix this another way
              val (x, y) = coordinates[i]
              sortedData[x][y] = item
            }
          }
        }
      }

      var x = PADDINGX / 2 + RADIUS
      var y = 0f

      for ((i, line) in sortedData.withIndex()) {
        for (item in line) {
          item?.let {
            bubbles.add(
              GeneratedBubble(
                x = x,
                y = y,
                data = item
              )
            )
          }
          x += RADIUS * 2 + PADDINGX // increase x for the next circle
        }

        /**
         * Instead of resetting x to 0 before going into a new loop, we check if the row is pair or not.
         * If it is, we reset it to 0. If not, we move it slightly to the right.
         */
        x = if (i % 2 == 0) 0f else PADDINGX / 2 + RADIUS

//        if (i == verticalCount - 2) {
//          x += (horizontalCount - (data.size - index)) / 2f * (RADIUS * 2 + PADDINGX)
//        }

        y += RADIUS * 2 + PADDINGY // Increase y for the next circle row.
      }

      draw()
    }
  }

  private fun getMatrixSize(listSize: Int): Int = ceil(sqrt(listSize.toDouble())).toInt() + 1

  private fun getCount(listSize: Int): Pair<Int, Int> {
    val sqrtListSize = sqrt(listSize.toDouble())
    val supposedCount = round(sqrtListSize).toInt()
    val lineCount = if (supposedCount < sqrtListSize) supposedCount + 1 else supposedCount
    return Pair(lineCount, supposedCount)
  }

//  private fun findPlaceFor(itemCount: Int): List<Pair<Int, Int>> {
//    val coords: MutableList<Pair<Int, Int>> = ArrayList()
//
//    val (lineCount, columnCount) = getCount(itemCount)
//
//    // Get all lines with enough empty space
//    val possibleLines =
//      sortedData
//        .withIndex()
//        .filter { column -> column.value.count { item -> item == null } >= columnCount }
//        .map { it.index }
//
//    // Get lines with `lineCount` consecutive numbers
//    val linesIndexes: MutableList<Int> = ArrayList()
//    for (possibleLine in possibleLines) {
//      if (linesIndexes.size == lineCount) {
//        break
//      }
//
//      if (linesIndexes.isEmpty()) {
//        linesIndexes.add(possibleLine)
//      } else {
//
//
////        val previousFreeColumnIndexes =
////          sortedData[linesIndexes.last()]
////            .withIndex()
////            .filter { item -> item.value == null }
////            .map { it.index }
////            .getFirstConsecutive(columnCount)
////
//////        MODIFICATION 30.01.22.03.39
//////        val currentFreeColumnIndexes = sortedData[possibleLine]
//////          .withIndex()
//////          .filter { item -> item.value == null }
//////          .map { it.index }
//////          .getFirstConsecutive(columnCount)
////
////        val currentFreeColumnIndexes =
////          sortedData[possibleLine]
////            .withIndex()
////            .filter { item -> item.value == null }
////            .map { it.index }
////
////        // If this number doesn't come just after the last one in the list, clear it
////        // OR
////        // If the positions are not above each other, clear it
////        if (
////          possibleLine - linesIndexes.last() != 1 ||
////          !currentFreeColumnIndexes.containsAll(previousFreeColumnIndexes) // TODO: Check specific sequence not just containsAll. We need containsThisSpecificSequenceAndOrder lol.
////        ) {
////          linesIndexes.clear()
////        }
////
////        linesIndexes.add(possibleLine)
//
//
//        val firstFreeColumnIndexes =
//          sortedData[linesIndexes[0]]
//            .withIndex()
//            .filter { item -> item.value == null }
//            .map { it.index }
//            .takeFirstConsecutive(columnCount)
//
////        MODIFICATION 30.01.22.03.39
////        val currentFreeColumnIndexes = sortedData[possibleLine]
////          .withIndex()
////          .filter { item -> item.value == null }
////          .map { it.index }
////          .getFirstConsecutive(columnCount)
//
//        val currentFreeColumnIndexes =
//          sortedData[possibleLine]
//            .withIndex()
//            .filter { item -> item.value == null }
//            .map { it.index }
//
//        // If this number doesn't come just after the last one in the list, clear it
//        // OR
//        // If the positions are not above each other, clear it
//        if (
//          possibleLine - linesIndexes.last() != 1 ||
//          !currentFreeColumnIndexes.containsAll(firstFreeColumnIndexes) // TODO: Check specific sequence not just containsAll. We need containsThisSpecificSequenceAndOrder lol.
//        ) {
//          linesIndexes.clear()
//        }
//
//        linesIndexes.add(possibleLine)
//      }
//    }
//
////    MODIFICATION 30.01.22.03.39
////    for (lineIndex in linesIndexes) {
////      val freeColumns =
////        sortedData[lineIndex]
////          .withIndex()
////          .filter { it.value == null }
////          .map { it.index }
////          .take(columnCount)
////
////      for (y in freeColumns) {
////        coords.add(Pair(lineIndex, y))
////      }
////    }
//
//    val firstLineFreeColumns =
//      sortedData[linesIndexes[0]]
//        .withIndex()
//        .filter { it.value == null }
//        .map { it.index }
//        .takeFirstConsecutive(columnCount)
//
//    for (lineIndex in linesIndexes) {
//      for (y in firstLineFreeColumns) {
//        coords.add(Pair(lineIndex, y))
//      }
//    }
//
//    Log.d("tgblog", "${coords}")
//
//    return coords
//  }

  private fun findPlaceFor(itemCount: Int): List<Pair<Int, Int>> {
    val coords: MutableList<Pair<Int, Int>> = ArrayList()

    val (lineCount, columnCount) = getCount(itemCount)

    val tesg = sortedData
      .map { line ->
        line
          /**
           * Get column indexes for null items
           */
          .takeNullItemsIndexes()
          /**
           * Select only those that are:
           * - next to each other
           * and
           * - at least _columnCount_ wide
           */
          .groupByConsecutive(columnCount)
          .filter { it.size >= columnCount }
          /**
           * Take only starting indexes for each group
           */
          .map { it[0] }
      }
//    Log.d("tgblog", "${tesg}")

    var startLineIndex: Int? = null
    var startColumnIndex: Int? = null
    for ((lineIndex, line) in tesg.withIndex()) {
      if (line.isEmpty()) {
        startLineIndex = null
        startColumnIndex = null
      } else {
        if (startLineIndex != null && startColumnIndex != null) {
          break
        }

        if (lineIndex + lineCount <= tesg.size) {
          for (columnIndex in line) {
            var valid = true
            for (i in (lineIndex + 1) until (lineIndex + lineCount)) {
              if (!tesg[i].any { it <= columnIndex && columnIndex <= it + columnCount }) {
                valid = false
                break
              }

//              for (startIndex in tesg[i]) {
//                if (startIndex <= columnIndex && columnIndex <= startIndex + columnCount) {
//
//                } else {
//                  valid = false
//                  break
//                }
//              }

//              if (!tesg[i].contains(columnIndex)) {
//                valid = false
//                break
//              }
            }
            if (valid) {
              startLineIndex = lineIndex
              startColumnIndex = columnIndex
              break
            }
          }
        }
      }
    }

    startLineIndex?.let {
      startColumnIndex?.let {
        for (x in startLineIndex until (startLineIndex + lineCount)) {
          for (y in startColumnIndex until (startColumnIndex + columnCount)) {
            coords.add(Pair(x, y))
          }
        }
      }
    }

    return coords
  }

  private fun List<Int>.takeFirstConsecutive(n: Int): List<Int> {
    val consecutiveNumbers: MutableList<Int> = ArrayList()
    for (number in this) {
      if (consecutiveNumbers.size == n) {
        break
      }

      if (consecutiveNumbers.isEmpty()) {
        consecutiveNumbers.add(number)
      } else {
        // If this number doesn't come _directly after_ the last one in the list, clear it
        if (number - consecutiveNumbers.last() != 1) {
          consecutiveNumbers.clear()
        }

        consecutiveNumbers.add(number)
      }
    }
    return consecutiveNumbers
  }

  private fun List<Int>.groupByConsecutive(n: Int): List<List<Int>> {
    val consecutiveNumberGroups: MutableList<MutableList<Int>> = mutableListOf(ArrayList())
    for (number in this) {
      if (consecutiveNumberGroups.last().size == n) {
        consecutiveNumberGroups.add(ArrayList())
      }

      if (consecutiveNumberGroups.last().isEmpty()) {
        consecutiveNumberGroups.last().add(number)
      } else {
        // If this number isn't consecutive in the last group, create a new group
        if (number - consecutiveNumberGroups.last().last() != 1) {
          consecutiveNumberGroups.add(ArrayList())
        }

        consecutiveNumberGroups.last().add(number)
      }
    }
    return consecutiveNumberGroups
  }

  fun update(data: List<BubbleData>, categories: List<CategoryData>) {
    this.data = data
    this.categories = categories
    invalidate()
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
//        circleView.addView(TextView(context).apply {
//          text = "${bubble.data.category}"
//          alpha = scale
//        })
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

private fun <T> Array<T>.takeNullItemsIndexes(): List<Int> =
  withIndex()
    .filter { item -> item.value == null }
    .map { it.index }
