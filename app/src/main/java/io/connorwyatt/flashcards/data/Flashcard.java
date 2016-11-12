package io.connorwyatt.flashcards.data;

import java.util.ArrayList;
import java.util.List;

public class Flashcard extends BaseEntity {
    private String title;
    private String text;
    private List<Category> categories = new ArrayList<>();

    public Flashcard() {
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

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
}
