package io.connorwyatt.flashcards.data.entities

import com.google.firebase.database.DataSnapshot

abstract class BaseEntity(data: DataSnapshot?)
{
    val id: String?
    val timestamps: Timestamps
    val relationships: Relationships

    init
    {
        this.id = data?.key

        val values = data?.value as? Map<*, *>

        val created = values?.get(PropertyKeys.created) as Long?
        val lastModified = values?.get(PropertyKeys.lastModified) as Long?
        this.timestamps = Timestamps(created, lastModified)

        val relationshipMap = values?.get("_relationships") as? Map<*, *>
        this.relationships = relationshipMap?.let(::Relationships) ?: Relationships(null)
    }

    open fun serialise(): MutableMap<String, Any?>
    {
        val serialisedEntity: MutableMap<String, Any?> = mutableMapOf()

        serialisedEntity.put(PropertyKeys.created, timestamps.created)
        serialisedEntity.put(PropertyKeys.lastModified, timestamps.lastModified)
        serialisedEntity.put(PropertyKeys.relationships, relationships.serialise())

        return serialisedEntity
    }

    fun existsInDatabase(): Boolean = id != null

    companion object
    {
        object PropertyKeys
        {
            val created = "_created_on"
            val lastModified = "_last_modified_on"
            val relationships = "_relationships"
        }
    }
}
