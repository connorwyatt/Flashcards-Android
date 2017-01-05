package io.connorwyatt.flashcards.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import io.connorwyatt.flashcards.R

class FlashcardTestActivity : BaseActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flashcard_test)
    }

    companion object
    {
        fun startActivity(context: Context)
        {
            val intent = Intent(context, FlashcardTestActivity::class.java)

            context.startActivity(intent)
        }

        fun startActivityWithCategoryFilter(context: Context, categoryId: String)
        {
            val extras = Bundle()
            extras.putString(IntentExtras.CATEGORY_ID, categoryId)

            val intent = Intent(context, FlashcardTestActivity::class.java)
            intent.putExtras(extras)

            context.startActivity(intent)
        }

        object IntentExtras
        {
            val CATEGORY_ID = "CATEGORY_ID"
        }
    }
}
