/*
 * Copyright (c) 2016-2017 Connor Wyatt <connorwyatt1@gmail.com>.
 *
 * This file can not be copied and/or distributed without the express permission of Connor Wyatt.
 */

package io.connorwyatt.flashcards.adapters

import android.app.Fragment
import android.app.FragmentManager
import android.os.Bundle
import android.support.v4.view.PagerAdapter
import io.connorwyatt.flashcards.data.viewmodels.FlashcardViewModel
import io.connorwyatt.flashcards.fragments.FlashcardTestCardFragment
import io.connorwyatt.flashcards.fragments.FlashcardTestSummaryFragment

class FlashcardTestPagerAdapter(fragmentManager: FragmentManager)
  : FixedFragmentStatePagerAdapter(fragmentManager) {
  private val summaryFragment = FlashcardTestSummaryFragment()
  private var viewModels: MutableList<FlashcardViewModel>? = null

  //region Pager

  override fun getItem(position: Int): Fragment {
    val flashcard = viewModels?.getOrNull(position)

    return flashcard?.let { getFlashcardFragment(it) } ?: summaryFragment
  }

  override fun getItemPosition(item: Any?): Int {
    return PagerAdapter.POSITION_NONE
  }

  override fun getFragmentTag(position: Int): String {
    return if (position < viewModels?.size ?: 0) viewModels?.get(position)?.flashcard?.id!! else "summary"
  }

  override fun getCount(): Int {
    return (viewModels?.size ?: 0) + 1
  }

  //endregion

  //region Data

  fun getFlashcardById(id: String): FlashcardViewModel {
    return viewModels!!.find { it.flashcard.id!! == id }!!
  }

  fun setData(flashcards: List<FlashcardViewModel>): Unit {
    viewModels = flashcards.toMutableList()
    notifyDataSetChanged()
  }

  fun removeItem(position: Int): FlashcardViewModel {
    val removedFlashcard = viewModels!!.removeAt(position)
    notifyDataSetChanged()
    return removedFlashcard
  }

  //endregion

  //region Fragments

  private fun getFlashcardFragment(viewModel: FlashcardViewModel): Fragment {
    val fragment = FlashcardTestCardFragment()

    val arguments = Bundle()

    arguments.putString(
      FlashcardTestCardFragment.Companion.ArgumentKeys.FLASHCARD_ID, viewModel.flashcard.id!!)
    fragment.arguments = arguments


    return fragment
  }

  //endregion
}
