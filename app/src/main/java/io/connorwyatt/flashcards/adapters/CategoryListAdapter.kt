/*
 * Copyright (c) 2016-2017 Connor Wyatt <connorwyatt1@gmail.com>.
 *
 * This file can not be copied and/or distributed without the express permission of Connor Wyatt.
 */

package io.connorwyatt.flashcards.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import io.connorwyatt.flashcards.R
import io.connorwyatt.flashcards.data.viewmodels.CategoryViewModel
import io.connorwyatt.flashcards.viewholders.CategoryViewHolder

class CategoryListAdapter : RecyclerView.Adapter<CategoryViewHolder>() {
  private var viewModels: List<CategoryViewModel>? = null
  private val idMap: MutableList<String> = mutableListOf()
  private val onEditListeners: MutableList<(CategoryViewModel) -> Unit> = mutableListOf()
  private val onDeleteListeners: MutableList<(CategoryViewModel) -> Unit> = mutableListOf()

  init {
    setHasStableIds(true)
  }

  override fun getItemCount(): Int = viewModels?.size ?: 0

  override fun getItemId(position: Int): Long
    = idMap.indexOf(viewModels!![position].category.id!!).toLong()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
    val inflatedView = LayoutInflater.from(parent.context)
      .inflate(R.layout.fragment_category_list_item, parent, false)

    return CategoryViewHolder(inflatedView)
  }

  override fun onBindViewHolder(holder: CategoryViewHolder, position: Int): Unit {
    val bundle = viewModels!![position]

    holder.setData(bundle)

    holder.setOnEditListener { dispatchOnEditEvent(it) }
    holder.setOnDeleteListener { dispatchOnDeleteEvent(it) }
  }

  // region Data

  fun setItems(viewModels: List<CategoryViewModel>): Unit {
    this.viewModels = viewModels

    updateIdMap(viewModels)

    notifyDataSetChanged()
  }

  fun updateIdMap(viewModels: List<CategoryViewModel>): Unit {
    viewModels.forEach { bundle ->
      if (bundle.category.id!! !in idMap) {
        idMap.add(bundle.category.id!!)
      }
    }
  }

  // endregion

  //region Listeners

  fun addOnEditListener(listener: (CategoryViewModel) -> Unit): () -> Unit {
    onEditListeners.add(listener)

    return { onEditListeners.removeAll(listOf(listener)) }
  }

  fun addOnDeleteListener(listener: (CategoryViewModel) -> Unit): () -> Unit {
    onDeleteListeners.add(listener)

    return { onDeleteListeners.removeAll(listOf(listener)) }
  }

  private fun dispatchOnEditEvent(viewModel: CategoryViewModel): Unit {
    onEditListeners.forEach { it.invoke(viewModel) }
  }

  private fun dispatchOnDeleteEvent(viewModel: CategoryViewModel): Unit {
    onDeleteListeners.forEach { it.invoke(viewModel) }
  }

  //endregion
}
