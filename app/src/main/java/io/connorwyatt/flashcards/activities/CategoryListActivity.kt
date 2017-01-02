package io.connorwyatt.flashcards.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import io.connorwyatt.flashcards.R

class CategoryListActivity : BaseActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_list)
    }

    companion object
    {
        fun startActivity(context: Context)
        {
            val intent = Intent(context, CategoryListActivity::class.java)

            context.startActivity(intent)
        }
    }
}
