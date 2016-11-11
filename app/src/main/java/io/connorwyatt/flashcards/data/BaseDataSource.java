package io.connorwyatt.flashcards.data;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class BaseDataSource {
    protected SQLiteDatabase database;
    protected DBHelper dbHelper;

    /**
     * This constructor is for instantiating a data source with a preexisting database, typically
     * from another data source. This is useful as it means that working with transactions is
     * possible.
     *
     * @param database The database instance to use.
     */
    BaseDataSource(SQLiteDatabase database) {
        this.database = database;
    }

    /**
     * This constructor is for instantiating a data source without a preexisting database, typically
     * from an activity.
     *
     * @param context The context to use for the DBHelper class.
     */
    BaseDataSource(Context context) {
        this.dbHelper = new DBHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }
}
