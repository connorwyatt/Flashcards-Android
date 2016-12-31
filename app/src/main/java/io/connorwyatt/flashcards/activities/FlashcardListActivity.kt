package io.connorwyatt.flashcards.activities

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import io.connorwyatt.flashcards.R
import io.connorwyatt.flashcards.adapters.FlashcardListAdapter
import io.connorwyatt.flashcards.data.entities.Category
import io.connorwyatt.flashcards.data.entities.Flashcard
import io.connorwyatt.flashcards.data.services.CategoryService
import io.connorwyatt.flashcards.data.services.FlashcardService
import io.connorwyatt.flashcards.data.services.FlashcardTestService
import io.connorwyatt.flashcards.data.viewmodels.FlashcardViewModel
import io.connorwyatt.flashcards.enums.Rating
import io.reactivex.Observable

class FlashcardListActivity : BaseActivity()
{
    private var adapter = FlashcardListAdapter()

    // region Activity Lifecycle

    override fun onCreate(savedInstanceState: Bundle?): Unit
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flashcard_list)

        initialiseUI()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean
    {
        menuInflater.inflate(R.menu.activity_flashcard_card_list_menu, menu)

        if (adapter.itemCount == 0)
        {
            menu.findItem(R.id.action_test).isEnabled = false
            menu.findItem(R.id.action_test).icon.mutate().alpha = 100
        }

        return super.onCreateOptionsMenu(menu)
    }

    // endregion

    //region Data

    fun getData(): Observable<List<FlashcardViewModel>>
    {
        return FlashcardService.getAll().flatMap { flashcards ->
            val observables = flashcards.map { flashcard ->
                val flashcardId = flashcard.id!!
                val categoriesObservable
                    = CategoryService.getByFlashcardId(flashcardId)
                val flashcardTestsObservable
                    = FlashcardTestService.getAverageRatingForFlashcard(flashcardId)

                return@map Observable.combineLatest(
                    listOf(categoriesObservable, flashcardTestsObservable),
                    { it }
                ).map {
                    var categories = it[0] as List<*>
                    val ratingValue = it[1] as Double

                    categories = categories.filterIsInstance(Category::class.java)

                    val rating = Rating.fromValue(ratingValue) ?: Rating.NOT_RATED

                    FlashcardViewModel(flashcard, categories, rating)
                }
            }

            return@flatMap Observable.combineLatest(observables,
                                                    { it.filterIsInstance(FlashcardViewModel::class.java) })
        }
    }

    //endregion

    // region UI

    private fun initialiseUI(): Unit
    {
        setUpToolbar()

        setUpFAB()

        setUpListRecyclerView()
    }

    private fun setUpToolbar(): Unit
    {
        val toolbar = findViewById(R.id.flashcard_list_toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val actionBar = supportActionBar
        actionBar!!.setDisplayShowTitleEnabled(false)
    }

    private fun setUpListRecyclerView(): Unit
    {
        val recyclerView = findViewById(
            R.id.flashcard_list_recycler_view) as RecyclerView

        recyclerView.setHasFixedSize(true)

        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        adapter.addOnCardClickListener {
            navigateToFlashcardDetails(it.flashcard)
        }

        recyclerView.adapter = adapter

        getData().subscribe { updateAdapterData(it) }
    }

    private fun setUpFAB(): Unit
    {
        val fab = findViewById(R.id.flashcard_list_floating_action_button) as FloatingActionButton

        fab.setOnClickListener {
            navigateToFlashcardDetails()
        }
    }

    private fun updateAdapterData(viewModels: List<FlashcardViewModel>): Unit
    {
        adapter.setItems(viewModels)
    }

    // endregion

    // region Navigation

    private fun navigateToFlashcardDetails(): Unit
    {
        // TODO Add navigation to details
    }

    private fun navigateToFlashcardDetails(flashcard: Flashcard): Unit
    {
        // TODO Add navigation to details
    }

    // endregion
}
