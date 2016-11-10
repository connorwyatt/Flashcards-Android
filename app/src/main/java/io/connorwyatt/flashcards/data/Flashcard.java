package io.connorwyatt.flashcards.data;

public class Flashcard {
    private long id;
    private String title;
    private String text;

    public Flashcard() {
    }

    public Flashcard(String title, String text) {
        this.title = title;
        this.text = text;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
