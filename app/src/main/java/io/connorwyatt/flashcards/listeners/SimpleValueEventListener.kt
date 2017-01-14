/*
 * Copyright (c) 2016-2017 Connor Wyatt <connorwyatt1@gmail.com>.
 *
 * This file can not be copied and/or distributed without the express permission of Connor Wyatt.
 */

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
