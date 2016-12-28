package io.connorwyatt.flashcards.data.services

import io.connorwyatt.flashcards.data.datasources.FlashcardDataSource
import io.connorwyatt.flashcards.data.entities.Flashcard
import io.reactivex.Observable

object FlashcardService
{
    fun getAll(): Observable<List<Flashcard>>
        = FlashcardDataSource().getAll()

    fun getById(id: String): Observable<Flashcard>
        = FlashcardDataSource().getById(id)

    fun getByCategory(categoryId: String): Observable<List<Flashcard>>
    {
        val flashcardDataSource = FlashcardDataSource()

        return flashcardDataSource.getByCategoryId(categoryId)
    }
}

