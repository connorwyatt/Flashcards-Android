package io.connorwyatt.flashcards.data.entities

import com.google.firebase.database.DataSnapshot

class Category(data: DataSnapshot?) : BaseEntity(data)
{
    var name: String? = null

    init
    {
        val values = data?.value as? Map<*, *>

        name = values?.get(PropertyKeys.name) as String
    }

    override fun serialise(): MutableMap<String, Any?>
    {
        val serialisedEntity = super.serialise()

        serialisedEntity.put(PropertyKeys.name, name)

        return serialisedEntity
    }

    companion object
    {
        object PropertyKeys
        {
            val name = "name"
        }
    }
}
