package io.connorwyatt.flashcards.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v7.widget.Toolbar
import io.connorwyatt.flashcards.R
import io.connorwyatt.flashcards.data.entities.Flashcard
import io.connorwyatt.flashcards.data.viewmodels.FlashcardViewModel
import io.reactivex.Observable

class FlashcardDetailsActivity : BaseActivity()
{
    private var titleInput: TextInputEditText? = null
    private var textInput: TextInputEditText? = null
    private var categoriesInput: TextInputEditText? = null

    //region Activity

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flashcard_details)

        initialiseUI(intent.getStringExtra(IntentExtras.FLASHCARD_ID))
    }

    //endregion

    //region Data

    private fun getData(flashcardId: String): Observable<FlashcardViewModel>
    {
        return FlashcardViewModel.get(flashcardId, false)
    }

    //endregion

    //region UI

    private fun initialiseUI(flashcardId: String?): Unit
    {
        flashcardId?.let {
            getData(it).subscribe {
                updateControls(it)
            }
        }

        setUpToolbar()

        setUpControls()
    }

    private fun setUpToolbar(): Unit
    {
        val toolbar = findViewById(R.id.flashcard_details_toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayShowTitleEnabled(false)
    }

    private fun setUpControls(): Unit
    {
        titleInput = findViewById(R.id.flashcard_details_title) as TextInputEditText
        textInput = findViewById(R.id.flashcard_details_text) as TextInputEditText
        categoriesInput = findViewById(R.id.flashcard_details_categories) as TextInputEditText
    }

    private fun updateControls(flashcardViewModel: FlashcardViewModel): Unit
    {
        val (flashcard, categories) = flashcardViewModel

        if (flashcard.title != null && flashcard.title!!.isNotEmpty())
            titleInput!!.setText(flashcard.title)

        if (flashcard.text != null && flashcard.text!!.isNotEmpty())
            textInput!!.setText(flashcard.text)

        if (categories.isNotEmpty())
            categoriesInput!!.setText(
                categories.map { it.name }.joinToString(separator = ", ")
            )
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

            intent.putExtra(IntentExtras.FLASHCARD_ID, flashcard.id)

            context.startActivity(intent)
        }

        object IntentExtras
        {
            val FLASHCARD_ID = "FLASHCARD_ID"
        }
    }
}
