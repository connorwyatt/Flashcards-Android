package io.connorwyatt.flashcards.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "flashcards.db";
    private static final int DATABASE_VERSION = 9;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase database) {
        super.onConfigure(database);
        database.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(FlashcardContract.TABLE_CREATE);
        database.execSQL(CategoryContract.TABLE_CREATE);
        database.execSQL(FlashcardCategoryContract.TABLE_CREATE);
        database.execSQL(FlashcardTestContract.TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        DBUpgradeHelper.upgradeDB(database, oldVersion, newVersion);
    }
}
