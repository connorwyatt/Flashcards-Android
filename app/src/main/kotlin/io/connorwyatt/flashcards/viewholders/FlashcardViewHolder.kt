/*
 * Copyright (c) 2016-2017 Connor Wyatt <connorwyatt1@gmail.com>.
 *
 * This file can not be copied and/or distributed without the express permission of Connor Wyatt.
 */

package io.connorwyatt.flashcards.viewholders

import android.support.v4.content.ContextCompat
import android.support.v4.graphics.ColorUtils
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import io.connorwyatt.flashcards.R
import io.connorwyatt.flashcards.data.viewmodels.FlashcardViewModel
import io.connorwyatt.flashcards.views.progressbar.ProgressBar

class FlashcardViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
  private var viewModel: FlashcardViewModel? = null
  private val cv = itemView.findViewById(R.id.flashcard_card) as CardView
  private val layout = itemView.findViewById(R.id.flashcard_card_layout) as LinearLayout
  private val bodyLayout = itemView.findViewById(R.id.flashcard_card_body_layout) as LinearLayout
  private val progress = itemView.findViewById(R.id.flashcard_card_rating) as ProgressBar
  private val titleText = itemView.findViewById(R.id.flashcard_card_title) as TextView
  private val textText = itemView.findViewById(R.id.flashcard_card_text) as TextView
  private val tagsText = itemView.findViewById(R.id.flashcard_card_tags) as TextView

  fun setData(viewModel: FlashcardViewModel): Unit {
    this.viewModel = viewModel
    val (flashcard, tags, rating) = viewModel

    val colorId = rating?.let {
      if (rating >= 0) {
        when {
          rating > 2.0 / 3.0 -> R.color.colorPositive
          rating < 1.0 / 2.0 -> R.color.colorNegative
          else -> R.color.colorNeutral
        }
      } else null
    } ?: R.color.colorGrey

    val color = ContextCompat.getColor(layout.context, colorId)
    val backgroundColor = ColorUtils.setAlphaComponent(color, 128)

    progress.setBarColor(color)
    progress.setUnfilledBarColor(backgroundColor)

    val progressValue = rating?.let {
      if (it >= 0) it else 0.0
    } ?: 0.0

    progress.setProgress(progressValue)

    titleText.text = flashcard.title
    textText.text = flashcard.text

    if (tags.isNotEmpty()) {
      tagsText.text = tags.map { it.name }.joinToString(separator = ", ")
    } else {
      bodyLayout.removeView(tagsText)
    }
  }

  fun setOnCardClickListener(listener: (FlashcardViewModel) -> Unit): Unit {
    cv.setOnClickListener { listener(viewModel!!) }
  }
}
