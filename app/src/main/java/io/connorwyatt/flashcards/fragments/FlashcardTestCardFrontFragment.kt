package io.connorwyatt.flashcards.fragments

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import io.connorwyatt.flashcards.R

class FlashcardTestCardFrontFragment : Fragment()
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

        val flipButton = viewGroup.findViewById(R.id.flashcard_test_card_flip_button) as ImageButton
        flipButton.setOnClickListener { parentFragment.flipCard() }
    }
}
