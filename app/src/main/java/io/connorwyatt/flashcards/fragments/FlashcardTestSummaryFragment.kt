package io.connorwyatt.flashcards.fragments

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.connorwyatt.flashcards.R

class FlashcardTestSummaryFragment : Fragment()
{
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle?): View
    {
        super.onCreateView(inflater, container, savedInstanceState)

        return inflater.inflate(
            R.layout.fragment_flashcard_test_summary_card, container, false) as ViewGroup
    }
}
