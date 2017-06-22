/*
 * Copyright (c) 2016-2017 Connor Wyatt <connorwyatt1@gmail.com>.
 *
 * This file can not be copied and/or distributed without the express permission of Connor Wyatt.
 */

package io.connorwyatt.flashcards.data.viewmodels

import io.connorwyatt.flashcards.data.entities.Flashcard
import io.connorwyatt.flashcards.data.entities.Tag
import io.connorwyatt.flashcards.data.services.FlashcardService
import io.connorwyatt.flashcards.data.services.FlashcardTestService
import io.connorwyatt.flashcards.data.services.TagService
import io.reactivex.Observable

data class FlashcardViewModel(
  var flashcard: Flashcard,
  var tags: List<Tag>,
  var averageRating: Double? = null
) {
  fun save(): Observable<FlashcardViewModel> {
    return TagService.createTagsByName(tags.map { it.name!! })
      .flatMap { tags ->
        flashcard.relationships.setRelationships("tag", tags.map { it.id!! })

        FlashcardService.save(flashcard)
      }
      .flatMap { flashcard ->
        FlashcardViewModel.get(flashcard.id!!)
      }
  }

  fun delete(): Observable<Any?> {
    return FlashcardService.delete(flashcard)
  }

  companion object {
    fun get(flashcardId: String, includeRating: Boolean = true):
      Observable<FlashcardViewModel> {
      val observables: MutableList<Observable<*>> = mutableListOf()
      observables.add(FlashcardService.getById(flashcardId))
      observables.add(TagService.getByFlashcardId(flashcardId))

      if (includeRating) {
        observables.add(FlashcardTestService.getAverageRatingForFlashcard(flashcardId))
      }

      return Observable.combineLatest(
        observables,
        {
          val flashcard = it[0] as Flashcard

          var tags = it[1] as List<*>
          tags = tags.filterIsInstance(Tag::class.java)

          val averageRating = if (it.size >= 3) it[2] as Double else null

          FlashcardViewModel(flashcard, tags, averageRating)
        }
      )
    }

    fun getFromFlashcard(flashcard: Flashcard, includeRating: Boolean = true):
      Observable<FlashcardViewModel> {
      val flashcardId = flashcard.id!!

      val observables: MutableList<Observable<*>> = mutableListOf()
      observables.add(TagService.getByFlashcardId(flashcardId))

      if (includeRating) {
        observables.add(FlashcardTestService.getAverageRatingForFlashcard(flashcardId))
      }

      return Observable.combineLatest(
        observables,
        {
          var tags = it[0] as List<*>
          tags = tags.filterIsInstance(Tag::class.java)

          val averageRating = if (it.size >= 2) it[1] as Double else null

          FlashcardViewModel(flashcard, tags, averageRating)
        }
      )
    }
  }
}
