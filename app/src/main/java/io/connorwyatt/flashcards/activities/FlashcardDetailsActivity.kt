package io.connorwyatt.flashcards.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import io.connorwyatt.flashcards.R
import io.connorwyatt.flashcards.data.entities.Flashcard

class FlashcardDetailsActivity : BaseActivity()
{
    //region Activity

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flashcard_details)
    }

    //endregion

    companion object
    {
        fun startActivity(context: Context)
        {
            val intent = Intent(context, FlashcardDetailsActivity::class.java)

            context.startActivity(intent)
        }

        fun startActivityWithFlashcard(context: Context, flashcard: Flashcard)
        {
            val intent = Intent(context, FlashcardDetailsActivity::class.java)

            intent.putExtra(INTENT_EXTRAS.FLASHCARD_ID, flashcard.id)

            context.startActivity(intent)
        }

        object INTENT_EXTRAS
        {
            val FLASHCARD_ID = "FLASHCARD_ID"
        }
    }
}
