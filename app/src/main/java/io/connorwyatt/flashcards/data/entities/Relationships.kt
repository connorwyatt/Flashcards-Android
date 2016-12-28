package io.connorwyatt.flashcards.data.entities


class Relationships(relationships: Map<*, *>?)
{
    val relationships: MutableMap<String, MutableList<String>>

    init
    {
        this.relationships =
            relationships?.let { parseRawRelationships(it) } ?: mutableMapOf()
    }

    fun getRelationships(name: String): List<String>?
        = relationships[name]

    fun getUpdatedRelationships(): UpdatedRelationships
    {
        return UpdatedRelationships(null, null)
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
            val added: List<Pair<String, String>>?,
            val removed: List<Pair<String, String>>?
        )
    }
}
