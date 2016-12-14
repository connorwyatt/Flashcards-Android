package io.connorwyatt.flashcards.data.services

import android.content.Context
import io.connorwyatt.flashcards.data.datasources.CategoryDataSource
import io.connorwyatt.flashcards.data.datasources.FlashcardDataSource
import io.connorwyatt.flashcards.data.entities.Category
import io.connorwyatt.flashcards.data.entities.Flashcard

class CategoryService(private val context: Context)
{
    fun getById(categoryId: Long): Category
    {
        val dataSource = CategoryDataSource(context)

        try
        {
            dataSource.open()

            return dataSource.getById(categoryId)
        }
        finally
        {
            dataSource.close()
        }
    }

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

            dataSource.deleteById(category.id!!)
        }
        finally
        {
            dataSource.close()
        }
    }

    fun deleteWithFlashcards(category: Category): Unit
    {
        val flashcardDataSource = FlashcardDataSource(context)

        try
        {
            flashcardDataSource.open()

            flashcardDataSource.deleteByCategory(category.id!!)

            delete(category)
        }
        finally
        {
            flashcardDataSource.close()
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

    fun isCategoryNameTaken(name: String): Boolean
    {
        val dataSource = CategoryDataSource(context)

        try
        {
            dataSource.open()

            return dataSource.getByName(name) !== null
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
            flashcardTestService.getAverageRatingForFlashcard(flashcard.id!!)
        }

        if (ratings.isNotEmpty())
        {
            return ratings.sum() / ratings.size.toDouble()
        }
        else
        {
            return null
        }
    }
}
