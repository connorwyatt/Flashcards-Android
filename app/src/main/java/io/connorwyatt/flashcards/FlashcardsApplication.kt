package io.connorwyatt.flashcards

import android.app.Application
import com.google.firebase.database.FirebaseDatabase

class FlashcardsApplication : Application()
{
    override fun onCreate()
    {
        super.onCreate()

        initialise()
    }

    private fun initialise(): Unit
    {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }
}
