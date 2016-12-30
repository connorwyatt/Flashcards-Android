package io.connorwyatt.flashcards.data.entities.legacy

@Deprecated("This is considered legacy.")
abstract class BaseEntityLegacy
{
    var id: Long? = null

    fun existsInDatabase() = id != null
}
