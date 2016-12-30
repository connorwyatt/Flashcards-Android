package io.connorwyatt.flashcards.data.contracts

import io.connorwyatt.flashcards.data.entities.legacy.BaseColumnsTimelineLegacy

@Deprecated("This is considered legacy.")
object FlashcardContract
{
    val TABLE_NAME = "flashcard"
    val TABLE_CREATE = "create table $TABLE_NAME(" +
                       "${BaseColumnsTimelineLegacy._ID} integer primary key autoincrement, " +
                       "${BaseColumnsTimelineLegacy._CREATED_ON} integer not null, " +
                       "${BaseColumnsTimelineLegacy._LAST_MODIFIED_ON} integer not null, " +
                       "${Columns.TITLE} text not null, " +
                       "${Columns.TEXT} text not null" +
                       ");"

    object Columns
    {
        val TITLE = "title"
        val TEXT = "text"
    }
}
