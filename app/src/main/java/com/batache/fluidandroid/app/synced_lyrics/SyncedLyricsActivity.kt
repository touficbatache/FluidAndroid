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
import kotlinx.android.synthetic.main.activity_synced_lyrics.*

class SyncedLyricsActivity : AppCompatActivity() {

  private lateinit var lyrics: List<String>

  private val controller: LyricsController by lazy { LyricsController() }

  private var prevSelectedIndex: Int = -1
  private var selectedIndex: Int = -1

  private val mainHandler = Handler(Looper.getMainLooper())
  private val callback = Runnable { onLyricSelected(selectedIndex + 1) }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_synced_lyrics)

    lyrics = resources.getStringArray(R.array.demo_lyrics).toList()

    lyricsRv.setController(controller)
    controller.setLyrics(lyrics)
    controller.requestModelBuild()

    mainHandler.post(callback)
  }

  private fun onLyricSelected(position: Int) {
    prevSelectedIndex = selectedIndex
    selectedIndex = position
    controller.requestModelBuild()

    (lyricsRv.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(selectedIndex, 32.dp)

    // Move to next line in 3 seconds
    if (selectedIndex < lyrics.size - 1) mainHandler.postDelayed(callback, 3000)
  }

  inner class LyricsController : EpoxyController() {
    private val lyrics: MutableList<String> = ArrayList()

    fun setLyrics(lyrics: List<String>) {
      this.lyrics.clear()
      this.lyrics.addAll(lyrics)
    }

    override fun buildModels() {
      val animationStiffness = 250f
      val animationDampingRatio = SpringForce.DAMPING_RATIO_NO_BOUNCY

      for ((i, lyricStr) in lyrics.withIndex()) {
        val wasPrevSelected = i == prevSelectedIndex
        val isSelected = i == selectedIndex

        lyric {
          id(i)
          lyric(lyricStr)
          animStiffness(animationStiffness)
          animDampRatio(animationDampingRatio)
          selected(isSelected)
          animate(wasPrevSelected || isSelected)
          onClickListener { model, parentView, clickedView, position ->
            // Reset scroll timer
            mainHandler.removeCallbacks(callback)
            onLyricSelected(position)
          }
        }
      }
    }
  }
}