/*
 * Copyright (c) 2016-2017 Connor Wyatt <connorwyatt1@gmail.com>.
 *
 * This file can not be copied and/or distributed without the express permission of Connor Wyatt.
 */

package io.connorwyatt.flashcards.viewholders

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import io.connorwyatt.flashcards.R
import io.connorwyatt.flashcards.data.viewmodels.TagViewModel

class TagViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
  private var viewModel: TagViewModel? = null
  private val title: TextView = itemView.findViewById(
    R.id.fragment_tag_list_item_tag_title) as TextView
  private val count: TextView = itemView.findViewById(
    R.id.fragment_tag_list_item_tag_flashcard_count) as TextView
  private val status: TextView = itemView.findViewById(
    R.id.fragment_tag_list_item_tag_status) as TextView
  private val statusBar: LinearLayout = itemView.findViewById(
    R.id.fragment_tag_list_item_tag_status_bar) as LinearLayout
  private val menuButton: ImageButton = itemView.findViewById(
    R.id.fragment_tag_list_item_tag_menu) as ImageButton
  private var onEditListener: ((TagViewModel) -> Unit)? = null
  private var onDeleteListener: ((TagViewModel) -> Unit)? = null

  fun setData(viewModel: TagViewModel): Unit {
    val context = itemView.context
    val resources = context.resources

    this.viewModel = viewModel
    val (tag, flashcardCount, rating) = viewModel

    title.text = tag.name
    count.text = resources.getQuantityString(R.plurals.flashcard_count,
                                             flashcardCount,
                                             flashcardCount)

    var statusText: String = context.getString(R.string.tag_status_no_data)
    var statusTextColor: Int = ContextCompat.getColor(context, R.color.colorTextDisabled)
    var statusBarColor: Int = ContextCompat.getColor(context, R.color.colorGrey)

    rating?.let {
      if (it >= 0) {
        when {
          it > 2.0 / 3.0 -> {
            statusText = context.getString(R.string.tag_status_positive)
            statusTextColor = ContextCompat.getColor(context, R.color.colorPositiveDark)
            statusBarColor = ContextCompat.getColor(context, R.color.colorPositive)
          }
          it < 1.0 / 2.0 -> {
            statusText = context.getString(R.string.tag_status_negative)
            statusTextColor = ContextCompat.getColor(context, R.color.colorNegativeDark)
            statusBarColor = ContextCompat.getColor(context, R.color.colorNegative)
          }
          else -> {
            statusText = context.getString(R.string.tag_status_neutral)
            statusTextColor = ContextCompat.getColor(context, R.color.colorNeutralDark)
            statusBarColor = ContextCompat.getColor(context, R.color.colorNeutral)
          }
        }
      }
    }

    status.text = statusText
    statusBar.setBackgroundColor(statusBarColor)
    status.setTextColor(statusTextColor)

    menuButton.setOnClickListener { view ->
      createPopup(view) { menuItem ->
        when (menuItem.itemId) {
          R.id.fragment_tag_list_item_menu_action_edit -> {
            onEditListener?.invoke(viewModel)
          }
          R.id.fragment_tag_list_item_menu_action_delete -> {
            onDeleteListener?.invoke(viewModel)
          }
        }

        true
      }
    }
  }

  //region UI

  fun setOnEditListener(listener: (TagViewModel) -> Unit): Unit {
    onEditListener = listener
  }

  fun setOnDeleteListener(listener: (TagViewModel) -> Unit): Unit {
    onDeleteListener = listener
  }

  private fun createPopup(view: View, onMenuItemClickListener: (MenuItem) -> Boolean): Unit {
    val context = itemView.context
    val popup = PopupMenu(context, view, Gravity.RIGHT)

    popup.menuInflater.inflate(R.menu.fragment_tag_list_item_menu, popup.menu)

    popup.setOnMenuItemClickListener(onMenuItemClickListener)

    popup.show()
  }

  //endregion
}
