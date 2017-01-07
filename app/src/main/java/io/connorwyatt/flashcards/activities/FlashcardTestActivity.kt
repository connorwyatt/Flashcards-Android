package io.connorwyatt.flashcards.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import io.connorwyatt.flashcards.R
import io.connorwyatt.flashcards.fragments.FlashcardTestFragment

class FlashcardTestActivity : BaseActivity()
{
    lateinit var flashcardTestFragment: FlashcardTestFragment
        private set

    //region Activity

    override fun onCreate(savedInstanceState: Bundle?): Unit
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flashcard_test)

        val categoryId = intent.getStringExtra(IntentExtras.CATEGORY_ID)

        initialiseFragment(categoryId)
    }

    override fun onBackPressed()
    {
        flashcardTestFragment.onBackPressed { super.onBackPressed() }
    }

    //endregion

    //region UI

    private fun initialiseFragment(categoryId: String?): Unit
    {
        flashcardTestFragment =
            fragmentManager.findFragmentByTag(FRAGMENT_TAG) as FlashcardTestFragment?
            ?: FlashcardTestFragment(categoryId)

        fragmentManager
            .beginTransaction()
            .add(R.id.flashcard_test_frame, flashcardTestFragment, FRAGMENT_TAG)
            .commit()
    }

    //endregion

    companion object
    {
        private val FRAGMENT_TAG = "DATA"

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
