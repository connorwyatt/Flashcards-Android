package io.connorwyatt.flashcards.data.services

import io.connorwyatt.flashcards.data.datasources.CategoryDataSource
import io.connorwyatt.flashcards.data.entities.Category
import io.reactivex.Observable

class CategoryService
{
    fun getAll(): Observable<List<Category>>
    {
        val dataSource = CategoryDataSource()

        return dataSource.getAll()
    }
}
