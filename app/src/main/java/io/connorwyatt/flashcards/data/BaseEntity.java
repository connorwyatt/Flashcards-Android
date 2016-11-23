package io.connorwyatt.flashcards.data;

public abstract class BaseEntity {
    protected Long id;

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean existsInDatabase() {
        return id != null;
    }
}
