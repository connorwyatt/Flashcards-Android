package io.connorwyatt.flashcards.data.datasources

import com.google.firebase.database.DatabaseReference
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

    fun getByCategoryId(id: String): Observable<List<Flashcard>>
    {
        return executeQueryRelationship(
            getQuery = { getFlashcardsQuery(userId = it.uid) },
            resourceName = "category",
            resourceId = id,
            parser = { Observable.just(Flashcard(it)) },
            clazz = Flashcard::class.java
        )
    }

    fun save(flashcard: Flashcard): Observable<String>
    {
        return executeSave(
            resource = flashcard,
            getCreateReference = { getFlashcardsQuery(userId = it.uid).push() },
            getUpdateReference = {
                getFlashcardQuery(userId = it.uid,
                                  flashcardId = flashcard.id!!)
            }
        )
    }

    fun delete(flashcard: Flashcard): Observable<Any?>
    {
        return executeDelete(
            resource = flashcard,
            getReference = { getFlashcardQuery(userId = it.uid, flashcardId = flashcard.id!!) }
        )
    }

    private fun getFlashcardsQuery(userId: String): DatabaseReference =
        getUserDataQuery(userId).child("flashcard")

    private fun getFlashcardQuery(userId: String, flashcardId: String): DatabaseReference =
        getFlashcardsQuery(userId).child(flashcardId)
}
