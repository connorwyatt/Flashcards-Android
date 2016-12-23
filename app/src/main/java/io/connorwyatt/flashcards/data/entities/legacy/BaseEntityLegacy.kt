package io.connorwyatt.flashcards.data.entities.legacy

abstract class BaseEntityLegacy
{
    var id: Long? = null

    fun existsInDatabase() = id != null
}
