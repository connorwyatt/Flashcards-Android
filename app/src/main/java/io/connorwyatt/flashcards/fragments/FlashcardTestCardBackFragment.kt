package io.connorwyatt.flashcards.fragments

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import io.connorwyatt.flashcards.R
import io.connorwyatt.flashcards.activities.FlashcardTestActivity

class FlashcardTestCardBackFragment : Fragment()
{
    //region Fragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle?): View
    {
        super.onCreateView(inflater, container, savedInstanceState)

        val viewGroup = inflater.inflate(
            R.layout.fragment_flashcard_test_card_back, container, false) as LinearLayout

        initialiseUI(viewGroup)

        return viewGroup
    }

    //endregion

    //region UI

    private fun initialiseUI(viewGroup: ViewGroup)
    {
        val parentFragment = parentFragment as FlashcardTestCardFragment

        val flashcardId = parentFragment.arguments.getString(
            FlashcardTestCardFragment.Companion.ArgumentKeys.FLASHCARD_ID)

        val flashcard = (activity as FlashcardTestActivity).flashcardTestFragment
            .getFlashcardFromAdapter(flashcardId)

        val titleTextView = viewGroup.findViewById(R.id.flashcard_test_card_title) as TextView

        titleTextView.text = flashcard.title

        val textTextView = viewGroup.findViewById(R.id.flashcard_test_card_text) as TextView

        textTextView.text = flashcard.text
    }

    //endregion
}
