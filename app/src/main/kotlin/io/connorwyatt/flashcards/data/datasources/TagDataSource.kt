/*
 * Copyright (c) 2016-2017 Connor Wyatt <connorwyatt1@gmail.com>.
 *
 * This file can not be copied and/or distributed without the express permission of Connor Wyatt.
 */

package io.connorwyatt.flashcards.data.datasources

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query
import io.connorwyatt.flashcards.data.entities.Tag
import io.reactivex.Observable

class TagDataSource : BaseDataSource() {
  fun getAll(stream: Boolean): Observable<List<Tag>> {
    return executeQueryList(
      { getTagsQuery(userId = it.uid) },
      { Observable.just(Tag(it)) },
      Tag::class.java,
      stream
    )
  }

  fun getById(id: String): Observable<Tag> {
    return executeQuerySingle(
      { getTagQuery(userId = it.uid, tagId = id) },
      { Observable.just(Tag(it)) }
    )
  }

  fun getByFlashcardId(id: String): Observable<List<Tag>> {
    return executeQueryRelationship(
      getQuery = { getTagsQuery(userId = it.uid) },
      resourceId = id,
      resourceName = "flashcard",
      parser = { Observable.just(Tag(it)) },
      clazz = Tag::class.java
    )
  }

  fun getByName(name: String): Observable<List<Tag>> {
    return executeQueryList(
      { getTagNameQuery(userId = it.uid, tagName = name) },
      { Observable.just(Tag(it)) },
      Tag::class.java
    )
  }

  fun save(tag: Tag): Observable<String> {
    return executeSave(
      resource = tag,
      getCreateReference = { getTagsQuery(userId = it.uid).push() },
      getUpdateReference = { getTagQuery(userId = it.uid, tagId = tag.id!!) }
    )
  }

  fun delete(tag: Tag): Observable<Any?> {
    return executeDelete(
      resource = tag,
      getReference = { getTagQuery(userId = it.uid, tagId = tag.id!!) }
    )
  }

  private fun getTagsQuery(userId: String): DatabaseReference =
    getUserDataQuery(userId).child("tag")

  private fun getTagQuery(userId: String, tagId: String): DatabaseReference =
    getTagsQuery(userId).child(tagId)

  private fun getTagNameQuery(userId: String, tagName: String): Query =
    getTagsQuery(userId)
      .orderByChild("name")
      .startAt(tagName)
      .endAt(tagName)
}
