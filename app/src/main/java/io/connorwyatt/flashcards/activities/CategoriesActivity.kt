package io.connorwyatt.flashcards.activities

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import io.connorwyatt.flashcards.R
import io.connorwyatt.flashcards.adapters.CategoryListAdapter
import io.connorwyatt.flashcards.data.entities.Category
import io.connorwyatt.flashcards.data.services.CategoryService

class CategoriesActivity : BaseActivity()
{
    private val categoryService = CategoryService(this)
    private var categoryItems: MutableList<CategoryListAdapter.ListItem> = mutableListOf()
    private val removedCategoryIds: MutableList<Long> = mutableListOf()

    private var categoryListAdapter: CategoryListAdapter? = null
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

    private fun getCategoryListItems(): List<CategoryListAdapter.ListItem>
    {
        val categories = categoryService.getAll()

        val categoryListItems: List<CategoryListAdapter.ListItem> =
            categories.map(
                fun(category: Category): CategoryListAdapter.ListItem
                {
                    val flashcardCountForCategory =
                        categoryService.getFlashcardsForCategory(category.id!!).size
                    val averageRating = categoryService.getAverageRatingForCategory(category.id!!)

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
        coordinatorLayout = findViewById(R.id.categories_coordinator_layout) as CoordinatorLayout
        categoryListAdapter = CategoryListAdapter(categoryListItems)

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

    private fun editCategory(category: Category)
    {
        CategoryDetailsActivity.startActivity(this, category)
    }

    private fun showDeleteCategoryDialog(category: Category)
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

    private fun deleteCategory(category: Category, deleteFlashcards: Boolean = false)
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
            CategoryDetailsActivity.startActivity(this)
        }
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
