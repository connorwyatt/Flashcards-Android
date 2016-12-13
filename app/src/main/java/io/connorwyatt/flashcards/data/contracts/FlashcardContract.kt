package io.connorwyatt.flashcards.data.contracts

import io.connorwyatt.flashcards.data.entities.BaseColumnsTimeline

object FlashcardContract
{
    val TABLE_NAME = "flashcard"
    val TABLE_CREATE = "create table $TABLE_NAME(" +
                       "${BaseColumnsTimeline._ID} integer primary key autoincrement, " +
                       "${BaseColumnsTimeline._CREATED_ON} integer not null, " +
                       "${BaseColumnsTimeline._LAST_MODIFIED_ON} integer not null, " +
                       "${Columns.TITLE} text not null, " +
                       "${Columns.TEXT} text not null" +
                       ");"

    object Columns
    {
        val TITLE = "title"
        val TEXT = "text"
    }
}
