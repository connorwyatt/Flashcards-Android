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
        }
        finally
        {
            dataSource.close()
        }
    }

    fun save(category: Category): Category
    {
        val dataSource = CategoryDataSource(context)

        try
        {
            dataSource.open()

            return dataSource.save(category)
        }
        finally
        {
            dataSource.close()
        }
    }

    fun delete(category: Category): Unit
    {
        val dataSource = CategoryDataSource(context)

        try
        {
            dataSource.open()

            dataSource.deleteById(category.id)
        }
        finally
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
        }
        finally
        {
            dataSource.close()
        }
    }

    fun getAverageRatingForCategory(categoryId: Long): Double?
    {
        val flashcardTestService = FlashcardTestService(context)
        val flashcards = getFlashcardsForCategory(categoryId)

        val ratings = flashcards.mapNotNull { flashcard ->
            flashcardTestService.getAverageRatingForFlashcard(flashcard.id)
        }

        if (ratings.size > 0)
        {
            return ratings.sum() / ratings.size.toDouble()
        }
        else
        {
            return null
        }
    }
}
