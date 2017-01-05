package io.connorwyatt.flashcards.fragments

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.connorwyatt.flashcards.R

class FlashcardTestFragment : Fragment()
{
    //region Activity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup,
                              savedInstanceState: Bundle?): View
    {
        super.onCreateView(inflater, container, savedInstanceState)


        val viewGroup = inflater.inflate(
            R.layout.fragment_flashcard_test, container, false) as ViewGroup

        return viewGroup
    }

    fun onBackPressed(callback: () -> Unit): Unit
    {
        callback.invoke()
    }

    //endregion
}
