package io.connorwyatt.flashcards.services

import android.content.Context
import io.connorwyatt.flashcards.data.datasources.FlashcardTestDataSource
import io.connorwyatt.flashcards.data.entities.FlashcardTest

class FlashcardTestService {
    private var context: Context?

    constructor(context: Context) {
        this.context = context
    }

    fun getAverageRatingForFlashcard(id: Long): Double? {
        val ftds = FlashcardTestDataSource(context)
        ftds.open()
        val flashcardTests = ftds.getByFlashcardId(id)
        ftds.close()

        var averageRating: Double? = null

        if (flashcardTests.size > 0) {
            var total = 0.0

            for (flashcardTest in flashcardTests) {
                when (flashcardTest.rating) {
                    FlashcardTest.Rating.POSITIVE -> total += 1.0
                    FlashcardTest.Rating.NEUTRAL -> total += 0.5
                    FlashcardTest.Rating.NEGATIVE -> total += 0.0
                }
            }

            averageRating = total / flashcardTests.size.toDouble()
        }

        return averageRating
    }
}
