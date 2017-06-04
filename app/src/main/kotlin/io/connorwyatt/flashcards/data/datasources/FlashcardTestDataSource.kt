/*
 * Copyright (c) 2016-2017 Connor Wyatt <connorwyatt1@gmail.com>.
 *
 * This file can not be copied and/or distributed without the express permission of Connor Wyatt.
 */

package io.connorwyatt.flashcards.data.datasources

import com.google.firebase.database.DatabaseReference
import io.connorwyatt.flashcards.data.entities.FlashcardTest
import io.reactivex.Observable

class FlashcardTestDataSource : BaseDataSource() {
  fun getById(id: String): Observable<FlashcardTest> {
    return executeQuerySingle(
      getQuery = { getFlashcardTestQuery(userId = it.uid, flashcardTestId = id) },
      parser = { Observable.just(FlashcardTest(it)) }
    )
  }

  fun getByFlashcardId(id: String): Observable<List<FlashcardTest>> {
    return executeQueryRelationship(
      getQuery = { getFlashcardTestsQuery(userId = it.uid) },
      resourceName = "flashcard",
      resourceId = id,
      parser = { Observable.just(FlashcardTest(it)) },
      clazz = FlashcardTest::class.java
    )
  }

  fun save(flashcardTest: FlashcardTest): Observable<String> {
    return executeSave(
      resource = flashcardTest,
      getCreateReference = {
        getFlashcardTestsQuery(userId = it.uid).push()
      },
      getUpdateReference = {
        getFlashcardTestQuery(userId = it.uid, flashcardTestId = flashcardTest.id!!)
      }
    )
  }

  fun delete(flashcardTest: FlashcardTest): Observable<Any?> {
    return executeDelete(
      resource = flashcardTest,
      getReference = {
        getFlashcardTestQuery(
          userId = it.uid,
          flashcardTestId = flashcardTest.id!!
        )
      }
    )
  }

  private fun getFlashcardTestsQuery(userId: String): DatabaseReference
    = getUserDataQuery(userId).child("flashcardTest")

  private fun getFlashcardTestQuery(userId: String, flashcardTestId: String): DatabaseReference
    = getUserDataQuery(userId).child("flashcardTest").child(flashcardTestId)
}
