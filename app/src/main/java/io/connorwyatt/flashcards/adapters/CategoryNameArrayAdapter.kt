package io.connorwyatt.flashcards.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

import io.connorwyatt.flashcards.data.entities.legacy.CategoryLegacy

class CategoryNameArrayAdapter(context: Context, categories: List<CategoryLegacy>) :
    ArrayAdapter<CategoryLegacy>(context, 0, categories)
{
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View
    {
        val category = getItem(position)
        var view = convertView

        if (view == null)
        {
            view = LayoutInflater
                .from(context)
                .inflate(android.R.layout.simple_spinner_item, parent, false)
        }

        val text = view!!.findViewById(android.R.id.text1) as TextView
        text.text = category.name

        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View
    {
        val category = getItem(position)
        var view = convertView

        if (view == null)
        {
            view = LayoutInflater
                .from(context)
                .inflate(android.R.layout.simple_spinner_dropdown_item, parent, false)
        }

        val text = view!!.findViewById(android.R.id.text1) as TextView
        text.text = category.name

        return view
    }
}
