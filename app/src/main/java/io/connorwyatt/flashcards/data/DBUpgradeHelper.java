package io.connorwyatt.flashcards.data;

import android.database.sqlite.SQLiteDatabase;

import java.lang.reflect.Method;

import io.connorwyatt.flashcards.exceptions.DBUpgradeException;

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
}
