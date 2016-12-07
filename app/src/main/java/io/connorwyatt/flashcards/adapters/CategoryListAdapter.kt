package io.connorwyatt.flashcards.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.connorwyatt.flashcards.R
import io.connorwyatt.flashcards.data.entities.Category

class CategoryListAdapter(categories: List<Category>) : RecyclerView.Adapter<CategoryListAdapter.ViewHolder>()
{
    private val categories: List<Category>

    init
    {
        this.categories = categories
    }

    override fun getItemCount(): Int
    {
        return categories.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        val inflatedView = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_category_list_item, parent, false)

        return ViewHolder(inflatedView)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        val category = categories[position];

        holder.title.text = category.name
    }

    class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        internal val title: TextView;

        init
        {
            title = itemView.findViewById(R.id.fragment_category_list_item_category_title) as TextView
        }
    }
}
