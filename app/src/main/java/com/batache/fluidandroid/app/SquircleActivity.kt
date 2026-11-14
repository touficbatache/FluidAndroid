package com.batache.fluidandroid.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.batache.fluidandroid.R

class SquircleActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_squircle)
    title = "Squircle"
  }
}