package com.batache.fluidandroid.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.batache.fluidandroid.R
import com.batache.fluidandroid.app.bubbles.BubblesActivity
import com.batache.fluidandroid.app.bubbles.BubblesMenuActivity
import com.batache.fluidandroid.app.home_screen_animation.HomeScreenAnimationActivity
import com.batache.fluidandroid.app.synced_lyrics.SyncedLyricsActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    squircle.setOnClickListener {
      startActivity(Intent(this, SquircleActivity::class.java))
    }

    homeScreenAnimation.setOnClickListener {
      startActivity(Intent(this, HomeScreenAnimationActivity::class.java))
    }

    faceTimePip.setOnClickListener {
      startActivity(Intent(this, FaceTimePipActivity::class.java))
    }

    insta3dTouch.setOnClickListener {
      startActivity(Intent(this, Insta3DTouchActivity::class.java))
    }

    syncedLyrics.setOnClickListener {
      startActivity(Intent(this, SyncedLyricsActivity::class.java))
    }

    bubbles.setOnClickListener {
      startActivity(Intent(this, BubblesMenuActivity::class.java))
    }

    jumping.setOnClickListener {
      startActivity(Intent(this, JumpingActivity::class.java))
    }

//    testing.setOnClickListener {
//      startActivity(Intent(this, TestingActivity::class.java))
//    }

    player.setOnClickListener {
      startActivity(Intent(this, PlayerActivity::class.java))
    }
  }
}