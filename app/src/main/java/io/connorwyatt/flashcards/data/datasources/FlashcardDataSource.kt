package io.connorwyatt.flashcards.data.datasources

import io.connorwyatt.flashcards.data.entities.Flashcard
import io.reactivex.Observable

class FlashcardDataSource : BaseDataSource()
{
    fun getAll(): Observable<List<Flashcard>>
    {
        return executeQueryList(
            { getFlashcardsQuery(userId = it.uid) },
            { Observable.just(Flashcard(it)) },
            Flashcard::class.java
        )
    }

    fun getById(id: String): Observable<Flashcard>
    {
        return executeQuerySingle(
            { getFlashcardQuery(userId = it.uid, flashcardId = id) },
            { Observable.just(Flashcard(it)) }
        )
    }

    private fun getFlashcardsQuery(userId: String) =
        getUserDataQuery(userId).child("flashcard")

    private fun getFlashcardQuery(userId: String, flashcardId: String) =
        getFlashcardsQuery(userId).child(flashcardId)
}
