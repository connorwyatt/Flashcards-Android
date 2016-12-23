package io.connorwyatt.flashcards.data.datasources.legacy

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import io.connorwyatt.flashcards.data.contracts.FlashcardContract
import io.connorwyatt.flashcards.data.entities.legacy.BaseColumnsTimelineLegacy
import io.connorwyatt.flashcards.data.entities.legacy.CategoryLegacy
import io.connorwyatt.flashcards.data.entities.legacy.FlashcardLegacy
import io.connorwyatt.flashcards.exceptions.SQLNoRowsAffectedException
import java.util.ArrayList

class FlashcardDataSourceLegacy : BaseDataSourceLegacy
{
    private val allColumns = arrayOf(BaseColumnsTimelineLegacy._ID,
                                     FlashcardContract.Columns.TITLE,
                                     FlashcardContract.Columns.TEXT)

    constructor(context: Context) : super(context)
    {
    }

    internal constructor(database: SQLiteDatabase) : super(database)
    {
    }

    val all: List<FlashcardLegacy>
        get()
        {
            val flashcards = ArrayList<FlashcardLegacy>()

            val cursor = database!!.query(FlashcardContract.TABLE_NAME,
                                          allColumns, null, null, null, null, null)

            cursor.moveToFirst()

            while (!cursor.isAfterLast)
            {
                val flashcard = cursorToFlashcard(cursor)
                flashcards.add(flashcard)
                cursor.moveToNext()
            }

            cursor.close()

            return flashcards
        }

    fun getById(id: Long): FlashcardLegacy
    {
        val cursor = database!!.query(FlashcardContract.TABLE_NAME,
                                      allColumns,
                                      "${BaseColumnsTimelineLegacy._ID} = $id",
                                      null,
                                      null,
                                      null,
                                      null)

        cursor.moveToFirst()

        val flashcard = cursorToFlashcard(cursor)

        cursor.close()

        return flashcard
    }

    fun getByCategory(categoryId: Long): List<FlashcardLegacy>
    {
        val fcds = FlashcardCategoryDataSourceLegacy(database!!)
        val flashcardIds = fcds.getFlashcardIdsForCategoryId(categoryId)

        val flashcards = ArrayList<FlashcardLegacy>()

        flashcardIds.forEach { flashcards.add(getById(it)) }

        return flashcards
    }

    fun save(flashcard: FlashcardLegacy): FlashcardLegacy
    {
        val savedFlashcardId: Long

        try
        {
            database!!.beginTransaction()

            val values = ContentValues()
            values.put(FlashcardContract.Columns.TITLE, flashcard.title)
            values.put(FlashcardContract.Columns.TEXT, flashcard.text)

            if (!flashcard.existsInDatabase())
            {
                addCreateTimestamp(values)
                savedFlashcardId = database!!.insertOrThrow(FlashcardContract.TABLE_NAME, null,
                                                            values)
            }
            else
            {
                savedFlashcardId = flashcard.id!!
                addUpdateTimestamp(values)
                val rowsAffected = database!!.update(FlashcardContract.TABLE_NAME, values,
                                                     "${BaseColumnsTimelineLegacy._ID} = $savedFlashcardId",
                                                     null)

                if (rowsAffected == 0)
                {
                    throw SQLNoRowsAffectedException()
                }
            }

            createNonexistentCategories(flashcard)

            val categoryIds = getIdsFromList(flashcard.categories)

            val fcds = FlashcardCategoryDataSourceLegacy(database!!)
            fcds.updateFlashcardCategoryLinks(savedFlashcardId, categoryIds)

            database!!.setTransactionSuccessful()
        }
        catch (e: Exception)
        {
            database!!.endTransaction()
            throw e
        }

        database!!.endTransaction()

        return getById(savedFlashcardId)
    }

    fun deleteById(id: Long)
    {
        try
        {
            database!!.beginTransaction()

            val rowsAffected = database!!.delete(FlashcardContract.TABLE_NAME,
                                                 "${BaseColumnsTimelineLegacy._ID} = $id", null)

            if (rowsAffected == 0)
            {
                throw SQLNoRowsAffectedException()
            }

            database!!.setTransactionSuccessful()
        }
        catch (e: Exception)
        {
            database!!.endTransaction()
            throw e
        }

        database!!.endTransaction()
    }

    fun deleteByCategory(categoryId: Long)
    {
        getByCategory(categoryId).forEach { deleteById(it.id!!) }
    }

    private fun cursorToFlashcard(cursor: Cursor): FlashcardLegacy
    {
        val flashcard = FlashcardLegacy()

        flashcard.id = cursor.getLong(0)
        flashcard.title = cursor.getString(1)
        flashcard.text = cursor.getString(2)

        populateCategories(flashcard)

        return flashcard
    }

    private fun createNonexistentCategories(flashcard: FlashcardLegacy)
    {
        val savedCategories = ArrayList<CategoryLegacy>()
        val categories = flashcard.categories

        val cds = CategoryDataSourceLegacy(database!!)

        for (category in categories)
        {
            var dbCategory = cds.getByName(category.name!!)

            if (dbCategory == null)
            {
                dbCategory = cds.save(category)
            }

            savedCategories.add(dbCategory)
        }

        flashcard.categories = savedCategories
    }

    private fun populateCategories(flashcard: FlashcardLegacy)
    {
        val fcds = FlashcardCategoryDataSourceLegacy(database!!)
        val categoryIds = fcds.getCategoryIdsForFlashcardId(flashcard.id!!)

        val cds = CategoryDataSourceLegacy(database!!)

        flashcard.categories = categoryIds.map { cds.getById(it) }.toMutableList()
    }
}
