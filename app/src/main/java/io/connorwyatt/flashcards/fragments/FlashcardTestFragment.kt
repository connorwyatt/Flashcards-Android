package io.connorwyatt.flashcards.fragments

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.connorwyatt.flashcards.R
import io.connorwyatt.flashcards.adapters.FlashcardTestPagerAdapter
import io.connorwyatt.flashcards.data.entities.Flashcard
import io.connorwyatt.flashcards.data.entities.FlashcardTest
import io.connorwyatt.flashcards.data.services.FlashcardService
import io.connorwyatt.flashcards.data.services.FlashcardTestService
import io.connorwyatt.flashcards.enums.Rating
import io.connorwyatt.flashcards.views.directionalviewpager.DirectionalViewPager
import io.reactivex.Observable

class FlashcardTestFragment : Fragment()
{
    private var flashcardTestPagerAdapter: FlashcardTestPagerAdapter? = null
    private var flashcards: List<Flashcard>? = null

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

        val categoryId = arguments.getString(ArgumentKeys.CATEGORY_ID)

        initialiseUI(viewGroup, categoryId)

        return viewGroup
    }

    fun onBackPressed(callback: () -> Unit): Unit
    {
        callback.invoke()
    }

    //endregion

    //region Data

    fun getFlashcardFromAdapter(id: String) = flashcardTestPagerAdapter!!.getFlashcardById(id)

    fun rateFlashcard(flashcard: Flashcard, rating: Rating): Observable<FlashcardTest>
    {
        val flashcardTest = FlashcardTest(null)

        flashcardTest.relationships.setRelationships("flashcard", listOf(flashcard.id!!))

        flashcardTest.rating = rating

        return saveFlashcardTest(flashcardTest)
    }

    private fun getData(categoryId: String?): Observable<List<Flashcard>>
    {
        return if (categoryId !== null)
            FlashcardService.getByCategory(categoryId)
        else
            FlashcardService.getAll()
    }

    private fun saveFlashcardTest(flashcardTest: FlashcardTest): Observable<FlashcardTest>
    {
        return FlashcardTestService.save(flashcardTest)
    }

    //endregion

    //region UI

    private fun initialiseUI(viewGroup: ViewGroup, categoryId: String?): Unit
    {
        initialisePager(viewGroup, categoryId)
    }

    private fun initialisePager(viewGroup: ViewGroup, categoryId: String?): Unit
    {
        flashcardTestPagerAdapter = FlashcardTestPagerAdapter(fragmentManager)

        if (flashcards == null)
        {
            getData(categoryId).subscribe {
                flashcards = it

                flashcardTestPagerAdapter!!.setData(it)
            }
        }
        else
        {
            flashcardTestPagerAdapter!!.setData(flashcards!!)
        }

        val viewPager =
            viewGroup.findViewById(R.id.flashcard_test_view_pager) as DirectionalViewPager

        viewPager.adapter = flashcardTestPagerAdapter
        viewPager.allowLeftSwipe = false
    }

    //endregion

    companion object
    {
        object ArgumentKeys
        {
            val CATEGORY_ID = "CATEGORY_ID"
        }
    }
}
