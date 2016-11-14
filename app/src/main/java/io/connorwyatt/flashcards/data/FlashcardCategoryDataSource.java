package io.connorwyatt.flashcards.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import io.connorwyatt.flashcards.exceptions.SQLNoRowsAffectedException;
import io.connorwyatt.flashcards.utils.ListUtils;

public class FlashcardCategoryDataSource extends BaseDataSource {
    public FlashcardCategoryDataSource(Context context) {
        super(context);
    }

    FlashcardCategoryDataSource(SQLiteDatabase database) {
        super(database);
    }

    public void updateFlashcardCategoryLinks(long flashcardId, List<Long> categoryIds) {
        if (categoryIds == null) {
            categoryIds = new ArrayList<>();
        }

        List<Long> previousCategoryIds = getCategoryIdsForFlashcardId(flashcardId);
        List<Long> addedCategoryIds = ListUtils.difference(categoryIds, previousCategoryIds);
        List<Long> removedCategoryIds = ListUtils.difference(previousCategoryIds, categoryIds);

        for (long id : addedCategoryIds) {
            addLink(flashcardId, id);
        }

        for (long id : removedCategoryIds) {
            removeLink(flashcardId, id);
        }
    }

    public List<Long> getCategoryIdsForFlashcardId(long flashcardId) {
        List<Long> categoryIds = new ArrayList<>();

        Cursor cursor = database.query(FlashcardCategoryContract.TABLE_NAME,
                                       new String[]{FlashcardCategoryContract.Columns.CATEGORY_ID},
                                       FlashcardCategoryContract.Columns.FLASHCARD_ID + " = " +
                                               flashcardId,
                                       null, null, null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            categoryIds.add(cursor.getLong(0));
            cursor.moveToNext();
        }

        cursor.close();

        return categoryIds;
    }

    public List<Long> getFlashcardIdsForCategoryId(long categoryId) {
        List<Long> flashcardIds = new ArrayList<>();

        Cursor cursor = database.query(FlashcardCategoryContract.TABLE_NAME,
                                       new String[]{FlashcardCategoryContract.Columns.FLASHCARD_ID},
                                       FlashcardCategoryContract.Columns.CATEGORY_ID + " = " +
                                               categoryId,
                                       null, null, null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            flashcardIds.add(cursor.getLong(0));
            cursor.moveToNext();
        }

        cursor.close();

        return flashcardIds;
    }

    private void addLink(long flashcardId, long categoryId) {
        ContentValues values = new ContentValues();
        values.put(FlashcardCategoryContract.Columns.FLASHCARD_ID, flashcardId);
        values.put(FlashcardCategoryContract.Columns.CATEGORY_ID, categoryId);
        addCreateTimestamp(values);

        database.insertOrThrow(FlashcardCategoryContract.TABLE_NAME, null, values);
    }

    private void removeLink(long flashcardId, long categoryId) {
        String whereClause =
                FlashcardCategoryContract.Columns.FLASHCARD_ID + " = " + flashcardId +
                        " AND " +
                        FlashcardCategoryContract.Columns.CATEGORY_ID + " = " + categoryId;

        int rowsAffected = database.delete(FlashcardCategoryContract.TABLE_NAME, whereClause, null);

        if (rowsAffected == 0) {
            throw new SQLNoRowsAffectedException();
        }
    }
}
