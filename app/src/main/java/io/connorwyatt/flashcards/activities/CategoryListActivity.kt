package io.connorwyatt.flashcards.activities

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
import io.connorwyatt.flashcards.adapters.CategoryListAdapter
import io.connorwyatt.flashcards.data.services.CategoryService
import io.connorwyatt.flashcards.data.viewmodels.CategoryViewModel
import io.reactivex.Observable

class CategoryListActivity : BaseActivity()
{
    lateinit private var coordinatorLayout: CoordinatorLayout
    private val categoryListAdapter = CategoryListAdapter()
    private val removedCategoryIds: MutableSet<String> = mutableSetOf()

    //region Activity

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_list)

        initialiseUI()
    }

    //endregion

    //region Data

    private fun getData(): Observable<List<CategoryViewModel>>
    {
        return CategoryService.getAll().flatMap { categories ->
            val observables = categories.map {
                CategoryViewModel.getFromCategory(it)
            }

            if (observables.isNotEmpty())
            {
                Observable.combineLatest(
                    observables,
                    { it.filterIsInstance(CategoryViewModel::class.java) }
                )
            }
            else
            {
                Observable.just(listOf())
            }
        }
    }

    private fun deleteCategory(viewModel: CategoryViewModel, deleteFlashcards: Boolean): Unit
    {
        removedCategoryIds.add(viewModel.category.id!!)

        refreshUIData()

        val snackbar = Snackbar.make(coordinatorLayout,
                                     getString(R.string.deleted_category_snackbar,
                                               viewModel.category.name),
                                     Snackbar.LENGTH_LONG)

        snackbar.setAction(getString(R.string.action_undo)) { view ->
            removedCategoryIds.remove(viewModel.category.id!!)

            refreshUIData()
        }

        snackbar.addCallback(
            object : Snackbar.Callback()
            {
                override fun onDismissed(snackbar: Snackbar?, event: Int)
                {
                    if (removedCategoryIds.contains(viewModel.category.id!!))
                    {
                        removedCategoryIds.remove(viewModel.category.id!!)

                        viewModel.delete(deleteFlashcards).subscribe { refreshUIData() }
                    }
                }
            }
        )

        snackbar.show()
    }

    private fun refreshUIData()
    {
        getData().subscribe {
            updateAdapterData(it.filterNot { it.category.id!! in removedCategoryIds })
        }
    }

    //endregion

    //region UI

    private fun initialiseUI(): Unit
    {
        initialiseToolbar()

        initialiseRecycler()

        initialiseCoordinatorLayout()

        initialiseFAB()
    }

    private fun initialiseToolbar()
    {
        val toolbar = findViewById(R.id.category_list_toolbar) as Toolbar
        toolbar.setTitle(R.string.categories_title)
        setSupportActionBar(toolbar)
    }

    private fun initialiseRecycler(): Unit
    {
        val recycler = findViewById(R.id.category_list_recycler) as RecyclerView
        recycler.adapter = categoryListAdapter
        recycler.layoutManager = LinearLayoutManager(this)

        categoryListAdapter.addOnDeleteListener { showDeleteCategoryDialog(it) }

        refreshUIData()
    }

    private fun initialiseCoordinatorLayout(): Unit
    {
        coordinatorLayout = findViewById(R.id.category_list_coordinator_layout) as CoordinatorLayout
    }

    private fun initialiseFAB(): Unit
    {
        val fab = findViewById(R.id.category_list_add_button) as FloatingActionButton

        fab.setOnClickListener { view ->
            navigateToCategoryDetails()
        }
    }

    private fun updateAdapterData(viewModels: List<CategoryViewModel>): Unit
    {
        categoryListAdapter.setItems(viewModels)
    }

    private fun showDeleteCategoryDialog(viewModel: CategoryViewModel): Unit
    {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.delete_category_dialog_title))
            .setMessage(getString(R.string.delete_category_dialog_message))
            .setPositiveButton(
                getString(R.string.delete_category_dialog_yes),
                { di, i -> deleteCategory(viewModel, true) }
            )
            .setNegativeButton(
                getString(R.string.delete_category_dialog_no),
                { di, i -> deleteCategory(viewModel, false) }
            )
            .setNeutralButton(
                getString(R.string.delete_category_dialog_cancel),
                { di, i -> }
            )
            .create()
            .show()
    }

    //endregion

    //region Navigation

    private fun navigateToCategoryDetails(): Unit
    {
        //TODO Navigate to Category Details
    }

    //endregion

    companion object
    {
        fun startActivity(context: Context)
        {
            val intent = Intent(context, CategoryListActivity::class.java)

            context.startActivity(intent)
        }
    }
}
