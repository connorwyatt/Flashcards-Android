package io.connorwyatt.flashcards.data.entities

import com.google.firebase.database.DataSnapshot
import io.connorwyatt.flashcards.enums.Rating

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

    override fun getType() = "flashcardTest"

    companion object
    {
        object PropertyKeys
        {
            val rating = "rating"
        }
    }
}
