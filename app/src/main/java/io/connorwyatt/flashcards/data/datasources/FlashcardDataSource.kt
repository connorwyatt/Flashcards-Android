package io.connorwyatt.flashcards.data.datasources

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import io.connorwyatt.flashcards.data.contracts.FlashcardContract
import io.connorwyatt.flashcards.data.entities.BaseColumnsTimeline
import io.connorwyatt.flashcards.data.entities.Category
import io.connorwyatt.flashcards.data.entities.Flashcard
import io.connorwyatt.flashcards.exceptions.SQLNoRowsAffectedException
import java.util.ArrayList

class FlashcardDataSource : BaseDataSource
{
    private val allColumns = arrayOf(BaseColumnsTimeline._ID,
                                     FlashcardContract.Columns.TITLE,
                                     FlashcardContract.Columns.TEXT)

    constructor(context: Context) : super(context)
    {
    }

    internal constructor(database: SQLiteDatabase) : super(database)
    {
    }

    val all: List<Flashcard>
        get()
        {
            val flashcards = ArrayList<Flashcard>()

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

    fun getById(id: Long): Flashcard
    {
        val cursor = database!!.query(FlashcardContract.TABLE_NAME,
                                      allColumns,
                                      "${BaseColumnsTimeline._ID} = $id",
                                      null,
                                      null,
                                      null,
                                      null)

        cursor.moveToFirst()

        val flashcard = cursorToFlashcard(cursor)

        cursor.close()

        return flashcard
    }

    fun getByCategory(categoryId: Long): List<Flashcard>
    {
        val fcds = FlashcardCategoryDataSource(database!!)
        val flashcardIds = fcds.getFlashcardIdsForCategoryId(categoryId)

        val flashcards = ArrayList<Flashcard>()

        flashcardIds.forEach { flashcards.add(getById(it)) }

        return flashcards
    }

    fun save(flashcard: Flashcard): Flashcard
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
                                                     "${BaseColumnsTimeline._ID} = $savedFlashcardId",
                                                     null)

                if (rowsAffected == 0)
                {
                    throw SQLNoRowsAffectedException()
                }
            }

            createNonexistentCategories(flashcard)

            val categoryIds = getIdsFromList(flashcard.categories)

            val fcds = FlashcardCategoryDataSource(database!!)
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
                                                 "${BaseColumnsTimeline._ID} = $id", null)

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

    private fun cursorToFlashcard(cursor: Cursor): Flashcard
    {
        val flashcard = Flashcard()

        flashcard.id = cursor.getLong(0)
        flashcard.title = cursor.getString(1)
        flashcard.text = cursor.getString(2)

        populateCategories(flashcard)

        return flashcard
    }

    private fun createNonexistentCategories(flashcard: Flashcard)
    {
        val savedCategories = ArrayList<Category>()
        val categories = flashcard.categories

        val cds = CategoryDataSource(database!!)

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

    private fun populateCategories(flashcard: Flashcard)
    {
        val fcds = FlashcardCategoryDataSource(database!!)
        val categoryIds = fcds.getCategoryIdsForFlashcardId(flashcard.id!!)

        val cds = CategoryDataSource(database!!)

        flashcard.categories = categoryIds.map { cds.getById(it) }.toMutableList()
    }
}
