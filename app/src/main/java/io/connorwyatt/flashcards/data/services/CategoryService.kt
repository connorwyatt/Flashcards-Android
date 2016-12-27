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

    private fun isNameTaken(name: String): Observable<Boolean>
    {
        val normalisedName = normaliseCategoryName(name)

        return getAll().map { list ->
            list.any { category ->
                category.name?.let { compareNames(it, normalisedName) } ?: false
            }
        }
    }

    private fun normaliseCategoryName(name: String): String = name.trim()

    private fun compareNames(name1: String, name2: String): Boolean
    {
        return name1.toLowerCase() == name2.toLowerCase()
    }
}
