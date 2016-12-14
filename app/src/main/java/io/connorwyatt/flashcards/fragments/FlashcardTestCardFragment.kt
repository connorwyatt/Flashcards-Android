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
import io.connorwyatt.flashcards.data.entities.FlashcardTest
import io.connorwyatt.flashcards.data.services.FlashcardTestService
import java.util.ArrayList

class FlashcardTestCardFragment : Fragment()
{
    private val flashcardTest = FlashcardTest()
    private var isFlipped = false

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup,
                              savedInstanceState: Bundle?): View
    {
        super.onCreateView(inflater, container, savedInstanceState)

        val viewGroup = inflater.inflate(R.layout.fragment_flashcard_test_card,
                                         container, false) as ViewGroup

        val cardFragment = if (!isFlipped) CardFrontFragment() else CardBackFragment()

        childFragmentManager
            .beginTransaction()
            .add(R.id.flashcard_test_card_frame, cardFragment)
            .commit()

        return viewGroup
    }

    private fun flipCard()
    {
        if (!isFlipped)
        {
            childFragmentManager
                .beginTransaction()
                .setCustomAnimations(R.animator.card_flip_in,
                                     R.animator.card_flip_out)
                .replace(R.id.flashcard_test_card_frame, CardBackFragment())
                .commit()

            isFlipped = !isFlipped
        }
    }

    private fun saveFlashcardTest()
    {
        val flashcardTestService = FlashcardTestService(activity)
        flashcardTestService.save(flashcardTest)

        val flashcardTestFragment = (activity as FlashcardTestActivity).flashcardTestFragment

        flashcardTestFragment!!.updateFlashcardTest(flashcardTest)
    }

    object ARGUMENT_KEYS
    {
        var ID = "ID"
        var TITLE = "TITLE"
        var TEXT = "TEXT"
    }

    class CardFrontFragment : Fragment()
    {
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup,
                                  savedInstanceState: Bundle?): View
        {
            super.onCreateView(inflater, container, savedInstanceState)

            val layout = inflater.inflate(
                R.layout.fragment_flashcard_test_card_front, container, false) as LinearLayout

            val parentFragment = parentFragment as FlashcardTestCardFragment

            val arguments = parentFragment.arguments
            val titleText = arguments.getString(FlashcardTestCardFragment.ARGUMENT_KEYS.TITLE)

            val title = layout.findViewById(R.id.flashcard_test_card_title) as TextView
            title.text = titleText

            val flipButton = layout.findViewById(R.id
                                                     .flashcard_test_card_flip_button) as ImageButton
            flipButton.setOnClickListener { parentFragment.flipCard() }

            return layout
        }
    }

    class CardBackFragment : Fragment()
    {
        private var testCardFragment: FlashcardTestCardFragment? = null
        private val buttons = ArrayList<ImageButton>()
        private var currentlySelectedButton: ImageButton? = null
        private var layout: LinearLayout? = null

        private fun setButtonClickHandlers()
        {
            for (currentButton in buttons)
            {
                currentButton.imageAlpha = 100
                currentButton.setOnClickListener { onButtonClick(currentButton) }
            }
        }

        private fun onButtonClick(button: ImageButton)
        {
            val buttonId = button.id

            when (buttonId)
            {
                R.id.flashcard_test_card_positive_button -> testCardFragment!!.flashcardTest.setRatingPositive()
                R.id.flashcard_test_card_neutral_button  -> testCardFragment!!.flashcardTest.setRatingNeutral()
                R.id.flashcard_test_card_negative_button -> testCardFragment!!.flashcardTest.setRatingNegative()
            }

            testCardFragment!!.saveFlashcardTest()

            setCurrentButton()
        }

        private fun setCurrentButton()
        {
            var button: ImageButton? = null

            when (testCardFragment!!.flashcardTest.rating)
            {
                FlashcardTest.Rating.POSITIVE -> button = layout!!.findViewById(R.id
                                                                                    .flashcard_test_card_positive_button) as ImageButton
                FlashcardTest.Rating.NEUTRAL  -> button = layout!!.findViewById(R.id
                                                                                    .flashcard_test_card_neutral_button) as ImageButton
                FlashcardTest.Rating.NEGATIVE -> button = layout!!.findViewById(R.id
                                                                                    .flashcard_test_card_negative_button) as ImageButton
            }

            if (currentlySelectedButton != null)
            {
                currentlySelectedButton!!.imageAlpha = 100
            }

            currentlySelectedButton = button

            currentlySelectedButton!!.imageAlpha = 255
        }

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup,
                                  savedInstanceState: Bundle?): View
        {
            super.onCreateView(inflater, container, savedInstanceState)

            testCardFragment = parentFragment as FlashcardTestCardFragment

            layout = inflater.inflate(R.layout.fragment_flashcard_test_card_back,
                                      container, false) as LinearLayout

            val arguments = testCardFragment!!.arguments
            val titleText = arguments.getString(FlashcardTestCardFragment.ARGUMENT_KEYS.TITLE)
            val textText = arguments.getString(FlashcardTestCardFragment.ARGUMENT_KEYS.TEXT)

            testCardFragment!!.flashcardTest.flashcardId = arguments.getLong(
                FlashcardTestCardFragment.ARGUMENT_KEYS.ID)

            val title = layout!!.findViewById(R.id.flashcard_test_card_title) as TextView
            title.text = titleText
            val text = layout!!.findViewById(R.id.flashcard_test_card_text) as TextView
            text.text = textText

            buttons.add(layout!!.findViewById(
                R.id.flashcard_test_card_negative_button) as ImageButton)
            buttons.add(layout!!.findViewById(
                R.id.flashcard_test_card_neutral_button) as ImageButton)
            buttons.add(layout!!.findViewById(
                R.id.flashcard_test_card_positive_button) as ImageButton)

            setButtonClickHandlers()

            if (testCardFragment!!.flashcardTest.rating != null)
            {
                setCurrentButton()
            }

            return layout as LinearLayout
        }
    }
}
