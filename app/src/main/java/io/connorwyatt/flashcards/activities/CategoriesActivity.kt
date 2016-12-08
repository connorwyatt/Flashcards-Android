package io.connorwyatt.flashcards.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import io.connorwyatt.flashcards.R
import io.connorwyatt.flashcards.adapters.CategoryListAdapter
import io.connorwyatt.flashcards.data.entities.Category
import io.connorwyatt.flashcards.services.CategoryService

class CategoriesActivity : AppCompatActivity()
{
    private var categoryItems: List<CategoryListAdapter.ListItem>? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)

        categoryItems = getCategoryListItems()

        setUpToolbar()

        setUpRecycler(categoryItems as List<CategoryListAdapter.ListItem>)
    }

    private fun getCategoryListItems(): List<CategoryListAdapter.ListItem>
    {
        val categoryService = CategoryService(this)
        val categories = categoryService.getAll()

        val categoryListItems: List<CategoryListAdapter.ListItem> = categories.map(fun(category: Category): CategoryListAdapter.ListItem
        {
            val flashcardCountForCategory = categoryService.getFlashcardsForCategory(category.id).size
            val averageRating = categoryService.getAverageRatingForCategory(category.id)

            return CategoryListAdapter.ListItem(category, flashcardCountForCategory, averageRating)
        })

        return categoryListItems
    }

    private fun setUpToolbar()
    {
        val toolbar = findViewById(R.id.categories_toolbar) as Toolbar
        toolbar.setTitle(R.string.categories_title)
        setSupportActionBar(toolbar)
    }

    private fun setUpRecycler(categoryListItems: List<CategoryListAdapter.ListItem>)
    {
        val categoryListAdapter = CategoryListAdapter(categoryListItems)

        val recycler = findViewById(R.id.categories_recycler) as RecyclerView
        recycler.adapter = categoryListAdapter
        recycler.layoutManager = LinearLayoutManager(this)
    }

    companion object Activities
    {
        fun startActivity(context: Context)
        {
            val intent = Intent(context, CategoriesActivity::class.java)

            context.startActivity(intent)
        }
    }
}
