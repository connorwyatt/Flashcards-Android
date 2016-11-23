package io.connorwyatt.flashcards.data.entities;

public abstract class BaseEntity {
    protected long id = -1;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean existsInDatabase() {
        return id != -1;
    }
}
