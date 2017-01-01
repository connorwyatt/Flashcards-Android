package io.connorwyatt.flashcards.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.NavUtils
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import io.connorwyatt.flashcards.R
import io.connorwyatt.flashcards.data.entities.Category
import io.connorwyatt.flashcards.data.entities.Flashcard
import io.connorwyatt.flashcards.data.viewmodels.FlashcardViewModel
import io.connorwyatt.flashcards.views.textinput.EnhancedTextInputEditText
import io.reactivex.Observable

class FlashcardDetailsActivity : BaseActivity()
{
    private var viewModel: FlashcardViewModel? = null
    private var titleInput: EnhancedTextInputEditText? = null
    private var textInput: EnhancedTextInputEditText? = null
    private var categoriesInput: EnhancedTextInputEditText? = null
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        return when (item.itemId)
        {
            R.id.action_delete ->
            {
                deleteViewModel(viewModel!!)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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

    private fun saveViewModel(flashcardViewModel: FlashcardViewModel): Unit
    {
        flashcardViewModel.save().subscribe {
            viewModel = it
            updateUI()
            showToast(R.string.save_toast)
        }
    }

    private fun deleteViewModel(flashcardViewModel: FlashcardViewModel): Unit
    {
        flashcardViewModel.delete()
            .subscribe {
                showToast(R.string.flashcard_details_delete_toast)
                NavUtils.navigateUpFromSameTask(this)
            }
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
        titleInput = findViewById(R.id.flashcard_details_title) as EnhancedTextInputEditText
        textInput = findViewById(R.id.flashcard_details_text) as EnhancedTextInputEditText
        categoriesInput = findViewById(R.id.flashcard_details_categories) as EnhancedTextInputEditText
        saveButton = findViewById(R.id.flashcard_details_save_button) as Button

        saveButton!!.setOnClickListener {
            updateViewModelFromControls()

            saveViewModel(viewModel!!)
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

    private fun showToast(stringResource: Int): Unit
    {
        val toastMessage = getString(stringResource)
        val duration = Toast.LENGTH_SHORT

        val toast = Toast.makeText(this, toastMessage, duration)
        toast.show()
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
