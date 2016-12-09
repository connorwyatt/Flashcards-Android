package io.connorwyatt.flashcards.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.widget.Button
import android.widget.Toast
import io.connorwyatt.flashcards.R
import io.connorwyatt.flashcards.data.entities.Category
import io.connorwyatt.flashcards.services.CategoryService

class CategoryDetailsActivity : AppCompatActivity()
{
    private var category: Category = Category()
    private var name: TextInputEditText? = null


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_details)

        name = findViewById(R.id.category_details_name) as TextInputEditText
        val saveButton = findViewById(R.id.category_details_save_button) as Button

        saveButton.setOnClickListener { save() }

        val toolbar = findViewById(R.id.category_details_toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val actionBar = supportActionBar
        actionBar!!.setDisplayShowTitleEnabled(false)
        actionBar.setDisplayHomeAsUpEnabled(true)
    }

    private fun save()
    {
        category.name = name!!.text.toString()

        val categoryService = CategoryService(this)

        val nameTaken = categoryService.isCategoryNameTaken(category.name)

        if (nameTaken)
        {
            showToast(R.string.category_name_taken, category.name)

            return
        }

        category = categoryService.save(category)

        showToast(R.string.save_toast)
    }

    private fun showToast(messageStringId: Int, vararg values: String)
    {
        val toastMessage = getString(messageStringId, *values)
        val duration = Toast.LENGTH_SHORT

        val toast = Toast.makeText(this, toastMessage, duration)
        toast.show()
    }

    companion object Activities
    {
        fun startActivity(context: Context)
        {
            val intent = Intent(context, CategoryDetailsActivity::class.java)

            context.startActivity(intent)
        }
    }
}
