package io.connorwyatt.flashcards.data.datasources;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import io.connorwyatt.flashcards.data.contracts.FlashcardContract;
import io.connorwyatt.flashcards.data.entities.BaseColumnsTimeline;
import io.connorwyatt.flashcards.data.entities.Category;
import io.connorwyatt.flashcards.data.entities.Flashcard;
import io.connorwyatt.flashcards.exceptions.SQLNoRowsAffectedException;

public class FlashcardDataSource extends BaseDataSource {
    private String[] allColumns = {BaseColumnsTimeline.INSTANCE.get_ID(), FlashcardContract.Columns
            .INSTANCE.getTITLE(), FlashcardContract.Columns.INSTANCE.getTEXT()};

    public FlashcardDataSource(Context context) {
        super(context);
    }

    FlashcardDataSource(SQLiteDatabase database) {
        super(database);
    }

    public List<Flashcard> getAll() {
        List<Flashcard> flashcards = new ArrayList<>();

        Cursor cursor = getDatabase().query(FlashcardContract.INSTANCE.getTABLE_NAME(),
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

    public Flashcard getById(long id) {
        Cursor cursor = getDatabase().query(FlashcardContract.INSTANCE.getTABLE_NAME(),
                allColumns, BaseColumnsTimeline.INSTANCE.get_ID() + " = " + id, null, null, null,
                null);

        cursor.moveToFirst();

        Flashcard flashcard = cursorToFlashcard(cursor);

        cursor.close();

        return flashcard;
    }

    public List<Flashcard> getByCategory(long categoryId) {
        FlashcardCategoryDataSource fcds = new FlashcardCategoryDataSource(getDatabase());
        List<Long> flashcardIds = fcds.getFlashcardIdsForCategoryId(categoryId);

        List<Flashcard> flashcards = new ArrayList<>();

        for (Long flashcardId : flashcardIds) {
            flashcards.add(getById(flashcardId));
        }

        return flashcards;
    }

    public Flashcard save(Flashcard flashcard) {
        long savedFlashcardId;

        try {
            getDatabase().beginTransaction();

            ContentValues values = new ContentValues();
            values.put(FlashcardContract.Columns.INSTANCE.getTITLE(), flashcard.getTitle());
            values.put(FlashcardContract.Columns.INSTANCE.getTEXT(), flashcard.getText());

            if (!flashcard.existsInDatabase()) {
                addCreateTimestamp(values);
                savedFlashcardId = getDatabase().insertOrThrow(FlashcardContract.INSTANCE.getTABLE_NAME(), null,
                        values);
            } else {
                savedFlashcardId = flashcard.getId();
                addUpdateTimestamp(values);
                int rowsAffected = getDatabase().update(FlashcardContract.INSTANCE.getTABLE_NAME
                                (), values,
                        BaseColumnsTimeline.INSTANCE.get_ID() + " = " + savedFlashcardId,
                        null);

                if (rowsAffected == 0) {
                    throw new SQLNoRowsAffectedException();
                }
            }

            createNonexistentCategories(flashcard);

            List<Long> categoryIds = getIdsFromList(flashcard.getCategories());

            FlashcardCategoryDataSource fcds = new FlashcardCategoryDataSource(getDatabase());
            fcds.updateFlashcardCategoryLinks(savedFlashcardId, categoryIds);

            getDatabase().setTransactionSuccessful();
        } catch (Exception e) {
            getDatabase().endTransaction();
            throw e;
        }

        getDatabase().endTransaction();

        return getById(savedFlashcardId);
    }

    public void deleteById(long id) {
        try {
            getDatabase().beginTransaction();

            int rowsAffected = getDatabase().delete(FlashcardContract.INSTANCE.getTABLE_NAME(),
                    BaseColumnsTimeline.INSTANCE.get_ID() + " = " + id, null);

            if (rowsAffected == 0) {
                throw new SQLNoRowsAffectedException();
            }

            getDatabase().setTransactionSuccessful();
        } catch (Exception e) {
            getDatabase().endTransaction();
            throw e;
        }

        getDatabase().endTransaction();
    }

    public void deleteByCategory(long categoryId) {
        for (Flashcard flashcard : getByCategory(categoryId)) {
            deleteById(flashcard.getId());
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
            CategoryDataSource cds = new CategoryDataSource(getDatabase());

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
        FlashcardCategoryDataSource fcds = new FlashcardCategoryDataSource(getDatabase());
        List<Long> categoryIds = fcds.getCategoryIdsForFlashcardId(flashcard.getId());
        List<Category> categories = new ArrayList<>();

        CategoryDataSource cds = new CategoryDataSource(getDatabase());
        for (Long categoryId : categoryIds) {
            categories.add(cds.getById(categoryId));
        }

        flashcard.setCategories(categories);
    }
}
