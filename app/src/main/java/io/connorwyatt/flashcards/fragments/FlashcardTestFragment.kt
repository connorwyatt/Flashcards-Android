package io.connorwyatt.flashcards.fragments

import android.app.Fragment

class FlashcardTestFragment : Fragment()
{
    fun onBackPressed(callback: () -> Unit): Unit
    {
        callback.invoke()
    }
}
