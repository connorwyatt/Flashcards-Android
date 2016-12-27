package io.connorwyatt.flashcards.data.entities

import com.google.firebase.database.DataSnapshot

class FlashcardTest(data: DataSnapshot?) : BaseEntity(data)
{
    var rating: Rating?

    init
    {
        val values = data?.value as? Map<*, *>

        val ratingValue = values?.get(PropertyKeys.rating)?.let {
            when (it)
            {
                is Long -> it.toDouble()
                is Double -> it
                else -> null
            }
        }

        rating = ratingValue?.let { Rating.fromValue(it) }
    }

    companion object
    {
        object PropertyKeys
        {
            val rating = "rating"
        }

        enum class Rating(val value: Double)
        {
            POSITIVE(1.0),
            NEUTRAL(0.5),
            NEGATIVE(0.0);

            companion object
            {
                fun fromValue(value: Double): Rating? = Rating.values().find { it.value == value }
            }
        }
    }
}
