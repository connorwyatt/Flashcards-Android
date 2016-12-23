package io.connorwyatt.flashcards.data.services.legacy

import android.content.Context
import io.connorwyatt.flashcards.data.datasources.legacy.FlashcardDataSourceLegacy
import io.connorwyatt.flashcards.data.entities.legacy.FlashcardLegacy

class FlashcardServiceLegacy(private val context: Context)
{
    fun getAll(): List<FlashcardLegacy>
    {
        val dataSource = FlashcardDataSourceLegacy(context)

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

    fun getByCategory(categoryId: Long): List<FlashcardLegacy>
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

    fun getById(flashcardId: Long): FlashcardLegacy
    {
        val dataSource = FlashcardDataSourceLegacy(context)

        try
        {
            dataSource.open()

            return dataSource.getById(flashcardId)
        }
        finally
        {
            dataSource.close()
        }
    }

    fun save(flashcard: FlashcardLegacy): FlashcardLegacy
    {
        val dataSource = FlashcardDataSourceLegacy(context)

        try
        {
            dataSource.open()

            return dataSource.save(flashcard)
        }
        finally
        {
            dataSource.close()
        }
    }

    fun delete(flashcard: FlashcardLegacy): Unit
    {
        val dataSource = FlashcardDataSourceLegacy(context)

        try
        {
            dataSource.open()

            dataSource.deleteById(flashcard.id!!)
        }
        finally
        {
            dataSource.close()
        }
    }
}
