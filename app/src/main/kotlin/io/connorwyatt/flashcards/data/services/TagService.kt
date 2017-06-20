/*
 * Copyright (c) 2016-2017 Connor Wyatt <connorwyatt1@gmail.com>.
 *
 * This file can not be copied and/or distributed without the express permission of Connor Wyatt.
 */

package io.connorwyatt.flashcards.data.services

import io.connorwyatt.flashcards.data.datasources.TagDataSource
import io.connorwyatt.flashcards.data.entities.Tag
import io.connorwyatt.flashcards.exceptions.TagNameTakenException
import io.reactivex.Observable

object TagService {
  fun getAll(): Observable<List<Tag>>
    = TagDataSource().getAll(stream = false)

  fun getAllAsStream(): Observable<List<Tag>>
    = TagDataSource().getAll(stream = true)

  fun getById(id: String): Observable<Tag>
    = TagDataSource().getById(id)

  fun getByFlashcardId(id: String): Observable<List<Tag>>
    = TagDataSource().getByFlashcardId(id)

  fun getByName(name: String): Observable<List<Tag>> {
    val normalisedName = normaliseTagName(name)

    return TagDataSource().getByName(normalisedName)
  }

  fun createTagsByName(tagNames: List<String>): Observable<List<Tag>> {
    val observables = tagNames.distinctBy { normaliseTagName(it) }
      .filter(String::isNotBlank)
      .map { name ->
        getByName(name)
          .flatMap { tag ->
            tag.singleOrNull()?.let { return@flatMap Observable.just(it) }

            val newTag = Tag(null)
            newTag.name = name

            return@flatMap save(newTag)
          }
      }

    if (observables.isNotEmpty()) {
      return Observable.combineLatest(observables,
                                      { it.filterIsInstance(Tag::class.java) })
    }

    return Observable.just(listOf())
  }

  fun save(tag: Tag): Observable<Tag> {
    return getByName(tag.name!!)
      .flatMap { tags ->
        if (tags.isEmpty())
          TagDataSource().save(tag).flatMap { getById(it) }
        else
          Observable.error<Tag>(TagNameTakenException())
      }
  }

  fun delete(tag: Tag): Observable<Any?>
    = TagDataSource().delete(tag)

  fun deleteWithFlashcards(tag: Tag): Observable<Any?> {
    return FlashcardService.deleteByTagId(tag.id!!).flatMap {
      TagDataSource().delete(tag)
    }
  }

  private fun normaliseTagName(name: String): String = name.trim()
}
