/*
 * Copyright (c) 2016-2017 Connor Wyatt <connorwyatt1@gmail.com>.
 *
 * This file can not be copied and/or distributed without the express permission of Connor Wyatt.
 */

package io.connorwyatt.flashcards.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.NavUtils
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import io.connorwyatt.flashcards.R
import io.connorwyatt.flashcards.data.entities.Flashcard
import io.connorwyatt.flashcards.data.entities.Tag
import io.connorwyatt.flashcards.data.services.TagService
import io.connorwyatt.flashcards.data.viewmodels.FlashcardViewModel
import io.connorwyatt.flashcards.views.inputs.EnhancedMultiAutoCompleteTextView
import io.connorwyatt.flashcards.views.inputs.EnhancedTextInputEditText
import io.reactivex.Observable

class FlashcardDetailsActivity : BaseActivity() {
  private var viewModel: FlashcardViewModel? = null
  lateinit private var titleInput: EnhancedTextInputEditText
  lateinit private var textInput: EnhancedTextInputEditText
  lateinit private var tagsInput: EnhancedMultiAutoCompleteTextView
  lateinit private var saveButton: Button

  //region Activity

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_flashcard_details)

    val flashcardId = intent.getStringExtra(IntentExtras.FLASHCARD_ID)

    initialiseUI(flashcardId, savedInstanceState)
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.activity_flashcard_details_menu, menu)

    val existsInDatabase = viewModel?.flashcard?.existsInDatabase() ?: false

    if (!existsInDatabase) {
      menu.findItem(R.id.action_delete).isEnabled = false
    }

    return super.onCreateOptionsMenu(menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.action_delete -> {
        deleteViewModel(viewModel!!)
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }

  override fun onSaveInstanceState(outState: Bundle) {
    outState.putString(SavedInstanceState.TITLE, titleInput.text.toString())
    outState.putString(SavedInstanceState.TEXT, textInput.text.toString())
    outState.putString(SavedInstanceState.TAGS, tagsInput.text.toString())

    super.onSaveInstanceState(outState)
  }

  //endregion

  //region Data

  private fun getData(flashcardId: String): Observable<FlashcardViewModel> {
    return FlashcardViewModel.get(flashcardId, false)
  }

  private fun updateViewModelFromControls(): Unit {
    viewModel!!.flashcard.title = titleInput.text.toString()
    viewModel!!.flashcard.text = textInput.text.toString()

    val tags = parseTagsString(tagsInput.text.toString())

    viewModel!!.tags = tags
  }

  private fun parseTagsString(tagsString: String): List<Tag> {
    val tagNames = tagsString.split(",")

    return tagNames.map {
      val tag = Tag(null)
      tag.name = it.trim()

      return@map tag
    }
  }

  private fun saveViewModel(flashcardViewModel: FlashcardViewModel): Unit {
    flashcardViewModel.save().subscribe {
      viewModel = it
      updateUI()
      showToast(R.string.save_toast)
    }
  }

  private fun deleteViewModel(flashcardViewModel: FlashcardViewModel): Unit {
    flashcardViewModel.delete()
      .subscribe {
        showToast(R.string.delete_toast)
        NavUtils.navigateUpFromSameTask(this)
      }
  }

  //endregion

  //region UI

  private fun initialiseUI(flashcardId: String?, savedInstanceState: Bundle?): Unit {
    initialiseToolbar()

    initialiseControls()

    if (flashcardId != null) {
      getData(flashcardId).subscribe {
        viewModel = mergeModelAndSavedInstanceState(it, savedInstanceState)
        updateUI()
      }
    } else {
      viewModel = mergeModelAndSavedInstanceState(
        FlashcardViewModel(Flashcard(null), listOf()),
        savedInstanceState
      )
      updateUI()
    }

    updateButton()
  }

  private fun initialiseToolbar(): Unit {
    val toolbar = findViewById(R.id.flashcard_details_toolbar) as Toolbar
    setSupportActionBar(toolbar)

    val actionBar = supportActionBar
    actionBar!!.setDisplayHomeAsUpEnabled(true)
    actionBar.setDisplayShowTitleEnabled(false)
  }

  private fun initialiseControls(): Unit {
    titleInput = findViewById(R.id.flashcard_details_title) as EnhancedTextInputEditText
    textInput = findViewById(R.id.flashcard_details_text) as EnhancedTextInputEditText
    tagsInput = findViewById(
      R.id.flashcard_details_tags) as EnhancedMultiAutoCompleteTextView
    saveButton = findViewById(R.id.flashcard_details_save_button) as Button

    titleInput.addRequiredValidator(getString(R.string.validation_required))
    titleInput.addMaxLengthValidator(80, { actualLength, maxLength ->
      getString(R.string.validation_max_length, actualLength, maxLength)
    })
    titleInput.addTextChangedListener { updateButton() }

    textInput.addRequiredValidator(getString(R.string.validation_required))
    textInput.addTextChangedListener { updateButton() }

    initialiseTagsControl()

    saveButton.setOnClickListener {
      updateViewModelFromControls()

      saveViewModel(viewModel!!)
    }
  }

  private fun initialiseTagsControl(): Unit {
    val tagsAdapter = ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line)

    tagsInput.setAdapter(tagsAdapter)

    tagsInput.addTagMaxLengthValidator(40) { maxLength ->
      getString(R.string.validation_tags_max_length, maxLength)
    }

    tagsInput.addTextChangedListener { updateButton() }

    TagService.getAllAsStream().subscribe { tags ->
      val tagNames = tags.map { it.name }

      tagsAdapter.clear()

      tagsAdapter.addAll(tagNames)
    }
  }

  private fun updateUI(): Unit {
    updateControls()

    updateButton()

    invalidateOptionsMenu()
  }

  private fun updateControls(): Unit {
    val (flashcard, tags) = viewModel!!

    if (flashcard.title != null && flashcard.title!!.isNotEmpty())
      titleInput.setText(flashcard.title)

    if (flashcard.text != null && flashcard.text!!.isNotEmpty())
      textInput.setText(flashcard.text)

    if (tags.isNotEmpty())
      tagsInput.setText(
        tags.map { it.name }.joinToString(separator = ", ")
      )
  }

  private fun updateButton(): Unit {
    saveButton.isEnabled = isValid()
  }

  private fun mergeModelAndSavedInstanceState(
    flashcardViewModel: FlashcardViewModel, savedInstanceState: Bundle?): FlashcardViewModel {
    savedInstanceState?.let {
      flashcardViewModel.flashcard.title = savedInstanceState.getString(SavedInstanceState.TITLE)
      flashcardViewModel.flashcard.text = savedInstanceState.getString(SavedInstanceState.TEXT)

      val tags = parseTagsString(
        savedInstanceState.getString(SavedInstanceState.TAGS))

      flashcardViewModel.tags = tags

      flashcardViewModel
    }

    return flashcardViewModel
  }

  private fun isValid()
    = titleInput.isValid() && textInput.isValid() && tagsInput.isValid()

  private fun showToast(stringResource: Int): Unit {
    val toastMessage = getString(stringResource)
    val duration = Toast.LENGTH_SHORT

    Toast.makeText(this, toastMessage, duration).show()
  }

  //endregion

  companion object {
    fun startActivity(context: Context) {
      val intent = Intent(context, FlashcardDetailsActivity::class.java)

      context.startActivity(intent)
    }

    fun startActivityWithFlashcard(context: Context, flashcard: Flashcard) {
      val intent = Intent(context, FlashcardDetailsActivity::class.java)

      intent.putExtra(IntentExtras.FLASHCARD_ID, flashcard.id)

      context.startActivity(intent)
    }

    object IntentExtras {
      val FLASHCARD_ID = "FLASHCARD_ID"
    }

    object SavedInstanceState {
      val TITLE = "TITLE"
      val TEXT = "TEXT"
      val TAGS = "TAGS"
    }
  }
}
