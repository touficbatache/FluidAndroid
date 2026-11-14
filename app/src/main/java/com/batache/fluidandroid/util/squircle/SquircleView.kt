package com.batache.fluidandroid.util.squircle

import android.annotation.TargetApi
import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import com.batache.fluidandroid.R

class SquircleView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

  private val clipPaint = Paint(Paint.ANTI_ALIAS_FLAG)
  private val clipPath = Path()

  private val pathManager: SquirclePathManager = SquirclePathManager()
  private var requiresShapeUpdate = true

  private val rectView = Path()

  init {
    init(context, attrs)
  }

//  override fun setBackground(background: Drawable) {
//    //disabled here, please set a background to to this view child
//    //super.setBackground(background);
//  }
//
//  override fun setBackgroundResource(resid: Int) {
//    //disabled here, please set a background to to this view child
//    //super.setBackgroundResource(resid);
//  }
//
//  override fun setBackgroundColor(color: Int) {
//    //disabled here, please set a background to to this view child
//    //super.setBackgroundColor(color);
//  }

  private fun init(context: Context, attrs: AttributeSet?) {
    setWillNotDraw(false)

    clipPaint.color = Color.WHITE
    clipPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
    setLayerType(LAYER_TYPE_HARDWARE, null)

//    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
//      clipPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
//      setLayerType(LAYER_TYPE_SOFTWARE, clipPaint); //Only works for software layers
//    } else {
//      clipPaint.setXfermode(pdMode);
////      setLayerType(LAYER_TYPE_SOFTWARE, null); //Only works for software layers
//    }
    if (attrs != null) {
      val attributes = context.obtainStyledAttributes(attrs, R.styleable.SquircleView)

      if (attributes.hasValue(R.styleable.SquircleView_cornerRadius)) {
        val cornerRadius = attributes.getDimension(R.styleable.SquircleView_cornerRadius, 0f)
        if (pathManager.cornerRadius == null && cornerRadius != -1f) {
          setCornerRadius(cornerRadius)
        }
      }

      attributes.recycle()
    }
  }

  fun setCornerRadius(cornerRadius: Float) {
    pathManager.cornerRadius = cornerRadius
    requiresShapeUpdate()
  }

  override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
    super.onLayout(changed, left, top, right, bottom)
    if (changed) {
      requiresShapeUpdate()
    }
  }

  override fun dispatchDraw(canvas: Canvas) {
    super.dispatchDraw(canvas)

    if (requiresShapeUpdate) {
      calculateLayout(canvas.width, canvas.height)
      requiresShapeUpdate = false
    }
    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
      canvas.drawPath(clipPath, clipPaint)
    } else {
      canvas.drawPath(rectView, clipPaint)
    }

    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
      setLayerType(LAYER_TYPE_HARDWARE, null)
    }
  }

  private fun calculateLayout(width: Int, height: Int) {
    rectView.reset()
    rectView.addRect(0f, 0f, 1f * getWidth(), 1f * getHeight(), Path.Direction.CW)

    if (width > 0 && height > 0) {
      pathManager.updateSize(width, height)
      clipPath.reset()
      clipPath.set(pathManager.path)

      //invert the path for android P
      if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
        val success = rectView.op(clipPath, Path.Op.DIFFERENCE)
      }

      //this needs to be fixed for 25.4.0
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        try {
          outlineProvider = outlineProvider
        } catch (e: Exception) {
          e.printStackTrace()
        }
      }
    }

    postInvalidate()
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  override fun getOutlineProvider(): ViewOutlineProvider {
    return object : ViewOutlineProvider() {
      override fun getOutline(view: View, outline: Outline) {
        if (!isInEditMode) {
          val shadowConvexPath = pathManager.path
          try {
            outline.setConvexPath(shadowConvexPath)
          } catch (e: Exception) {
            e.printStackTrace()
          }
        }
      }
    }
  }

  private fun requiresShapeUpdate() {
    requiresShapeUpdate = true
    postInvalidate()
  }
}