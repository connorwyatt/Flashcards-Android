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

class FlashcardTestCardBackFragment : Fragment()
{
    private val buttons: MutableList<ImageButton> = mutableListOf()
    private var currentButton: ImageButton? = null
        set(value)
        {
            field?.let { it.imageAlpha = 100 }

            value?.imageAlpha = 255

            field = value
        }
    lateinit private var flashcardTestFragment: FlashcardTestFragment
    lateinit private var flashcard: Flashcard
    private var rating: Rating? = null

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

    override fun onDestroyView()
    {
        super.onDestroyView()

        rating?.let { flashcardTestFragment.rateFlashcard(flashcard, it).subscribe() }
    }

    //endregion

    //region UI

    private fun initialiseUI(viewGroup: ViewGroup): Unit
    {
        flashcardTestFragment = (activity as FlashcardTestActivity).flashcardTestFragment

        parentFragment as FlashcardTestCardFragment

        val flashcardId = parentFragment.arguments.getString(
            FlashcardTestCardFragment.Companion.ArgumentKeys.FLASHCARD_ID)

        flashcard = flashcardTestFragment.getFlashcardFromAdapter(flashcardId)

        val titleTextView = viewGroup.findViewById(R.id.flashcard_test_card_title) as TextView

        titleTextView.text = flashcard.title

        val textTextView = viewGroup.findViewById(R.id.flashcard_test_card_text) as TextView

        textTextView.text = flashcard.text

        initialiseButtons(viewGroup)
    }

    private fun initialiseButtons(viewGroup: ViewGroup): Unit
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
    }

    //endregion
}
