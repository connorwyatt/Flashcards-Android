package io.connorwyatt.flashcards.data.viewmodels

import io.connorwyatt.flashcards.data.entities.Category
import io.connorwyatt.flashcards.data.entities.Flashcard
import io.connorwyatt.flashcards.data.services.CategoryService
import io.connorwyatt.flashcards.data.services.FlashcardService
import io.connorwyatt.flashcards.data.services.FlashcardTestService
import io.connorwyatt.flashcards.enums.Rating
import io.reactivex.Observable

data class FlashcardViewModel(
    var flashcard: Flashcard,
    var categories: List<Category>,
    var rating: Rating? = null
)
{
    fun save(): Observable<FlashcardViewModel>
    {
        return CategoryService.createCategoriesByName(categories.map { it.name!! })
            .flatMap { categories ->
                flashcard.relationships.setRelationships("category", categories.map { it.id!! })

                FlashcardService.save(flashcard)
            }
            .flatMap { flashcard ->
                FlashcardViewModel.get(flashcard.id!!)
            }
    }

    fun delete(): Observable<Any?>
    {
        return FlashcardService.delete(flashcard)
    }

    companion object
    {
        fun get(flashcardId: String, includeRating: Boolean = true):
            Observable<FlashcardViewModel>
        {
            val observables: MutableList<Observable<*>> = mutableListOf()
            observables.add(FlashcardService.getById(flashcardId))
            observables.add(CategoryService.getByFlashcardId(flashcardId))

            if (includeRating)
            {
                observables.add(FlashcardTestService.getAverageRatingForFlashcard(flashcardId))
            }

            return Observable.combineLatest(
                observables,
                {
                    val flashcard = it[0] as Flashcard

                    var categories = it[1] as List<*>
                    categories = categories.filterIsInstance(Category::class.java)

                    val rating = if (it.size >= 3)
                    {
                        Rating.fromValue(it[2] as Double)
                    }
                    else null

                    FlashcardViewModel(flashcard, categories, rating)
                }
            )
        }

        fun getFromFlashcard(flashcard: Flashcard, includeRating: Boolean = true):
            Observable<FlashcardViewModel>
        {
            val flashcardId = flashcard.id!!

            val observables: MutableList<Observable<*>> = mutableListOf()
            observables.add(CategoryService.getByFlashcardId(flashcardId))

            if (includeRating)
            {
                observables.add(FlashcardTestService.getAverageRatingForFlashcard(flashcardId))
            }

            return Observable.combineLatest(
                observables,
                {
                    var categories = it[0] as List<*>
                    categories = categories.filterIsInstance(Category::class.java)

                    val rating = if (it.size >= 2)
                    {
                        Rating.fromValue(it[1] as Double)
                    }
                    else null

                    FlashcardViewModel(flashcard, categories, rating)
                }
            )
        }
    }
}
