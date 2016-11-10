package io.connorwyatt.flashcards.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class FlashcardDataSource {
    private SQLiteDatabase database;
    private DBHelper dbHelper;
    private String[] allColumns = {FlashcardContract.Columns._ID, FlashcardContract.Columns.TITLE, FlashcardContract.Columns.TEXT};

    public FlashcardDataSource(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public List<Flashcard> getAllFlashcards() {
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

    public Flashcard createFlashcard(Flashcard flashcard) {
        ContentValues values = new ContentValues();
        values.put(FlashcardContract.Columns.TITLE, flashcard.getTitle());
        values.put(FlashcardContract.Columns.TEXT, flashcard.getText());

        long insertId = database.insert(FlashcardContract.TABLE_NAME, null, values);
        Cursor cursor = database.query(FlashcardContract.TABLE_NAME, allColumns, FlashcardContract.Columns._ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        Flashcard newFlashcard = cursorToFlashcard(cursor);
        cursor.close();
        return newFlashcard;
    }

    private Flashcard cursorToFlashcard(Cursor cursor) {
        Flashcard flashcard = new Flashcard();

        flashcard.setId(cursor.getLong(0));
        flashcard.setTitle(cursor.getString(1));
        flashcard.setText(cursor.getString(2));

        return flashcard;
    }
}
