package io.connorwyatt.flashcards.data;

public class FlashcardCategoryContract {
    public static final String TABLE_NAME = "flashcard_category";

    public static final String TABLE_CREATE = "create table "
            + TABLE_NAME + "( "
            + Columns._ID + " integer primary key autoincrement, "
            + Columns._CREATED_ON + " integer not null, "
            + Columns._LAST_MODIFIED_ON + " integer not null, "
            + Columns.FLASHCARD_ID + " integer not null, "
            + Columns.CATEGORY_ID + " integer not null, "
            + "FOREIGN KEY(" + Columns.FLASHCARD_ID + ")"
            + " REFERENCES " + FlashcardContract.TABLE_NAME
            + "(" + FlashcardContract.Columns._ID + "), "
            + "FOREIGN KEY(" + Columns.CATEGORY_ID + ")"
            + " REFERENCES " + CategoryContract.TABLE_NAME
            + "(" + CategoryContract.Columns._ID + ")"
            + ");";

    private FlashcardCategoryContract() {
    }

    public static class Columns implements BaseColumnsTimeline {
        public static final String FLASHCARD_ID = "flashcard_id";
        public static final String CATEGORY_ID = "category_id";
    }

}
