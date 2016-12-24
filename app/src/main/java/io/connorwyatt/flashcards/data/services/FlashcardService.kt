package io.connorwyatt.flashcards.data.services

import io.connorwyatt.flashcards.data.datasources.FlashcardDataSource
import io.connorwyatt.flashcards.data.entities.Flashcard
import io.reactivex.Observable

class FlashcardService
{
    fun getAll(): Observable<List<Flashcard>>
    {
        val dataSource = FlashcardDataSource()

        return dataSource.getAll()
    }

    fun getById(id: String): Observable<Flashcard>
    {
        val dataSource = FlashcardDataSource()

        return dataSource.getById(id)
    }
}

