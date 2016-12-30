package io.connorwyatt.flashcards.data.datasources.legacy

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import io.connorwyatt.flashcards.data.contracts.FlashcardTestContract
import io.connorwyatt.flashcards.data.entities.legacy.BaseColumnsTimelineLegacy
import io.connorwyatt.flashcards.data.entities.legacy.FlashcardTestLegacy
import io.connorwyatt.flashcards.exceptions.SQLNoRowsAffectedException
import java.util.ArrayList

@Deprecated("This is considered legacy.")
class FlashcardTestDataSourceLegacy : BaseDataSourceLegacy
{
    private val allColumns = arrayOf(BaseColumnsTimelineLegacy._ID,
                                     FlashcardTestContract.Columns.FLASHCARD_ID,
                                     FlashcardTestContract.Columns.RATING)

    constructor(context: Context) : super(context)
    {
    }

    internal constructor(database: SQLiteDatabase) : super(database)
    {
    }

    fun getById(id: Long): FlashcardTestLegacy
    {
        val cursor = database!!.query(FlashcardTestContract.TABLE_NAME,
                                      allColumns,
                                      BaseColumnsTimelineLegacy._ID + " = " + id,
                                      null,
                                      null,
                                      null,
                                      null)

        cursor.moveToFirst()

        val flashcardTest = cursorToFlashcardTest(cursor)

        cursor.close()

        return flashcardTest
    }

    fun getByFlashcardId(flashcardId: Long): List<FlashcardTestLegacy>
    {
        val cursor = database!!.query(FlashcardTestContract.TABLE_NAME,
                                      allColumns,
                                      FlashcardTestContract.Columns.FLASHCARD_ID + " = " + flashcardId,
                                      null,
                                      null,
                                      null,
                                      null)
        val flashcardTests = ArrayList<FlashcardTestLegacy>()

        cursor.moveToFirst()

        while (!cursor.isAfterLast)
        {
            flashcardTests.add(cursorToFlashcardTest(cursor))

            cursor.moveToNext()
        }

        cursor.close()

        return flashcardTests
    }

    fun save(flashcardTest: FlashcardTestLegacy): FlashcardTestLegacy
    {
        val savedFlashcardTestId: Long

        try
        {
            database!!.beginTransaction()

            val values = ContentValues()
            values.put(FlashcardTestContract.Columns.RATING, flashcardTest.rating!!.ordinal)
            values.put(FlashcardTestContract.Columns.FLASHCARD_ID, flashcardTest.flashcardId)

            if (!flashcardTest.existsInDatabase())
            {
                addCreateTimestamp(values)
                savedFlashcardTestId = database!!.insertOrThrow(FlashcardTestContract.TABLE_NAME,
                                                                null, values)
            }
            else
            {
                savedFlashcardTestId = flashcardTest.id!!
                addUpdateTimestamp(values)
                val rowsAffected = database!!.update(FlashcardTestContract.TABLE_NAME, values,
                                                     BaseColumnsTimelineLegacy._ID + " = " + savedFlashcardTestId,
                                                     null)

                if (rowsAffected == 0)
                {
                    throw SQLNoRowsAffectedException()
                }
            }

            database!!.setTransactionSuccessful()
        }
        catch (e: Exception)
        {
            database!!.endTransaction()
            throw e
        }

        database!!.endTransaction()

        return getById(savedFlashcardTestId)
    }

    private fun cursorToFlashcardTest(cursor: Cursor): FlashcardTestLegacy
    {
        val flashcardTest = FlashcardTestLegacy()

        flashcardTest.id = cursor.getLong(0)
        flashcardTest.flashcardId = cursor.getLong(1)
        flashcardTest.setRatingFromInt(cursor.getInt(2))

        return flashcardTest
    }
}
