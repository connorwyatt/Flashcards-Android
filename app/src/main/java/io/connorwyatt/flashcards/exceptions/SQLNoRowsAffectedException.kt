package io.connorwyatt.flashcards.exceptions

import android.database.SQLException

@Deprecated("This is considered legacy.")
class SQLNoRowsAffectedException : SQLException("No rows were affected by the operation.")
