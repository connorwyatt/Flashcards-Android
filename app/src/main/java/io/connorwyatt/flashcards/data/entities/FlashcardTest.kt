package io.connorwyatt.flashcards.data.entities

class FlashcardTest : BaseEntity()
{
    var rating: Rating? = null
    var flashcard: Long? = null

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
