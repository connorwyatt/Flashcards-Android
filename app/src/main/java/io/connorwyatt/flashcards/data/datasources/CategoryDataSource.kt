package io.connorwyatt.flashcards.data.datasources

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import io.connorwyatt.flashcards.data.contracts.CategoryContract
import io.connorwyatt.flashcards.data.entities.BaseColumnsTimeline
import io.connorwyatt.flashcards.data.entities.Category
import io.connorwyatt.flashcards.exceptions.SQLNoRowsAffectedException
import java.util.ArrayList

class CategoryDataSource : BaseDataSource
{
    private val allColumns = arrayOf(BaseColumnsTimeline._ID, CategoryContract.TABLE_NAME)

    constructor(context: Context) : super(context)
    {
    }

    internal constructor(database: SQLiteDatabase) : super(database)
    {
    }

    val all: List<Category>
        get()
        {
            val categories = ArrayList<Category>()

            val cursor = database!!
                .query(CategoryContract.TABLE_NAME, allColumns, null, null, null, null, null)

            cursor.moveToFirst()

            while (!cursor.isAfterLast)
            {
                val category = cursorToCategory(cursor)
                categories.add(category)
                cursor.moveToNext()
            }

            cursor.close()

            return categories
        }

    fun getById(id: Long): Category
    {
        val cursor = database!!.query(CategoryContract.TABLE_NAME,
                                      allColumns, BaseColumnsTimeline._ID + " = " + id, null,
                                      null, null, null)

        cursor.moveToFirst()

        val category = cursorToCategory(cursor)

        cursor.close()

        return category
    }

    fun getByName(name: String): Category?
    {
        var category: Category? = null

        val cursor = database!!.query(
            CategoryContract.TABLE_NAME, allColumns,
            "${CategoryContract.Columns.NAME} LIKE '$name'", null, null, null, null)

        if (cursor.count > 0)
        {
            cursor.moveToFirst()

            category = cursorToCategory(cursor)

            cursor.close()
        }

        return category
    }

    fun save(category: Category): Category
    {
        val values = ContentValues()
        values.put(CategoryContract.Columns.NAME, category.name)

        val savedCategoryId: Long?

        if (!category.existsInDatabase())
        {
            addCreateTimestamp(values)
            savedCategoryId = database!!.insertOrThrow(CategoryContract.TABLE_NAME, null, values)
        }
        else
        {
            savedCategoryId = category.id
            addUpdateTimestamp(values)
            val rowsAffected = database!!.update(CategoryContract.TABLE_NAME,
                                                 values,
                                                 BaseColumnsTimeline._ID + " = " + savedCategoryId,
                                                 null)

            if (rowsAffected == 0)
            {
                throw SQLNoRowsAffectedException()
            }
        }

        return getById(savedCategoryId!!)
    }

    fun deleteById(id: Long)
    {
        try
        {
            database!!.beginTransaction()

            val rowsAffected = database!!.delete(CategoryContract.TABLE_NAME,
                                                 BaseColumnsTimeline._ID + " = " + id, null)

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

    private fun cursorToCategory(cursor: Cursor): Category
    {
        val category = Category()

        category.id = cursor.getLong(0)
        category.name = cursor.getString(1)

        return category
    }
}
