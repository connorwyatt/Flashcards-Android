/*
 * Copyright (c) 2016-2017 Connor Wyatt <connorwyatt1@gmail.com>.
 *
 * This file can not be copied and/or distributed without the express permission of Connor Wyatt.
 */

package io.connorwyatt.flashcards.data.services

import io.connorwyatt.flashcards.data.datasources.FlashcardDataSource
import io.connorwyatt.flashcards.data.entities.Flashcard
import io.reactivex.Observable

object FlashcardService
{
    fun getAll(): Observable<List<Flashcard>>
        = FlashcardDataSource().getAll(false)

    fun getAllAsStream(): Observable<List<Flashcard>>
        = FlashcardDataSource().getAll(true)

    fun getById(id: String): Observable<Flashcard>
        = FlashcardDataSource().getById(id)

    fun getByCategory(categoryId: String): Observable<List<Flashcard>>
        = FlashcardDataSource().getByCategoryId(categoryId, false)


    fun getByCategoryAsStream(categoryId: String): Observable<List<Flashcard>>
        = FlashcardDataSource().getByCategoryId(categoryId, true)


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
