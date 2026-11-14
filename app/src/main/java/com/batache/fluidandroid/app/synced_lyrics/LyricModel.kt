package com.batache.fluidandroid.app.synced_lyrics

import android.view.View
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.batache.fluidandroid.R
import com.github.lcdsmao.springx.ViewPropertySpringAnimator

@EpoxyModelClass(layout = R.layout.model_lyric)
abstract class LyricModel : EpoxyModelWithHolder<LyricModel.LyricHolder>() {

  private lateinit var spring: ViewPropertySpringAnimator<TextView>

  @EpoxyAttribute
  var lyric: String? = null

  @EpoxyAttribute
  var animStiffness: Float? = null

  @EpoxyAttribute
  var animDampRatio: Float? = null

  @EpoxyAttribute
  var selected: Boolean? = null

  @EpoxyAttribute
  var animate: Boolean? = null

  @EpoxyAttribute
  lateinit var onClickListener: View.OnClickListener

  override fun bind(holder: LyricHolder) {
    super.bind(holder)

    if (animStiffness != null && animDampRatio != null) {
      spring = ViewPropertySpringAnimator(holder.itemView)
        .defaultStiffness(animStiffness!!)
        .defaultDampingRatio(animDampRatio!!)
    }

    holder.itemView.text = lyric
    holder.itemView.setOnClickListener(onClickListener)

    selected?.let { selected ->
      val destScale = if (selected) 1.1f else 1f
      val destAlpha = if (selected) 1f else .6f

      animate?.let { animate ->
        if (animate) {
          spring
            .scaleX(destScale)
            .scaleY(destScale)
            .alpha(destAlpha)
            .start()
        } else {
          holder.itemView.apply {
            scaleX = destScale
            scaleY = destScale
            alpha = destAlpha
          }
        }
      }
    }
  }

  override fun unbind(holder: LyricHolder) {
    super.unbind(holder)

    holder.itemView.setOnClickListener(null)
  }

  class LyricHolder : EpoxyHolder() {
    lateinit var itemView: TextView

    override fun bindView(itemView: View) {
      this.itemView = itemView as TextView
    }
  }
}