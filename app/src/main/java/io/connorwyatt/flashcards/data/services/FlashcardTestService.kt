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

    fun getAverageRatingForFlashcard(id: String): Observable<Double>
    {
        return getByFlashcardId(id).map { tests ->
            averageFlashcardTests(tests) ?: -1.0
        }
    }

    fun getByCategoryId(id: String): Observable<List<FlashcardTest>>
    {
        return FlashcardService.getByCategory(id).flatMap { flashcards ->
            val observables = flashcards.map { getByFlashcardId(it.id!!) }

            if (observables.isNotEmpty())
            {
                Observable.combineLatest(observables, {
                    it.filterIsInstance(FlashcardTest::class.java)
                })
            }
            else
            {
                Observable.just(listOf())
            }
        }
    }

    fun getAverageRatingForCategory(id: String): Observable<Double>
    {
        return getByCategoryId(id).map { tests ->
            averageFlashcardTests(tests) ?: -1.0
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

            if (observables.isNotEmpty())
            {
                Observable.combineLatest(observables, { it }).map { true }
            }
            else
            {
                Observable.just(true)
            }
        }
    }

    private fun averageFlashcardTests(tests: List<FlashcardTest>): Double?
    {
        val ratings = tests.mapNotNull { it.rating?.value }

        if (ratings.isNotEmpty())
        {
            return ratings.sum() / ratings.size
        }
        else
        {
            return null
        }
    }
}
