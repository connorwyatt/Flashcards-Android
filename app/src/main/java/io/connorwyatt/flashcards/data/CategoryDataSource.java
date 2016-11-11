package io.connorwyatt.flashcards.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import io.connorwyatt.flashcards.exceptions.SQLNoRowsAffectedException;

public class CategoryDataSource extends BaseDataSource {
    private String[] allColumns = {CategoryContract.Columns._ID, CategoryContract.Columns.NAME};

    public CategoryDataSource(Context context) {
        super(context);
    }

    CategoryDataSource(SQLiteDatabase database) {
        super(database);
    }

    public Category getById(long id) {
        Cursor cursor = database.query(CategoryContract.TABLE_NAME,
                allColumns, CategoryContract.Columns._ID + " = " + id, null, null, null, null);

        cursor.moveToFirst();

        Category category = cursorToCategory(cursor);

        cursor.close();

        return category;
    }

    public Category save(Category category) {
        boolean isCreate = category.getId() <= 0;
        Long currentTimestamp = System.currentTimeMillis() / 1000;

        ContentValues values = new ContentValues();
        values.put(CategoryContract.Columns.NAME, category.getName());
        values.put(CategoryContract.Columns._LAST_MODIFIED_ON, currentTimestamp.intValue());

        long id;

        if (isCreate) {
            values.put(CategoryContract.Columns._CREATED_ON, currentTimestamp.intValue());
            id = database.insertOrThrow(CategoryContract.TABLE_NAME, null, values);
        } else {
            id = category.getId();
            int rowsAffected = database.update(CategoryContract.TABLE_NAME, values, CategoryContract.Columns._ID + " = " + id, null);

            if (rowsAffected == 0) {
                throw new SQLNoRowsAffectedException();
            }
        }

        Cursor cursor = database.query(CategoryContract.TABLE_NAME, allColumns, CategoryContract.Columns._ID + " = " + id, null, null, null, null);
        cursor.moveToFirst();
        Category newCategory = cursorToCategory(cursor);
        cursor.close();
        return newCategory;
    }

    private Category cursorToCategory(Cursor cursor) {
        Category category = new Category();

        category.setId(cursor.getLong(0));
        category.setName(cursor.getString(1));

        return category;
    }
}
