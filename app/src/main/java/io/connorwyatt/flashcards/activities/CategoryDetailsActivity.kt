package io.connorwyatt.flashcards.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import io.connorwyatt.flashcards.R
import io.connorwyatt.flashcards.data.entities.Category
import io.connorwyatt.flashcards.data.services.CategoryService
import io.connorwyatt.flashcards.exceptions.CategoryNameTakenException
import io.connorwyatt.flashcards.views.textinput.EnhancedTextInputEditText
import io.reactivex.Observable

class CategoryDetailsActivity : BaseActivity()
{
    lateinit private var category: Category
    lateinit private var nameInput: EnhancedTextInputEditText
    lateinit private var saveButton: Button

    //region Activity

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_details)

        initialiseUI(intent.getStringExtra(IntentExtras.CATEGORY_ID))
    }

    //endregion

    //region Data

    private fun getData(categoryId: String): Observable<Category>
    {
        return CategoryService.getById(categoryId)
    }

    private fun updateCategoryFromControls(): Unit
    {
        category.name = nameInput.editableText.toString()
    }

    private fun saveCategory(category: Category): Unit
    {
        category.save().subscribe(
            {
                this.category = it
                updateUI()
                showToast(R.string.save_toast)
            },
            {
                when (it)
                {
                    is CategoryNameTakenException ->
                    {
                        showToast(R.string.category_name_taken, category.name!!)
                    }
                }
            }
        )
    }

    //endregion

    //region UI

    private fun initialiseUI(categoryId: String?): Unit
    {
        if (categoryId != null)
        {
            getData(categoryId).subscribe {
                category = it
                updateUI()
            }
        }
        else
        {
            category = Category(null)
        }

        initialiseControls()
    }

    private fun initialiseControls(): Unit
    {
        nameInput = findViewById(R.id.category_details_name) as EnhancedTextInputEditText

        saveButton = findViewById(R.id.category_details_save_button) as Button

        saveButton.setOnClickListener {
            updateCategoryFromControls()

            saveCategory(category)
        }
    }

    private fun updateUI(): Unit
    {
        updateControls()
    }

    private fun updateControls(): Unit
    {
        nameInput.setText(category.name)
    }

    private fun showToast(messageStringId: Int, vararg values: String)
    {
        val toastMessage = getString(messageStringId, *values)
        val duration = Toast.LENGTH_SHORT

        val toast = Toast.makeText(this, toastMessage, duration)
        toast.show()
    }

    //endregion

    companion object
    {
        internal val CATEGORY_ID = "CATEGORY_ID"

        fun startActivity(context: Context)
        {
            val intent = Intent(context, CategoryDetailsActivity::class.java)

            context.startActivity(intent)
        }

        fun startActivity(context: Context, category: Category)
        {
            val intent = Intent(context, CategoryDetailsActivity::class.java)

            intent.putExtra(CATEGORY_ID, category.id)

            context.startActivity(intent)
        }

        object IntentExtras
        {
            val CATEGORY_ID = "CATEGORY_ID"
        }
    }
}
