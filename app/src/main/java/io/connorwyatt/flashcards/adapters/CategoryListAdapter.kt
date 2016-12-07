package io.connorwyatt.flashcards.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.connorwyatt.flashcards.R
import io.connorwyatt.flashcards.data.entities.Category

class CategoryListAdapter(private val categoryListItems: List<ListItem>) : RecyclerView.Adapter<CategoryListAdapter.ViewHolder>()
{
    override fun getItemCount(): Int
    {
        return categoryListItems.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        val inflatedView = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_category_list_item, parent, false)

        return ViewHolder(inflatedView)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        val categoryListItem = categoryListItems[position];
        val resources = holder.itemView.context.resources

        holder.title.text = categoryListItem.category.name
        holder.count.text = resources.getQuantityString(R.plurals.flashcard_count,
                categoryListItem.flashcardCount,
                categoryListItem.flashcardCount)
    }

    class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        internal val title: TextView;
        internal val count: TextView;

        init
        {
            title = itemView.findViewById(R.id.fragment_category_list_item_category_title) as TextView
            count = itemView.findViewById(R.id.fragment_category_list_item_category_flashcard_count) as TextView
        }
    }

    data class ListItem(val category: Category, val flashcardCount: Int)
}
