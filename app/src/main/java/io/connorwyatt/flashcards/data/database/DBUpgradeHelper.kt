package io.connorwyatt.flashcards.data.database

import android.database.sqlite.SQLiteDatabase
import io.connorwyatt.flashcards.data.contracts.CategoryContract
import io.connorwyatt.flashcards.data.contracts.FlashcardCategoryContract
import io.connorwyatt.flashcards.data.contracts.FlashcardTestContract
import io.connorwyatt.flashcards.data.entities.legacy.BaseColumnsTimelineLegacy
import io.connorwyatt.flashcards.exceptions.DBUpgradeException

object DBUpgradeHelper
{
    @Throws(DBUpgradeException::class)
    fun upgradeDB(db: SQLiteDatabase, oldVersion: Int, newVersion: Int)
    {
        var currentVersion = oldVersion

        while (currentVersion < newVersion)
        {
            val currentTargetVersion = currentVersion + 1
            try
            {
                val m = DBUpgradeHelper::class.java
                    .getDeclaredMethod(
                        "upgrade${currentVersion}to$currentTargetVersion",
                        SQLiteDatabase::class.java
                    )

                m?.invoke(null, db)
            }
            catch (e: Exception)
            {
                throw DBUpgradeException(e, currentVersion, currentTargetVersion)
            }

            currentVersion = currentTargetVersion
        }
    }

    /**
     * Changes in between these versions were:
     *
     *
     * - CategoryLegacy table was created.
     * - FlashcardCategory table was created.
     */
    private fun upgrade5to6(db: SQLiteDatabase)
    {
        db.beginTransaction()
        db.execSQL(CategoryContract.TABLE_CREATE)
        db.execSQL(FlashcardCategoryContract.TABLE_CREATE)
        db.setTransactionSuccessful()
        db.endTransaction()
    }

    /**
     * Changes in between these versions were:
     *
     *
     * - Foreign keys added to FlashcardCategory table.
     */
    private fun upgrade6to7(db: SQLiteDatabase)
    {
        db.beginTransaction()
        alterFlashcardCategoryTable(db)
        db.setTransactionSuccessful()
        db.endTransaction()
    }

    /**
     * Changes in between these versions were:
     *
     *
     * - FlashcardTestLegacy table was created.
     */
    private fun upgrade7to8(db: SQLiteDatabase)
    {
        db.beginTransaction()
        db.execSQL(FlashcardTestContract.TABLE_CREATE)
        db.setTransactionSuccessful()
        db.endTransaction()
    }

    /**
     * Changes in between these versions were:
     *
     *
     * - The rating column in FlashcardTestLegacy has been changed to an integer.
     */
    private fun upgrade8to9(db: SQLiteDatabase)
    {
        db.beginTransaction()
        alterFlashcardTestTable(db)
        db.setTransactionSuccessful()
        db.endTransaction()
    }

    /**
     * Changes in between these versions were:
     *
     *
     * - All foreign keys have been set to use cascade delete.
     */
    private fun upgrade9to10(db: SQLiteDatabase)
    {
        db.beginTransaction()
        alterFlashcardCategoryTable(db)
        alterFlashcardTestTable(db)
        db.setTransactionSuccessful()
        db.endTransaction()
    }

    private fun alterFlashcardCategoryTable(db: SQLiteDatabase)
    {
        val tempTableName = "${FlashcardCategoryContract.TABLE_NAME}_old"
        db.execSQL("ALTER TABLE ${FlashcardCategoryContract.TABLE_NAME} RENAME TO $tempTableName;")
        db.execSQL(FlashcardCategoryContract.TABLE_CREATE)
        db.execSQL("INSERT INTO ${FlashcardCategoryContract.TABLE_NAME} " +
                   "SELECT " +
                   "${BaseColumnsTimelineLegacy._ID}, " +
                   "${BaseColumnsTimelineLegacy._CREATED_ON}, " +
                   "${BaseColumnsTimelineLegacy._LAST_MODIFIED_ON}, " +
                   "${FlashcardCategoryContract.Columns.FLASHCARD_ID}, " +
                   "${FlashcardCategoryContract.Columns.CATEGORY_ID} " +
                   "FROM $tempTableName;"
        )
        db.execSQL("DROP TABLE $tempTableName;")
    }

    private fun alterFlashcardTestTable(db: SQLiteDatabase)
    {
        val tempTableName = "${FlashcardTestContract.TABLE_NAME}_old"
        db.execSQL("ALTER TABLE ${FlashcardTestContract.TABLE_NAME} RENAME TO $tempTableName;")
        db.execSQL(FlashcardTestContract.TABLE_CREATE)
        db.execSQL("INSERT INTO ${FlashcardTestContract.TABLE_NAME} " +
                   "SELECT ${BaseColumnsTimelineLegacy._ID}, " +
                   "${BaseColumnsTimelineLegacy._CREATED_ON}, " +
                   "${BaseColumnsTimelineLegacy._LAST_MODIFIED_ON}, " +
                   "${FlashcardTestContract.Columns.FLASHCARD_ID}, " +
                   "${FlashcardTestContract.Columns.RATING} " +
                   "FROM $tempTableName;"
        )
        db.execSQL("DROP TABLE $tempTableName;")
    }
}
