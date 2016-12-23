package io.connorwyatt.flashcards.data.datasources.legacy

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import io.connorwyatt.flashcards.data.contracts.FlashcardCategoryContract
import io.connorwyatt.flashcards.exceptions.SQLNoRowsAffectedException
import io.connorwyatt.flashcards.utils.ListUtils
import java.util.ArrayList

class FlashcardCategoryDataSourceLegacy : BaseDataSourceLegacy
{
    constructor(context: Context) : super(context)
    {
    }

    internal constructor(database: SQLiteDatabase) : super(database)
    {
    }

    fun updateFlashcardCategoryLinks(flashcardId: Long, categoryIds: List<Long>?)
    {
        var categoryIds = categoryIds
        if (categoryIds == null)
        {
            categoryIds = ArrayList<Long>()
        }

        val previousCategoryIds = getCategoryIdsForFlashcardId(flashcardId)
        val addedCategoryIds = ListUtils.difference(categoryIds, previousCategoryIds)
        val removedCategoryIds = ListUtils.difference(previousCategoryIds, categoryIds)

        for (id in addedCategoryIds)
        {
            addLink(flashcardId, id)
        }

        for (id in removedCategoryIds)
        {
            removeLink(flashcardId, id)
        }
    }

    fun getCategoryIdsForFlashcardId(flashcardId: Long): List<Long>
    {
        val categoryIds = ArrayList<Long>()

        val cursor = database!!.query(FlashcardCategoryContract.TABLE_NAME,
                                      arrayOf(FlashcardCategoryContract.Columns.CATEGORY_ID),
                                      FlashcardCategoryContract.Columns.FLASHCARD_ID + " = " +
                                      flashcardId,
                                      null, null, null, null)

        cursor.moveToFirst()

        while (!cursor.isAfterLast)
        {
            categoryIds.add(cursor.getLong(0))
            cursor.moveToNext()
        }

        cursor.close()

        return categoryIds
    }

    fun getFlashcardIdsForCategoryId(categoryId: Long): List<Long>
    {
        val flashcardIds = ArrayList<Long>()

        val cursor = database!!.query(FlashcardCategoryContract.TABLE_NAME,
                                      arrayOf(FlashcardCategoryContract.Columns.FLASHCARD_ID),
                                      FlashcardCategoryContract.Columns.CATEGORY_ID + " = " +
                                      categoryId,
                                      null, null, null, null)

        cursor.moveToFirst()

        while (!cursor.isAfterLast)
        {
            flashcardIds.add(cursor.getLong(0))
            cursor.moveToNext()
        }

        cursor.close()

        return flashcardIds
    }

    private fun addLink(flashcardId: Long, categoryId: Long)
    {
        val values = ContentValues()
        values.put(FlashcardCategoryContract.Columns.FLASHCARD_ID, flashcardId)
        values.put(FlashcardCategoryContract.Columns.CATEGORY_ID, categoryId)
        addCreateTimestamp(values)

        database!!.insertOrThrow(FlashcardCategoryContract.TABLE_NAME, null, values)
    }

    private fun removeLink(flashcardId: Long, categoryId: Long)
    {
        val whereClause = "${FlashcardCategoryContract.Columns.FLASHCARD_ID} = $flashcardId AND " +
                          "${FlashcardCategoryContract.Columns.CATEGORY_ID} = $categoryId"

        val rowsAffected = database!!.delete(FlashcardCategoryContract.TABLE_NAME,
                                             whereClause,
                                             null)

        if (rowsAffected == 0)
        {
            throw SQLNoRowsAffectedException()
        }
    }
}
