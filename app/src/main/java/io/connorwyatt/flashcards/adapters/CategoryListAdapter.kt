package io.connorwyatt.flashcards.adapters

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
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
        val categoryListItem = categoryListItems[position]
        val context = holder.itemView.context
        val resources = context.resources

        holder.title.text = categoryListItem.category.name
        holder.count.text = resources.getQuantityString(R.plurals.flashcard_count,
                categoryListItem.flashcardCount,
                categoryListItem.flashcardCount)

        var statusText: String = context.getString(R.string.category_status_no_data)
        var statusTextColor: Int? = null
        var statusBarColor: Int = ContextCompat.getColor(context, R.color.colorGrey)

        if (categoryListItem.averageRating != null)
        {
            when
            {
                categoryListItem.averageRating > 2.0 / 3.0 ->
                {
                    statusText = context.getString(R.string.category_status_positive)
                    statusTextColor = ContextCompat.getColor(context, R.color.colorPositiveDark)
                    statusBarColor = ContextCompat.getColor(context, R.color.colorPositive)
                }
                categoryListItem.averageRating < 1.0 / 3.0 ->
                {
                    statusText = context.getString(R.string.category_status_negative)
                    statusTextColor = ContextCompat.getColor(context, R.color.colorNegativeDark)
                    statusBarColor = ContextCompat.getColor(context, R.color.colorNegative)
                }
                else                                       ->
                {
                    statusText = context.getString(R.string.category_status_neutral)
                    statusTextColor = ContextCompat.getColor(context, R.color.colorNeutralDark)
                    statusBarColor = ContextCompat.getColor(context, R.color.colorNeutral)
                }
            }
        }

        holder.status.text = statusText
        holder.statusBar.setBackgroundColor(statusBarColor)
        if (statusTextColor != null) holder.status.setTextColor(statusTextColor)
    }

    class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        internal val title: TextView
        internal val count: TextView
        internal val status: TextView
        internal val statusBar: LinearLayout

        init
        {
            title = itemView.findViewById(R.id.fragment_category_list_item_category_title) as TextView
            count = itemView.findViewById(R.id.fragment_category_list_item_category_flashcard_count) as TextView
            status = itemView.findViewById(R.id.fragment_category_list_item_category_status) as TextView
            statusBar = itemView.findViewById(R.id.fragment_category_list_item_category_status_bar) as LinearLayout
        }
    }

    data class ListItem(val category: Category, val flashcardCount: Int, val averageRating: Double?)
}
