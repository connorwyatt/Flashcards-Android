package io.connorwyatt.flashcards.data.datasources

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import io.connorwyatt.flashcards.data.contracts.FlashcardTestContract
import io.connorwyatt.flashcards.data.entities.BaseColumnsTimeline
import io.connorwyatt.flashcards.data.entities.FlashcardTest
import io.connorwyatt.flashcards.exceptions.SQLNoRowsAffectedException
import java.util.ArrayList

class FlashcardTestDataSource : BaseDataSource
{
    private val allColumns = arrayOf(BaseColumnsTimeline._ID, FlashcardTestContract
        .Columns.FLASHCARD_ID, FlashcardTestContract.Columns.RATING)

    constructor(context: Context) : super(context)
    {
    }

    internal constructor(database: SQLiteDatabase) : super(database)
    {
    }

    fun getById(id: Long): FlashcardTest
    {
        val cursor = database!!.query(FlashcardTestContract.TABLE_NAME,
                                      allColumns,
                                      BaseColumnsTimeline._ID + " = " + id,
                                      null,
                                      null,
                                      null,
                                      null)

        cursor.moveToFirst()

        val flashcardTest = cursorToFlashcardTest(cursor)

        cursor.close()

        return flashcardTest
    }

    fun getByFlashcardId(flashcardId: Long): List<FlashcardTest>
    {
        val cursor = database!!.query(FlashcardTestContract.TABLE_NAME,
                                      allColumns,
                                      FlashcardTestContract.Columns.FLASHCARD_ID + " = " + flashcardId,
                                      null,
                                      null,
                                      null,
                                      null)
        val flashcardTests = ArrayList<FlashcardTest>()

        cursor.moveToFirst()

        while (!cursor.isAfterLast)
        {
            flashcardTests.add(cursorToFlashcardTest(cursor))

            cursor.moveToNext()
        }

        cursor.close()

        return flashcardTests
    }

    fun save(flashcardTest: FlashcardTest): FlashcardTest
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
                                                     BaseColumnsTimeline._ID + " = " + savedFlashcardTestId,
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

    private fun cursorToFlashcardTest(cursor: Cursor): FlashcardTest
    {
        val flashcardTest = FlashcardTest()

        flashcardTest.id = cursor.getLong(0)
        flashcardTest.flashcardId = cursor.getLong(1)
        flashcardTest.setRatingFromInt(cursor.getInt(2))

        return flashcardTest
    }
}
