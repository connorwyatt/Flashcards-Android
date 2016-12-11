package io.connorwyatt.flashcards.data.services

import android.content.Context
import io.connorwyatt.flashcards.data.datasources.FlashcardTestDataSource
import io.connorwyatt.flashcards.data.entities.FlashcardTest

class FlashcardTestService(private val context: Context)
{
    fun getAverageRatingForFlashcard(id: Long): Double?
    {
        val dataSource = FlashcardTestDataSource(context)
        val flashcardTests: List<FlashcardTest>

        try
        {
            dataSource.open()

            flashcardTests = dataSource.getByFlashcardId(id)
        }
        finally
        {
            dataSource.close()
        }

        var averageRating: Double? = null

        if (flashcardTests.isNotEmpty())
        {
            var total = 0.0

            for (flashcardTest in flashcardTests)
            {
                when (flashcardTest.rating)
                {
                    FlashcardTest.Rating.POSITIVE -> total += 1.0
                    FlashcardTest.Rating.NEUTRAL  -> total += 0.5
                    FlashcardTest.Rating.NEGATIVE -> total += 0.0
                }
            }

            averageRating = total / flashcardTests.size.toDouble()
        }

        return averageRating
    }

    fun save(flashcardTest: FlashcardTest): FlashcardTest
    {
        val dataSource = FlashcardTestDataSource(context)

        try
        {
            dataSource.open()

            return dataSource.save(flashcardTest)
        }
        finally
        {
            dataSource.close()
        }
    }
}
