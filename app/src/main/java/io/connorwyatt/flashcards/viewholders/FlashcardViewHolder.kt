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
import io.connorwyatt.flashcards.enums.Rating
import io.connorwyatt.flashcards.views.progressbar.ProgressBar

class FlashcardViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView)
{
    private var viewModel: FlashcardViewModel? = null
    private val cv = itemView.findViewById(R.id.flashcard_card) as CardView
    private val layout = itemView.findViewById(R.id.flashcard_card_layout) as LinearLayout
    private val bodyLayout = itemView.findViewById(R.id.flashcard_card_body_layout) as LinearLayout
    private val progress = itemView.findViewById(R.id.flashcard_card_rating) as ProgressBar
    private val titleText = itemView.findViewById(R.id.flashcard_card_title) as TextView
    private val textText = itemView.findViewById(R.id.flashcard_card_text) as TextView
    private val categoriesText = itemView.findViewById(R.id.flashcard_card_categories) as TextView

    fun setData(viewModel: FlashcardViewModel): Unit
    {
        this.viewModel = viewModel
        val (flashcard, categories, rating) = viewModel

        val colorId = when (rating)
        {
            Rating.POSITIVE -> R.color.colorPositive
            Rating.NEGATIVE -> R.color.colorNegative
            Rating.NEUTRAL -> R.color.colorNeutral
            Rating.NOT_RATED -> R.color.colorGrey
            else -> R.color.colorGrey
        }

        val color = ContextCompat.getColor(layout.context, colorId)
        val backgroundColor = ColorUtils.setAlphaComponent(color, 128)

        progress.setBarColor(color)
        progress.setUnfilledBarColor(backgroundColor)

        val progressValue = if (rating?.value!! >= 0) rating?.value else 0.0

        progress.setProgress(progressValue)

        titleText.text = flashcard.title
        textText.text = flashcard.text

        if (categories.isNotEmpty())
        {
            categoriesText.text = categories.map { it.name }.joinToString(separator = ", ")
        }
        else
        {
            bodyLayout.removeView(categoriesText)
        }
    }

    fun setOnCardClickListener(listener: (FlashcardViewModel) -> Unit): Unit
    {
        cv.setOnClickListener { listener(viewModel!!) }
    }
}
