package io.connorwyatt.flashcards.data.datasources

import com.google.firebase.database.DatabaseReference
import io.connorwyatt.flashcards.data.entities.FlashcardTest
import io.reactivex.Observable

class FlashcardTestDataSource : BaseDataSource()
{
    fun getById(id: String): Observable<FlashcardTest>
    {
        return executeQuerySingle(
            query = { getFlashcardTestQuery(userId = it.uid, flashcardTestId = id) },
            parser = { Observable.just(FlashcardTest(it)) }
        )
    }

    fun getByFlashcardId(id: String): Observable<List<FlashcardTest>>
    {
        return executeQueryRelationship(
            query = { getFlashcardTestsQuery(userId = it.uid) },
            resourceName = "flashcard",
            resourceId = id,
            parser = { Observable.just(FlashcardTest(it)) },
            clazz = FlashcardTest::class.java
        )
    }

    fun save(flashcardTest: FlashcardTest): Observable<String>
    {
        return executeSave(
            resource = flashcardTest,
            createReference = {
                getFlashcardTestsQuery(userId = it.uid).push()
            },
            updateReference = {
                getFlashcardTestQuery(userId = it.uid, flashcardTestId = flashcardTest.id!!)
            }
        )
    }

    fun delete(flashcardTest: FlashcardTest): Observable<Any?>
    {
        return executeDelete(
            resource = flashcardTest,
            reference = {
                getFlashcardTestQuery(
                    userId = it.uid,
                    flashcardTestId = flashcardTest.id!!
                )
            }
        )
    }

    private fun getFlashcardTestsQuery(userId: String): DatabaseReference
        = getUserDataQuery(userId).child("flashcardTest")

    private fun getFlashcardTestQuery(userId: String, flashcardTestId: String): DatabaseReference
        = getUserDataQuery(userId).child("flashcardTest").child(flashcardTestId)
}
