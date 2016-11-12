package io.connorwyatt.flashcards.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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

    public Category getByName(String name) {
        Category category = null;

        Cursor cursor = database.query(CategoryContract.TABLE_NAME,
                                       allColumns,
                                       CategoryContract.Columns.NAME + " LIKE '" + name + "'", null,
                                       null, null, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            category = cursorToCategory(cursor);

            cursor.close();
        }

        return category;
    }

    public Category save(Category category) {
        boolean isCreate = category.getId() <= 0;

        ContentValues values = new ContentValues();
        values.put(CategoryContract.Columns.NAME, category.getName());

        long id;

        if (isCreate) {
            addCreateTimestamp(values);
            id = database.insertOrThrow(CategoryContract.TABLE_NAME, null, values);
        } else {
            id = category.getId();
            addUpdateTimestamp(values);
            int rowsAffected = database.update(CategoryContract.TABLE_NAME, values, CategoryContract.Columns._ID + " = " + id, null);

            if (rowsAffected == 0) {
                throw new SQLNoRowsAffectedException();
            }
        }

        return getById(id);
    }

    private Category cursorToCategory(Cursor cursor) {
        Category category = new Category();

        category.setId(cursor.getLong(0));
        category.setName(cursor.getString(1));

        return category;
    }
}
