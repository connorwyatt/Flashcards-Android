package io.connorwyatt.flashcards.data;

public class FlashcardTest extends BaseEntity {
    private float rating;
    private long flashcardId;

    public FlashcardTest() {
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public long getFlashcardId() {
        return flashcardId;
    }

    public void setFlashcardId(long flashcardId) {
        this.flashcardId = flashcardId;
    }
}
