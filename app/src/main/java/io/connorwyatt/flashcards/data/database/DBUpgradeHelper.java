package io.connorwyatt.flashcards.data.database;

import java.lang.reflect.Method;

import android.database.sqlite.SQLiteDatabase;

import io.connorwyatt.flashcards.data.contracts.CategoryContract;
import io.connorwyatt.flashcards.data.contracts.FlashcardCategoryContract;
import io.connorwyatt.flashcards.data.contracts.FlashcardTestContract;
import io.connorwyatt.flashcards.exceptions.DBUpgradeException;

public class DBUpgradeHelper {
    private DBUpgradeHelper() {
    }

    public static void upgradeDB(SQLiteDatabase db, int oldVersion, int newVersion) throws
            DBUpgradeException {
        int currentVersion = oldVersion;

        while (currentVersion < newVersion) {
            int currentTargetVersion = currentVersion + 1;
            try {
                Method m = DBUpgradeHelper.class.getDeclaredMethod("upgrade" + currentVersion +
                        "to" + currentTargetVersion, SQLiteDatabase.class);

                if (m != null) {
                    m.invoke(null, db);
                }
            } catch (Exception e) {
                throw new DBUpgradeException(e, currentVersion, currentTargetVersion);
            }

            currentVersion = currentTargetVersion;
        }
    }

    /**
     * Changes in between these versions were:
     * <p>
     * - Category table was created.
     * - FlashcardCategory table was created.
     */
    private static void upgrade5to6(SQLiteDatabase db) {
        db.beginTransaction();
        db.execSQL(CategoryContract.TABLE_CREATE);
        db.execSQL(FlashcardCategoryContract.TABLE_CREATE);
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    /**
     * Changes in between these versions were:
     * <p>
     * - Foreign keys added to FlashcardCategory table.
     */
    private static void upgrade6to7(SQLiteDatabase db) {
        db.beginTransaction();
        alterFlashcardCategoryTable(db);
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    /**
     * Changes in between these versions were:
     * <p>
     * - FlashcardTest table was created.
     */
    private static void upgrade7to8(SQLiteDatabase db) {
        db.beginTransaction();
        db.execSQL(FlashcardTestContract.TABLE_CREATE);
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    /**
     * Changes in between these versions were:
     * <p>
     * - The rating column in FlashcardTest has been changed to an integer.
     */
    private static void upgrade8to9(SQLiteDatabase db) {
        db.beginTransaction();
        alterFlashcardTestTable(db);
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    /**
     * Changes in between these versions were:
     * <p>
     * - All foreign keys have been set to use cascade delete.
     */
    private static void upgrade9to10(SQLiteDatabase db) {
        db.beginTransaction();
        alterFlashcardCategoryTable(db);
        alterFlashcardTestTable(db);
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    private static void alterFlashcardCategoryTable(SQLiteDatabase db) {
        String tempTableName = FlashcardCategoryContract.TABLE_NAME + "_old";
        db.execSQL("ALTER TABLE " + FlashcardCategoryContract.TABLE_NAME
                + " RENAME TO " + tempTableName + ";");
        db.execSQL(FlashcardCategoryContract.TABLE_CREATE);
        db.execSQL("INSERT INTO " + FlashcardCategoryContract.TABLE_NAME +
                " SELECT "
                + FlashcardCategoryContract.Columns._ID + ", "
                + FlashcardCategoryContract.Columns._CREATED_ON + ", "
                + FlashcardCategoryContract.Columns._LAST_MODIFIED_ON + ", "
                + FlashcardCategoryContract.Columns.FLASHCARD_ID + ", "
                + FlashcardCategoryContract.Columns.CATEGORY_ID
                + " FROM "
                + tempTableName + ";");
        db.execSQL("DROP TABLE " + tempTableName + ";");
    }

    private static void alterFlashcardTestTable(SQLiteDatabase db) {
        String tempTableName = FlashcardTestContract.TABLE_NAME + "_old";
        db.execSQL("ALTER TABLE " + FlashcardTestContract.TABLE_NAME
                + " RENAME TO " + tempTableName + ";");
        db.execSQL(FlashcardTestContract.TABLE_CREATE);
        db.execSQL("INSERT INTO " + FlashcardTestContract.TABLE_NAME +
                " SELECT "
                + FlashcardTestContract.Columns._ID + ", "
                + FlashcardTestContract.Columns._CREATED_ON + ", "
                + FlashcardTestContract.Columns._LAST_MODIFIED_ON + ", "
                + FlashcardTestContract.Columns.FLASHCARD_ID + ", "
                + FlashcardTestContract.Columns.RATING
                + " FROM "
                + tempTableName + ";");
        db.execSQL("DROP TABLE " + tempTableName + ";");
    }
}
