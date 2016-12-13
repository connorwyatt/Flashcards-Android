package io.connorwyatt.flashcards.data.contracts

import io.connorwyatt.flashcards.data.entities.BaseColumnsTimeline

object CategoryContract
{
    val TABLE_NAME = "category"
    val TABLE_CREATE = "create table $TABLE_NAME(" +
                       "${BaseColumnsTimeline._ID} integer primary key autoincrement," +
                       "${BaseColumnsTimeline._CREATED_ON} integer not null," +
                       "${BaseColumnsTimeline._LAST_MODIFIED_ON} integer not null," +
                       "${Columns.NAME} text not null" +
                       ");"

    object Columns
    {
        val NAME = "name"
    }
}
