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

    fun getByFlashcardId(id: String): Observable<List<Category>>
    {
        return executeQueryRelationship(
            getQuery = { getCategoriesQuery(userId = it.uid) },
            resourceId = id,
            resourceName = "flashcard",
            parser = { Observable.just(Category(it)) },
            clazz = Category::class.java
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

    fun save(category: Category): Observable<String>
    {
        return executeSave(
            resource = category,
            getCreateReference = { getCategoriesQuery(userId = it.uid).push() },
            getUpdateReference = { getCategoryQuery(userId = it.uid, categoryId = category.id!!) }
        )
    }

    fun delete(category: Category): Observable<Any?>
    {
        return executeDelete(
            resource = category,
            getReference = { getCategoryQuery(userId = it.uid, categoryId = category.id!!) }
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
