package io.connorwyatt.flashcards.fragments

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.connorwyatt.flashcards.R
import io.connorwyatt.flashcards.data.entities.Flashcard
import io.connorwyatt.flashcards.data.services.FlashcardService
import io.connorwyatt.flashcards.views.directionalviewpager.DirectionalViewPager
import io.reactivex.Observable

class FlashcardTestFragment(private val categoryId: String?) : Fragment()
{
    private var initialCount: Int = 0

    //region Activity

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup,
                              savedInstanceState: Bundle?): View
    {
        super.onCreateView(inflater, container, savedInstanceState)

        val viewGroup = inflater.inflate(
            R.layout.fragment_flashcard_test, container, false) as ViewGroup

        initialiseUI(viewGroup, categoryId)

        return viewGroup
    }

    fun onBackPressed(callback: () -> Unit): Unit
    {
        callback.invoke()
    }

    //endregion

    //region Data

    private fun getData(categoryId: String?): Observable<List<Flashcard>>
    {
        return if (categoryId !== null)
            FlashcardService.getByCategory(categoryId)
        else
            FlashcardService.getAll()
    }

    //endregion

    //region UI

    private fun initialiseUI(viewGroup: ViewGroup, categoryId: String?)
    {
        initialisePager(viewGroup, categoryId)
    }

    private fun initialisePager(viewGroup: ViewGroup, categoryId: String?)
    {
        getData(categoryId).subscribe {
            initialCount = it.size
        }

        val viewPager =
            viewGroup.findViewById(R.id.flashcard_test_view_pager) as DirectionalViewPager

        viewPager.allowLeftSwipe = false
    }

    //endregion
}
