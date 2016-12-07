package io.connorwyatt.flashcards.services

import android.content.Context
import io.connorwyatt.flashcards.data.datasources.CategoryDataSource
import io.connorwyatt.flashcards.data.entities.Category

class CategoryService(context: Context)
{
    private val dataSource: CategoryDataSource

    init
    {
        dataSource = CategoryDataSource(context)
    }

    fun getAll(): List<Category>
    {
        try
        {
            dataSource.open()

            return dataSource.all
        } finally
        {
            dataSource.close()
        }
    }
}
