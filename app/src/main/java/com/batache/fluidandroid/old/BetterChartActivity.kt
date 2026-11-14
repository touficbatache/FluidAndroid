package com.batache.fluidandroid.old

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.batache.fluidandroid.R
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.android.synthetic.main.activity_chart.*

class BetterChartActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_chart)

    val axisX = intent.getStringArrayListExtra("axisX")
    val axisY = intent.getStringArrayListExtra("axisY")

    if (axisX == null || axisX.isEmpty() || axisY == null || axisY.isEmpty()) {
      finish()
      return
    }

    val points: MutableList<DataPoint> = ArrayList()

//    for (i in 0 until 10) {
//      points.add(DataPoint(
//        i.toDouble(),
//        0.0
//      ))
//    }

    for (i in 0 until axisX.size) {
      points.add(DataPoint(
        axisX[i].toDouble() / 1000f,
        axisY[i].toDouble() / 1000f,
      ))
    }

    for (i in 0..15) {
      points.add(DataPoint(
        i.toDouble()/10f + 1,
        -0.07
      ))
    }

    val series: LineGraphSeries<DataPoint> = LineGraphSeries(points.toTypedArray())
    graph.addSeries(series)
  }
}