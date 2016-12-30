package io.connorwyatt.flashcards.activities.legacy

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import io.connorwyatt.flashcards.R
import io.connorwyatt.flashcards.activities.BaseActivity
import io.connorwyatt.flashcards.adapters.legacy.CategoryListAdapterLegacy
import io.connorwyatt.flashcards.data.entities.legacy.CategoryLegacy
import io.connorwyatt.flashcards.data.services.legacy.CategoryServiceLegacy

@Deprecated("This is considered legacy.")
class CategoriesActivityLegacy : BaseActivity()
{
    private val categoryService = CategoryServiceLegacy(this)
    private var categoryItems: MutableList<CategoryListAdapterLegacy.ListItem> = mutableListOf()
    private val removedCategoryIds: MutableList<Long> = mutableListOf()

    private var categoryListAdapter: CategoryListAdapterLegacy? = null
    private var coordinatorLayout: CoordinatorLayout? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)

        categoryItems = getCategoryListItems().toMutableList()

        setUpToolbar()

        setUpRecycler(categoryItems.toList())

        setUpFloatingActionButton()
    }

    private fun getCategoryListItems(): List<CategoryListAdapterLegacy.ListItem>
    {
        val categories = categoryService.getAll()

        val categoryListItems: List<CategoryListAdapterLegacy.ListItem> =
            categories.map(
                fun(category: CategoryLegacy): CategoryListAdapterLegacy.ListItem
                {
                    val flashcardCountForCategory =
                        categoryService.getFlashcardsForCategory(category.id!!).size
                    val averageRating = categoryService.getAverageRatingForCategory(category.id!!)

                    return CategoryListAdapterLegacy.ListItem(
                        category,
                        flashcardCountForCategory,
                        averageRating)
                }
            )

        return categoryListItems
    }

    private fun getFilteredCategoryListItems(): List<CategoryListAdapterLegacy.ListItem>
    {
        return categoryItems.filterNot { categoryItem -> removedCategoryIds.contains(categoryItem.category.id) }
    }

    private fun setUpToolbar()
    {
        val toolbar = findViewById(R.id.categories_toolbar) as Toolbar
        toolbar.setTitle(R.string.categories_title)
        setSupportActionBar(toolbar)
    }

    private fun setUpRecycler(categoryListItems: List<CategoryListAdapterLegacy.ListItem>)
    {
        coordinatorLayout = findViewById(R.id.categories_coordinator_layout) as CoordinatorLayout
        categoryListAdapter = CategoryListAdapterLegacy(categoryListItems)

        categoryListAdapter!!.addOnEditListener { category ->
            editCategory(category)
        }

        categoryListAdapter!!.addOnDeleteListener { category ->
            showDeleteCategoryDialog(category)
        }

        val recycler = findViewById(R.id.categories_recycler) as RecyclerView
        recycler.adapter = categoryListAdapter
        recycler.layoutManager = LinearLayoutManager(this)
    }

    private fun editCategory(category: CategoryLegacy)
    {
        CategoryDetailsActivityLegacy.startActivity(this, category)
    }

    private fun showDeleteCategoryDialog(category: CategoryLegacy)
    {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.delete_category_dialog_title))
            .setMessage(getString(R.string.delete_category_dialog_message))
            .setPositiveButton(
                getString(R.string.delete_category_dialog_yes),
                { dialogInterface, i -> deleteCategory(category, true) }
            )
            .setNegativeButton(
                getString(R.string.delete_category_dialog_no),
                { dialogInterface, i -> deleteCategory(category) }
            )
            .setNeutralButton(
                getString(R.string.delete_category_dialog_cancel),
                { dialogInterface, i -> }
            )
            .create()
            .show()
    }

    private fun deleteCategory(category: CategoryLegacy, deleteFlashcards: Boolean = false)
    {
        removedCategoryIds.add(category.id!!)

        categoryListAdapter!!.updateData(getFilteredCategoryListItems())

        val snackbar = Snackbar.make(coordinatorLayout!!,
                                     getString(R.string.deleted_category_snackbar,
                                               category.name),
                                     Snackbar.LENGTH_LONG)
            .setAction(getString(R.string.action_undo), { view ->
                removedCategoryIds.remove(category.id!!)
                categoryListAdapter!!.updateData(getFilteredCategoryListItems())
            })
            .setCallback(object : Snackbar.Callback()
                         {
                             override fun onDismissed(snackbar: Snackbar?, event: Int)
                             {
                                 if (removedCategoryIds.contains(category.id!!))
                                 {
                                     removedCategoryIds.remove(category.id!!)

                                     if (deleteFlashcards)
                                         categoryService.deleteWithFlashcards(category)
                                     else
                                         categoryService.delete(category)

                                     categoryItems.removeAll { categoryItem -> categoryItem.category.id === category.id }
                                     categoryListAdapter!!.updateData(
                                         getFilteredCategoryListItems())
                                 }
                             }
                         })

        snackbar.show()
    }

    private fun setUpFloatingActionButton()
    {
        val fab = findViewById(R.id.categories_add_button) as FloatingActionButton

        fab.setOnClickListener { view ->
            CategoryDetailsActivityLegacy.startActivity(this)
        }
    }

    companion object Activities
    {
        fun startActivity(context: Context)
        {
            val intent = Intent(context, CategoriesActivityLegacy::class.java)

            context.startActivity(intent)
        }
    }
}
