package io.connorwyatt.flashcards.exceptions;

import android.database.SQLException;

public class SQLNoRowsAffectedException extends SQLException {
    public SQLNoRowsAffectedException() {
        super("No rows were affected by the operation.");
    }
}
