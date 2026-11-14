package com.batache.fluidandroid.util.squircle

import android.graphics.Path

class SquirclePathManager {

  var cornerRadius: Float? = null

  val path = Path()

  fun updateSize(width: Int, height: Int) {
    path.reset()

    val clipPath = Path()
    val cornerRadius = this.cornerRadius?.coerceAtMost(width.coerceAtMost(height) / 2f / 1.52866483f) ?: 0f
    clipPath.moveTo(1.52866483f * cornerRadius, 0.00000000f * cornerRadius)
    clipPath.lineTo(width - 1.52866471f * cornerRadius, 0.00000000f * cornerRadius)
    clipPath.cubicTo(
      width - 1.08849323f * cornerRadius,
      0.00000000f * cornerRadius,
      width - 0.86840689f * cornerRadius,
      0.00000000f * cornerRadius,
      width - 0.63149369f * cornerRadius,
      0.07491139f * cornerRadius
    )
    clipPath.cubicTo(
      width - 0.37282392f * cornerRadius,
      0.16905899f * cornerRadius,
      width - 0.16906013f * cornerRadius,
      0.37282401f * cornerRadius,
      width - 0.07491176f * cornerRadius,
      0.63149399f * cornerRadius
    )
    clipPath.cubicTo(
      width - 0.00000000f * cornerRadius,
      0.86840701f * cornerRadius,
      width - 0.00000000f * cornerRadius,
      1.08849299f * cornerRadius,
      width - 0.00000000f * cornerRadius,
      1.52866483f * cornerRadius
    )
    clipPath.lineTo(width - 0.00000000f * cornerRadius, height - 1.52866471f * cornerRadius)
    clipPath.cubicTo(
      width - 0.00000000f * cornerRadius,
      height - 1.08849323f * cornerRadius,
      width - 0.00000000f * cornerRadius,
      height - 0.86840689f * cornerRadius,
      width - 0.07491139f * cornerRadius,
      height - 0.63149369f * cornerRadius
    )
    clipPath.cubicTo(
      width - 0.16905883f * cornerRadius,
      height - 0.37282392f * cornerRadius,
      width - 0.37282392f * cornerRadius,
      height - 0.16905883f * cornerRadius,
      width - 0.63149399f * cornerRadius,
      height - 0.07491111f * cornerRadius
    )
    clipPath.cubicTo(
      width - 0.86840689f * cornerRadius,
      height - 0.00000000f * cornerRadius,
      width - 1.08849323f * cornerRadius,
      height - 0.00000000f * cornerRadius,
      width - 1.52866471f * cornerRadius,
      height - 0.00000000f * cornerRadius
    )
    clipPath.lineTo(1.52866483f * cornerRadius, height - 0.00000000f * cornerRadius)
    clipPath.cubicTo(
      1.08849299f * cornerRadius,
      height - 0.00000000f * cornerRadius,
      0.86840701f * cornerRadius,
      height - 0.00000000f * cornerRadius,
      0.63149381f * cornerRadius,
      height - 0.07491139f * cornerRadius
    )
    clipPath.cubicTo(
      0.37282401f * cornerRadius,
      height - 0.16905883f * cornerRadius,
      0.16906001f * cornerRadius,
      height - 0.37282392f * cornerRadius,
      0.07491100f * cornerRadius,
      height - 0.63149399f * cornerRadius
    )
    clipPath.cubicTo(
      0.00000000f * cornerRadius,
      height - 0.86840689f * cornerRadius,
      0.00000000f * cornerRadius,
      height - 1.08849323f * cornerRadius,
      0.00000000f * cornerRadius,
      height - 1.52866471f * cornerRadius
    )
    clipPath.lineTo(0.00000000f * cornerRadius, 1.52866483f * cornerRadius)
    clipPath.cubicTo(
      0.00000000f * cornerRadius,
      1.08849299f * cornerRadius,
      0.00000000f * cornerRadius,
      0.86840701f * cornerRadius,
      0.07491141f * cornerRadius,
      0.63149381f * cornerRadius
    )
    clipPath.cubicTo(
      0.16906001f * cornerRadius,
      0.37282401f * cornerRadius,
      0.37282401f * cornerRadius,
      0.16906001f * cornerRadius,
      0.63149399f * cornerRadius,
      0.07491100f * cornerRadius
    )
    clipPath.cubicTo(
      0.86840701f * cornerRadius,
      0.00000000f * cornerRadius,
      1.08849299f * cornerRadius,
      0.00000000f * cornerRadius,
      1.52866483f * cornerRadius,
      0.00000000f * cornerRadius
    )

    path.set(clipPath)
  }
}