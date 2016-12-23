package io.connorwyatt.flashcards.adapters

import android.app.Fragment
import android.app.FragmentManager
import android.os.Bundle
import android.support.v4.view.PagerAdapter

import io.connorwyatt.flashcards.data.entities.legacy.FlashcardLegacy
import io.connorwyatt.flashcards.fragments.FlashcardTestCardFragment
import io.connorwyatt.flashcards.fragments.FlashcardTestSummaryFragment

class FlashcardTestPagerAdapter(fragmentManager: FragmentManager, private val flashcards: MutableList<FlashcardLegacy>) :
    FixedFragmentStatePagerAdapter(fragmentManager)
{
    private val summaryFragment = FlashcardTestSummaryFragment()

    override fun getCount(): Int
    {
        return flashcards.size + 1
    }

    override fun getItemPosition(item: Any?): Int
    {
        return PagerAdapter.POSITION_NONE
    }

    override fun getItem(position: Int): Fragment
    {
        val isFlashcard = position < flashcards.size

        if (isFlashcard)
        {
            return getFlashcardFragment(position)
        }
        else
        {
            return summaryFragment
        }
    }

    override fun getFragmentTag(position: Int): String
    {
        val tag: String

        if (position < flashcards.size)
        {
            tag = flashcards[position].id!!.toString()
        }
        else
        {
            tag = "summary"
        }

        return tag
    }

    fun removeItem(position: Int): FlashcardLegacy
    {
        val removedFlashcard = flashcards.removeAt(position)
        notifyDataSetChanged()
        return removedFlashcard
    }

    private fun getFlashcardFragment(position: Int): Fragment
    {
        val currentFlashcard = flashcards[position]

        val fragment = FlashcardTestCardFragment()

        val arguments = Bundle()
        arguments.putLong(FlashcardTestCardFragment.ARGUMENT_KEYS.ID, currentFlashcard.id!!)
        arguments.putString(FlashcardTestCardFragment.ARGUMENT_KEYS.TITLE, currentFlashcard
            .title)
        arguments.putString(FlashcardTestCardFragment.ARGUMENT_KEYS.TEXT, currentFlashcard
            .text)
        fragment.arguments = arguments

        return fragment
    }
}
