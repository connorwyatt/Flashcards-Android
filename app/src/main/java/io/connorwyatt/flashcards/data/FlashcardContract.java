package io.connorwyatt.flashcards.data;

import android.provider.BaseColumns;

public final class FlashcardContract {
    public static final String TABLE_NAME = "flashcard";

    public static final String TABLE_CREATE = "create table "
            + TABLE_NAME + "( "
            + Columns._ID + " integer primary key autoincrement, "
            + Columns.TITLE + " text not null, "
            + Columns.TEXT + " text not null"
            + ");";

    private FlashcardContract() {
    }

    public static class Columns implements BaseColumns {
        public static final String TITLE = "title";
        public static final String TEXT = "text";
    }
}
