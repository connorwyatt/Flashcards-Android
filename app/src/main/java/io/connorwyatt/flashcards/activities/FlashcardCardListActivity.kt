package io.connorwyatt.flashcards.activities

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import io.connorwyatt.flashcards.R
import io.connorwyatt.flashcards.adapters.CategoryNameArrayAdapter
import io.connorwyatt.flashcards.adapters.FlashcardCardListAdapter
import io.connorwyatt.flashcards.data.entities.legacy.CategoryLegacy
import io.connorwyatt.flashcards.data.entities.legacy.FlashcardLegacy
import io.connorwyatt.flashcards.data.services.legacy.CategoryServiceLegacy
import io.connorwyatt.flashcards.data.services.legacy.FlashcardServiceLegacy

class FlashcardCardListActivity : BaseActivity()
{
    private var adapter: FlashcardCardListAdapter? = null
    private var allCategory: CategoryLegacy? = null
    private var filterCategory: CategoryLegacy? = null

    private val allCategories: List<CategoryLegacy>
        get()
        {
            val categoryService = CategoryServiceLegacy(this)
            val categories = categoryService.getAll().toMutableList()

            if (allCategory == null)
            {
                allCategory = CategoryLegacy()
                allCategory!!.name = getString(R.string.flashcard_cards_list_all_category)
            }

            categories.add(0, allCategory!!)

            return categories
        }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flashcard_card_list)

        setUpToolbar()

        setUpListRecyclerView()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean
    {
        menuInflater.inflate(R.menu.activity_flashcard_card_list_menu, menu)

        if (adapter!!.itemCount == 0)
        {
            menu.findItem(R.id.action_test).isEnabled = false
            menu.findItem(R.id.action_test).icon.mutate().alpha = 100
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        when (item.itemId)
        {
            R.id.action_test                   ->
            {
                if (allCategory == filterCategory)
                {
                    FlashcardTestActivity.startActivity(this)
                }
                else
                {
                    FlashcardTestActivity
                        .startActivityWithCategoryFilter(this, filterCategory!!.id!!)
                }
                return true
            }
            R.id.action_navigate_to_categories ->
            {
                CategoriesActivity.startActivity(this)
                return true
            }
            else                               -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onResume()
    {
        super.onResume()

        updateAdapterData()
    }

    fun addNewFlashcard(view: View)
    {
        FlashcardDetailsActivity.startActivity(this)
    }

    private fun setUpListRecyclerView()
    {
        val recyclerView = findViewById(
            R.id.flashcard_card_list_recycler_view) as RecyclerView

        recyclerView.setHasFixedSize(true)

        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        adapter = FlashcardCardListAdapter()
        adapter!!.setOnCardClickListener(
            object : FlashcardCardListAdapter.OnCardClickListener
            {
                override fun onClick(flashcard: FlashcardLegacy)
                {
                    FlashcardDetailsActivity.startActivityWithFlashcard(
                        this@FlashcardCardListActivity, flashcard)
                }
            }
        )

        updateAdapterData()

        recyclerView.adapter = adapter
    }

    private fun setUpSpinner()
    {
        val categories = allCategories

        val spinner = findViewById(R.id.flashcard_card_list_spinner) as Spinner
        val spinnerAdapter = CategoryNameArrayAdapter(
            supportActionBar!!.themedContext, categories)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener
        {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View?, position: Int,
                                        id: Long)
            {
                val category = adapterView.getItemAtPosition(position) as CategoryLegacy

                filterCategory = category

                if (category == allCategory)
                {
                    adapter!!.removeFilter()
                }
                else
                {
                    adapter!!.applyCategoryFilter(category.id!!)
                }

                invalidateOptionsMenu()
            }

            override fun onNothingSelected(adapterView: AdapterView<*>)
            {
            }
        }

        spinner.adapter = spinnerAdapter
    }

    private fun setUpToolbar()
    {
        val toolbar = findViewById(R.id.flashcard_card_list_toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val actionBar = supportActionBar
        actionBar!!.setDisplayShowTitleEnabled(false)

        setUpSpinner()
    }

    private fun updateAdapterData()
    {
        val flashcardService = FlashcardServiceLegacy(this)
        val flashcards = flashcardService.getAll()

        adapter!!.setItems(flashcards)
        invalidateOptionsMenu()
    }
}
