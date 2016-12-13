package io.connorwyatt.flashcards.adapters

import android.support.v4.content.ContextCompat
import android.support.v4.graphics.ColorUtils
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import io.connorwyatt.flashcards.R
import io.connorwyatt.flashcards.data.entities.Flashcard
import io.connorwyatt.flashcards.data.services.FlashcardTestService
import io.connorwyatt.flashcards.views.progressbar.ProgressBar
import java.util.ArrayList

class FlashcardCardListAdapter :
    RecyclerView.Adapter<FlashcardCardListAdapter.FlashcardCardViewHolder>()
{
    private var flashcards: List<Flashcard>? = null
    private var viewFlashcards: List<Flashcard>? = null
    private var categoryFilter: Long? = null
    private var onCardClickListener: OnCardClickListener? = null

    fun setItems(flashcards: List<Flashcard>)
    {
        this.flashcards = flashcards
        updateViewFlashcards()
    }

    fun setOnCardClickListener(onCardClickListener: OnCardClickListener): Unit
    {
        this.onCardClickListener = onCardClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlashcardCardViewHolder
    {
        val inflatedView = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_flashcard_card, parent, false)
        return FlashcardCardViewHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: FlashcardCardViewHolder, position: Int)
    {
        val currentFlashcard = viewFlashcards!![position]

        onCardClickListener.let {
            holder.cv.setOnClickListener {
                this.onCardClickListener!!.onClick(viewFlashcards!![holder.adapterPosition])
            }
        }

        val context = holder.layout.context

        val fts = FlashcardTestService(context)
        val averageRating = fts.getAverageRatingForFlashcard(currentFlashcard.id!!)

        if (averageRating == null)
        {
            holder.rating.setProgress(0.0)
        }
        else
        {
            var color: Int? = null

            when
            {
                averageRating > 2.0 / 3.0 ->
                {
                    val colorId = R.color.colorPositive
                    color = ContextCompat.getColor(context, colorId)
                }
                averageRating < 1.0 / 3.0 ->
                {
                    val colorId = R.color.colorNegative
                    color = ContextCompat.getColor(context, colorId)
                }
                else                      ->
                {
                    val colorId = R.color.colorNeutral
                    color = ContextCompat.getColor(context, colorId)
                }
            }

            val backgroundColor = ColorUtils.setAlphaComponent(color, 128)

            holder.rating.setBarColor(color)
            holder.rating.setUnfilledBarColor(backgroundColor)
            holder.rating.setProgress(averageRating)
        }

        holder.title.text = currentFlashcard.title
        holder.text.text = currentFlashcard.text

        val categoriesString = currentFlashcard.categoriesString
        if (categoriesString.isNotEmpty())
        {
            holder.categories.text = categoriesString
        }
        else
        {
            holder.bodyLayout.removeView(holder.categories)
        }
    }

    override fun getItemCount(): Int
    {
        return viewFlashcards!!.size
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView?)
    {
        super.onAttachedToRecyclerView(recyclerView)
    }

    fun applyCategoryFilter(categoryId: Long)
    {
        categoryFilter = categoryId
        updateViewFlashcards()
    }

    fun removeFilter()
    {
        categoryFilter = null
        updateViewFlashcards()
    }

    private fun updateViewFlashcards()
    {
        viewFlashcards = applyFilters(flashcards!!)

        notifyDataSetChanged()
    }

    private fun applyFilters(flashcards: List<Flashcard>): List<Flashcard>
    {
        var filteredList: List<Flashcard> = ArrayList(flashcards)

        categoryFilter.let {
            filteredList = filteredList.filter {
                it.categories.count { it.id === categoryFilter } > 0
            }
        }

        return filteredList
    }

    interface OnCardClickListener
    {
        fun onClick(flashcard: Flashcard)
    }

    class FlashcardCardViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView)
    {
        internal val cv = itemView.findViewById(R.id.flashcard_card) as CardView
        internal val layout = itemView.findViewById(R.id.flashcard_card_layout) as LinearLayout
        internal val bodyLayout = itemView.findViewById(R.id.flashcard_card_body_layout) as LinearLayout
        internal val rating = itemView.findViewById(R.id.flashcard_card_rating) as ProgressBar
        internal val title = itemView.findViewById(R.id.flashcard_card_title) as TextView
        internal val text = itemView.findViewById(R.id.flashcard_card_text) as TextView
        internal val categories = itemView.findViewById(R.id.flashcard_card_categories) as TextView
    }
}