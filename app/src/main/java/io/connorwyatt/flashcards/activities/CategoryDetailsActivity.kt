package io.connorwyatt.flashcards.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import io.connorwyatt.flashcards.R
import io.connorwyatt.flashcards.data.entities.Category

class CategoryDetailsActivity : BaseActivity()
{
    //region Activity

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_details)
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
    }
}
