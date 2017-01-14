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
import io.connorwyatt.flashcards.data.entities.Flashcard

class FlashcardTestCardFrontFragment(private val flashcard: Flashcard) : Fragment()
{
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle?): View
    {
        super.onCreateView(inflater, container, savedInstanceState)

        val layout = inflater.inflate(
            R.layout.fragment_flashcard_test_card_front, container, false) as LinearLayout

        initialiseUI(layout)

        return layout
    }

    private fun initialiseUI(viewGroup: ViewGroup): Unit
    {
        val parentFragment = parentFragment as FlashcardTestCardFragment

        val titleTextView = viewGroup.findViewById(R.id.flashcard_test_card_title) as TextView

        titleTextView.text = flashcard.title

        val flipButton = viewGroup.findViewById(R.id.flashcard_test_card_flip_button) as ImageButton
        flipButton.setOnClickListener { parentFragment.flipCard() }
    }
}
