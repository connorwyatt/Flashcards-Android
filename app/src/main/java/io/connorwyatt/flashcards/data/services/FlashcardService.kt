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

    fun save(flashcard: Flashcard): Observable<Flashcard>
    {
        val flashcardDataSource = FlashcardDataSource()

        return flashcardDataSource.save(flashcard)
            .flatMap { getById(it) }
    }

    fun delete(flashcard: Flashcard): Observable<Any?>
    {
        return FlashcardTestService.deleteByFlashcardId(flashcard.id!!).flatMap {
            FlashcardDataSource().delete(flashcard)
        }
    }

    fun deleteByCategoryId(categoryId: String): Observable<Any?>
    {
        return getByCategory(categoryId).flatMap { flashcards ->
            val observables = flashcards.map { delete(it) }

            if (observables.isNotEmpty())
                Observable.combineLatest(observables, { it })
            else
                Observable.just(true)
        }
    }
}
