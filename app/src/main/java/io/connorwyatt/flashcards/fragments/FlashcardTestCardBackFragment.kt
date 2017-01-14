/*
 * Copyright (c) 2016-2017 Connor Wyatt <connorwyatt1@gmail.com>.
 *
 * This file can not be copied and/or distributed without the express permission of Connor Wyatt.
 */

package io.connorwyatt.flashcards.fragments

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import io.connorwyatt.flashcards.R
import io.connorwyatt.flashcards.activities.FlashcardTestActivity
import io.connorwyatt.flashcards.data.entities.Flashcard
import io.connorwyatt.flashcards.enums.Rating

class FlashcardTestCardBackFragment(
    private val flashcard: Flashcard,
    initialRating: Rating?,
    private val onRatingChangeListener: (Rating?) -> Unit)
    : Fragment()
{
    lateinit private var viewGroup: ViewGroup
    private val buttons: MutableList<ImageButton> = mutableListOf()
    private var currentButton: ImageButton? = null
        set(value)
        {
            field?.let { it.imageAlpha = 100 }

            value?.imageAlpha = 255

            field = value
        }
    lateinit private var flashcardTestFragment: FlashcardTestFragment
    private var rating: Rating? = null
        set(value)
        {
            field = value

            onRatingChangeListener(field)
        }

    init
    {
        rating = initialRating
    }

    //region Fragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle?): View
    {
        super.onCreateView(inflater, container, savedInstanceState)

        viewGroup = inflater.inflate(
            R.layout.fragment_flashcard_test_card_back, container, false) as LinearLayout

        initialiseUI()

        return viewGroup
    }

    //endregion

    //region UI

    private fun initialiseUI(): Unit
    {
        flashcardTestFragment = (activity as FlashcardTestActivity).flashcardTestFragment!!

        parentFragment as FlashcardTestCardFragment

        val titleTextView = viewGroup.findViewById(R.id.flashcard_test_card_title) as TextView

        titleTextView.text = flashcard.title

        val textTextView = viewGroup.findViewById(R.id.flashcard_test_card_text) as TextView

        textTextView.text = flashcard.text

        initialiseButtons()
    }

    private fun initialiseButtons(): Unit
    {
        buttons.add(viewGroup.findViewById(R.id.flashcard_test_card_negative_button) as ImageButton)
        buttons.add(viewGroup.findViewById(R.id.flashcard_test_card_neutral_button) as ImageButton)
        buttons.add(viewGroup.findViewById(R.id.flashcard_test_card_positive_button) as ImageButton)

        buttons.forEach {
            it.imageAlpha = 100
            it.setOnClickListener {
                it as ImageButton

                currentButton = it

                rating = when (it.id)
                {
                    R.id.flashcard_test_card_positive_button -> Rating.POSITIVE
                    R.id.flashcard_test_card_neutral_button -> Rating.NEUTRAL
                    R.id.flashcard_test_card_negative_button -> Rating.NEGATIVE
                    else -> null
                }
            }
        }

        rating?.let { setCurrentButtonFromRating(it) }
    }

    private fun setCurrentButtonFromRating(rating: Rating): Unit
    {
        currentButton = when (rating)
        {
            Rating.POSITIVE -> viewGroup.findViewById(R.id.flashcard_test_card_positive_button) as ImageButton
            Rating.NEUTRAL -> viewGroup.findViewById(R.id.flashcard_test_card_neutral_button) as ImageButton
            Rating.NEGATIVE -> viewGroup.findViewById(R.id.flashcard_test_card_negative_button) as ImageButton
            else -> null
        }
    }

    //endregion
}
