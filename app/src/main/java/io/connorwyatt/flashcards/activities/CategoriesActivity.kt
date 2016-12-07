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
    private var categories: List<Category>? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)

        categories = CategoryService(this).getAll()

        setUpToolbar()

        setUpRecycler(categories as List<Category>)
    }

    private fun setUpToolbar()
    {
        val toolbar = findViewById(R.id.categories_toolbar) as Toolbar
        toolbar.setTitle(R.string.categories_title)
        setSupportActionBar(toolbar)
    }

    private fun setUpRecycler(categories: List<Category>)
    {
        val categoryListAdapter = CategoryListAdapter(categories)

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
