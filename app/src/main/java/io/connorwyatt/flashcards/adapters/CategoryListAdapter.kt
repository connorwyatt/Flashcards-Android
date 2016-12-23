package io.connorwyatt.flashcards.adapters

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import io.connorwyatt.flashcards.R
import io.connorwyatt.flashcards.data.entities.legacy.CategoryLegacy
import java.util.ArrayList

class CategoryListAdapter(private var categoryListItems: List<ListItem>) : RecyclerView.Adapter<CategoryListAdapter.ViewHolder>()
{
    private val onDeleteListeners = ArrayList<(CategoryLegacy) -> Unit>()
    private val onEditListeners = ArrayList<(CategoryLegacy) -> Unit>()

    override fun getItemCount(): Int = categoryListItems.size

    override fun getItemId(position: Int): Long = categoryListItems[position].category.id!!

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
        var statusTextColor: Int = ContextCompat.getColor(context, R.color.colorTextDisabled)
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
        holder.status.setTextColor(statusTextColor)

        holder.menuButton.setOnClickListener { view ->
            val popup = PopupMenu(context, view, Gravity.RIGHT)

            popup.menuInflater.inflate(R.menu.fragment_category_list_item_menu, popup.menu)

            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId)
                {
                    R.id.fragment_category_list_item_menu_action_edit   ->
                    {
                        dispatchOnEditEvent(categoryListItem.category)
                    }
                    R.id.fragment_category_list_item_menu_action_delete ->
                    {
                        dispatchOnDeleteEvent(categoryListItem.category)
                    }
                    else                                                ->
                    {
                    }
                }
                true
            }

            popup.show()
        }
    }

    fun updateData(categoryListItems: List<ListItem>)
    {
        this.categoryListItems = categoryListItems

        notifyDataSetChanged()
    }

    fun addOnDeleteListener(listener: (CategoryLegacy) -> Unit)
    {
        onDeleteListeners.add(listener)
    }

    fun removeOnDeleteListener(listener: (CategoryLegacy) -> Unit)
    {
        onDeleteListeners.remove(listener)
    }

    fun clearOnDeleteListeners()
    {
        onDeleteListeners.clear()
    }

    fun addOnEditListener(listener: (CategoryLegacy) -> Unit)
    {
        onEditListeners.add(listener)
    }

    fun removeOnEditListener(listener: (CategoryLegacy) -> Unit)
    {
        onEditListeners.remove(listener)
    }

    fun clearOnEditListeners()
    {
        onEditListeners.clear()
    }

    private fun dispatchOnDeleteEvent(category: CategoryLegacy)
    {
        onDeleteListeners.forEach { listener ->
            listener(category)
        }
    }

    private fun dispatchOnEditEvent(category: CategoryLegacy)
    {
        onEditListeners.forEach { listener ->
            listener(category)
        }
    }

    class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        internal val title: TextView = itemView.findViewById(R.id.fragment_category_list_item_category_title) as TextView
        internal val count: TextView = itemView.findViewById(R.id.fragment_category_list_item_category_flashcard_count) as TextView
        internal val status: TextView = itemView.findViewById(R.id.fragment_category_list_item_category_status) as TextView
        internal val statusBar: LinearLayout = itemView.findViewById(R.id.fragment_category_list_item_category_status_bar) as LinearLayout
        internal val menuButton: ImageButton = itemView.findViewById(R.id.fragment_category_list_item_category_menu) as ImageButton
    }

    data class ListItem(val category: CategoryLegacy, val flashcardCount: Int, val averageRating: Double?)
}
