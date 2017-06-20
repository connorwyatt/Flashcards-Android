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
import io.connorwyatt.flashcards.data.viewmodels.TagViewModel
import io.connorwyatt.flashcards.viewholders.TagViewHolder

class TagListAdapter : RecyclerView.Adapter<TagViewHolder>() {
  private var viewModels: List<TagViewModel>? = null
  private val idMap: MutableList<String> = mutableListOf()
  private val onEditListeners: MutableList<(TagViewModel) -> Unit> = mutableListOf()
  private val onDeleteListeners: MutableList<(TagViewModel) -> Unit> = mutableListOf()

  init {
    setHasStableIds(true)
  }

  override fun getItemCount(): Int = viewModels?.size ?: 0

  override fun getItemId(position: Int): Long
    = idMap.indexOf(viewModels!![position].tag.id!!).toLong()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
    val inflatedView = LayoutInflater.from(parent.context)
      .inflate(R.layout.fragment_tag_list_item, parent, false)

    return TagViewHolder(inflatedView)
  }

  override fun onBindViewHolder(holder: TagViewHolder, position: Int): Unit {
    val bundle = viewModels!![position]

    holder.setData(bundle)

    holder.setOnEditListener { dispatchOnEditEvent(it) }
    holder.setOnDeleteListener { dispatchOnDeleteEvent(it) }
  }

  // region Data

  fun setItems(viewModels: List<TagViewModel>): Unit {
    this.viewModels = viewModels

    updateIdMap(viewModels)

    notifyDataSetChanged()
  }

  fun updateIdMap(viewModels: List<TagViewModel>): Unit {
    viewModels.forEach { bundle ->
      if (bundle.tag.id!! !in idMap) {
        idMap.add(bundle.tag.id!!)
      }
    }
  }

  // endregion

  //region Listeners

  fun addOnEditListener(listener: (TagViewModel) -> Unit): () -> Unit {
    onEditListeners.add(listener)

    return { onEditListeners.removeAll(listOf(listener)) }
  }

  fun addOnDeleteListener(listener: (TagViewModel) -> Unit): () -> Unit {
    onDeleteListeners.add(listener)

    return { onDeleteListeners.removeAll(listOf(listener)) }
  }

  private fun dispatchOnEditEvent(viewModel: TagViewModel): Unit {
    onEditListeners.forEach { it.invoke(viewModel) }
  }

  private fun dispatchOnDeleteEvent(viewModel: TagViewModel): Unit {
    onDeleteListeners.forEach { it.invoke(viewModel) }
  }

  //endregion
}
