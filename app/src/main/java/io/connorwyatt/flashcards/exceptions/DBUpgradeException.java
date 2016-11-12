package io.connorwyatt.flashcards.exceptions;

public class DBUpgradeException extends RuntimeException {
    public DBUpgradeException(Throwable cause, int oldVersion, int newVersion) {
        super("Unable to upgrade DB from v" + oldVersion + " to v" + newVersion + ".", cause);
    }
}
