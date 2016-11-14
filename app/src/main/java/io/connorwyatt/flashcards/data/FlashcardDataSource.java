package io.connorwyatt.flashcards.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import io.connorwyatt.flashcards.exceptions.SQLNoRowsAffectedException;

public class FlashcardDataSource extends BaseDataSource {
    private String[] allColumns = {FlashcardContract.Columns._ID, FlashcardContract.Columns.TITLE, FlashcardContract.Columns.TEXT};

    public FlashcardDataSource(Context context) {
        super(context);
    }

    FlashcardDataSource(SQLiteDatabase database) {
        super(database);
    }

    public Flashcard getById(long id) {
        Cursor cursor = database.query(FlashcardContract.TABLE_NAME,
                allColumns, FlashcardContract.Columns._ID + " = " + id, null, null, null, null);

        cursor.moveToFirst();

        Flashcard flashcard = cursorToFlashcard(cursor);

        cursor.close();

        return flashcard;
    }

    public List<Flashcard> getByCategory(long categoryId) {
        FlashcardCategoryDataSource fcds = new FlashcardCategoryDataSource(database);
        List<Long> flashcardIds = fcds.getFlashcardIdsForCategoryId(categoryId);

        List<Flashcard> flashcards = new ArrayList<>();

        for (Long flashcardId : flashcardIds) {
            flashcards.add(getById(flashcardId));
        }

        return flashcards;
    }

    public List<Flashcard> getAll() {
        List<Flashcard> flashcards = new ArrayList<>();

        Cursor cursor = database.query(FlashcardContract.TABLE_NAME,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Flashcard flashcard = cursorToFlashcard(cursor);
            flashcards.add(flashcard);
            cursor.moveToNext();
        }

        cursor.close();

        return flashcards;
    }

    public Flashcard save(Flashcard flashcard) {
        long savedFlashcardId;

        try {
            database.beginTransaction();

            boolean isCreate = flashcard.getId() <= 0;

            ContentValues values = new ContentValues();
            values.put(FlashcardContract.Columns.TITLE, flashcard.getTitle());
            values.put(FlashcardContract.Columns.TEXT, flashcard.getText());

            if (isCreate) {
                addCreateTimestamp(values);
                savedFlashcardId = database.insertOrThrow(FlashcardContract.TABLE_NAME, null, values);
            } else {
                savedFlashcardId = flashcard.getId();
                addUpdateTimestamp(values);
                int rowsAffected = database.update(FlashcardContract.TABLE_NAME, values,
                                                   FlashcardContract.Columns._ID + " = " + savedFlashcardId,
                                                   null);

                if (rowsAffected == 0) {
                    throw new SQLNoRowsAffectedException();
                }
            }

            createNonexistentCategories(flashcard);

            List<Long> categoryIds = getIdsFromList(flashcard.getCategories());

            FlashcardCategoryDataSource fcds = new FlashcardCategoryDataSource(database);
            fcds.updateFlashcardCategoryLinks(savedFlashcardId, categoryIds);

            database.setTransactionSuccessful();
        } catch (Exception e) {
            database.endTransaction();
            throw e;
        }

        database.endTransaction();

        return getById(savedFlashcardId);
    }

    public void deleteById(long id) {
        int rowsAffected = database.delete(FlashcardContract.TABLE_NAME, FlashcardContract.Columns._ID + " = " + id, null);

        if (rowsAffected == 0) {
            throw new SQLNoRowsAffectedException();
        }
    }

    private Flashcard cursorToFlashcard(Cursor cursor) {
        Flashcard flashcard = new Flashcard();

        flashcard.setId(cursor.getLong(0));
        flashcard.setTitle(cursor.getString(1));
        flashcard.setText(cursor.getString(2));

        populateCategories(flashcard);

        return flashcard;
    }

    private void createNonexistentCategories(Flashcard flashcard) {
        List<Category> savedCategories = new ArrayList<>();
        List<Category> categories = flashcard.getCategories();

        if (categories != null) {
            CategoryDataSource cds = new CategoryDataSource(database);

            for (Category category : categories) {
                Category dbCategory = cds.getByName(category.getName());

                if (dbCategory == null) {
                    dbCategory = cds.save(category);
                }

                savedCategories.add(dbCategory);
            }

            flashcard.setCategories(savedCategories);
        }
    }

    private void populateCategories(Flashcard flashcard) {
        FlashcardCategoryDataSource fcds = new FlashcardCategoryDataSource(database);
        List<Long> categoryIds = fcds.getCategoryIdsForFlashcardId(flashcard.getId());
        List<Category> categories = new ArrayList<>();

        CategoryDataSource cds = new CategoryDataSource(database);
        for (Long categoryId : categoryIds) {
            categories.add(cds.getById(categoryId));
        }

        flashcard.setCategories(categories);
    }
}
