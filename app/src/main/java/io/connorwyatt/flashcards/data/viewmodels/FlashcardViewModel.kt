package io.connorwyatt.flashcards.data.viewmodels

import io.connorwyatt.flashcards.data.entities.Category
import io.connorwyatt.flashcards.data.entities.Flashcard
import io.connorwyatt.flashcards.enums.Rating

data class FlashcardViewModel(
    val flashcard: Flashcard,
    val categories: List<Category>,
    val rating: Rating
)
