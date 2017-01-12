package io.connorwyatt.flashcards.data.entities


class Relationships(relationships: Map<*, *>?)
{
    private val relationships: MutableMap<String, MutableList<String>>
    private val originalRelationships: MutableMap<String, MutableList<String>>

    init
    {
        this.relationships =
            relationships?.let { parseRawRelationships(it) } ?: mutableMapOf()

        originalRelationships = cloneRelationships(this.relationships)
    }

    fun getAllRelationships(): Map<String, List<String>>
    {
        return relationships
    }

    fun getRelationships(name: String): List<String>?
        = relationships[name]

    fun getUpdatedRelationships(): UpdatedRelationships
    {
        val addedMap: MutableMap<String, MutableList<String>> = mutableMapOf()
        val removedMap: MutableMap<String, MutableList<String>> = mutableMapOf()

        relationships.forEach { entry ->
            val newIds = entry.value
            val oldIds = originalRelationships[entry.key]

            if (oldIds == null)
            {
                addedMap.put(entry.key, newIds)
            }
            else
            {
                val addedIds = newIds.filterNot { it in oldIds }.toMutableList()
                val removedIds = oldIds.filterNot { it in newIds }.toMutableList()

                if (addedIds.isNotEmpty())
                {
                    addedMap.put(entry.key, addedIds)
                }

                if (removedIds.isNotEmpty())
                {
                    removedMap.put(entry.key, removedIds)
                }
            }
        }

        return UpdatedRelationships(added = addedMap, removed = removedMap)
    }

    fun setRelationships(name: String, ids: List<String>): Unit
    {
        relationships[name] = ids.toMutableList()
    }

    fun serialise(): MutableMap<String, MutableMap<String, Any>>
    {
        val serialisedRelationships: MutableMap<String, MutableMap<String, Any>> = mutableMapOf()

        relationships.forEach { relationship ->
            val relationshipIds: MutableMap<String, Any> = mutableMapOf()

            relationship.value.forEach { id ->
                relationshipIds.put(id, true)
            }

            serialisedRelationships.put(relationship.key, relationshipIds)
        }

        return serialisedRelationships
    }

    private fun cloneRelationships(relationships: MutableMap<String, MutableList<String>>): MutableMap<String, MutableList<String>>
    {
        val cloned: MutableMap<String, MutableList<String>> = mutableMapOf()

        relationships.forEach { entry ->
            val clonedList = entry.value.map { it }.toMutableList()

            cloned.put(entry.key, clonedList)
        }

        return cloned
    }

    private fun parseRawRelationships(rawRelationships: Map<*, *>): MutableMap<String, MutableList<String>>
    {
        val relationships: MutableMap<String, MutableList<String>> = mutableMapOf()

        rawRelationships.forEach { relationship ->
            val (key, value) = relationship

            if (key is String && value is Map<*, *>)
            {
                val ids = value.keys.mapNotNull { it as? String }.toMutableList()

                relationships.put(key, ids)
            }
        }

        return relationships
    }

    companion object
    {
        data class UpdatedRelationships(
            val added: MutableMap<String, MutableList<String>>,
            val removed: MutableMap<String, MutableList<String>>
        )
    }
}
