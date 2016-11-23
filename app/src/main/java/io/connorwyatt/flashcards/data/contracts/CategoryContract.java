package io.connorwyatt.flashcards.data.contracts;

import io.connorwyatt.flashcards.data.entities.BaseColumnsTimeline;

public class CategoryContract {
    public static final String TABLE_NAME = "category";

    public static final String TABLE_CREATE = "create table "
            + TABLE_NAME + "( "
            + Columns._ID + " integer primary key autoincrement, "
            + Columns._CREATED_ON + " integer not null, "
            + Columns._LAST_MODIFIED_ON + " integer not null, "
            + Columns.NAME + " text not null"
            + ");";

    private CategoryContract() {
    }

    public static class Columns implements BaseColumnsTimeline {
        public static final String NAME = "name";
    }

}
