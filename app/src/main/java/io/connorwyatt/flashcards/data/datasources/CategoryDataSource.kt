package io.connorwyatt.flashcards.data.datasources

import com.google.firebase.database.DatabaseReference
import io.connorwyatt.flashcards.data.entities.Category
import io.reactivex.Observable

class CategoryDataSource : BaseDataSource()
{
    fun getAll(): Observable<List<Category>>
    {
        return executeQueryList(
            { getCategoriesQuery(userId = it.uid) },
            { Observable.just(Category(it)) },
            Category::class.java
        )
    }

    fun getById(id: String): Observable<Category>
    {
        return executeQuerySingle(
            { getCategoryQuery(userId = it.uid, categoryId = id) },
            { Observable.just(Category(it)) }
        )
    }

    private fun getCategoriesQuery(userId: String): DatabaseReference =
        getUserDataQuery(userId).child("category")

    private fun getCategoryQuery(userId: String, categoryId: String): DatabaseReference =
        getCategoriesQuery(userId).child(categoryId)
}
