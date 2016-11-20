package io.connorwyatt.flashcards.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import io.connorwyatt.flashcards.exceptions.SQLNoRowsAffectedException;

public class FlashcardTestDataSource extends BaseDataSource {
    public FlashcardTestDataSource(Context context) {
        super(context);
    }

    FlashcardTestDataSource(SQLiteDatabase database) {
        super(database);
    }

    public void save(FlashcardTest flashcardTest) {
        try {
            database.beginTransaction();

            ContentValues values = new ContentValues();
            values.put(FlashcardTestContract.Columns.RATING, flashcardTest.getRating().ordinal());
            values.put(FlashcardTestContract.Columns.FLASHCARD_ID, flashcardTest.getFlashcardId());

            if (!flashcardTest.existsInDatabase()) {
                addCreateTimestamp(values);
                database.insertOrThrow(FlashcardTestContract.TABLE_NAME, null, values);
            } else {
                addUpdateTimestamp(values);
                int rowsAffected = database.update(FlashcardTestContract.TABLE_NAME, values,
                        FlashcardTestContract.Columns._ID + " = " + flashcardTest.getId(),
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
    }
}
