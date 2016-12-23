package io.connorwyatt.flashcards.data.datasources

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import io.connorwyatt.flashcards.data.entities.Category
import io.connorwyatt.flashcards.data.entities.Flashcard
import io.connorwyatt.flashcards.helpers.auth.AuthHelper
import io.connorwyatt.flashcards.listeners.SimpleValueEventListener
import io.reactivex.Observable

class FlashcardDataSource
{
    private val database = FirebaseDatabase.getInstance().reference
    private val authHelper = AuthHelper.getInstance()

    fun getAll(): Observable<List<Flashcard>>
    {
        return Observable.create<List<Flashcard>> { observer ->
            authHelper.currentUser?.uid?.let {
                getFlashcardsQuery(userId = it).addListenerForSingleValueEvent(
                    object : SimpleValueEventListener()
                    {
                        override fun onDataChange(dataSnapshot: DataSnapshot?)
                        {
                            val flashcardSingles =
                                dataSnapshot?.children?.map { flashcardFromDataSnapshot(it) }
                                ?: arrayListOf()

                            Observable
                                .combineLatest(flashcardSingles, { it.asList() as List<Flashcard> })
                                .subscribe { list: List<Flashcard> ->
                                    observer.onNext(list)
                                    observer.onComplete()
                                }
                        }
                    }
                )
            }
        }
    }

    private fun getFlashcardsQuery(userId: String) =
        database.child("users").child(userId).child("flashcard")

    private fun flashcardFromDataSnapshot(dataSnapshot: DataSnapshot): Observable<Flashcard>
    {
        return Observable.create { observer ->
            val categoryDataSource = CategoryDataSource()

            val flashcard = dataSnapshot.getValue(Flashcard::class.java)
            flashcard.id = dataSnapshot.key

            val categorySingles =
                dataSnapshot.child("_relationships").child("category").children
                    .map { categoryDataSource.getById(it.key) }

            Observable
                .combineLatest(categorySingles, { it.asList() as List<Category> })
                .subscribe { list: List<Category> ->
                    flashcard.categories = list.toMutableList()

                    observer.onNext(flashcard)
                    observer.onComplete()
                }
        }
    }
}
