package io.connorwyatt.flashcards.data.datasources

import com.google.firebase.database.DatabaseReference
import io.connorwyatt.flashcards.data.entities.FlashcardTest
import io.reactivex.Observable

class FlashcardTestDataSource : BaseDataSource()
{
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
