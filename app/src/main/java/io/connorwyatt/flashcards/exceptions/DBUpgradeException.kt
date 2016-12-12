package io.connorwyatt.flashcards.exceptions

class DBUpgradeException(cause: Throwable, oldVersion: Int, newVersion: Int) :
    RuntimeException("Unable to upgrade DB from v$oldVersion to v$newVersion.", cause)
