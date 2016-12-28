package io.connorwyatt.flashcards.data.entities

import com.google.firebase.database.DataSnapshot

class Flashcard(data: DataSnapshot?) : BaseEntity(data)
{
    var title: String? = null
    var text: String? = null

    init
    {
        val values = data?.value as? Map<*, *>

        title = values?.get(PropertyKeys.title) as String?
        text = values?.get(PropertyKeys.text) as String?
    }

    override fun serialise(): MutableMap<String, Any?>
    {
        val serialisedEntity = super.serialise()

        serialisedEntity.put(PropertyKeys.title, title)
        serialisedEntity.put(PropertyKeys.text, text)

        return serialisedEntity
    }

    companion object
    {
        object PropertyKeys
        {
            val title = "title"
            val text = "text"
        }
    }
}
