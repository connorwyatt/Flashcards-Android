package io.connorwyatt.flashcards.data.entities.legacy

import java.util.ArrayList

@Deprecated("This is considered legacy.")
class FlashcardLegacy : BaseEntityLegacy()
{
    var title: String? = null
    var text: String? = null
    var categories: MutableList<CategoryLegacy> = ArrayList()

    val categoriesString: String
        get() = categories.map { it.name }.joinToString()
}
