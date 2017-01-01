package io.connorwyatt.flashcards.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.widget.Button
import io.connorwyatt.flashcards.R
import io.connorwyatt.flashcards.data.entities.Category
import io.connorwyatt.flashcards.data.entities.Flashcard
import io.connorwyatt.flashcards.data.viewmodels.FlashcardViewModel
import io.reactivex.Observable

class FlashcardDetailsActivity : BaseActivity()
{
    private var viewModel: FlashcardViewModel? = null
    private var titleInput: TextInputEditText? = null
    private var textInput: TextInputEditText? = null
    private var categoriesInput: TextInputEditText? = null
    private var saveButton: Button? = null

    //region Activity

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flashcard_details)

        initialiseUI(intent.getStringExtra(IntentExtras.FLASHCARD_ID))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean
    {
        menuInflater.inflate(R.menu.activity_flashcard_details_menu, menu)

        val existsInDatabase = viewModel?.flashcard?.existsInDatabase() ?: false

        if (!existsInDatabase)
        {
            menu.findItem(R.id.action_delete).isEnabled = false
        }

        return super.onCreateOptionsMenu(menu)
    }

    //endregion

    //region Data

    private fun getData(flashcardId: String): Observable<FlashcardViewModel>
    {
        return FlashcardViewModel.get(flashcardId, false)
    }

    private fun updateViewModelFromControls(): Unit
    {
        viewModel!!.flashcard.title = titleInput!!.text.toString()
        viewModel!!.flashcard.text = textInput!!.text.toString()

        val categories = parseCategoriesString(categoriesInput!!.text.toString())

        viewModel!!.categories = categories
    }

    private fun parseCategoriesString(categoriesString: String): List<Category>
    {
        val categoryNames = categoriesString.split(", ")

        return categoryNames.map {
            val category = Category(null)
            category.name = it

            category
        }
    }

    private fun saveViewModel(flashcardViewModel: FlashcardViewModel): Observable<FlashcardViewModel>
    {
        return flashcardViewModel.save()
    }

    //endregion

    //region UI

    private fun initialiseUI(flashcardId: String?): Unit
    {
        if (flashcardId != null)
        {
            getData(flashcardId).subscribe {
                viewModel = it
                updateUI()
            }
        }
        else
        {
            viewModel = FlashcardViewModel(Flashcard(null), listOf())
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
        saveButton = findViewById(R.id.flashcard_details_save_button) as Button

        saveButton!!.setOnClickListener {
            updateViewModelFromControls()

            saveViewModel(viewModel!!).subscribe {
                viewModel = it
                updateUI()
            }
        }
    }

    private fun updateUI(): Unit
    {
        updateControls()

        invalidateOptionsMenu()
    }

    private fun updateControls(): Unit
    {
        val (flashcard, categories) = viewModel!!

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
