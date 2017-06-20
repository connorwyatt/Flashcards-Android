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
import android.widget.Button
import android.widget.TextView
import io.connorwyatt.flashcards.R
import io.connorwyatt.flashcards.activities.FlashcardTestActivity
import io.connorwyatt.flashcards.data.viewmodels.PerformanceViewModel
import io.reactivex.disposables.Disposable

class FlashcardTestSummaryFragment : Fragment() {
  lateinit private var viewGroup: ViewGroup
  lateinit private var disposable: Disposable

  //region Activity

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle?): View {
    super.onCreateView(inflater, container, savedInstanceState)

    viewGroup = inflater.inflate(
      R.layout.fragment_flashcard_test_summary, container, false) as ViewGroup

    initialiseUI()

    return viewGroup
  }

  override fun onDestroyView() {
    super.onDestroyView()

    disposable.dispose()
  }

  //endregion

  //region UI

  private fun initialiseUI() {
    initialiseButton()

    disposable = (activity as FlashcardTestActivity).flashcardTestFragment!!.getPerformanceObservable()
      .subscribe {
        updateValues(it)
      }
  }

  private fun initialiseButton(): Unit {
    val button = viewGroup.findViewById(R.id.flashcard_test_summary_finish_button) as Button

    button.setOnClickListener { activity.finish() }
  }

  private fun updateValues(performanceViewModel: PerformanceViewModel): Unit {
    (viewGroup.findViewById(R.id.flashcard_test_summary_positive_percent) as TextView).text =
      getString(R.string.percentage, toPercent(performanceViewModel.positivePercent))
    (viewGroup.findViewById(R.id.flashcard_test_summary_positive_count) as TextView).text =
      performanceViewModel.positiveCount.toString()
    (viewGroup.findViewById(R.id.flashcard_test_summary_neutral_percent) as TextView).text =
      getString(R.string.percentage, toPercent(performanceViewModel.neutralPercent))
    (viewGroup.findViewById(R.id.flashcard_test_summary_neutral_count) as TextView).text =
      performanceViewModel.neutralCount.toString()
    (viewGroup.findViewById(R.id.flashcard_test_summary_negative_percent) as TextView).text =
      getString(R.string.percentage, toPercent(performanceViewModel.negativePercent))
    (viewGroup.findViewById(R.id.flashcard_test_summary_negative_count) as TextView).text =
      performanceViewModel.negativeCount.toString()
    (viewGroup.findViewById(R.id.flashcard_test_summary_skip_count) as TextView).text =
      resources.getQuantityString(R.plurals.flashcard_test_summary_skip_count,
                                  performanceViewModel.skipCount,
                                  performanceViewModel.skipCount)
  }

  private fun toPercent(decimal: Double): Long {
    return Math.round(decimal * 100)
  }

  //endregion
}
