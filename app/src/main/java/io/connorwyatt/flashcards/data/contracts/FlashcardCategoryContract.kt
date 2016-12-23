package io.connorwyatt.flashcards.data.contracts

import io.connorwyatt.flashcards.data.entities.legacy.BaseColumnsTimelineLegacy

object FlashcardCategoryContract
{
    val TABLE_NAME = "flashcard_category"
    val TABLE_CREATE = "create table $TABLE_NAME(" +
                       "${BaseColumnsTimelineLegacy._ID} integer primary key autoincrement, " +
                       "${BaseColumnsTimelineLegacy._CREATED_ON} integer not null, " +
                       "${BaseColumnsTimelineLegacy._LAST_MODIFIED_ON} integer not null, " +
                       "${Columns.FLASHCARD_ID} integer not null, " +
                       "${Columns.CATEGORY_ID} integer not null," +
                       "FOREIGN KEY(${Columns.FLASHCARD_ID}) " +
                       "REFERENCES ${FlashcardContract.TABLE_NAME}(${BaseColumnsTimelineLegacy._ID}) " +
                       "ON DELETE CASCADE, " +
                       "FOREIGN KEY(${Columns.CATEGORY_ID}) " +
                       "REFERENCES ${CategoryContract.TABLE_NAME}(${BaseColumnsTimelineLegacy._ID}) " +
                       "ON DELETE CASCADE" +
                       ");"

    object Columns
    {
        val FLASHCARD_ID = "flashcard_id"
        val CATEGORY_ID = "category_id"
    }
}
