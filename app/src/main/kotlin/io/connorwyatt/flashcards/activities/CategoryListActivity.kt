/*
 * Copyright (c) 2016-2017 Connor Wyatt <connorwyatt1@gmail.com>.
 *
 * This file can not be copied and/or distributed without the express permission of Connor Wyatt.
 */

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
import io.connorwyatt.flashcards.data.entities.Category
import io.connorwyatt.flashcards.data.services.CategoryService
import io.connorwyatt.flashcards.data.viewmodels.CategoryViewModel
import io.reactivex.Observable

class CategoryListActivity : BaseActivity() {
  lateinit private var coordinatorLayout: CoordinatorLayout
  private val categoryListAdapter = CategoryListAdapter()
  private val removedCategoryIds: MutableSet<String> = mutableSetOf()
  private var categoryViewModels: List<CategoryViewModel>? = null

  //region Activity

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_category_list)
  }

  override fun onStart() {
    super.onStart()

    initialiseUI()
  }

  override fun onResume() {
    super.onResume()

    refreshUIData()
  }

  //endregion

  //region Data

  private fun getData(): Observable<List<CategoryViewModel>> {
    return CategoryService.getAllAsStream().flatMap { categories ->
      val observables = categories.map {
        CategoryViewModel.getFromCategory(it)
      }

      val observable = if (observables.isNotEmpty()) {
        Observable.combineLatest(
          observables,
          { it.filterIsInstance(CategoryViewModel::class.java) }
        )
      } else {
        Observable.just(listOf())
      }

      observable.doAfterNext { categoryViewModels = it }
    }
  }

  private fun deleteCategory(viewModel: CategoryViewModel, deleteFlashcards: Boolean): Unit {
    removedCategoryIds.add(viewModel.category.id!!)

    refreshUIData()

    val snackbar = Snackbar.make(coordinatorLayout,
                                 getString(R.string.deleted_category_snackbar,
                                           viewModel.category.name),
                                 Snackbar.LENGTH_LONG)

    snackbar.setAction(getString(R.string.action_undo)) { _ ->
      removedCategoryIds.remove(viewModel.category.id!!)

      refreshUIData()
    }

    snackbar.addCallback(
      object : Snackbar.Callback() {
        override fun onDismissed(snackbar: Snackbar?, event: Int) {
          if (removedCategoryIds.contains(viewModel.category.id!!)) {
            removedCategoryIds.remove(viewModel.category.id!!)

            viewModel.delete(deleteFlashcards).subscribe { refreshUIData() }
          }
        }
      }
    )

    snackbar.show()
  }

  private fun refreshUIData() {
    updateAdapterData(getFilteredCategories(categoryViewModels))
  }

  private fun getFilteredCategories(categories: List<CategoryViewModel>?)
    = categories?.filterNot { it.category.id!! in removedCategoryIds } ?: listOf()

  //endregion

  //region UI

  private fun initialiseUI(): Unit {
    initialiseToolbar()

    initialiseRecycler()

    initialiseCoordinatorLayout()

    initialiseFAB()
  }

  private fun initialiseToolbar() {
    val toolbar = findViewById(R.id.category_list_toolbar) as Toolbar
    toolbar.setTitle(R.string.categories_title)
    setSupportActionBar(toolbar)
  }

  private fun initialiseRecycler(): Unit {
    val recycler = findViewById(R.id.category_list_recycler) as RecyclerView
    recycler.adapter = categoryListAdapter
    recycler.layoutManager = LinearLayoutManager(this)

    categoryListAdapter.addOnEditListener { navigateToCategoryDetails(it.category) }
    categoryListAdapter.addOnDeleteListener { showDeleteCategoryDialog(it) }

    getData().subscribe { updateAdapterData(getFilteredCategories(it)) }
  }

  private fun initialiseCoordinatorLayout(): Unit {
    coordinatorLayout = findViewById(R.id.category_list_coordinator_layout) as CoordinatorLayout
  }

  private fun initialiseFAB(): Unit {
    val fab = findViewById(R.id.category_list_add_button) as FloatingActionButton

    fab.setOnClickListener {
      navigateToCategoryDetails()
    }
  }

  private fun updateAdapterData(viewModels: List<CategoryViewModel>): Unit {
    categoryListAdapter.setItems(viewModels)
  }

  private fun showDeleteCategoryDialog(viewModel: CategoryViewModel): Unit {
    AlertDialog.Builder(this)
      .setTitle(getString(R.string.delete_category_dialog_title))
      .setMessage(getString(R.string.delete_category_dialog_message))
      .setPositiveButton(
        getString(R.string.delete_category_dialog_yes),
        { _, _ -> deleteCategory(viewModel, true) }
      )
      .setNegativeButton(
        getString(R.string.delete_category_dialog_no),
        { _, _ -> deleteCategory(viewModel, false) }
      )
      .setNeutralButton(
        getString(R.string.delete_category_dialog_cancel),
        { _, _ -> }
      )
      .create()
      .show()
  }

  //endregion

  //region Navigation

  private fun navigateToCategoryDetails(): Unit {
    CategoryDetailsActivity.startActivity(this)
  }

  private fun navigateToCategoryDetails(category: Category): Unit {
    CategoryDetailsActivity.startActivity(this, category)
  }

  //endregion

  companion object {
    fun startActivity(context: Context) {
      val intent = Intent(context, CategoryListActivity::class.java)

      context.startActivity(intent)
    }
  }
}
