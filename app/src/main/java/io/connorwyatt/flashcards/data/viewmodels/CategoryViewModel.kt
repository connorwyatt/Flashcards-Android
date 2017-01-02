package io.connorwyatt.flashcards.data.viewmodels

import io.connorwyatt.flashcards.data.entities.Category
import io.connorwyatt.flashcards.data.services.CategoryService
import io.connorwyatt.flashcards.data.services.FlashcardTestService
import io.connorwyatt.flashcards.enums.Rating
import io.reactivex.Observable

data class CategoryViewModel(var category: Category, var flashcardCount: Int, var rating: Rating?)
{
    fun delete(deleteFlashcards: Boolean = false): Observable<Any?>
    {
        return if (deleteFlashcards)
        {
            CategoryService.deleteWithFlashcards(this.category)
        }
        else
        {
            CategoryService.delete(this.category)
        }
    }

    companion object
    {
        fun getFromCategory(category: Category,
                            includeRating: Boolean = true): Observable<CategoryViewModel>
        {
            val ratingObservable: Observable<*>

            if (includeRating)
            {
                ratingObservable = FlashcardTestService.getAverageRatingForCategory(category.id!!)
            }
            else
            {
                ratingObservable = Observable.just(false)
            }

            return ratingObservable.map {
                val flashcardCount = category.relationships.getRelationships("flashcard")?.count() ?: 0
                val rating = if (it is Double) Rating.fromValue(it) else null

                CategoryViewModel(category, flashcardCount, rating)
            }
        }
    }
}
