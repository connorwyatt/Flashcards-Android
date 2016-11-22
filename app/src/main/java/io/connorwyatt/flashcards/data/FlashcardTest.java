package io.connorwyatt.flashcards.data;

public class FlashcardTest extends BaseEntity {
    private Rating rating;
    private Long flashcardId;

    public FlashcardTest() {
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public void setRatingInt(int rating) {
        setRating(Rating.values()[rating]);
    }

    public void setRatingPositive() {
        setRating(Rating.POSITIVE);
    }

    public void setRatingNeutral() {
        setRating(Rating.NEUTRAL);
    }

    public void setRatingNegative() {
        setRating(Rating.NEGATIVE);
    }

    public Long getFlashcardId() {
        return flashcardId;
    }

    public void setFlashcardId(Long flashcardId) {
        this.flashcardId = flashcardId;
    }

    public static enum Rating {
        POSITIVE,
        NEUTRAL,
        NEGATIVE
    }
}
