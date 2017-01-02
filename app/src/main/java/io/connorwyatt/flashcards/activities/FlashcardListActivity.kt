package io.connorwyatt.flashcards.activities

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import io.connorwyatt.flashcards.R
import io.connorwyatt.flashcards.adapters.DropdownItem
import io.connorwyatt.flashcards.adapters.FlashcardListAdapter
import io.connorwyatt.flashcards.adapters.GenericArrayAdapter
import io.connorwyatt.flashcards.data.entities.Category
import io.connorwyatt.flashcards.data.entities.Flashcard
import io.connorwyatt.flashcards.data.services.CategoryService
import io.connorwyatt.flashcards.data.services.FlashcardService
import io.connorwyatt.flashcards.data.viewmodels.FlashcardViewModel
import io.connorwyatt.flashcards.listeners.SimpleOnItemSelectedListener
import io.reactivex.Observable

class FlashcardListActivity : BaseActivity()
{
    private var adapter = FlashcardListAdapter()
    private var filterCategory: Category? = null

    // region Activity

    override fun onCreate(savedInstanceState: Bundle?): Unit
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flashcard_list)

        initialiseUI()
    }

    override fun onResume()
    {
        super.onResume()

        filterByCategory(filterCategory)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        return when (item.itemId)
        {
            R.id.action_test ->
            {
                val category = filterCategory

                if (category != null)
                {
                    navigateToFlashcardTest(category)
                }
                else
                {
                    navigateToFlashcardTest()
                }
                true
            }
            R.id.action_navigate_to_categories ->
            {
                navigateToCategoryList()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // endregion

    //region Data

    fun getData(): Observable<List<FlashcardViewModel>>
    {
        return mapFlashcardsToViewModels(FlashcardService.getAll())
    }

    fun getDataWithCategoryFilter(category: Category): Observable<List<FlashcardViewModel>>
    {
        return mapFlashcardsToViewModels(FlashcardService.getByCategory(category.id!!))
    }

    fun getDropdownCategories(): Observable<List<DropdownItem<Category?>>>
    {
        return CategoryService.getAll().map { categories ->
            val dropdownItems = categories.map { category ->
                DropdownItem<Category?>(value = category.name!!, data = category)
            }

            val allCategoryName = getString(R.string.flashcard_cards_list_all_category)

            listOf(DropdownItem<Category?>(value = allCategoryName, data = null))
                .plus(dropdownItems)
        }
    }

    private fun mapFlashcardsToViewModels(flashcardsObservable: Observable<List<Flashcard>>): Observable<List<FlashcardViewModel>>
    {
        return flashcardsObservable.flatMap { flashcards ->
            val observables = flashcards.map { FlashcardViewModel.getFromFlashcard(it) }

            if (observables.isNotEmpty())
            {
                Observable.combineLatest(
                    observables,
                    { it.filterIsInstance(FlashcardViewModel::class.java) }
                )
            }
            else
            {
                Observable.just(listOf())
            }
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

        setUpSpinner()
    }

    private fun setUpSpinner()
    {
        getDropdownCategories().subscribe { dropdownCategories ->
            val spinner = findViewById(R.id.flashcard_list_spinner) as Spinner

            val spinnerAdapter = GenericArrayAdapter<Category?>(
                supportActionBar!!.themedContext,
                dropdownCategories
            )

            spinner.onItemSelectedListener = object : SimpleOnItemSelectedListener()
            {
                override fun onItemSelected(adapterView: AdapterView<*>,
                                            view: View?,
                                            position: Int,
                                            id: Long)
                {
                    val category
                        = (adapterView.getItemAtPosition(position) as DropdownItem<*>).data as Category?

                    filterByCategory(category)
                }
            }

            spinner.adapter = spinnerAdapter
        }
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

        invalidateOptionsMenu()
    }

    private fun filterByCategory(category: Category?): Unit
    {
        filterCategory = category

        if (category !== null)
        {
            getDataWithCategoryFilter(category).subscribe {
                updateAdapterData(it)
            }
        }
        else
        {
            getData().subscribe {
                updateAdapterData(it)
            }
        }
    }

    // endregion

    // region Navigation

    private fun navigateToFlashcardDetails(): Unit
    {
        FlashcardDetailsActivity.startActivity(this)
    }

    private fun navigateToFlashcardDetails(flashcard: Flashcard): Unit
    {
        FlashcardDetailsActivity.startActivityWithFlashcard(this, flashcard)
    }

    private fun navigateToFlashcardTest(): Unit
    {
        // TODO Add navigation to FlashcardTest
    }

    private fun navigateToFlashcardTest(category: Category): Unit
    {
        // TODO Add navigation to FlashcardTest
    }

    private fun navigateToCategoryList(): Unit
    {
        CategoryListActivity.startActivity(this)
    }

    // endregion
}
