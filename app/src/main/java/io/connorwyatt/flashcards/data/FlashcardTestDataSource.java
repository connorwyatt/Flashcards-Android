package io.connorwyatt.flashcards.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import io.connorwyatt.flashcards.exceptions.SQLNoRowsAffectedException;

public class FlashcardTestDataSource extends BaseDataSource {
    private String[] allColumns = {FlashcardTestContract.Columns._ID, FlashcardTestContract.Columns.FLASHCARD_ID, FlashcardTestContract.Columns.RATING};

    public FlashcardTestDataSource(Context context) {
        super(context);
    }

    FlashcardTestDataSource(SQLiteDatabase database) {
        super(database);
    }

    public FlashcardTest getById(long id) {
        Cursor cursor = database.query(FlashcardTestContract.TABLE_NAME,
                allColumns, FlashcardTestContract.Columns._ID + " = " + id, null, null, null, null);

        cursor.moveToFirst();

        FlashcardTest flashcardTest = cursorToFlashcardTest(cursor);

        cursor.close();

        return flashcardTest;
    }

    public FlashcardTest save(FlashcardTest flashcardTest) {
        long savedFlashcardTestId;

        try {
            database.beginTransaction();

            ContentValues values = new ContentValues();
            values.put(FlashcardTestContract.Columns.RATING, flashcardTest.getRating().ordinal());
            values.put(FlashcardTestContract.Columns.FLASHCARD_ID, flashcardTest.getFlashcardId());

            if (!flashcardTest.existsInDatabase()) {
                addCreateTimestamp(values);
                savedFlashcardTestId = database.insertOrThrow(FlashcardTestContract.TABLE_NAME, null, values);
            } else {
                savedFlashcardTestId = flashcardTest.getId();
                addUpdateTimestamp(values);
                int rowsAffected = database.update(FlashcardTestContract.TABLE_NAME, values,
                        FlashcardTestContract.Columns._ID + " = " + savedFlashcardTestId,
                        null);

                if (rowsAffected == 0) {
                    throw new SQLNoRowsAffectedException();
                }
            }

            database.setTransactionSuccessful();
        } catch (Exception e) {
            database.endTransaction();
            throw e;
        }

        database.endTransaction();

        return getById(savedFlashcardTestId);
    }

    private FlashcardTest cursorToFlashcardTest(Cursor cursor) {
        FlashcardTest flashcardTest = new FlashcardTest();

        flashcardTest.setId(cursor.getLong(0));
        flashcardTest.setFlashcardId(cursor.getLong(1));
        flashcardTest.setRatingInt(cursor.getInt(2));

        return flashcardTest;
    }
}
