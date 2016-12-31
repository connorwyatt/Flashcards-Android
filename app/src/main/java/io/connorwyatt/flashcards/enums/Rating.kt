package io.connorwyatt.flashcards.enums

enum class Rating(val value: Double)
{
    POSITIVE(1.0),
    NEUTRAL(0.5),
    NEGATIVE(0.0),
    NOT_RATED(-1.0);

    companion object
    {
        fun fromValue(value: Double): Rating? = Rating.values().find { it.value == value }
    }
}
