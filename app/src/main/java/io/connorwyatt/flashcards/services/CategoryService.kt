package io.connorwyatt.flashcards.services

import android.content.Context
import io.connorwyatt.flashcards.data.datasources.CategoryDataSource
import io.connorwyatt.flashcards.data.datasources.FlashcardDataSource
import io.connorwyatt.flashcards.data.entities.Category
import io.connorwyatt.flashcards.data.entities.Flashcard

class CategoryService(private val context: Context)
{
    fun getAll(): List<Category>
    {
        val dataSource = CategoryDataSource(context)

        try
        {
            dataSource.open()

            return dataSource.all
        } finally
        {
            dataSource.close()
        }
    }

    fun getFlashcardsForCategory(categoryId: Long): List<Flashcard>
    {
        val dataSource = FlashcardDataSource(context)

        try
        {
            dataSource.open()

            return dataSource.getByCategory(categoryId)
        } finally
        {
            dataSource.close()
        }
    }
}
