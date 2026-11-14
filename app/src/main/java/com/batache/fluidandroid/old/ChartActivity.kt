package com.batache.fluidandroid.old

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.batache.fluidandroid.R
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.android.synthetic.main.activity_chart.*

class ChartActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_chart)

    val velocitiesY = intent.getStringArrayListExtra("velocitiesY")

    if (velocitiesY == null || velocitiesY.isEmpty()) {
      finish()
      return
    }

    val points: MutableList<DataPoint> = ArrayList()

    for (i in 0 until 10) {
      points.add(DataPoint(
        i.toDouble(),
        0.0
      ))
    }

    for (i in 0 until velocitiesY.size) {
      points.add(DataPoint(
        i.toDouble() + 10,
        velocitiesY[i].toDouble()
      ))
    }

    for (i in 0..10) {
      points.add(DataPoint(
        i.toDouble() + 10 + velocitiesY.size,
        1.0
      ))
    }

    val series: LineGraphSeries<DataPoint> = LineGraphSeries(points.toTypedArray())
    graph.addSeries(series)
  }
}