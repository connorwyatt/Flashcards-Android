package io.connorwyatt.flashcards.adapters

import android.app.Fragment
import android.app.FragmentManager
import android.support.v4.view.PagerAdapter
import io.connorwyatt.flashcards.data.entities.Flashcard
import io.connorwyatt.flashcards.fragments.FlashcardTestSummaryFragment

class FlashcardTestPagerAdapter(fragmentManager: FragmentManager)
    : FixedFragmentStatePagerAdapter(fragmentManager)
{
    private val summaryFragment = FlashcardTestSummaryFragment()
    private var flashcards: MutableList<Flashcard>? = null

    //region Pager

    override fun getItem(position: Int): Fragment
    {
        val isFlashcard = position < flashcards?.size ?: 0

        return if (isFlashcard) getFlashcardFragment(position) else summaryFragment
    }

    override fun getItemPosition(item: Any?): Int
    {
        return PagerAdapter.POSITION_NONE
    }

    override fun getFragmentTag(position: Int): String
    {
        return if (position < flashcards?.size ?: 0) flashcards?.get(position)?.id!! else "summary"
    }

    override fun getCount(): Int
    {
        return (flashcards?.size ?: 0) + 1
    }

    //endregion

    //region Data

    fun setData(flashcards: List<Flashcard>): Unit
    {
        this.flashcards = flashcards.toMutableList()
        notifyDataSetChanged()
    }

    fun removeItem(position: Int): Flashcard
    {
        val removedFlashcard = flashcards!!.removeAt(position)
        notifyDataSetChanged()
        return removedFlashcard
    }

    //endregion

    //region Fragments

    private fun getFlashcardFragment(position: Int): Fragment
    {
        val fragment = Fragment()

        return fragment
    }

    //endregion
}
