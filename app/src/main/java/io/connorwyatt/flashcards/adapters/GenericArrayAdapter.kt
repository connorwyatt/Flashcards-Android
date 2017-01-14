/*
 * Copyright (c) 2016-2017 Connor Wyatt <connorwyatt1@gmail.com>.
 *
 * This file can not be copied and/or distributed without the express permission of Connor Wyatt.
 */

package io.connorwyatt.flashcards.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class GenericArrayAdapter<T> constructor(context: Context,
                                         private val itemList: List<DropdownItem<T>>) :
  ArrayAdapter<DropdownItem<T>>(context, 0, itemList) {
  override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
    return getViewForLayout(android.R.layout.simple_spinner_item,
                            position,
                            convertView,
                            parent)
  }

  override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
    return getViewForLayout(android.R.layout.simple_spinner_dropdown_item,
                            position,
                            convertView,
                            parent)
  }

  private fun getViewForLayout(layout: Int,
                               position: Int,
                               convertView: View?,
                               parent: ViewGroup): View {
    val item = getItem(position)
    var view = convertView

    if (view == null) {
      view = LayoutInflater
        .from(context)
        .inflate(layout, parent, false)
    }

    val text = view!!.findViewById(android.R.id.text1) as TextView
    text.text = item.value

    return view
  }
}
