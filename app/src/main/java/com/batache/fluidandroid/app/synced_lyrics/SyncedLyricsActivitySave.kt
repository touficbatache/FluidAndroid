package com.batache.fluidandroid.app.synced_lyrics

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.dynamicanimation.animation.SpringForce
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.EpoxyController
import com.batache.fluidandroid.R
import com.batache.fluidandroid.dp
import com.github.lcdsmao.springx.ViewPropertySpringAnimator
import kotlinx.android.synthetic.main.activity_synced_lyrics.*

class SyncedLyricsActivitySave : AppCompatActivity() {

  private val lyricsRvSpring by lazy {
    ViewPropertySpringAnimator(lyricsRv)
      .defaultStiffness(250f)
      .defaultDampingRatio(SpringForce.DAMPING_RATIO_LOW_BOUNCY)
  }

  private val controller: LyricsController by lazy { LyricsController() }

  private val animationStiffness: Float = 250f
  private val animationDampingRatio = SpringForce.DAMPING_RATIO_NO_BOUNCY
  private var selectedIndex: Int = -1

  private val mainHandler = Handler(Looper.getMainLooper())
  private val callback = object : Runnable {
    override fun run() {
      onLyricSelected(selectedIndex + 1)

//      lyricsRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//          Log.d("TGBLOG SCROLL", recyclerView.computeVerticalScrollOffset().px.toString())
//        }
//      })
//      lyricsRv.scrollBy(0, 220)


//      val loc = IntArray(2)
//      lyricsRv.layoutManager?.findViewByPosition(1)?.getLocationOnScreen(loc)
//      Log.d("TGBLOG Y SHOULD GO TO", (loc[1].f).toString())
//
//
//      (lyricsRv.layoutManager as LinearLayoutManager?)?.scrollToPositionWithOffset(1, 0)
//      Log.d("TGBLOG VERT SCROL OFFST", (lyricsRv.computeVerticalScrollOffset()).toString())


//      val prevLyricHeight =
//        lyricsRv.layoutManager?.findViewByPosition(selectedIndex - 1)?.measuredHeight?.f ?: 0f
//
//      val lyricHeight =
//        lyricsRv.layoutManager?.findViewByPosition(selectedIndex)?.measuredHeight?.f ?: 0f
//      val loc = IntArray(2)
//      lyricsRv.layoutManager?.findViewByPosition(selectedIndex)?.getLocationOnScreen(loc)
//
//      Log.d("TGBLOG Y TO GO TO", (loc[1].f).toString())
//      Log.d("TGBLOG VERTICAL SCROLL OFFSET", (lyricsRv.computeVerticalScrollOffset()).toString())
//      Log.d("TGBLOG SCROLL Y BY", (loc[1].f - lyricsRv.computeVerticalScrollOffset()).toString())
//      Log.d("TGBLOG SEPARATOR", "---------------")
//
//      lyricsRv.scrollBy(0, loc[1] - lyricsRv.computeVerticalScrollOffset())


//      lyricsRvSpring
//        .scrollYBy(loc[1].f - lyricsRv.computeVerticalScrollOffset()) {
//          minimumVisibleChange = 1 / 500f
//        }
//        .start()
      if (on) mainHandler.postDelayed(this, 3000)
    }
  }

  //  private val lyricViewSpring by lazy {
//    ViewPropertySpringAnimator(lyricTv)
//      .defaultStiffness(animationStiffness)
//      .defaultDampingRatio(animationDampingRatio)
//  }
//
  private var on: Boolean = true

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_synced_lyrics)

    lyricsRv.setController(controller)
    controller.setLyrics(resources.getStringArray(R.array.demo_lyrics).toList())
    controller.requestModelBuild()

    mainHandler.post(callback)

    lyricsRv.setOnClickListener {
      on = !on
    }

//    lyricsRv.layoutManager?.findViewByPosition(selectedIndex)?.getLocationOnScreen()

//    lyricTv.pivotX = 0f
//
//    background.setOnClickListener {
//      if (!on) {
//        lyricViewSpring
//          .scaleX(1.2f)
//          .scaleY(1.2f)
////          .textSize(28f)
//          .alpha(1f)
//          .start()
//      } else {
//        lyricViewSpring
//          .scaleX(1f)
//          .scaleY(1f)
////          .textSize(26f) {
////            minimumVisibleChange = 1f / 1000f
////          }
//          .alpha(.6f)
//          .start()
//      }
//
//      on = !on
//    }
  }

  fun onLyricSelected(position: Int) {
    selectedIndex = position
    controller.requestModelBuild()

//    lyricsRvSpring
//      .scrollToPos(selectedIndex.f) {
//        minimumVisibleChange = 1 / 500f
//      }
//      .start()

    (lyricsRv.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(selectedIndex, 32.dp)

//    (lyricsRv.layoutManager as LinearLayoutManager).smoothScrollToPosition()
//
//    lyricsRv.scrollTo()
//
//    lyricsRv.scrollBy()




//    val params = lyricsRv.layoutParams as RecyclerView.LayoutParams
//    val top: Int = (lyricsRv.layoutManager as LinearLayoutManager).getDecoratedTop(lyricsRv) - params.topMargin

//    val view = (lyricsRv.layoutManager as LinearLayoutManager).findViewByPosition(0)
//    val layoutParams = view?.layoutParams as RecyclerView.LayoutParams?
//
//    Log.d("TGBLOG topmargin", "${view?.x}")
  }

  inner class LyricsController : EpoxyController() {
    private val lyrics: MutableList<String> = ArrayList()

    fun setLyrics(lyrics: List<String>) {
      this.lyrics.clear()
      this.lyrics.addAll(lyrics)
    }

    override fun buildModels() {
      for ((i, lyricStr) in lyrics.withIndex()) {
        lyric {
          id(i)
          lyric(lyricStr)
          animStiffness(animationStiffness)
          animDampRatio(animationDampingRatio)
          selected(i == selectedIndex)
          onClickListener { model, parentView, clickedView, position ->
            onLyricSelected(position)

            mainHandler.removeCallbacks(callback)
            if (on) mainHandler.postDelayed(callback, 3000)
          }
        }
      }
    }
  }
}