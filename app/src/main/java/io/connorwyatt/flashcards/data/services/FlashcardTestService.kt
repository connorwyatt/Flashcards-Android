package io.connorwyatt.flashcards.data.services

import io.connorwyatt.flashcards.data.datasources.FlashcardTestDataSource
import io.connorwyatt.flashcards.data.entities.FlashcardTest
import io.reactivex.Observable

object FlashcardTestService
{
    fun getById(id: String): Observable<FlashcardTest>
        = FlashcardTestDataSource().getById(id)


    fun getByFlashcardId(id: String): Observable<List<FlashcardTest>>
        = FlashcardTestDataSource().getByFlashcardId(id)

    fun getAverageRatingForFlashcard(id: String): Observable<Double?>
    {
        return getByFlashcardId(id).map { tests ->
            averageFlashcardTests(tests)
        }
    }

    fun getByCategoryId(id: String): Observable<List<FlashcardTest>>
    {
        return FlashcardService.getByCategory(id).flatMap { flashcards ->
            val observables = flashcards.map { getByFlashcardId(it.id!!) }

            Observable.combineLatest(observables, {
                it.filterIsInstance(FlashcardTest::class.java)
            })
        }
    }

    fun getAverageRatingForCategory(id: String): Observable<Double?>
    {
        return getByCategoryId(id).map { tests ->
            averageFlashcardTests(tests)
        }
    }

    fun save(flashcardTest: FlashcardTest): Observable<FlashcardTest>
    {
        return FlashcardTestDataSource().save(flashcardTest).flatMap {
            getById(it)
        }
    }

    fun delete(flashcardTest: FlashcardTest): Observable<Any?>
        = FlashcardTestDataSource().delete(flashcardTest)

    fun deleteByFlashcardId(flashcardId: String): Observable<Any?>
    {
        return getByFlashcardId(flashcardId).flatMap { flashcardTests ->
            val observables = flashcardTests.map { delete(it) }

            return@flatMap Observable.combineLatest(observables, { it })
        }
    }

    private fun averageFlashcardTests(tests: List<FlashcardTest>): Double
    {
        val ratings = tests.mapNotNull { it.rating?.value }

        return ratings.sum() / ratings.size
    }
}
