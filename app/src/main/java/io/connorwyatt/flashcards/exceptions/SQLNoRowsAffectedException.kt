package io.connorwyatt.flashcards.exceptions

import android.database.SQLException

class SQLNoRowsAffectedException : SQLException("No rows were affected by the operation.")
