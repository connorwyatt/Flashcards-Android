package io.connorwyatt.flashcards.data.services

import io.connorwyatt.flashcards.data.datasources.FlashcardTestDataSource
import io.connorwyatt.flashcards.data.entities.FlashcardTest
import io.reactivex.Observable

object FlashcardTestService
{
    fun getByFlashcardId(id: String): Observable<List<FlashcardTest>>
        = FlashcardTestDataSource().getByFlashcardId(id)

    fun getAverageRatingForFlashcard(id: String): Observable<Double?>
    {
        return getByFlashcardId(id).map { tests ->
            val ratings = tests.mapNotNull { it.rating?.value }

            ratings.sum() / ratings.size
        }
    }

    fun getAverageRatingForCategory(): Observable<Double?>
        = TODO("Stub Method") // TODO Replace with real method body

    fun save(flashcardTest: FlashcardTest): Observable<FlashcardTest>
        = TODO("Stub Method") // TODO Replace with real method body

    fun delete(flashcardTest: FlashcardTest): Observable<Any?>
        = FlashcardTestDataSource().delete(flashcardTest)

    fun deleteByFlashcardId(flashcardId: String): Observable<Any?>
    {
        return getByFlashcardId(flashcardId).flatMap { flashcardTests ->
            val observables = flashcardTests.map { delete(it) }

            return@flatMap Observable.combineLatest(observables, { it })
        }
    }
}

