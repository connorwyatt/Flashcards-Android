package io.connorwyatt.flashcards.activities.legacy

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v4.app.NavUtils
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import io.connorwyatt.flashcards.R
import io.connorwyatt.flashcards.activities.BaseActivity
import io.connorwyatt.flashcards.data.entities.legacy.CategoryLegacy
import io.connorwyatt.flashcards.data.services.legacy.CategoryServiceLegacy

@Deprecated("This is considered legacy.")
class CategoryDetailsActivityLegacy : BaseActivity()
{
    private var category: CategoryLegacy = CategoryLegacy()
    private var name: TextInputEditText? = null
    private var nameLayout: TextInputLayout? = null
    private var saveButton: Button? = null
    private val categoryService = CategoryServiceLegacy(this)

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_details)

        name = findViewById(R.id.category_details_name) as TextInputEditText
        nameLayout = findViewById(R.id.category_details_name_layout) as TextInputLayout

        setUpTextListeners()

        saveButton = findViewById(R.id.category_details_save_button) as Button

        saveButton!!.setOnClickListener { save() }

        if (intent.hasExtra(CATEGORY_ID))
        {
            category = categoryService.getById(intent.getLongExtra(CATEGORY_ID, -1))
            updateViewFromCategory(category)
        }

        updateButton()

        val toolbar = findViewById(R.id.category_details_toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val actionBar = supportActionBar
        actionBar!!.setDisplayShowTitleEnabled(false)
        actionBar.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean
    {
        menuInflater.inflate(R.menu.activity_category_details_menu, menu)

        if (!category.existsInDatabase())
        {
            menu.findItem(R.id.action_delete).isEnabled = false
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        when (item.itemId)
        {
            R.id.action_delete ->
            {
                showDeleteCategoryDialog(category)
                return true
            }
            else               ->
            {
                return super.onOptionsItemSelected(item)
            }
        }
    }

    private fun save()
    {
        category.name = name!!.text.toString()

        val nameTaken = categoryService.isCategoryNameTaken(category.name!!)

        if (nameTaken)
        {
            showToast(R.string.category_name_taken, category.name!!)

            return
        }

        category = categoryService.save(category)

        showToast(R.string.save_toast)
        invalidateOptionsMenu()
        updateViewFromCategory(category)
        updateButton()
    }

    private fun deleteCategory(category: CategoryLegacy, deleteFlashcards: Boolean = false)
    {
        if (deleteFlashcards)
            categoryService.deleteWithFlashcards(category)
        else
            categoryService.delete(category)

        showToast(R.string.flashcard_details_delete_toast)

        NavUtils.navigateUpFromSameTask(this)
    }

    private fun showDeleteCategoryDialog(category: CategoryLegacy)
    {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.delete_category_dialog_title))
            .setMessage(getString(R.string.delete_category_dialog_message))
            .setPositiveButton(
                getString(R.string.delete_category_dialog_yes),
                { dialogInterface, i -> deleteCategory(category, true) }
            )
            .setNegativeButton(
                getString(R.string.delete_category_dialog_no),
                { dialogInterface, i -> deleteCategory(category) }
            )
            .setNeutralButton(
                getString(R.string.delete_category_dialog_cancel),
                { dialogInterface, i -> }
            )
            .create()
            .show()
    }

    private fun showToast(messageStringId: Int, vararg values: String)
    {
        val toastMessage = getString(messageStringId, *values)
        val duration = Toast.LENGTH_SHORT

        val toast = Toast.makeText(this, toastMessage, duration)
        toast.show()
    }

    private fun updateViewFromCategory(category: CategoryLegacy)
    {
        name!!.setText(category.name)
    }

    private fun updateButton()
    {
        saveButton!!.isEnabled = isValid()
    }

    private fun isValid(): Boolean
    {
        return getNameError() === null
    }

    private fun getNameError(): String?
    {
        val value = name!!.text.toString()
        val maxLength = 40

        when
        {
            value.length === 0       ->
            {
                return getString(R.string.validation_required)
            }
            value.length > maxLength ->
            {
                return getString(R.string.validation_max_length, value.length, maxLength)
            }
        }

        return null
    }

    private fun setUpTextListeners()
    {
        fun updateNameError()
        {
            val error = getNameError()

            if (error !== null)
            {
                nameLayout!!.error = error
            }
            else
            {
                nameLayout!!.isErrorEnabled = false
            }
        }

        name!!.setOnFocusChangeListener { view, isFocused ->
            if (!isFocused)
            {
                updateNameError()
            }
        }

        name!!.addTextChangedListener(
            object : TextWatcher
            {
                override fun afterTextChanged(s: Editable?)
                {
                }

                override fun beforeTextChanged(s: CharSequence?,
                                               start: Int,
                                               count: Int,
                                               after: Int)
                {
                }

                override fun onTextChanged(s: CharSequence?,
                                           start: Int,
                                           before: Int,
                                           count: Int)
                {
                    updateNameError()
                    updateButton()
                }
            }
        )
    }

    companion object Activities
    {
        internal val CATEGORY_ID = "CATEGORY_ID"

        fun startActivity(context: Context, category: CategoryLegacy? = null)
        {
            val intent = Intent(context, CategoryDetailsActivityLegacy::class.java)

            if (category !== null)
            {
                intent.putExtra(CATEGORY_ID, category.id)
            }

            context.startActivity(intent)
        }
    }
}