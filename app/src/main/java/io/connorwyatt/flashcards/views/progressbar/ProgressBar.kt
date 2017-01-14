/*
 * Copyright (c) 2016-2017 Connor Wyatt <connorwyatt1@gmail.com>.
 *
 * This file can not be copied and/or distributed without the express permission of Connor Wyatt.
 */

package io.connorwyatt.flashcards.views.progressbar

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import io.connorwyatt.flashcards.R

class ProgressBar : View {
  private var paint: Paint? = null
  private var progress: Double = 0.toDouble()
  private var barColor: Int = 0
  private var unfilledBarColor: Int = 0
  private var animator: ValueAnimator? = null

  constructor(context: Context) : super(context) {
    init(null)
  }

  constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    init(attrs)
  }

  constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) :
    super(context, attrs, defStyleAttr) {
    init(attrs)
  }

  constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) :
    super(context, attrs, defStyleAttr, defStyleRes) {
    init(attrs)
  }

  fun setBarColor(color: Int) {
    barColor = color
    invalidate()
  }

  /**
   * A method for setting the progress of the progress bar via a decimal between 0 and 1.
   * If a value greater than 1 or less than 0 is passed, it will be clamped.

   * @param progress A decimal value between 0 and 1 to set the progress to.
   */
  fun setProgress(progress: Double) {
    this.progress = clampProgress(progress, 0.0, 1.0)
    invalidate()
  }

  fun setUnfilledBarColor(color: Int) {
    unfilledBarColor = color
    invalidate()
  }

  override fun onDraw(canvas: Canvas) {
    val width = width
    val height = height

    val barWidth = (width * progress).toInt()

    paint!!.color = barColor
    canvas.drawRect(0f, 0f, barWidth.toFloat(), height.toFloat(), paint)

    paint!!.color = unfilledBarColor
    canvas.drawRect(barWidth.toFloat(), 0f, width.toFloat(), height.toFloat(), paint)

    val notchWidth = (height.toDouble() / 2.toDouble()).toInt()
    var notchPosition = barWidth - notchWidth

    if (notchPosition < 0) notchPosition = 0

    paint!!.color = Color.argb(25, 0, 0, 0)
    canvas.drawRect(
      notchPosition.toFloat(),
      0f,
      (notchPosition + notchWidth).toFloat(),
      height.toFloat(),
      paint
    )
  }

  fun setProgress(progress: Double, animate: Boolean) {
    if (!animate) {
      setProgress(progress)
    } else {
      val oldProgress = this.progress
      val targetProgress = clampProgress(progress, 0.0, 1.0)
      val difference = targetProgress - oldProgress

      animator?.cancel()

      animator = ValueAnimator.ofFloat(0f, 1f)

      animator!!.duration = 700

      animator!!.interpolator = AccelerateDecelerateInterpolator()

      animator!!.addUpdateListener { animation ->
        val animatedValue = (animation.animatedValue as Float).toDouble()
        setProgress(oldProgress + animatedValue * difference, false)
      }

      if (!animator!!.isStarted) animator!!.start()
    }
  }

  private fun clampProgress(progress: Double, min: Double, max: Double): Double {
    if (progress > max) {
      return max
    } else if (progress < min) {
      return min
    }

    return progress
  }

  private fun init(attributeSet: AttributeSet?) {
    paint = Paint()

    val typedArray = context.theme.obtainStyledAttributes(attributeSet,
                                                          R.styleable.ProgressBar, 0, 0)
    try {
      setBarColor(typedArray.getColor(R.styleable.ProgressBar_barColor, Color.BLUE))
      setUnfilledBarColor(typedArray.getColor(R.styleable.ProgressBar_unfilledBarColor,
                                              Color.LTGRAY))
      setProgress(typedArray.getFloat(R.styleable.ProgressBar_progress, 0f).toDouble())
    } finally {
      typedArray.recycle()
    }
  }
}
