package io.connorwyatt.flashcards.data;

public final class FlashcardContract {
    public static final String TABLE_NAME = "flashcard";

    public static final String TABLE_CREATE = "create table "
            + TABLE_NAME + "( "
            + Columns._ID + " integer primary key autoincrement, "
            + Columns._CREATED_ON + " integer not null, "
            + Columns._LAST_MODIFIED_ON + " integer not null, "
            + Columns.TITLE + " text not null, "
            + Columns.TEXT + " text not null"
            + ");";

    private FlashcardContract() {
    }

    public static class Columns implements BaseColumnsTimeline {
        public static final String TITLE = "title";
        public static final String TEXT = "text";
    }
}
