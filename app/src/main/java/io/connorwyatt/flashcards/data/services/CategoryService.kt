package io.connorwyatt.flashcards.data.services

import io.connorwyatt.flashcards.data.datasources.CategoryDataSource
import io.connorwyatt.flashcards.data.entities.Category
import io.reactivex.Observable

object CategoryService
{
    fun getAll(): Observable<List<Category>>
        = CategoryDataSource().getAll()

    fun getById(id: String): Observable<Category>
        = CategoryDataSource().getById(id)

    fun getByName(name: String): Observable<List<Category>>
    {
        val normalisedName = normaliseCategoryName(name)

        return CategoryDataSource().getByName(normalisedName)
    }

    private fun normaliseCategoryName(name: String): String = name.trim()
}
