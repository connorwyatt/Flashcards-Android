package io.connorwyatt.flashcards.listeners

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

abstract class SimpleValueEventListener : ValueEventListener
{
    override fun onCancelled(error: DatabaseError?)
    {
    }

    override fun onDataChange(dataSnapshot: DataSnapshot?)
    {
    }
}
