package io.connorwyatt.flashcards.data.datasources

import com.google.firebase.database.DatabaseReference
import io.connorwyatt.flashcards.data.entities.FlashcardTest
import io.reactivex.Observable

class FlashcardTestDataSource : BaseDataSource()
{
    fun getByFlashcardId(id: String): Observable<List<FlashcardTest>>
    {
        return executeQueryRelationship(
            reference = { getFlashcardTestsQuery(userId = it.uid) },
            resourceName = "flashcard",
            resourceId = id,
            parser = { Observable.just(FlashcardTest(it)) },
            clazz = FlashcardTest::class.java
        )
    }

    private fun getFlashcardTestsQuery(userId: String): DatabaseReference
        = getUserDataQuery(userId).child("flashcardTest")
}
