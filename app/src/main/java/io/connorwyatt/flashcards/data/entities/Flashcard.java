package io.connorwyatt.flashcards.data.entities;

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

    public String getCategoriesString() {
        String categoriesString = "";

        for (Category category : categories) {
            if (categoriesString.length() != 0) {
                categoriesString = categoriesString.concat(", ");
            }

            categoriesString = categoriesString.concat(category.getName());
        }

        return categoriesString;
    }
}
