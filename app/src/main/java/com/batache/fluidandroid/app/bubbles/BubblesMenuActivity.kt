package com.batache.fluidandroid.app.bubbles

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.batache.fluidandroid.R
import com.batache.fluidandroid.app.SquircleActivity
import com.batache.fluidandroid.app.home_screen_animation.HomeScreenAnimationActivity
import kotlinx.android.synthetic.main.activity_bubbles_menu.*

class BubblesMenuActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_bubbles_menu)

    generic.setOnClickListener {
      startActivity(Intent(this, BubblesActivity::class.java))
    }

    products.setOnClickListener {
      startActivity(Intent(this, ProductsActivity::class.java))
    }
  }
}