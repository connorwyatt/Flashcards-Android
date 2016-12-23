package io.connorwyatt.flashcards.data.entities.legacy

class FlashcardTestLegacy : BaseEntityLegacy()
{
    var rating: Rating? = null
    var flashcardId: Long? = null

    fun setRatingFromInt(ratingOrdinal: Int): Unit
    {
        rating = Rating.values()[ratingOrdinal]
    }

    fun setRatingPositive(): Unit
    {
        rating = Rating.POSITIVE
    }

    fun setRatingNeutral(): Unit
    {
        rating = Rating.NEUTRAL
    }

    fun setRatingNegative(): Unit
    {
        rating = Rating.NEGATIVE
    }

    enum class Rating
    {
        POSITIVE,
        NEUTRAL,
        NEGATIVE
    }
}