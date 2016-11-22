package io.connorwyatt.flashcards.data;

public class FlashcardTestContract {
    public static final String TABLE_NAME = "flashcard_test";

    public static final String TABLE_CREATE = "create table "
            + TABLE_NAME + "( "
            + Columns._ID + " integer primary key autoincrement, "
            + Columns._CREATED_ON + " integer not null, "
            + Columns._LAST_MODIFIED_ON + " integer not null, "
            + Columns.FLASHCARD_ID + " integer not null, "
            + Columns.RATING + " integer not null, "
            + "FOREIGN KEY(" + Columns.FLASHCARD_ID + ")"
            + " REFERENCES " + FlashcardContract.TABLE_NAME
            + "(" + FlashcardContract.Columns._ID + ")"
            + ");";

    private FlashcardTestContract() {
    }

    public static class Columns implements BaseColumnsTimeline {
        public static final String FLASHCARD_ID = "flashcard_id";
        public static final String RATING = "rating";
    }
}