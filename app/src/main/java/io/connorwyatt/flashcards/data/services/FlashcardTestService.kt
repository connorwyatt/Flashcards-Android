package io.connorwyatt.flashcards.data.services

import io.connorwyatt.flashcards.data.datasources.FlashcardTestDataSource
import io.connorwyatt.flashcards.data.entities.FlashcardTest
import io.reactivex.Observable

object FlashcardTestService
{
    fun getAverageRatingForFlashcard(id: String): Observable<Double?>
    {
        val dataSource = FlashcardTestDataSource()

        return dataSource.getByFlashcardId(id).map { tests ->
            val ratings = tests.mapNotNull { it.rating?.value }

            ratings.sum() / ratings.size
        }
    }

    fun getAverageRatingForCategory(): Observable<Double?>
        = TODO("Stub Method") // TODO Replace with real method body

    fun save(flashcardTest: FlashcardTest): Observable<FlashcardTest>
        = TODO("Stub Method") // TODO Replace with real method body
}

