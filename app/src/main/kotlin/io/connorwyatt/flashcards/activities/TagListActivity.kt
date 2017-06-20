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
import io.connorwyatt.flashcards.adapters.TagListAdapter
import io.connorwyatt.flashcards.data.entities.Tag
import io.connorwyatt.flashcards.data.services.TagService
import io.connorwyatt.flashcards.data.viewmodels.TagViewModel
import io.reactivex.Observable

class TagListActivity : BaseActivity() {
  lateinit private var coordinatorLayout: CoordinatorLayout
  private val tagListAdapter = TagListAdapter()
  private val removedTagIds: MutableSet<String> = mutableSetOf()
  private var tagViewModels: List<TagViewModel>? = null

  //region Activity

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_tag_list)
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

  private fun getData(): Observable<List<TagViewModel>> {
    return TagService.getAllAsStream().flatMap { tags ->
      val observables = tags.map {
        TagViewModel.getFromTag(it)
      }

      val observable = if (observables.isNotEmpty()) {
        Observable.combineLatest(
          observables,
          { it.filterIsInstance(TagViewModel::class.java) }
        )
      } else {
        Observable.just(listOf())
      }

      observable.doAfterNext { tagViewModels = it }
    }
  }

  private fun deleteTag(viewModel: TagViewModel, deleteFlashcards: Boolean): Unit {
    removedTagIds.add(viewModel.tag.id!!)

    refreshUIData()

    val snackbar = Snackbar.make(coordinatorLayout,
                                 getString(R.string.deleted_tag_snackbar,
                                           viewModel.tag.name),
                                 Snackbar.LENGTH_LONG)

    snackbar.setAction(getString(R.string.action_undo)) { view ->
      removedTagIds.remove(viewModel.tag.id!!)

      refreshUIData()
    }

    snackbar.addCallback(
      object : Snackbar.Callback() {
        override fun onDismissed(snackbar: Snackbar?, event: Int) {
          if (removedTagIds.contains(viewModel.tag.id!!)) {
            removedTagIds.remove(viewModel.tag.id!!)

            viewModel.delete(deleteFlashcards).subscribe { refreshUIData() }
          }
        }
      }
    )

    snackbar.show()
  }

  private fun refreshUIData() {
    updateAdapterData(getFilteredTags(tagViewModels))
  }

  private fun getFilteredTags(tags: List<TagViewModel>?)
    = tags?.filterNot { it.tag.id!! in removedTagIds } ?: listOf()

  //endregion

  //region UI

  private fun initialiseUI(): Unit {
    initialiseToolbar()

    initialiseRecycler()

    initialiseCoordinatorLayout()

    initialiseFAB()
  }

  private fun initialiseToolbar() {
    val toolbar = findViewById(R.id.tag_list_toolbar) as Toolbar
    toolbar.setTitle(R.string.tags_title)
    setSupportActionBar(toolbar)
  }

  private fun initialiseRecycler(): Unit {
    val recycler = findViewById(R.id.tag_list_recycler) as RecyclerView
    recycler.adapter = tagListAdapter
    recycler.layoutManager = LinearLayoutManager(this)

    tagListAdapter.addOnEditListener { navigateToTagDetails(it.tag) }
    tagListAdapter.addOnDeleteListener { showDeleteTagDialog(it) }

    getData().subscribe { updateAdapterData(getFilteredTags(it)) }
  }

  private fun initialiseCoordinatorLayout(): Unit {
    coordinatorLayout = findViewById(R.id.tag_list_coordinator_layout) as CoordinatorLayout
  }

  private fun initialiseFAB(): Unit {
    val fab = findViewById(R.id.tag_list_add_button) as FloatingActionButton

    fab.setOnClickListener { view ->
      navigateToTagDetails()
    }
  }

  private fun updateAdapterData(viewModels: List<TagViewModel>): Unit {
    tagListAdapter.setItems(viewModels)
  }

  private fun showDeleteTagDialog(viewModel: TagViewModel): Unit {
    AlertDialog.Builder(this)
      .setTitle(getString(R.string.delete_tag_dialog_title))
      .setMessage(getString(R.string.delete_tag_dialog_message))
      .setPositiveButton(
        getString(R.string.delete_tag_dialog_yes),
        { di, i -> deleteTag(viewModel, true) }
      )
      .setNegativeButton(
        getString(R.string.delete_tag_dialog_no),
        { di, i -> deleteTag(viewModel, false) }
      )
      .setNeutralButton(
        getString(R.string.delete_tag_dialog_cancel),
        { di, i -> }
      )
      .create()
      .show()
  }

  //endregion

  //region Navigation

  private fun navigateToTagDetails(): Unit {
    TagDetailsActivity.startActivity(this)
  }

  private fun navigateToTagDetails(tag: Tag): Unit {
    TagDetailsActivity.startActivity(this, tag)
  }

  //endregion

  companion object {
    fun startActivity(context: Context) {
      val intent = Intent(context, TagListActivity::class.java)

      context.startActivity(intent)
    }
  }
}
