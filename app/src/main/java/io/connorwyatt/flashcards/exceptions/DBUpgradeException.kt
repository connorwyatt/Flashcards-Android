package io.connorwyatt.flashcards.exceptions

@Deprecated("This is considered legacy.")
class DBUpgradeException(cause: Throwable, oldVersion: Int, newVersion: Int) :
    RuntimeException("Unable to upgrade DB from v$oldVersion to v$newVersion.", cause)
