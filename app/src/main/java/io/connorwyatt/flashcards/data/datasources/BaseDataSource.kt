package io.connorwyatt.flashcards.data.datasources

import android.content.ContentValues
import android.content.Context
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import io.connorwyatt.flashcards.data.database.DBHelper
import io.connorwyatt.flashcards.data.entities.BaseColumnsTimeline
import io.connorwyatt.flashcards.data.entities.BaseEntity
import java.util.ArrayList

open class BaseDataSource
{
    protected var database: SQLiteDatabase? = null
    protected var dbHelper: DBHelper? = null

    /**
     * This constructor is for instantiating a data source with a preexisting database, typically
     * from another data source. This is useful as it means that working with transactions is
     * possible.

     * @param database The database instance to use.
     */
    internal constructor(database: SQLiteDatabase)
    {
        this.database = database
    }

    /**
     * This constructor is for instantiating a data source without a preexisting database, typically
     * from an activity.

     * @param context The context to use for the DBHelper class.
     */
    internal constructor(context: Context)
    {
        this.dbHelper = DBHelper(context)
    }

    @Throws(SQLException::class)
    fun open()
    {
        database = dbHelper!!.writableDatabase
    }

    fun close()
    {
        dbHelper!!.close()
    }

    protected fun addUpdateTimestamp(values: ContentValues)
    {
        val currentTimestamp = System.currentTimeMillis() / 1000
        values.put(BaseColumnsTimeline._LAST_MODIFIED_ON, currentTimestamp.toInt())
    }

    protected fun addCreateTimestamp(values: ContentValues)
    {
        val currentTimestamp = System.currentTimeMillis() / 1000
        values.put(BaseColumnsTimeline._CREATED_ON, currentTimestamp.toInt())
        values.put(BaseColumnsTimeline._LAST_MODIFIED_ON, currentTimestamp.toInt())
    }

    protected fun <T : BaseEntity> getIdsFromList(entityList: List<T>?): List<Long>
    {
        val idList = ArrayList<Long>()

        if (entityList != null)
        {
            for (entity in entityList)
            {
                idList.add(entity.id!!)
            }
        }

        return idList
    }
}
