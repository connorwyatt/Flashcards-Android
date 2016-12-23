package io.connorwyatt.flashcards.data.entities

abstract class BaseEntity
{
    var id: String? = null
    var _created_on: Long? = null
    var _last_modified_on: Long? = null
}
