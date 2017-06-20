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
import io.connorwyatt.flashcards.data.viewmodels.FlashcardViewModel
import io.connorwyatt.flashcards.viewholders.FlashcardViewHolder

class FlashcardListAdapter :
  RecyclerView.Adapter<FlashcardViewHolder>() {
  private var viewModels: List<FlashcardViewModel>? = null
  private val idMap: MutableList<String> = mutableListOf()
  private var cardClickListener: ((FlashcardViewModel) -> Unit)? = null

  init {
    setHasStableIds(true)
  }

  override fun getItemCount(): Int = viewModels?.size ?: 0


  override fun getItemId(position: Int): Long
    = idMap.indexOf(viewModels!![position].flashcard.id!!).toLong()


  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlashcardViewHolder {
    val inflatedView = LayoutInflater.from(parent.context)
      .inflate(R.layout.fragment_flashcard_card, parent, false)

    return FlashcardViewHolder(inflatedView)
  }

  override fun onBindViewHolder(holder: FlashcardViewHolder, position: Int): Unit {
    val bundle = viewModels!![position]

    holder.setOnCardClickListener {
      cardClickListener?.invoke(viewModels!![holder.adapterPosition])
    }

    holder.setData(bundle)
  }

  // region Data

  fun setItems(viewModels: List<FlashcardViewModel>): Unit {
    this.viewModels = viewModels

    updateIdMap(viewModels)

    notifyDataSetChanged()
  }

  fun updateIdMap(viewModels: List<FlashcardViewModel>): Unit {
    viewModels.forEach { (flashcard) ->
      if (flashcard.id!! !in idMap) {
        idMap.add(flashcard.id)
      }
    }
  }

  // endregion

  //region Listeners

  fun setOnCardClickListener(listener: (FlashcardViewModel) -> Unit): Unit {
    cardClickListener = listener
  }

  //endregion
}
