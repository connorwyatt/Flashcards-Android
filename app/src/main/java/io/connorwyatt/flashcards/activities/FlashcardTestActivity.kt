package io.connorwyatt.flashcards.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.connorwyatt.flashcards.R
import io.connorwyatt.flashcards.fragments.FlashcardTestFragment

class FlashcardTestActivity : BaseActivity()
{
    var flashcardTestFragment: FlashcardTestFragment? = null
        private set

    override fun onBackPressed()
    {
        flashcardTestFragment!!.onBackPressed(Runnable { super@FlashcardTestActivity.onBackPressed() })
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flashcard_test)

        val fm = fragmentManager
        flashcardTestFragment = fm.findFragmentByTag(FRAGMENT_TAG) as FlashcardTestFragment?

        if (flashcardTestFragment == null)
        {
            flashcardTestFragment = FlashcardTestFragment()

            fragmentManager
                .beginTransaction()
                .add(R.id.flashcard_test_frame, flashcardTestFragment, FRAGMENT_TAG)
                .commit()
        }
    }

    object EXTRA_KEYS
    {
        var CATEGORY_ID = "CATEGORY_ID"
    }

    companion object
    {
        private val FRAGMENT_TAG = "DATA"

        fun startActivity(context: Context)
        {
            val intent = Intent(context, FlashcardTestActivity::class.java)

            context.startActivity(intent)
        }

        fun startActivityWithCategoryFilter(context: Context, categoryId: Long)
        {
            val extras = Bundle()
            extras.putLong(EXTRA_KEYS.CATEGORY_ID, categoryId)

            val intent = Intent(context, FlashcardTestActivity::class.java)
            intent.putExtras(extras)

            context.startActivity(intent)
        }
    }
}
