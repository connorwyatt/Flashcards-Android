package io.connorwyatt.flashcards.fragments

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import io.connorwyatt.flashcards.R

class FlashcardTestCardBackFragment : Fragment()
{
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle?): View
    {
        super.onCreateView(inflater, container, savedInstanceState)

        return inflater.inflate(
            R.layout.fragment_flashcard_test_card_back, container, false) as LinearLayout
    }
}
