package io.connorwyatt.flashcards.data;

public class Flashcard {
    private String title;
    private String text;

    public Flashcard(String title, String text) {
        this.title = title;
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }
}
