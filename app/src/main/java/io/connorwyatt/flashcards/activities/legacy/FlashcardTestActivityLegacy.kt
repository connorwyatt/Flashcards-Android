package io.connorwyatt.flashcards.activities.legacy

import android.content.Context
import android.content.Intent
import android.os.Bundle
import io.connorwyatt.flashcards.R
import io.connorwyatt.flashcards.activities.BaseActivity
import io.connorwyatt.flashcards.fragments.legacy.FlashcardTestFragmentLegacy

@Deprecated("This is considered legacy.")
class FlashcardTestActivityLegacy : BaseActivity()
{
    var flashcardTestFragment: FlashcardTestFragmentLegacy? = null
        private set

    override fun onBackPressed()
    {
        flashcardTestFragment!!.onBackPressed(Runnable { super@FlashcardTestActivityLegacy.onBackPressed() })
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flashcard_test)

        val fm = fragmentManager
        flashcardTestFragment = fm.findFragmentByTag(FRAGMENT_TAG) as FlashcardTestFragmentLegacy?

        if (flashcardTestFragment == null)
        {
            flashcardTestFragment = FlashcardTestFragmentLegacy()

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
            val intent = Intent(context, FlashcardTestActivityLegacy::class.java)

            context.startActivity(intent)
        }

        fun startActivityWithCategoryFilter(context: Context, categoryId: Long)
        {
            val extras = Bundle()
            extras.putLong(EXTRA_KEYS.CATEGORY_ID, categoryId)

            val intent = Intent(context, FlashcardTestActivityLegacy::class.java)
            intent.putExtras(extras)

            context.startActivity(intent)
        }
    }
}
