package io.connorwyatt.flashcards.data.entities

abstract class BaseEntity
{
    var id: Long? = null

    fun existsInDatabase() = id != null
}
