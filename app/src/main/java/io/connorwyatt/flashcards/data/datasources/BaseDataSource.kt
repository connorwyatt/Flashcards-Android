package io.connorwyatt.flashcards.data.datasources

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

abstract class BaseDataSource
{
    protected val database = FirebaseDatabase.getInstance()

    protected fun getUserDataQuery(userId: String): DatabaseReference
    {
        val query = database.reference.child("users").child(userId)

        query.keepSynced(true)

        return query
    }
}
