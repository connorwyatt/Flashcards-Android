package io.connorwyatt.flashcards.data.datasources

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import io.connorwyatt.flashcards.data.entities.Category
import io.connorwyatt.flashcards.helpers.auth.AuthHelper
import io.connorwyatt.flashcards.listeners.SimpleValueEventListener
import io.reactivex.Observable

class CategoryDataSource
{
    private val database = FirebaseDatabase.getInstance().reference
    private val authHelper = AuthHelper.getInstance()

    fun getAll(): Observable<List<Category>>
    {
        return Observable.create { observer ->
            authHelper.currentUser?.uid?.let {
                getCategoriesQuery(userId = it).addListenerForSingleValueEvent(
                    object : SimpleValueEventListener()
                    {
                        override fun onDataChange(dataSnapshot: DataSnapshot?)
                        {
                            val categories = dataSnapshot?.children
                                ?.map { categoryFromDataSnapshot(it) }

                            observer.onNext(categories)
                            observer.onComplete()
                        }
                    }
                )
            }
        }
    }

    fun getById(id: String): Observable<Category>
    {
        return Observable.create { observer ->
            authHelper.currentUser?.uid?.let {
                getCategoryQuery(userId = it, categoryId = id).addListenerForSingleValueEvent(
                    object : SimpleValueEventListener()
                    {
                        override fun onDataChange(dataSnapshot: DataSnapshot?)
                        {
                            dataSnapshot?.let {
                                observer.onNext(categoryFromDataSnapshot(it))
                                observer.onComplete()
                            }
                        }
                    }
                )
            }
        }
    }

    private fun getCategoriesQuery(userId: String) =
        database.child("users").child(userId).child("category")

    private fun getCategoryQuery(userId: String, categoryId: String) =
        getCategoriesQuery(userId).child(categoryId)

    private fun categoryFromDataSnapshot(dataSnapshot: DataSnapshot): Category
    {
        val category = dataSnapshot.getValue(Category::class.java)
        category.id = dataSnapshot.key

        return category
    }
}
