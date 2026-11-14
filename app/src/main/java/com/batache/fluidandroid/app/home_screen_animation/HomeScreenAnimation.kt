package com.batache.fluidandroid.app.home_screen_animation

import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.doOnLayout
import androidx.core.view.updateLayoutParams
import com.batache.fluidandroid.*
import com.batache.fluidandroid.util.cornerRadius
import com.batache.fluidandroid.util.squircle.SquircleImageView
import com.github.lcdsmao.springx.ViewPropertySpringAnimator
import kotlin.math.exp

class HomeScreenAnimation(
  private val iconView: SquircleImageView,
  private val appView: SquircleImageView,
  private val containerView: View,
  private val debugTextView: TextView? = null,
  private val animationStiffness: Float,
  private val animationDampingRatio: Float,
) {

  var currentAnimationState: AnimationState? = null
    private set

  private val iconViewSpring by lazy {
    ViewPropertySpringAnimator(iconView)
      .defaultStiffness(animationStiffness)
      .defaultDampingRatio(animationDampingRatio)
  }
  private val appViewSpring by lazy {
    ViewPropertySpringAnimator(appView)
      .defaultStiffness(animationStiffness)
      .defaultDampingRatio(animationDampingRatio)
  }
  private lateinit var viewStates: ViewStates

  private val currentViewState: ViewState
    get() {
      return when (currentAnimationState) {
        AnimationState.RETRACTED -> viewStates.retracted
        AnimationState.EXPANDED -> viewStates.expanded
        AnimationState.SWITCHER_PEEKING -> viewStates.switcherPeeking
        AnimationState.SWITCHER_RESTING -> viewStates.switcherResting

        // Bug!
        else -> viewStates.retracted
      }
    }

  init {
    init()
  }

  private fun init() {
    iconView.doOnLayout {
      viewStates = ViewStates(
        retracted = ViewState(
          x = iconView.x,
          y = iconView.y,
          width = iconView.measuredWidth.f,
          height = iconView.measuredHeight.f,
          alphaIcon = 1f,
          cornerRadius = 10f,
          animationState = AnimationState.RETRACTED
        ),
        expanded = ViewState(
          x = 0f,
          y = 0f,
          width = containerView.measuredWidth.f,
          height = containerView.measuredHeight.f,
          alphaIcon = 0f,
          cornerRadius = 38.5f,
          animationState = AnimationState.EXPANDED
        ),
        switcherPeeking = ViewState(
          x = containerView.measuredWidth.f * -1,
          y = 0f,
          width = containerView.measuredWidth.f,
          height = containerView.measuredHeight.f,
          alphaIcon = 0f,
          cornerRadius = 38.5f * 0.85f,
          animationState = AnimationState.SWITCHER_PEEKING
        ),
        switcherResting = ViewState(
          x = containerView.measuredWidth.f * 0.3f / 2,
          y = containerView.measuredHeight.f * 0.3f / 2,
          width = containerView.measuredWidth.f * 0.7f,
          height = containerView.measuredHeight.f * 0.7f,
          alphaIcon = 0f,
          cornerRadius = 38.5f * 0.7f,
          animationState = AnimationState.SWITCHER_RESTING
        )
      )

      resetViewState(iconView, viewStates.retracted)
      resetViewState(appView, viewStates.retracted)
    }
  }

  fun animateForward() = animateTo(viewState = viewStates.expanded)

  fun animateBackward(velocityY: Float? = null) = animateTo(viewState = viewStates.retracted, velocityY = velocityY)

  fun prepareSwitcherPeeking() {
    resetViewState(iconView, viewStates.switcherPeeking)
    resetViewState(appView, viewStates.switcherPeeking)
  }

  fun animateSwitcherResting() = animateTo(viewState = viewStates.switcherResting)

  private fun animateTo(viewState: ViewState, velocityY: Float? = null) {
    val isLaunching =
      currentAnimationState == AnimationState.RETRACTED && viewState.animationState == AnimationState.EXPANDED
    val isRetracting =
      currentAnimationState == AnimationState.EXPANDED && viewState.animationState == AnimationState.RETRACTED

    val launchingYVelocity = -3000f

    val heightStiffnessLaunching = .8f
    val heightStiffnessScale =
      if (isLaunching) heightStiffnessLaunching else if (isRetracting) 1f / heightStiffnessLaunching else 1f

    val stiff = if (viewState == viewStates.switcherResting) 150f else animationStiffness

    iconViewSpring
      .defaultStiffness(stiff)
      .x(viewState.x)
      .y(viewState.y) {
        val stiffnessFactor =
          (if (velocityY != null) mapValues(0f, 1f, -30000f, .6f, velocityY) else 1f)
            .coerceAtLeast(.6f)
            .coerceAtMost(1f)
        if (isLaunching)
          startVelocity = launchingYVelocity
        else
          stiffness = animationStiffness * stiffnessFactor
      }//else startVelocity = -10_000f }
      .width(viewState.width)
      .height(viewState.height) { stiffness = stiff * heightStiffnessScale }
      .alpha(viewState.alphaIcon) { stiffness = if (isLaunching) 800f else animationStiffness }
//      .apply {
//        animateProperty(
//          viewState.alphaIcon,
//          { iconView.alpha = mapValues(.9f, 1f, .75f, 0f, it).coerceAtLeast(0f).coerceAtMost(1f) },
//          { iconView.alpha }
//        )
////        if (viewState.animationState != AnimationState.RETRACTED) {
////          animateProperty(
////            viewState.alphaIcon,
////            { iconView.alpha = mapValues(.9f, 1f, .75f, 0f, it) },
////            { iconView.alpha }
////          )
////        } else {
////          alpha(viewState.alphaIcon) { stiffness = 800f }
////        }
//      }
      .cornerRadius(viewState.cornerRadius)
      .start()

    appViewSpring
      .defaultStiffness(stiff)
      .x(viewState.x)
      .y(viewState.y) {
        val stiffnessFactor =
          (if (velocityY != null) mapValues(0f, 1f, -30000f, .6f, velocityY) else 1f)
            .coerceAtLeast(.6f)
            .coerceAtMost(1f)
        if (isLaunching)
          startVelocity = launchingYVelocity
        else
          stiffness = animationStiffness * stiffnessFactor
      }//else startVelocity = -10_000f }
      .width(viewState.width)
      .height(viewState.height) { stiffness = stiff * heightStiffnessScale }
      .cornerRadius(viewState.cornerRadius)
      .start()

    currentAnimationState = viewState.animationState
  }

  var skipOverview: Boolean = false
  fun animateDrag(offsetX: Float, offsetY: Float, opt: Float? = null) {
    /**
     * When the app is in overview, it already has a vertical offset.
     * This pre-existing offset needs to be added to our offsetY in order for it not to be overridden.
     */
    val correctOffsetY = offsetY - (containerView.measuredHeight - currentViewState.height) / 2f

    // The app cannot be made any smaller than this maxScale factor.
    val maxScaleFactor = .3f

    // The top and bottom gap sizes are determined by this ratio.
    val scaleGapRatio = Pair(.4f, 1f)
    // The scaleFactor is just the sum of the top and bottom gap sizes.
    val scaleFactor = scaleGapRatio.toList().sum()

    // The minimum vertical offset needed to reach the maxScaleFactor.
    val maxScaleOffsetY =
      (containerView.measuredHeight * (maxScaleFactor - 1.3f)) / scaleFactor // -1f
    val scale = getScale(containerView, correctOffsetY, scaleFactor, maxScaleFactor)

    val x = (containerView.measuredWidth * (1 - scale)) / 2f + offsetX
    val y = (containerView.measuredHeight * (1 - scale)) / scaleFactor * scaleGapRatio.first +
        ((correctOffsetY - maxScaleOffsetY).coerceAtMost(0f)).powMaintainSign(.8f)
    val width = viewStates.expanded.width * scale
    val height = viewStates.expanded.height * scale
    val cornerRadius = viewStates.expanded.cornerRadius * scale

    skipOverview = scale < .5f

    var force = 5000f //SpringForce.STIFFNESS_MEDIUM
    opt?.let {
      force = opt
    }

    iconViewSpring
      .defaultStiffness(force)
      .x(x)
      .y(y) { stiffness = force }
      .width(width).height(height)
      .cornerRadius(cornerRadius)
      .start()
    appViewSpring
      .defaultStiffness(force)
      .x(x)
      .y(y) { stiffness = force }
      .width(width).height(height)
      .cornerRadius(cornerRadius)
      .start()
  }

  private fun getScale(
    view: View,
    offsetY: Float,
    scaleFactor: Float,
    maxScaleFactor: Float
  ): Float {
//    val scale = (1 + (offsetY * scaleFactor) / view.measuredHeight)
//
//    val preMaxScaleFactor = maxScaleFactor + .25f
//    val realMaxScaleFactor = maxScaleFactor
//    if (scale <= preMaxScaleFactor) {
//      return mapValues(.01f, realMaxScaleFactor, preMaxScaleFactor, preMaxScaleFactor, scale).coerceAtLeast(realMaxScaleFactor)
//    }
//    return scale

//    val scale = (1 + (offsetY * scaleFactor) / view.measuredHeight) * 10 + 1.14f
//    return (exp(.19f * scale) + 1.7f) / 10f

//    val scale = (1 + (offsetY * scaleFactor) / view.measuredHeight) * 10 + 4.7f
//    return (exp(.15f * scale) + 1f) / 10f

    val x = (1 + (offsetY * scaleFactor) / view.measuredHeight) * 10 + 9.2f
    val y = exp(.12f * x)
    val scale = (y / 10f).coerceAtLeast(maxScaleFactor)
    return scale
  }

  fun animatePeeking(offsetX: Float, offsetY: Float) {
    val translationX = offsetX.coerceAtLeast(0f)
    val scale = 0.9f + offsetY.powMaintainSign(0.88f) / containerView.measuredHeight

    val x = translationX + -containerView.measuredWidth * scale + 40.dp
    val y = containerView.measuredHeight * (1 - scale) / 2
    val width = viewStates.expanded.width * scale
    val height = viewStates.expanded.height * scale
    val cornerRadius = viewStates.expanded.cornerRadius * scale

    iconViewSpring.x(x).y(y).width(width).height(height).cornerRadius(cornerRadius).start()
    appViewSpring.x(x).y(y).width(width).height(height).cornerRadius(cornerRadius).start()
  }

  /**
   * All-use
   */

  private fun resetViewState(view: SquircleImageView, viewState: ViewState) {
    view.apply {
      x = viewState.x
      y = viewState.y

      updateLayoutParams<ViewGroup.LayoutParams> {
        width = viewState.width.toInt()
        height = viewState.height.toInt()
      }

      if (view == iconView) {
        alpha = viewState.alphaIcon
      }
      cornerRadius = viewState.cornerRadius
    }

    runIn(1) {
      currentAnimationState = viewState.animationState
    }
  }

  data class ViewState(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val alphaIcon: Float,
    val cornerRadius: Float,
    val animationState: AnimationState
  )

  data class ViewStates(
    val retracted: ViewState,
    val expanded: ViewState,
    val switcherPeeking: ViewState,
    val switcherResting: ViewState
  )

  enum class AnimationState {
    RETRACTED, EXPANDED, SWITCHER_PEEKING, SWITCHER_RESTING
  }
}
