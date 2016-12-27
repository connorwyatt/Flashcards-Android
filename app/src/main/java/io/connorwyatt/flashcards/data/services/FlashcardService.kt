package io.connorwyatt.flashcards.data.services

import io.connorwyatt.flashcards.data.datasources.CategoryDataSource
import io.connorwyatt.flashcards.data.datasources.FlashcardDataSource
import io.connorwyatt.flashcards.data.entities.Flashcard
import io.reactivex.Observable

class FlashcardService
{
    fun getAll(): Observable<List<Flashcard>>
        = FlashcardDataSource().getAll()

    fun getById(id: String): Observable<Flashcard>
        = FlashcardDataSource().getById(id)

    fun getByCategory(categoryId: String): Observable<List<Flashcard>>
    {
        val categoryDataSource = CategoryDataSource()

        return categoryDataSource.getById(categoryId).flatMap { category ->
            val observables = category.relationships.getRelationships("flashcard")?.map { flashcardId ->
                getById(flashcardId)
            }

            Observable.combineLatest(observables, { it.filterIsInstance(Flashcard::class.java) })
        }
    }
}

