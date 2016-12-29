package io.connorwyatt.flashcards.data.datasources

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query
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

    fun getByName(name: String): Observable<List<Category>>
    {
        return executeQueryList(
            { getCategoryNameQuery(userId = it.uid, categoryName = name) },
            { Observable.just(Category(it)) },
            Category::class.java
        )
    }

    private fun getCategoriesQuery(userId: String): DatabaseReference =
        getUserDataQuery(userId).child("category")

    private fun getCategoryQuery(userId: String, categoryId: String): DatabaseReference =
        getCategoriesQuery(userId).child(categoryId)

    private fun getCategoryNameQuery(userId: String, categoryName: String): Query =
        getCategoriesQuery(userId)
            .orderByChild("name")
            .startAt(categoryName)
            .endAt(categoryName)
}
