package io.connorwyatt.flashcards.data.datasources

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

    private fun getCategoriesQuery(userId: String) =
        getUserDataQuery(userId).child("category")

    private fun getCategoryQuery(userId: String, categoryId: String) =
        getCategoriesQuery(userId).child(categoryId)
}
