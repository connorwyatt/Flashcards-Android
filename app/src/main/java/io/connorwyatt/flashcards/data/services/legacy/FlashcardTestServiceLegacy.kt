package io.connorwyatt.flashcards.data.services.legacy

import android.content.Context
import io.connorwyatt.flashcards.data.datasources.legacy.FlashcardTestDataSourceLegacy
import io.connorwyatt.flashcards.data.entities.legacy.FlashcardTestLegacy

@Deprecated("This is considered legacy.")
class FlashcardTestServiceLegacy(private val context: Context)
{
    fun getAverageRatingForFlashcard(id: Long): Double?
    {
        val dataSource = FlashcardTestDataSourceLegacy(context)
        val flashcardTests: List<FlashcardTestLegacy>

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
                    FlashcardTestLegacy.Rating.POSITIVE -> total += 1.0
                    FlashcardTestLegacy.Rating.NEUTRAL -> total += 0.5
                    FlashcardTestLegacy.Rating.NEGATIVE -> total += 0.0
                }
            }

            averageRating = total / flashcardTests.size.toDouble()
        }

        return averageRating
    }

    fun save(flashcardTest: FlashcardTestLegacy): FlashcardTestLegacy
    {
        val dataSource = FlashcardTestDataSourceLegacy(context)

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
