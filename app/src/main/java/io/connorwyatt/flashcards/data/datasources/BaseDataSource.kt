package io.connorwyatt.flashcards.data.datasources

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import io.connorwyatt.flashcards.helpers.auth.AuthHelper
import io.connorwyatt.flashcards.helpers.auth.exceptions.NotSignedInException
import io.connorwyatt.flashcards.listeners.SimpleValueEventListener
import io.reactivex.Observable

abstract class BaseDataSource
{
    protected val database = FirebaseDatabase.getInstance()
    protected val authHelper = AuthHelper.getInstance()

    protected fun getUserDataQuery(userId: String): DatabaseReference
    {
        val query = database.reference.child("users").child(userId)

        query.keepSynced(true)

        return query
    }

    protected fun <T> executeQuerySingle(query: (FirebaseUser) -> Query,
                                         parser: (DataSnapshot) -> Observable<T>): Observable<T>
    {
        return baseExecuteQuery<T>(query, parser)
    }

    protected fun <T> executeQueryList(query: (FirebaseUser) -> Query,
                                       parser: (DataSnapshot) -> Observable<T>,
                                       clazz: Class<T>): Observable<List<T>>
    {
        return baseExecuteQuery<List<T>>(query, { dataSnapshot ->
            dataSnapshot.children?.map { parser(it) }?.let {
                return@baseExecuteQuery Observable
                    .combineLatest(it, { it.filterIsInstance(clazz) })
            }

            return@baseExecuteQuery Observable.just(arrayListOf())
        })
    }

    protected fun <T> executeQueryRelationship(query: (FirebaseUser) -> Query,
                                               resourceName: String,
                                               resourceId: String,
                                               parser: (DataSnapshot) -> Observable<T>,
                                               clazz: Class<T>): Observable<List<T>>
    {
        return baseExecuteQuery<List<T>>(
            { user ->
                query(user)
                    .orderByChild("_relationships/$resourceName/$resourceId")
                    .startAt(true)
                    .endAt(true)
            },
            { dataSnapshot ->
                dataSnapshot.children?.map { parser(it) }?.let {
                    return@baseExecuteQuery Observable
                        .combineLatest(it, { it.filterIsInstance(clazz) })
                }

                return@baseExecuteQuery Observable.just(arrayListOf())
            }
        )
    }

    private fun <T> baseExecuteQuery(query: (FirebaseUser) -> Query,
                                     processData: (DataSnapshot) -> Observable<T>): Observable<T>
    {
        authHelper.currentUser?.let { user ->
            return Observable.create { observer ->
                query(user).addListenerForSingleValueEvent(
                    object : SimpleValueEventListener()
                    {
                        override fun onDataChange(dataSnapshot: DataSnapshot?)
                        {
                            dataSnapshot?.let(processData)?.let {
                                it.subscribe(
                                    {
                                        observer.onNext(it)
                                        observer.onComplete()
                                    }, {
                                        observer.onError(Exception()) // TODO Replace with more appropriate exception
                                    }
                                )

                                return
                            }

                            observer.onError(Exception())  // TODO Replace with more appropriate exception
                        }

                        override fun onCancelled(error: DatabaseError?)
                        {
                            val exception = error?.toException() ?: Exception()  // TODO Replace with more appropriate exception

                            observer.onError(exception)
                        }
                    })
            }
        }

        return Observable.error(NotSignedInException())
    }
}
