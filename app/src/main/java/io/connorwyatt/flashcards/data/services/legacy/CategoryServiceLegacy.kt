package io.connorwyatt.flashcards.data.services.legacy

import android.content.Context
import io.connorwyatt.flashcards.data.datasources.legacy.CategoryDataSourceLegacy
import io.connorwyatt.flashcards.data.datasources.legacy.FlashcardDataSourceLegacy
import io.connorwyatt.flashcards.data.entities.legacy.CategoryLegacy
import io.connorwyatt.flashcards.data.entities.legacy.FlashcardLegacy

class CategoryServiceLegacy(private val context: Context)
{
    fun getById(categoryId: Long): CategoryLegacy
    {
        val dataSource = CategoryDataSourceLegacy(context)

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

    fun getAll(): List<CategoryLegacy>
    {
        val dataSource = CategoryDataSourceLegacy(context)

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

    fun save(category: CategoryLegacy): CategoryLegacy
    {
        val dataSource = CategoryDataSourceLegacy(context)

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

    fun delete(category: CategoryLegacy): Unit
    {
        val dataSource = CategoryDataSourceLegacy(context)

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

    fun deleteWithFlashcards(category: CategoryLegacy): Unit
    {
        val flashcardDataSource = FlashcardDataSourceLegacy(context)

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

    fun getFlashcardsForCategory(categoryId: Long): List<FlashcardLegacy>
    {
        val dataSource = FlashcardDataSourceLegacy(context)

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
        val dataSource = CategoryDataSourceLegacy(context)

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
        val flashcardTestService = FlashcardTestServiceLegacy(context)
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
