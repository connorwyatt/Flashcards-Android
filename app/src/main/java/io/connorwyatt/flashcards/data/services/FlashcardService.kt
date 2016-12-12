package io.connorwyatt.flashcards.data.services

import android.content.Context
import io.connorwyatt.flashcards.data.datasources.FlashcardDataSource
import io.connorwyatt.flashcards.data.entities.Flashcard

class FlashcardService(private val context: Context)
{
    fun getAll(): List<Flashcard>
    {
        val dataSource = FlashcardDataSource(context)

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

    fun getByCategory(categoryId: Long): List<Flashcard>
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

    fun getById(flashcardId: Long): Flashcard
    {
        val dataSource = FlashcardDataSource(context)

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

    fun save(flashcard: Flashcard): Flashcard
    {
        val dataSource = FlashcardDataSource(context)

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

    fun delete(flashcard: Flashcard): Unit
    {
        val dataSource = FlashcardDataSource(context)

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
