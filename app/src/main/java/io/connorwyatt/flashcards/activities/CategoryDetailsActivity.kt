package io.connorwyatt.flashcards.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v4.app.NavUtils
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import io.connorwyatt.flashcards.R
import io.connorwyatt.flashcards.data.entities.Category
import io.connorwyatt.flashcards.services.CategoryService

class CategoryDetailsActivity : AppCompatActivity()
{
    private var category: Category = Category()
    private var name: TextInputEditText? = null
    private val categoryService = CategoryService(this)

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
                delete()
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

        val nameTaken = categoryService.isCategoryNameTaken(category.name)

        if (nameTaken)
        {
            showToast(R.string.category_name_taken, category.name)

            return
        }

        category = categoryService.save(category)

        showToast(R.string.save_toast)
        invalidateOptionsMenu()
    }

    private fun delete()
    {
        categoryService.delete(category)

        showToast(R.string.flashcard_details_delete_toast)

        NavUtils.navigateUpFromSameTask(this)
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
