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
        boolean isCreate = flashcard.getId() <= 0;

        ContentValues values = new ContentValues();
        values.put(FlashcardContract.Columns.TITLE, flashcard.getTitle());
        values.put(FlashcardContract.Columns.TEXT, flashcard.getText());

        long id;

        if (isCreate) {
            addCreateTimestamp(values);
            id = database.insertOrThrow(FlashcardContract.TABLE_NAME, null, values);
        } else {
            id = flashcard.getId();
            addUpdateTimestamp(values);
            int rowsAffected = database.update(FlashcardContract.TABLE_NAME, values, FlashcardContract.Columns._ID + " = " + id, null);

            if (rowsAffected == 0) {
                throw new SQLNoRowsAffectedException();
            }
        }

        return getById(id);
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

        return flashcard;
    }
}
