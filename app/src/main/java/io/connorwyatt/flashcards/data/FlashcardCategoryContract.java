package io.connorwyatt.flashcards.data;

public class FlashcardCategoryContract {
    public static final String TABLE_NAME = "flashcard_category";

    public static final String TABLE_CREATE = "create table "
            + TABLE_NAME + "( "
            + Columns._ID + " integer primary key autoincrement, "
            + Columns._CREATED_ON + " integer not null, "
            + Columns._LAST_MODIFIED_ON + " integer not null, "
            + Columns.FLASHCARD_ID + " integer not null, "
            + Columns.CATEGORY_ID + " integer not null"
            + ");";

    private FlashcardCategoryContract() {
    }

    public static class Columns implements BaseColumnsTimeline {
        public static final String FLASHCARD_ID = "flashcard_id";
        public static final String CATEGORY_ID = "category_id";
    }

}
