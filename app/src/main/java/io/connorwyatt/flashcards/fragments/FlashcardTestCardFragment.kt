package io.connorwyatt.flashcards.fragments

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.connorwyatt.flashcards.R

class FlashcardTestCardFragment : Fragment()
{
    private var isFlipped = false

    //region Fragment

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle?): View
    {
        super.onCreateView(inflater, container, savedInstanceState)

        val viewGroup = inflater.inflate(
            R.layout.fragment_flashcard_test_card, container, false) as ViewGroup

        initialiseUI()

        return viewGroup
    }

    //endregion

    //region UI

    fun flipCard()
    {
        if (!isFlipped)
        {
            childFragmentManager
                .beginTransaction()
                .setCustomAnimations(R.animator.card_flip_in, R.animator.card_flip_out)
                .replace(R.id.flashcard_test_card_frame, FlashcardTestCardBackFragment())
                .commit()

            isFlipped = true
        }
    }

    private fun initialiseUI(): Unit
    {
        initialiseFragment()
    }

    private fun initialiseFragment(): Unit
    {
        val fragment = if (!isFlipped)
            FlashcardTestCardFrontFragment()
        else
            FlashcardTestCardBackFragment()


        childFragmentManager
            .beginTransaction()
            .add(R.id.flashcard_test_card_frame, fragment)
            .commit()
    }

    //endregion

    companion object
    {
        object ArgumentKeys
        {
            val FLASHCARD_ID = "FLASHCARD_ID"
        }
    }
}
