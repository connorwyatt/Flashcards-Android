package io.connorwyatt.flashcards.data.entities

class Flashcard : BaseEntity()
{
    var title: String? = null
    var text: String? = null
    var categories: MutableList<Category>? = null

    val categoriesString: String
        get() = categories?.map { it.name }?.joinToString() ?: ""
}
