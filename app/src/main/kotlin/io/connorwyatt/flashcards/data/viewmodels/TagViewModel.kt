/*
 * Copyright (c) 2016-2017 Connor Wyatt <connorwyatt1@gmail.com>.
 *
 * This file can not be copied and/or distributed without the express permission of Connor Wyatt.
 */

package io.connorwyatt.flashcards.data.viewmodels

import io.connorwyatt.flashcards.data.entities.Tag
import io.connorwyatt.flashcards.data.services.TagService
import io.connorwyatt.flashcards.data.services.FlashcardTestService
import io.reactivex.Observable

data class TagViewModel(
  var tag: Tag,
  var flashcardCount: Int,
  var averageRating: Double? = null
) {
  fun delete(deleteFlashcards: Boolean = false): Observable<Any?> {
    return if (deleteFlashcards) {
      TagService.deleteWithFlashcards(this.tag)
    } else {
      TagService.delete(this.tag)
    }
  }

  companion object {
    fun getFromTag(tag: Tag,
                        includeRating: Boolean = true): Observable<TagViewModel> {
      val ratingObservable = if (includeRating)
        FlashcardTestService.getAverageRatingForTag(tag.id!!)
      else
        Observable.just(false)


      return ratingObservable.map {
        val flashcardCount = tag.relationships.getRelationships("flashcard")?.count() ?: 0
        val averageRating = if (it is Double) it else null

        TagViewModel(tag, flashcardCount, averageRating)
      }
    }
  }
}
