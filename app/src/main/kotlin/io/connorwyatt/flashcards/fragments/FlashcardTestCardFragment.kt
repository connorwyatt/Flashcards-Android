/*
 * Copyright (c) 2016-2017 Connor Wyatt <connorwyatt1@gmail.com>.
 *
 * This file can not be copied and/or distributed without the express permission of Connor Wyatt.
 */

package io.connorwyatt.flashcards.fragments

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.connorwyatt.flashcards.R
import io.connorwyatt.flashcards.activities.FlashcardTestActivity
import io.connorwyatt.flashcards.data.viewmodels.FlashcardViewModel
import io.connorwyatt.flashcards.enums.Rating

class FlashcardTestCardFragment : Fragment() {
  lateinit private var flashcardTestFragment: FlashcardTestFragment
  private var viewModel: FlashcardViewModel? = null
  private var isFlipped = false
  private var rating: Rating? = null

  //region Fragment

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    retainInstance = true
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle?): View {
    super.onCreateView(inflater, container, savedInstanceState)

    val viewGroup = inflater.inflate(
      R.layout.fragment_flashcard_test_card, container, false) as ViewGroup

    initialiseUI()

    return viewGroup
  }

  override fun onDestroy() {
    super.onDestroy()

    rating?.let { flashcardTestFragment.rateFlashcard(viewModel!!, it).subscribe() }
  }

  //endregion

  //region UI

  fun flipCard() {
    if (!isFlipped) {
      childFragmentManager
        .beginTransaction()
        .setCustomAnimations(R.animator.card_flip_in, R.animator.card_flip_out)
        .replace(R.id.flashcard_test_card_frame,
          getCardBackFragment())
        .commit()

      isFlipped = true
    }
  }

  private fun initialiseUI(): Unit {
    initialiseFragment()
  }

  private fun initialiseFragment(): Unit {
    flashcardTestFragment = (activity as FlashcardTestActivity).flashcardTestFragment!!

    viewModel = flashcardTestFragment
      .getFlashcardFromAdapter(arguments.getString(ArgumentKeys.FLASHCARD_ID))

    val fragment = if (!isFlipped)
      FlashcardTestCardFrontFragment(viewModel!!)
    else
      getCardBackFragment()

    childFragmentManager
      .beginTransaction()
      .add(R.id.flashcard_test_card_frame, fragment)
      .commit()
  }

  private fun getCardBackFragment()
    = FlashcardTestCardBackFragment(viewModel!!, rating, { rating = it })

  //endregion

  companion object {
    object ArgumentKeys {
      val FLASHCARD_ID = "FLASHCARD_ID"
    }
  }
}
