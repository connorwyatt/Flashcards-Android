/*
 * Copyright (c) 2016-2017 Connor Wyatt <connorwyatt1@gmail.com>.
 *
 * This file can not be copied and/or distributed without the express permission of Connor Wyatt.
 */

package io.connorwyatt.flashcards.data.datasources

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import io.connorwyatt.flashcards.data.entities.BaseEntity
import io.connorwyatt.flashcards.helpers.auth.AuthHelper
import io.connorwyatt.flashcards.helpers.auth.exceptions.NotSignedInException
import io.connorwyatt.flashcards.listeners.SimpleValueEventListener
import io.reactivex.Observable
import io.reactivex.ObservableEmitter

abstract class BaseDataSource {
  protected val database = FirebaseDatabase.getInstance()
  protected val authHelper = AuthHelper.getInstance()

  protected fun getUserDataQuery(userId: String): DatabaseReference {
    val query = database.reference.child("users").child(userId)

    query.keepSynced(true)

    return query
  }

  protected fun <T> executeQuerySingle(getQuery: (FirebaseUser) -> Query,
                                       parser: (DataSnapshot) -> Observable<T>,
                                       stream: Boolean = false): Observable<T> {
    return baseExecuteQuery<T>(getQuery, parser, stream)
  }

  protected fun <T> executeQueryList(getQuery: (FirebaseUser) -> Query,
                                     parser: (DataSnapshot) -> Observable<T>,
                                     clazz: Class<T>,
                                     stream: Boolean = false): Observable<List<T>> {
    return baseExecuteQuery<List<T>>(
      getQuery,
      { dataSnapshot ->
        if (dataSnapshot.hasChildren()) {
          dataSnapshot.children?.map { parser(it) }?.let {
            return@baseExecuteQuery Observable
              .combineLatest(it,
                             { it.filterIsInstance(clazz) })
          }
        }

        return@baseExecuteQuery Observable.just(listOf())
      },
      stream
    )
  }

  protected fun <T> executeQueryRelationship(getQuery: (FirebaseUser) -> Query,
                                             resourceName: String,
                                             resourceId: String,
                                             parser: (DataSnapshot) -> Observable<T>,
                                             clazz: Class<T>,
                                             stream: Boolean = false): Observable<List<T>> {
    return baseExecuteQuery<List<T>>(
      { user ->
        getQuery(user)
          .orderByChild("_relationships/$resourceName/$resourceId")
          .startAt(true)
          .endAt(true)
      },
      { dataSnapshot ->
        if (dataSnapshot.hasChildren()) {
          dataSnapshot.children?.map { parser(it) }?.let {
            return@baseExecuteQuery Observable
              .combineLatest(it, { it.filterIsInstance(clazz) })
          }
        }

        return@baseExecuteQuery Observable.just(listOf())
      },
      stream
    )
  }

  protected fun executeSave(resource: BaseEntity,
                            getCreateReference: (FirebaseUser) -> DatabaseReference,
                            getUpdateReference: (FirebaseUser) -> DatabaseReference): Observable<String> {
    return baseSave(resource = resource,
                    getCreateReference = getCreateReference,
                    getUpdateReference = getUpdateReference)
  }

  protected fun executeDelete(resource: BaseEntity,
                              getReference: (FirebaseUser) -> DatabaseReference): Observable<Any?> {
    return baseDelete(resource = resource,
                      reference = getReference)
  }

  private fun <T> baseExecuteQuery(getQuery: (FirebaseUser) -> Query,
                                   processData: (DataSnapshot) -> Observable<T>,
                                   stream: Boolean): Observable<T> {
    authHelper.currentUser?.let { user ->
      return Observable.create { observer ->
        val emitFn = getEmitFn<T>(observer, stream)

        val valueEventListener = getValueEventListener<T>(observer, processData, emitFn)

        val userQuery = getQuery(user)

        if (stream) {
          userQuery.addValueEventListener(valueEventListener)
        } else {
          userQuery.addListenerForSingleValueEvent(valueEventListener)
        }
      }
    }

    return Observable.error(NotSignedInException())
  }

  private fun <T> getEmitFn(observer: ObservableEmitter<T>,
                            stream: Boolean): (T) -> Unit {
    return if (stream) {
      { it: T ->
        observer.onNext(it)
      }
    } else {
      { it: T ->
        observer.onNext(it)
        observer.onComplete()
      }
    }
  }

  private fun <T> getValueEventListener(observer: ObservableEmitter<T>,
                                        processData: (DataSnapshot) -> Observable<T>,
                                        emitFn: (T) -> Unit): SimpleValueEventListener {
    return object : SimpleValueEventListener() {
      override fun onDataChange(dataSnapshot: DataSnapshot?) {
        dataSnapshot?.let(processData)?.let {
          it.subscribe(
            {
              emitFn(it)
            },
            {
              observer.onError(Exception()) // TODO Replace with more appropriate exception
            }
          )

          return
        }

        observer.onError(Exception())  // TODO Replace with more appropriate exception
      }

      override fun onCancelled(error: DatabaseError?) {
        val exception = error?.toException() ?: Exception()  // TODO Replace with more appropriate exception

        observer.onError(exception)
      }
    }
  }

  private fun baseSave(resource: BaseEntity,
                       getCreateReference: (FirebaseUser) -> DatabaseReference,
                       getUpdateReference: (FirebaseUser) -> DatabaseReference): Observable<String> {
    authHelper.currentUser?.let { user ->
      return Observable.create { observer ->
        val reference: DatabaseReference

        if (resource.existsInDatabase()) {
          resource.timestamps.modifiedNow()
          reference = getUpdateReference(user)
        } else {
          resource.timestamps.createdNow()
          reference = getCreateReference(user)
        }

        val updates = getUpdates(reference, user, reference.key, resource)

        getUserDataQuery(userId = user.uid).updateChildren(updates)
          .addOnFailureListener { observer.onError(it) }
          .addOnSuccessListener {
            observer.onNext(reference.key)
            observer.onComplete()
          }
      }
    }

    return Observable.error(NotSignedInException())
  }

  private fun baseDelete(resource: BaseEntity,
                         reference: (FirebaseUser) -> DatabaseReference): Observable<Any?> {
    authHelper.currentUser?.let { user ->
      return Observable.create { observer ->
        val ref = reference(user)

        val updates: MutableMap<String, Any?> = mutableMapOf()

        val pathToDelete
          = getPathRelativeToUserQuery(user, ref).joinToString(separator = "/")

        updates.put(pathToDelete, null)

        val relationships = resource.relationships.getAllRelationships()

        updates.putAll(getRelationshipUpdates(relationships, resource, ref.key, null))

        getUserDataQuery(userId = user.uid).updateChildren(updates)
          .addOnFailureListener { observer.onError(it) }
          .addOnSuccessListener {
            observer.onNext(true)
            observer.onComplete()
          }
      }
    }

    return Observable.error(NotSignedInException())
  }

  private fun getUpdates(reference: DatabaseReference,
                         user: FirebaseUser,
                         resourceId: String,
                         resource: BaseEntity): MutableMap<String, Any?> {
    val updates: MutableMap<String, Any?> = mutableMapOf()

    val resourcePath = getPathRelativeToUserQuery(user, reference)
    val resourcePathString = resourcePath.joinToString(separator = "/")

    updates.put(resourcePathString, resource.serialise())

    val (addedRelationships, removedRelationships)
      = resource.relationships.getUpdatedRelationships()

    updates.putAll(getRelationshipUpdates(addedRelationships, resource, resourceId, true))

    updates.putAll(getRelationshipUpdates(removedRelationships, resource, resourceId, null))

    return updates
  }

  private fun getRelationshipUpdates(relationships: Map<String, List<String>>,
                                     resource: BaseEntity,
                                     resourceId: String,
                                     value: Any?): Map<String, Any?> {
    val values: MutableMap<String, Any?> = mutableMapOf()

    relationships.forEach { resourceEntry ->
      resourceEntry.value.forEach { id ->
        val relationshipPath =
          getRelationshipPath(resourceEntry.key, id, resource.getType(), resourceId)

        values.put(relationshipPath, value)
      }
    }

    return values
  }

  private fun getPathRelativeToUserQuery(user: FirebaseUser,
                                         reference: DatabaseReference): List<String> {
    val userPath = getSplitPath(getUserDataQuery(user.uid))

    return getSplitPath(reference).filterNot { userPath.contains(it) }
  }

  private fun getSplitPath(reference: DatabaseReference): MutableList<String> {
    var currentRef = reference
    val path = mutableListOf(currentRef.key)

    while (currentRef.parent != null) {
      currentRef = currentRef.parent

      currentRef.key?.let { path.add(0, it) }
    }

    return path
  }

  private fun getRelationshipPath(resourceType: String,
                                  resourceId: String,
                                  relatedResourceType: String,
                                  relatedResourceId: String): String {
    val relationshipPath: MutableList<String> = mutableListOf(
      resourceType,
      resourceId,
      "_relationships",
      relatedResourceType,
      relatedResourceId
    )

    return relationshipPath.joinToString(separator = "/")
  }
}
