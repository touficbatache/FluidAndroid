package com.batache.fluidandroid.util.squircle;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.batache.fluidandroid.R;

public class SquircleImageView extends AppCompatImageView {

  private final Paint clipPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
  private final Path clipPath = new Path();

  private final SquirclePathManager pathManager = new SquirclePathManager();
  private boolean requiresShapeUpdate = true;

  private final Path rectView = new Path();

  public SquircleImageView(@NonNull Context context) {
    super(context);
    init(context, null);
  }

  public SquircleImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs);
  }

  public SquircleImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, attrs);
  }

  private void init(Context context, AttributeSet attrs) {
    clipPaint.setColor(Color.WHITE);
    clipPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
    setLayerType(LAYER_TYPE_HARDWARE, null);

//    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
//      clipPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
//      setLayerType(LAYER_TYPE_SOFTWARE, clipPaint); //Only works for software layers
//    } else {
//      clipPaint.setXfermode(pdMode);
////      setLayerType(LAYER_TYPE_SOFTWARE, null); //Only works for software layers
//    }

    if (attrs != null) {
      final TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.SquircleImageView);

      if (attributes.hasValue(R.styleable.SquircleImageView_cornerRadius)) {
        final float cornerRadius = attributes.getDimension(R.styleable.SquircleImageView_cornerRadius, 0f);
        if (pathManager.getCornerRadius() == null && cornerRadius != -1) {
          setCornerRadius(cornerRadius);
        }
      }

      attributes.recycle();
    }
  }

  public void setCornerRadius(float cornerRadius) {
    pathManager.setCornerRadius(cornerRadius);
    requiresShapeUpdate();
  }

  @Override
  protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    super.onLayout(changed, left, top, right, bottom);
    if (changed) {
      requiresShapeUpdate();
    }
  }

  @Override
  protected void dispatchDraw(Canvas canvas) {
    super.dispatchDraw(canvas);

    if (requiresShapeUpdate) {
      calculateLayout(canvas.getWidth(), canvas.getHeight());
      requiresShapeUpdate = false;
    }
    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
      canvas.drawPath(clipPath, clipPaint);
    } else {
      canvas.drawPath(rectView, clipPaint);

//      Paint strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//      strokePaint.setStyle(Paint.Style.STROKE);
//      strokePaint.setStrokeWidth(4);
//      strokePaint.setColor(Color.WHITE);
//      canvas.drawPath(pathManager.getPath(), strokePaint);
    }

    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
      setLayerType(LAYER_TYPE_HARDWARE, null);
    }
  }

  private void calculateLayout(int width, int height) {
    rectView.reset();
    rectView.addRect(0f, 0f, 1f * getWidth(), 1f * getHeight(), Path.Direction.CW);

    if (pathManager != null) {
      if (width > 0 && height > 0) {
        pathManager.updateSize(width, height);
        clipPath.reset();
        clipPath.set(pathManager.getPath());

        //invert the path for android P
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
          final boolean success = rectView.op(clipPath, Path.Op.DIFFERENCE);
        }

        //this needs to be fixed for 25.4.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
          try {
            setOutlineProvider(getOutlineProvider());
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
    }

    postInvalidate();
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  @Override
  public ViewOutlineProvider getOutlineProvider() {
    return new ViewOutlineProvider() {
      @Override
      public void getOutline(View view, Outline outline) {
        if (!isInEditMode()) {
          final Path shadowConvexPath = pathManager.getPath();
          try {
            outline.setConvexPath(shadowConvexPath);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
    };
  }

  public void requiresShapeUpdate() {
    this.requiresShapeUpdate = true;
    postInvalidate();
  }
}
