package io.connorwyatt.flashcards.data.datasources

import com.google.firebase.database.DataSnapshot
import io.connorwyatt.flashcards.data.entities.Category
import io.connorwyatt.flashcards.data.entities.Flashcard
import io.reactivex.Observable

class FlashcardDataSource : BaseDataSource()
{
    fun getAll(): Observable<List<Flashcard>>
    {
        return executeQueryList(
            { getFlashcardsQuery(userId = it.uid) }, { flashcardFromDataSnapshot(it) },
            Flashcard::class.java
        )
    }

    fun getById(id: String): Observable<Flashcard>
    {
        return executeQuerySingle(
            { getFlashcardQuery(userId = it.uid, flashcardId = id) },
            { flashcardFromDataSnapshot(it) }
        )
    }

    private fun getFlashcardsQuery(userId: String) =
        getUserDataQuery(userId).child("flashcard")

    private fun getFlashcardQuery(userId: String, flashcardId: String) =
        getFlashcardsQuery(userId).child(flashcardId)

    private fun flashcardFromDataSnapshot(dataSnapshot: DataSnapshot): Observable<Flashcard>
    {
        return Observable.create { observer ->
            val categoryDataSource = CategoryDataSource()

            val flashcard = dataSnapshot.getValue(Flashcard::class.java)
            flashcard.id = dataSnapshot.key

            val categoryObservables =
                dataSnapshot.child("_relationships").child("category").children
                    .map { categoryDataSource.getById(it.key) }

            Observable
                .combineLatest(categoryObservables, { it.filterIsInstance(Category::class.java) })
                .subscribe(
                    {
                        flashcard.categories = it.toMutableList()

                        observer.onNext(flashcard)
                        observer.onComplete()
                    },
                    { observer.onError(Exception()) } // TODO Replace with more appropriate exception
                )
        }
    }
}
