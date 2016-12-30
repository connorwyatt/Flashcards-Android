package io.connorwyatt.flashcards.data.datasources.legacy

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import io.connorwyatt.flashcards.data.contracts.CategoryContract
import io.connorwyatt.flashcards.data.entities.legacy.BaseColumnsTimelineLegacy
import io.connorwyatt.flashcards.data.entities.legacy.CategoryLegacy
import io.connorwyatt.flashcards.exceptions.SQLNoRowsAffectedException
import java.util.ArrayList

@Deprecated("This is considered legacy.")
class CategoryDataSourceLegacy : BaseDataSourceLegacy
{
    private val allColumns = arrayOf(BaseColumnsTimelineLegacy._ID, CategoryContract.Columns.NAME)

    constructor(context: Context) : super(context)
    {
    }

    internal constructor(database: SQLiteDatabase) : super(database)
    {
    }

    val all: List<CategoryLegacy>
        get()
        {
            val categories = ArrayList<CategoryLegacy>()

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

    fun getById(id: Long): CategoryLegacy
    {
        val cursor = database!!.query(CategoryContract.TABLE_NAME,
                                      allColumns, BaseColumnsTimelineLegacy._ID + " = " + id, null,
                                      null, null, null)

        cursor.moveToFirst()

        val category = cursorToCategory(cursor)

        cursor.close()

        return category
    }

    fun getByName(name: String): CategoryLegacy?
    {
        var category: CategoryLegacy? = null

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

    fun save(category: CategoryLegacy): CategoryLegacy
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
                                                 BaseColumnsTimelineLegacy._ID + " = " + savedCategoryId,
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
                                                 BaseColumnsTimelineLegacy._ID + " = " + id, null)

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

    private fun cursorToCategory(cursor: Cursor): CategoryLegacy
    {
        val category = CategoryLegacy()

        category.id = cursor.getLong(0)
        category.name = cursor.getString(1)

        return category
    }
}
