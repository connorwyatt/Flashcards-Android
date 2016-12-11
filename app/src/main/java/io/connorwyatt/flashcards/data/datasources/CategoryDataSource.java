package io.connorwyatt.flashcards.data.datasources;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import io.connorwyatt.flashcards.data.contracts.CategoryContract;
import io.connorwyatt.flashcards.data.entities.Category;
import io.connorwyatt.flashcards.exceptions.SQLNoRowsAffectedException;

public class CategoryDataSource extends BaseDataSource {
    private String[] allColumns = {CategoryContract.Columns._ID, CategoryContract.Columns.NAME};

    public CategoryDataSource(Context context) {
        super(context);
    }

    CategoryDataSource(SQLiteDatabase database) {
        super(database);
    }

    public List<Category> getAll() {
        List<Category> categories = new ArrayList<>();

        Cursor cursor = database
                .query(CategoryContract.TABLE_NAME, allColumns, null, null, null, null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Category category = cursorToCategory(cursor);
            categories.add(category);
            cursor.moveToNext();
        }

        cursor.close();

        return categories;
    }

    public Category getById(long id) {
        Cursor cursor = database.query(CategoryContract.TABLE_NAME,
                allColumns, CategoryContract.Columns._ID + " = " + id, null,
                null, null, null);

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
        ContentValues values = new ContentValues();
        values.put(CategoryContract.Columns.NAME, category.getName());

        Long savedCategoryId;

        if (!category.existsInDatabase()) {
            addCreateTimestamp(values);
            savedCategoryId = database.insertOrThrow(CategoryContract.TABLE_NAME, null, values);
        } else {
            savedCategoryId = category.getId();
            addUpdateTimestamp(values);
            int rowsAffected = database.update(CategoryContract.TABLE_NAME, values,
                    CategoryContract.Columns._ID + " = " + savedCategoryId, null);

            if (rowsAffected == 0) {
                throw new SQLNoRowsAffectedException();
            }
        }

        return getById(savedCategoryId);
    }

    public void deleteById(long id) {
        try {
            database.beginTransaction();

            int rowsAffected = database.delete(CategoryContract.TABLE_NAME,
                    CategoryContract.Columns._ID + " = " + id, null);

            if (rowsAffected == 0) {
                throw new SQLNoRowsAffectedException();
            }

            database.setTransactionSuccessful();
        } catch (Exception e) {
            database.endTransaction();
            throw e;
        }

        database.endTransaction();
    }

    private Category cursorToCategory(Cursor cursor) {
        Category category = new Category();

        category.setId(cursor.getLong(0));
        category.setName(cursor.getString(1));

        return category;
    }
}
