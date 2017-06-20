/*
 * Copyright (c) 2016-2017 Connor Wyatt <connorwyatt1@gmail.com>.
 *
 * This file can not be copied and/or distributed without the express permission of Connor Wyatt.
 */

package io.connorwyatt.flashcards.data.services

import io.connorwyatt.flashcards.data.datasources.FlashcardTestDataSource
import io.connorwyatt.flashcards.data.entities.FlashcardTest
import io.reactivex.Observable

object FlashcardTestService {
  fun getById(id: String): Observable<FlashcardTest>
    = FlashcardTestDataSource().getById(id)


  fun getByFlashcardId(id: String): Observable<List<FlashcardTest>>
    = FlashcardTestDataSource().getByFlashcardId(id)

  fun getAverageRatingForFlashcard(id: String): Observable<Double> {
    return getByFlashcardId(id).map { tests ->
      averageFlashcardTests(tests) ?: -1.0
    }
  }

  fun getByTagId(id: String): Observable<List<FlashcardTest>> {
    return FlashcardService.getByTag(id).flatMap { flashcards ->
      val observables = flashcards.map { getByFlashcardId(it.id!!) }

      if (observables.isNotEmpty()) {
        Observable.combineLatest(observables, {
          (it[0] as List<*>).filterIsInstance(FlashcardTest::class.java)
        })
      } else {
        Observable.just(listOf())
      }
    }
  }

  fun getAverageRatingForTag(id: String): Observable<Double> {
    return getByTagId(id).map { tests ->
      averageFlashcardTests(tests) ?: -1.0
    }
  }

  fun save(flashcardTest: FlashcardTest): Observable<FlashcardTest> {
    return FlashcardTestDataSource().save(flashcardTest).flatMap {
      getById(it)
    }
  }

  fun delete(flashcardTest: FlashcardTest): Observable<Any?>
    = FlashcardTestDataSource().delete(flashcardTest)

  fun deleteByFlashcardId(flashcardId: String): Observable<Any?> {
    return getByFlashcardId(flashcardId).flatMap { flashcardTests ->
      val observables = flashcardTests.map { delete(it) }

      if (observables.isNotEmpty()) {
        Observable.combineLatest(observables, { it }).map { true }
      } else {
        Observable.just(true)
      }
    }
  }

  private fun averageFlashcardTests(tests: List<FlashcardTest>): Double? {
    val ratings = tests.mapNotNull { it.rating?.value }

    if (ratings.isNotEmpty()) {
      return ratings.sum() / ratings.size.toDouble()
    } else {
      return null
    }
  }
}
