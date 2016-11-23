package io.connorwyatt.flashcards.data.datasources;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import io.connorwyatt.flashcards.data.entities.BaseColumnsTimeline;
import io.connorwyatt.flashcards.data.entities.BaseEntity;
import io.connorwyatt.flashcards.data.database.DBHelper;

import java.util.ArrayList;
import java.util.List;

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

    protected void addUpdateTimestamp(ContentValues values) {
        Long currentTimestamp = System.currentTimeMillis() / 1000;
        values.put(BaseColumnsTimeline._LAST_MODIFIED_ON, currentTimestamp.intValue());
    }

    protected void addCreateTimestamp(ContentValues values) {
        Long currentTimestamp = System.currentTimeMillis() / 1000;
        values.put(BaseColumnsTimeline._CREATED_ON, currentTimestamp.intValue());
        values.put(BaseColumnsTimeline._LAST_MODIFIED_ON, currentTimestamp.intValue());
    }

    protected <T extends BaseEntity> List<Long> getIdsFromList(List<T> entityList) {
        List<Long> idList = new ArrayList<>();

        if (entityList != null) {
            for (T entity : entityList) {
                idList.add(entity.getId());
            }
        }

            return idList;
    }
}
