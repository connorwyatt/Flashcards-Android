package io.connorwyatt.flashcards.data.entities

import java.util.ArrayList

class Flashcard : BaseEntity()
{
    var title: String? = null
    var text: String? = null
    var categories: List<Category> = ArrayList()

    val categoriesString: String
        get() = categories.map { it.name }.joinToString()
}
