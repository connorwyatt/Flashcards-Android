package io.connorwyatt.flashcards.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import io.connorwyatt.flashcards.R

class CategoriesActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)

        setUpToolbar()
    }

    private fun setUpToolbar()
    {
        val toolbar = findViewById(R.id.categories_toolbar) as Toolbar
        toolbar.setTitle(R.string.categories_title)
        setSupportActionBar(toolbar)
    }

    companion object Activities
    {
        fun startActivity(context: Context)
        {
            val intent = Intent(context, CategoriesActivity::class.java)

            context.startActivity(intent)
        }
    }
}
