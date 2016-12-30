package io.connorwyatt.flashcards.data.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

import io.connorwyatt.flashcards.data.contracts.CategoryContract
import io.connorwyatt.flashcards.data.contracts.FlashcardCategoryContract
import io.connorwyatt.flashcards.data.contracts.FlashcardContract
import io.connorwyatt.flashcards.data.contracts.FlashcardTestContract

@Deprecated("This is considered legacy.")
class DBHelper(context: Context) :
    SQLiteOpenHelper(context, DBHelper.DATABASE_NAME, null, DBHelper.DATABASE_VERSION)
{

    override fun onConfigure(database: SQLiteDatabase)
    {
        super.onConfigure(database)
        database.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(database: SQLiteDatabase)
    {
        database.execSQL(FlashcardContract.TABLE_CREATE)
        database.execSQL(CategoryContract.TABLE_CREATE)
        database.execSQL(FlashcardCategoryContract.TABLE_CREATE)
        database.execSQL(FlashcardTestContract.TABLE_CREATE)
    }

    override fun onUpgrade(database: SQLiteDatabase, oldVersion: Int, newVersion: Int)
    {
        DBUpgradeHelper.upgradeDB(database, oldVersion, newVersion)
    }

    companion object
    {
        private val DATABASE_NAME = "flashcards.db"
        private val DATABASE_VERSION = 10
    }
}
