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
import io.connorwyatt.flashcards.adapters.FlashcardTestPagerAdapter
import io.connorwyatt.flashcards.data.entities.Flashcard
import io.connorwyatt.flashcards.data.entities.FlashcardTest
import io.connorwyatt.flashcards.data.services.FlashcardService
import io.connorwyatt.flashcards.data.services.FlashcardTestService
import io.connorwyatt.flashcards.data.viewmodels.PerformanceViewModel
import io.connorwyatt.flashcards.enums.Rating
import io.connorwyatt.flashcards.views.directionalviewpager.DirectionalViewPager
import io.connorwyatt.flashcards.views.progressbar.ProgressBar
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class FlashcardTestFragment : Fragment() {
  lateinit private var viewGroup: ViewGroup
  lateinit private var progressBar: ProgressBar
  private var flashcardTestPagerAdapter: FlashcardTestPagerAdapter? = null
  private var flashcards: List<Flashcard>? = null
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

    val categoryId = arguments.getString(ArgumentKeys.CATEGORY_ID)

    initialiseUI(categoryId)

    updateUI(false)

    return viewGroup
  }

  fun onBackPressed(callback: () -> Unit): Unit {
    val totalCompleted = flashcardTests.size + skippedFlashcards.size
    val isComplete = totalCompleted >= flashcards?.size ?: 0

    if (isComplete) {
      callback.invoke()
    } else {
      AlertDialog.Builder(activity)
        .setTitle(R.string.flashcard_test_confirmation_title)
        .setMessage(R.string.flashcard_test_confirmation_message)
        .setPositiveButton(
          R.string.flashcard_test_confirmation_yes) { _, _ -> callback.invoke() }
        .setNegativeButton(R.string.flashcard_test_confirmation_no) { _, _ -> }
        .create()
        .show()
    }
  }

  //endregion

  //region Data

  fun getFlashcardFromAdapter(id: String) = flashcardTestPagerAdapter!!.getFlashcardById(id)

  fun rateFlashcard(flashcard: Flashcard, rating: Rating): Observable<FlashcardTest> {
    val flashcardTest = FlashcardTest(null)

    flashcardTest.relationships.setRelationships("flashcard", listOf(flashcard.id!!))

    flashcardTest.rating = rating

    flashcardTests.put(flashcard.id, flashcardTest)

    updateUI()

    performanceSubject.onNext(getPerformanceViewModel())

    return saveFlashcardTest(flashcardTest)
  }

  fun getPerformanceObservable(): Observable<PerformanceViewModel> = performanceSubject

  private fun getData(categoryId: String?): Observable<List<Flashcard>> {
    return if (categoryId !== null)
      FlashcardService.getByCategory(categoryId)
    else
      FlashcardService.getAll()
  }

  private fun saveFlashcardTest(flashcardTest: FlashcardTest): Observable<FlashcardTest> {
    return FlashcardTestService.save(flashcardTest)
  }

  private fun getPerformanceViewModel(): PerformanceViewModel {
    val ratings = flashcardTests.mapNotNull { it.value.rating }

    return PerformanceViewModel(ratings, flashcards?.size ?: 0)
  }

  //endregion

  //region UI

  private fun initialiseUI(categoryId: String?): Unit {
    initialisePager(categoryId)

    initialiseProgressBar()
  }

  private fun initialisePager(categoryId: String?): Unit {
    flashcardTestPagerAdapter = FlashcardTestPagerAdapter(fragmentManager)

    if (flashcards == null) {
      getData(categoryId).subscribe {
        flashcards = it

        flashcardTestPagerAdapter!!.setData(it)

        updateUI()

        performanceSubject.onNext(getPerformanceViewModel())
      }
    } else {
      flashcardTestPagerAdapter!!.setData(flashcards!!)

      performanceSubject.onNext(getPerformanceViewModel())
    }

    val viewPager =
      viewGroup.findViewById(R.id.flashcard_test_view_pager) as DirectionalViewPager

    viewPager.adapter = flashcardTestPagerAdapter
    viewPager.allowLeftSwipe = false

    viewPager.addOnPageSkipListener {
      val flashcard = it as Flashcard
      val flashcardId = flashcard.id!!

      if (!flashcardTests.containsKey(flashcardId)) {
        skippedFlashcards.add(flashcardId)

        val skipMessage = getString(R.string.flashcard_test_skip_toast, flashcard.title)
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
    val totalCards = flashcards?.size?.toDouble()

    totalCards?.let {
      progressBar.setProgress((ratedCards + skippedCards) / totalCards,
                              animate)
    }
  }

  //endregion

  companion object {
    object ArgumentKeys {
      val CATEGORY_ID = "CATEGORY_ID"
    }
  }
}
