package com.batache.fluidandroid.util

import android.view.View
import androidx.core.view.doOnLayout
import com.github.lcdsmao.springx.ViewPropertySpringAnimator

class SpringMorphAnimation(
  private val sourceView: View,
  private val destinationView: View,
  private val animationStiffness: Float,
  private val animationDampingRatio: Float,
  private val animateDestinationViewAlpha: Boolean = true
) {

  var isMorphed: Boolean = false
    private set

  private val sourceViewSpring by lazy {
    ViewPropertySpringAnimator(sourceView)
      .defaultStiffness(animationStiffness)
      .defaultDampingRatio(animationDampingRatio)
  }
  lateinit var sourceViewStates: ViewStates
    private set

  private val destinationViewSpring by lazy {
    ViewPropertySpringAnimator(destinationView)
      .defaultStiffness(animationStiffness)
      .defaultDampingRatio(animationDampingRatio)
  }
  lateinit var destinationViewStates: ViewStates
    private set

  init {
    init()
  }

  private fun init() {
    sourceView.doOnLayout {
      destinationView.doOnLayout {
        val sourceViewProperties = sourceView.properties
        val destinationViewProperties = destinationView.properties

        sourceViewStates = ViewStates(
          retracted = ViewState(
            translationX = 0f,
            translationY = 0f,
            scaleX = 1f,
            scaleY = 1f,
            alpha = 1f
          ),
          expanded = ViewState(
            translationX = destinationViewProperties.center.x - sourceViewProperties.center.x,
            translationY = destinationViewProperties.center.y - sourceViewProperties.center.y,
            scaleX = (destinationViewProperties.size.width.toDouble() / sourceViewProperties.size.width.toDouble()).toFloat(),
            scaleY = (destinationViewProperties.size.height.toDouble() / sourceViewProperties.size.height.toDouble()).toFloat(),
            alpha = 0f
          )
        )

        destinationViewStates = ViewStates(
          retracted = ViewState(
            translationX = sourceViewProperties.center.x - destinationViewProperties.center.x,
            translationY = sourceViewProperties.center.y - destinationViewProperties.center.y,
            scaleX = (sourceViewProperties.size.width.toDouble() / destinationViewProperties.size.width.toDouble()).toFloat(),
            scaleY = (sourceViewProperties.size.height.toDouble() / destinationViewProperties.size.height.toDouble()).toFloat(),
            alpha = 0f
          ),
          expanded = ViewState(
            translationX = 0f,
            translationY = 0f,
            scaleX = 1f,
            scaleY = 1f,
            alpha = 1f
          )
        )

        setDestinationViewRetractedState()
      }
    }
  }

  fun animateForward() {
    setDestinationViewRetractedState()
    destinationView.visibility = View.VISIBLE

    animateTo(
      sourceViewState = sourceViewStates.expanded,
      destinationViewState = destinationViewStates.expanded
    )

    isMorphed = true
  }

  fun animateBackward(startVelocity: Float? = null) {
    animateTo(
      sourceViewState = sourceViewStates.retracted,
      destinationViewState = destinationViewStates.retracted,
      startVelocity
    )

    isMorphed = false
  }

  private fun animateTo(
    sourceViewState: ViewState,
    destinationViewState: ViewState,
    yStartVelocity: Float? = null
  ) {
    sourceView.visibility = View.VISIBLE
    sourceViewSpring
      .translationX(sourceViewState.translationX)
      .translationY(sourceViewState.translationY) { yStartVelocity?.let { startVelocity = it } }
      .scaleX(sourceViewState.scaleX)
      .scaleY(sourceViewState.scaleY)
      .alpha(sourceViewState.alpha) {
        onEnd { spring, canceled, value, velocity ->
          if (value == 0f) {
            sourceView.visibility = View.GONE
          }
        }
      }
      .start()

    destinationView.visibility = View.VISIBLE
    destinationViewSpring
      .translationX(destinationViewState.translationX)
      .translationY(destinationViewState.translationY) { yStartVelocity?.let { startVelocity = it } }
      .scaleX(destinationViewState.scaleX)
      .scaleY(destinationViewState.scaleY)
      .apply {
        if (animateDestinationViewAlpha) {
          alpha(destinationViewState.alpha) {
            onEnd { spring, canceled, value, velocity ->
              if (value == 0f && !isMorphed) {
                destinationView.visibility = View.GONE
              }
            }
          }
        }
      }
      .start()
  }

  fun divertAnimation(translationX: Float, translationY: Float, scale: Float) {
    sourceViewSpring
      .translationX(sourceViewStates.expanded.translationX + translationX)
      .translationY(sourceViewStates.expanded.translationY + translationY)
      .scaleX(sourceViewStates.expanded.scaleX * scale)
      .scaleY(sourceViewStates.expanded.scaleY * scale)
      .start()

    destinationViewSpring
      .translationX(destinationViewStates.expanded.translationX + translationX)
      .translationY(destinationViewStates.expanded.translationY + translationY)
      .scaleX(destinationViewStates.expanded.scaleX * scale)
      .scaleY(destinationViewStates.expanded.scaleY * scale)
      .start()
  }

  fun animateSourceViewScale(scaleFactor: Float) {
    sourceViewSpring
      .scaleX(scaleFactor)
      .scaleY(scaleFactor)
      .start()
  }

  /**
   * All-use
   */

  private fun setDestinationViewRetractedState() {
    resetViewState(destinationView, destinationViewStates.retracted)
    destinationView.apply {
      if (animateDestinationViewAlpha) {
        visibility = View.GONE
        alpha = 0f
      } else {
        visibility = View.VISIBLE
        alpha = 1f
      }
    }
  }

  private fun resetViewState(view: View, viewState: ViewState) {
    view.apply {
      translationX = viewState.translationX
      translationY = viewState.translationY
      scaleX = viewState.scaleX
      scaleY = viewState.scaleY
    }
  }

  data class ViewState(
    val translationX: Float,
    val translationY: Float,
    val scaleX: Float,
    val scaleY: Float,
    val alpha: Float
  )

  data class ViewStates(
    val retracted: ViewState,
    val expanded: ViewState
  )
}