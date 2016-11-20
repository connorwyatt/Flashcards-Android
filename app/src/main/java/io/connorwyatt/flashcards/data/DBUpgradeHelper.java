package io.connorwyatt.flashcards.data;

import android.database.sqlite.SQLiteDatabase;
import io.connorwyatt.flashcards.exceptions.DBUpgradeException;

import java.lang.reflect.Method;

public class DBUpgradeHelper {
    private DBUpgradeHelper() {
    }

    public static void upgradeDB(SQLiteDatabase db, int oldVersion, int newVersion) throws DBUpgradeException {
        int currentVersion = oldVersion;

        while (currentVersion < newVersion) {
            int currentTargetVersion = currentVersion + 1;
            try {
                Method m = DBUpgradeHelper.class.getDeclaredMethod("upgrade" + currentVersion + "to" + currentTargetVersion, SQLiteDatabase.class);

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
        db.execSQL(CategoryContract.TABLE_CREATE);
        db.execSQL(FlashcardCategoryContract.TABLE_CREATE);
    }

    /**
     * Changes in between these versions were:
     * <p>
     * - Foreign keys added to FlashcardCategory table.
     */
    private static void upgrade6to7(SQLiteDatabase db) {
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

    /**
     * Changes in between these versions were:
     * <p>
     * - FlashcardTest table was created.
     */
    private static void upgrade7to8(SQLiteDatabase db) {
        db.execSQL(FlashcardTestContract.TABLE_CREATE);
    }
}
