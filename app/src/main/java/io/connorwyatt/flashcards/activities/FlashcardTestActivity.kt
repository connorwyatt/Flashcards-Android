package io.connorwyatt.flashcards.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import io.connorwyatt.flashcards.R
import io.connorwyatt.flashcards.fragments.FlashcardTestFragment

class FlashcardTestActivity : BaseActivity()
{
    var flashcardTestFragment: FlashcardTestFragment? = null
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
        flashcardTestFragment!!.onBackPressed { super.onBackPressed() }
    }

    //endregion

    //region UI

    private fun initialiseFragment(categoryId: String?): Unit
    {
        var fragment = fragmentManager.findFragmentByTag(FRAGMENT_TAG) as FlashcardTestFragment?

        if (fragment == null)
        {
            fragment = FlashcardTestFragment()

            val arguments = Bundle()

            arguments.putString(
                FlashcardTestFragment.Companion.ArgumentKeys.CATEGORY_ID, categoryId)

            fragment.arguments = arguments

            fragmentManager
                .beginTransaction()
                .add(R.id.flashcard_test_frame, fragment, FRAGMENT_TAG)
                .commit()
        }

        flashcardTestFragment = fragment
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
