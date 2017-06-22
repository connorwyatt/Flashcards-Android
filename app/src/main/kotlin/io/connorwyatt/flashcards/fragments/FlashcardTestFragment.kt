/*
 * Copyright (c) 2016-2017 Connor Wyatt <connorwyatt1@gmail.com>.
 *
 * This file can not be copied and/or distributed without the express permission of Connor Wyatt.
 */

package io.connorwyatt.flashcards.fragments

import android.app.AlertDialog
import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import io.connorwyatt.flashcards.R
import io.connorwyatt.flashcards.activities.FlashcardTestActivity.Companion.Order
import io.connorwyatt.flashcards.activities.FlashcardTestActivity.Companion.Order.RANDOM
import io.connorwyatt.flashcards.activities.FlashcardTestActivity.Companion.Order.WORST_TO_BEST
import io.connorwyatt.flashcards.adapters.FlashcardTestPagerAdapter
import io.connorwyatt.flashcards.data.entities.Flashcard
import io.connorwyatt.flashcards.data.entities.FlashcardTest
import io.connorwyatt.flashcards.data.services.FlashcardService
import io.connorwyatt.flashcards.data.services.FlashcardTestService
import io.connorwyatt.flashcards.data.viewmodels.FlashcardViewModel
import io.connorwyatt.flashcards.data.viewmodels.PerformanceViewModel
import io.connorwyatt.flashcards.enums.Rating
import io.connorwyatt.flashcards.shuffle
import io.connorwyatt.flashcards.views.directionalviewpager.DirectionalViewPager
import io.connorwyatt.flashcards.views.progressbar.ProgressBar
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class FlashcardTestFragment : Fragment() {
  lateinit private var viewGroup: ViewGroup
  lateinit private var progressBar: ProgressBar
  private var flashcardTestPagerAdapter: FlashcardTestPagerAdapter? = null
  private var viewModels: List<FlashcardViewModel>? = null
  private val flashcardTests = mutableMapOf<String, FlashcardTest>()
  private var skippedFlashcards = mutableListOf<String>()
  private val performanceSubject = BehaviorSubject.createDefault(getPerformanceViewModel())

  //region Activity

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    retainInstance = true
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup,
                            savedInstanceState: Bundle?): View {
    super.onCreateView(inflater, container, savedInstanceState)

    viewGroup = inflater.inflate(
      R.layout.fragment_flashcard_test, container, false) as ViewGroup

    val tagId = arguments.getString(ArgumentKeys.TAG_ID)
    val order = Order.values()[arguments.getInt(ArgumentKeys.ORDER)]
    val limit = arguments.getInt(ArgumentKeys.LIMIT, -1).takeUnless { it == -1 }

    initialiseUI(tagId, order, limit)

    updateUI(false)

    return viewGroup
  }

  fun onBackPressed(callback: () -> Unit): Unit {
    val totalCompleted = flashcardTests.size + skippedFlashcards.size
    val isComplete = totalCompleted >= viewModels?.size ?: 0

    if (isComplete) {
      callback()
    } else {
      AlertDialog.Builder(activity)
        .setTitle(R.string.flashcard_test_confirmation_title)
        .setMessage(R.string.flashcard_test_confirmation_message)
        .setPositiveButton(R.string.flashcard_test_confirmation_yes) { _, _ -> callback() }
        .setNegativeButton(R.string.flashcard_test_confirmation_no) { _, _ -> }
        .create()
        .show()
    }
  }

  //endregion

  //region Data

  fun getFlashcardFromAdapter(id: String) = flashcardTestPagerAdapter!!.getFlashcardById(id)

  fun rateFlashcard(viewModel: FlashcardViewModel, rating: Rating): Observable<FlashcardTest> {
    val flashcardTest = FlashcardTest(null)

    val id = viewModel.flashcard.id!!

    flashcardTest.relationships.setRelationships("flashcard", listOf(id))

    flashcardTest.rating = rating

    flashcardTests.put(id, flashcardTest)

    updateUI()

    performanceSubject.onNext(getPerformanceViewModel())

    return saveFlashcardTest(flashcardTest)
  }

  fun getPerformanceObservable(): Observable<PerformanceViewModel> = performanceSubject

  private fun getData(tagId: String?, order: Order, limit: Int?): Observable<List<FlashcardViewModel>> {
    val flashcards = if (tagId !== null)
      FlashcardService.getByTag(tagId)
    else
      FlashcardService.getAll()

    val enhancedFlashcards = flashcards.flatMap {
      val observables = it.map { FlashcardViewModel.getFromFlashcard(it, includeRating = order == WORST_TO_BEST) }

      return@flatMap Observable.combineLatest(observables) {
        val viewModels = it.filterIsInstance(FlashcardViewModel::class.java)

        return@combineLatest viewModels
      }
    }

    val sortedFlashcards = enhancedFlashcards.map {
      when (order) {
        RANDOM -> it.shuffle()
        WORST_TO_BEST -> it.sortedBy { it.averageRating }
        else -> it
      }
    }

    val limitedFlashcards = limit?.let { sortedFlashcards.map { it.subList(0, limit) } } ?: sortedFlashcards

    return limitedFlashcards
  }

  private fun saveFlashcardTest(flashcardTest: FlashcardTest): Observable<FlashcardTest> {
    return FlashcardTestService.save(flashcardTest)
  }

  private fun getPerformanceViewModel(): PerformanceViewModel {
    val ratings = flashcardTests.mapNotNull { it.value.rating }

    return PerformanceViewModel(ratings, viewModels?.size ?: 0)
  }

  //endregion

  //region UI

  private fun initialiseUI(tagId: String?, order: Order, limit: Int?): Unit {
    initialisePager(tagId, order, limit)

    initialiseProgressBar()
  }

  private fun initialisePager(tagId: String?, order: Order, limit: Int?): Unit {
    flashcardTestPagerAdapter = FlashcardTestPagerAdapter(fragmentManager)

    if (viewModels == null) {
      getData(tagId, order, limit).subscribe {
        viewModels = it

        flashcardTestPagerAdapter!!.setData(it)

        updateUI()

        performanceSubject.onNext(getPerformanceViewModel())
      }
    } else {
      flashcardTestPagerAdapter!!.setData(viewModels!!)

      performanceSubject.onNext(getPerformanceViewModel())
    }

    val viewPager =
      viewGroup.findViewById(R.id.flashcard_test_view_pager) as DirectionalViewPager

    viewPager.adapter = flashcardTestPagerAdapter
    viewPager.allowLeftSwipe = false

    viewPager.addOnPageSkipListener {
      val viewModel = it as FlashcardViewModel
      val flashcardId = viewModel.flashcard.id!!

      if (!flashcardTests.containsKey(flashcardId)) {
        skippedFlashcards.add(flashcardId)

        val skipMessage = getString(R.string.flashcard_test_skip_toast, viewModel.flashcard.title)
        Toast.makeText(activity, skipMessage, Toast.LENGTH_SHORT)
          .show()
      }

      updateUI()

      performanceSubject.onNext(getPerformanceViewModel())
    }
  }

  private fun initialiseProgressBar(): Unit {
    progressBar = viewGroup.findViewById(R.id.flashcard_test_progress_bar) as ProgressBar
  }

  private fun updateUI(animate: Boolean = true): Unit {
    updateProgressBar(animate)
  }

  private fun updateProgressBar(animate: Boolean): Unit {
    val ratedCards = flashcardTests.size.toDouble()
    val skippedCards = skippedFlashcards.size.toDouble()
    val totalCards = viewModels?.size?.toDouble()

    totalCards?.let {
      progressBar.setProgress((ratedCards + skippedCards) / totalCards, animate)
    }
  }

  //endregion

  companion object {
    object ArgumentKeys {
      val TAG_ID = "TAG_ID"
      val ORDER = "ORDER"
      val LIMIT = "LIMIT"
    }
  }
}
