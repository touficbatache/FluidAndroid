package com.batache.fluidandroid.app.home_screen_animation

import android.view.View
import androidx.core.view.doOnLayout
import com.batache.fluidandroid.dp
import com.batache.fluidandroid.f
import com.batache.fluidandroid.mapValues
import com.batache.fluidandroid.powMaintainSign
import com.batache.fluidandroid.util.properties
import com.batache.fluidandroid.util.size
import com.github.lcdsmao.springx.ViewPropertySpringAnimator
import com.google.android.material.imageview.ShapeableImageView

class HomeScreenAnimationNew(
  private val iconView: ShapeableImageView,
  private val appView: ShapeableImageView,
  private val containerView: View,
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
  private lateinit var iconViewStates: ViewStates

  private val appViewSpring by lazy {
    ViewPropertySpringAnimator(appView)
      .defaultStiffness(animationStiffness)
      .defaultDampingRatio(animationDampingRatio)
  }
  private lateinit var appViewStates: ViewStates

  init {
    init()
  }

  private fun init() {
    iconView.doOnLayout {
      appView.doOnLayout {
        val sourceViewProperties = iconView.properties
        val destinationViewProperties = appView.properties

        iconViewStates = ViewStates(
          retracted = ViewState(
            x = sourceViewProperties.coordinates.x,
            y = sourceViewProperties.coordinates.y,
            scaleX = 44.75f.dp / containerView.size.width.f,
            scaleY = 44.75f.dp / containerView.size.height.f,
            alpha = 1f,
            cornerRadius = 10f,
            animationState = AnimationState.RETRACTED
          ),
          expanded = ViewState(
            x = 0f,
            y = 0f,
            scaleX = 1f,
            scaleY = 1f,
            alpha = 0f,
            cornerRadius = 38.5f,
            animationState = AnimationState.EXPANDED
          ),
          switcherPeeking = ViewState(
            x = containerView.size.width.f * -1,
            y = 0f,
            scaleX = 1f,
            scaleY = 1f,
            alpha = 0f,
            cornerRadius = 38.5f * 0.85f,
            animationState = AnimationState.SWITCHER_PEEKING
          ),
          switcherResting = ViewState(
            x = 0f,
            y = 0f,
            scaleX = 1f * 0.7f,
            scaleY = 1f * 0.7f,
            alpha = 0f,
            cornerRadius = 38.5f * 0.7f,
            animationState = AnimationState.SWITCHER_RESTING
          )
        )

        appViewStates = ViewStates(
          retracted = ViewState(
            x = destinationViewProperties.coordinates.x,
            y = destinationViewProperties.coordinates.y,
            scaleX = 44.75f.dp / containerView.size.width.f,
            scaleY = 44.75f.dp / containerView.size.height.f,
            alpha = 1f,
            cornerRadius = 10f,
            animationState = AnimationState.RETRACTED
          ),
          expanded = ViewState(
            x = 0f,
            y = 0f,
            scaleX = 1f,
            scaleY = 1f,
            alpha = 1f,
            cornerRadius = 38.5f,
            animationState = AnimationState.EXPANDED
          ),
          switcherPeeking = ViewState(
            x = containerView.size.width.f * -1,
            y = 0f,
            scaleX = 1f,
            scaleY = 1f,
            alpha = 1f,
            cornerRadius = 38.5f * 0.85f,
            animationState = AnimationState.SWITCHER_PEEKING
          ),
          switcherResting = ViewState(
            x = 0f,
            y = 0f,
            scaleX = 1f * 0.7f,
            scaleY = 1f * 0.7f,
            alpha = 1f,
            cornerRadius = 38.5f * 0.7f,
            animationState = AnimationState.SWITCHER_RESTING
          )
        )

        setViewsStateRetracted()
      }
    }
  }

  fun animateForward(isLaunching: Boolean = false) {
    if (isLaunching) iconView.visibility = View.VISIBLE

//    setViewsStateRetracted()

    animateTo(
      iconViewState = iconViewStates.expanded,
      appViewState = appViewStates.expanded,
      isLaunchForce = isLaunching
    )
  }

  fun animateBackward(startVelocity: Float? = null) {
    animateTo(
      iconViewState = iconViewStates.retracted,
      appViewState = appViewStates.retracted,
      isRetractForce = true
    )
  }


  private fun animateTo(
    iconViewState: ViewState,
    appViewState: ViewState,
    isLaunchForce: Boolean = false,
    isRetractForce: Boolean = false
  ) {
    iconViewSpring.cancel()
    appViewSpring.cancel()

    val yStartVelocity = if (isLaunchForce) -3000f else null

    iconView.visibility = View.VISIBLE

    iconViewSpring
      .x(iconViewState.x)
      .y(iconViewState.y) { if (isLaunchForce) yStartVelocity?.let { startVelocity = it } }
      .scaleX(iconViewState.scaleX)
      .scaleY(iconViewState.scaleY) {
        if (isLaunchForce) stiffness = animationStiffness * 0.8f
        if (isRetractForce) stiffness = animationStiffness * 1.3f
      }
      .animateProperty(
        containerView.measuredWidth.toFloat() / 2,
        { pivotX = it },
        { pivotX }
      )
      .animateProperty(
        containerView.measuredHeight.toFloat() / 2,
        { pivotY = it },
        { pivotY }
      )
      .apply {
        if (currentAnimationState == AnimationState.RETRACTED) {
          animateProperty(
            iconViewState.alpha,
            { iconView.alpha = 1f - mapValues(.1f, 0f, .25f, 1f, 1f - it) },
            { iconView.alpha }
          ) {
            onEnd { spring, canceled, value, velocity ->
              if (value == 0f) {
                iconView.visibility = View.GONE
              }
            }
          }
        } else {
          alpha(iconViewState.alpha) { stiffness = 800f }
        }
      }
//      .cornerRadius(iconViewState.cornerRadius)
      .start()

    appViewSpring
      .x(appViewState.x)
      .y(appViewState.y) { if (isLaunchForce) yStartVelocity?.let { startVelocity = it } }
      .scaleX(appViewState.scaleX)
      .scaleY(appViewState.scaleY) {
        if (isLaunchForce) stiffness = animationStiffness * 0.8f
        if (isRetractForce) stiffness = animationStiffness * 1.3f
      }
      .animateProperty(
        containerView.measuredWidth.toFloat() / 2,
        { pivotX = it },
        { pivotX }
      )
      .animateProperty(
        containerView.measuredHeight.toFloat() / 2,
        { pivotY = it },
        { pivotY }
      )
//      .cornerRadius(appViewState.cornerRadius)
      .start()

    currentAnimationState = iconViewState.animationState
  }

  /**
   * All-use
   */

  private fun setViewsStateRetracted() {
    resetViewState(iconView, iconViewStates.retracted)
    resetViewState(appView, appViewStates.retracted)
  }

  fun setViewsStateSwitcherPeeking() {
    resetViewState(iconView, iconViewStates.switcherPeeking)
    resetViewState(appView, appViewStates.switcherPeeking)
  }

  fun setViewsStateSwitcherResting() {
    animateTo(
      iconViewState = iconViewStates.switcherResting,
      appViewState = appViewStates.switcherResting,
      isLaunchForce = false
    )
  }

  private fun resetViewState(view: ShapeableImageView, viewState: ViewState) {
    view.apply {
      x = viewState.x
      y = viewState.y

      scaleX = viewState.scaleX
      scaleY = viewState.scaleY

      alpha = viewState.alpha
//      cornerRadius = viewState.cornerRadius
    }

    currentAnimationState = viewState.animationState
  }

  var skipOverview: Boolean = false
  fun animateDrag(offsetX: Float, offsetY: Float) {
    val translationX = offsetX.powMaintainSign(0.9f)
    val maxScale = 0.3f
    val offsetYAtMaxScale = containerView.measuredHeight * (1 - maxScale) / 2 * -1
    val translationY = ((offsetY - offsetYAtMaxScale).coerceAtMost(0f)).powMaintainSign(0.88f)
    val scale = (1 + (offsetY * 2) / containerView.measuredHeight).coerceAtLeast(maxScale)

    skipOverview = scale < 0.5f

    iconView.pivotX = containerView.measuredWidth.toFloat() / 2
    iconView.pivotY = containerView.measuredHeight.toFloat() / 2
    appView.pivotX = containerView.measuredWidth.toFloat() / 2
    appView.pivotY = containerView.measuredHeight.toFloat() / 2

    iconViewSpring
      .x(translationX) //iconView.getTag(R.id.X) as Float + translationX
      .y(translationY)
      .scaleX(scale)
      .scaleY(scale)
      .start()

    appViewSpring
      .x(translationX)
      .y(translationY)
      .scaleX(scale)
      .scaleY(scale)
      .start()
  }

  fun animateDragPeeking(offsetX: Float, offsetY: Float) {
    val translationX = offsetX.powMaintainSign(0.9f).coerceAtLeast(0f)
    val scale = 0.9f + offsetY.powMaintainSign(0.88f) / containerView.measuredHeight

    iconView.pivotX = containerView.measuredWidth.toFloat()
    iconView.pivotY = containerView.measuredHeight.toFloat() / 2
    appView.pivotX = containerView.measuredWidth.toFloat()
    appView.pivotY = containerView.measuredHeight.toFloat() / 2

    iconViewSpring
      .x(translationX - containerView.measuredWidth + 40.dp) //iconView.getTag(R.id.X) as Float + translationX
      .scaleX(scale)
      .scaleY(scale)
      .start()

    appViewSpring
      .x(translationX - containerView.measuredWidth + 40.dp)
      .scaleX(scale)
      .scaleY(scale)
      .start()
  }

  data class ViewState(
    val x: Float,
    val y: Float,
    val scaleX: Float,
    val scaleY: Float,
    val alpha: Float,
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
