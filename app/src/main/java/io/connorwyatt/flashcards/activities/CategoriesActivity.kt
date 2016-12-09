package io.connorwyatt.flashcards.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
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
    private val categoryService = CategoryService(this)
    private var categoryItems: MutableList<CategoryListAdapter.ListItem> = mutableListOf()
    private val removedCategoryIds: MutableList<Long> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)

        categoryItems = getCategoryListItems().toMutableList()

        setUpToolbar()

        setUpRecycler(categoryItems.toList())
    }

    private fun getCategoryListItems(): List<CategoryListAdapter.ListItem>
    {
        val categories = categoryService.getAll()

        val categoryListItems: List<CategoryListAdapter.ListItem> =
            categories.map(
                fun(category: Category): CategoryListAdapter.ListItem
                {
                    val flashcardCountForCategory =
                        categoryService.getFlashcardsForCategory(category.id).size
                    val averageRating = categoryService.getAverageRatingForCategory(category.id)

                    return CategoryListAdapter.ListItem(
                        category,
                        flashcardCountForCategory,
                        averageRating)
                }
            )

        return categoryListItems
    }

    private fun getFilteredCategoryListItems(): List<CategoryListAdapter.ListItem>
    {
        return categoryItems.filterNot { categoryItem -> removedCategoryIds.contains(categoryItem.category.id) }
    }

    private fun setUpToolbar()
    {
        val toolbar = findViewById(R.id.categories_toolbar) as Toolbar
        toolbar.setTitle(R.string.categories_title)
        setSupportActionBar(toolbar)
    }

    private fun setUpRecycler(categoryListItems: List<CategoryListAdapter.ListItem>)
    {
        val coordinatorLayout = findViewById(R.id.categories_coordinator_layout) as CoordinatorLayout
        val categoryListAdapter = CategoryListAdapter(categoryListItems)

        categoryListAdapter.addOnDeleteListener { category ->
            removedCategoryIds.add(category.id)

            categoryListAdapter.updateData(getFilteredCategoryListItems())

            val snackbar = Snackbar.make(coordinatorLayout,
                                         getString(R.string.deleted_category_snackbar,
                                                   category.name),
                                         Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.action_undo), { view ->
                    removedCategoryIds.remove(category.id)
                    categoryListAdapter.updateData(getFilteredCategoryListItems())
                })
                .setCallback(object : Snackbar.Callback()
                             {
                                 override fun onDismissed(snackbar: Snackbar?, event: Int)
                                 {
                                     if (removedCategoryIds.contains(category.id))
                                     {
                                         removedCategoryIds.remove(category.id)
                                         categoryService.delete(category)
                                         categoryItems.removeAll { categoryItem -> categoryItem.category.id === category.id }
                                         categoryListAdapter.updateData(
                                             getFilteredCategoryListItems())
                                     }
                                 }
                             })

            snackbar.show()
        }

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
