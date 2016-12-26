package io.connorwyatt.flashcards.data.datasources

import com.google.firebase.database.DataSnapshot
import io.connorwyatt.flashcards.data.entities.Category
import io.reactivex.Observable

class CategoryDataSource : BaseDataSource()
{
    fun getAll(): Observable<List<Category>>
    {
        return executeQueryList(
            { getCategoriesQuery(userId = it.uid) }, { categoryFromDataSnapshot(it) },
            Category::class.java
        )
    }

    fun getById(id: String): Observable<Category>
    {
        return executeQuerySingle(
            { getCategoryQuery(userId = it.uid, categoryId = id) }, { categoryFromDataSnapshot(it) }
        )
    }

    private fun getCategoriesQuery(userId: String) =
        getUserDataQuery(userId).child("category")

    private fun getCategoryQuery(userId: String, categoryId: String) =
        getCategoriesQuery(userId).child(categoryId)

    private fun categoryFromDataSnapshot(dataSnapshot: DataSnapshot): Observable<Category>
    {
        val category = dataSnapshot.getValue(Category::class.java)
        category.id = dataSnapshot.key

        return Observable.just(category)
    }
}
