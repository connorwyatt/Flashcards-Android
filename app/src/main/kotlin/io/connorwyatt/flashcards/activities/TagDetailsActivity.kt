/*
 * Copyright (c) 2016-2017 Connor Wyatt <connorwyatt1@gmail.com>.
 *
 * This file can not be copied and/or distributed without the express permission of Connor Wyatt.
 */

package io.connorwyatt.flashcards.activities

import android.app.AlertDialog
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
import io.connorwyatt.flashcards.data.entities.Tag
import io.connorwyatt.flashcards.data.services.TagService
import io.connorwyatt.flashcards.exceptions.TagNameTakenException
import io.connorwyatt.flashcards.views.textinput.EnhancedTextInputEditText
import io.reactivex.Observable

class TagDetailsActivity : BaseActivity() {
  lateinit private var tag: Tag
  lateinit private var nameInput: EnhancedTextInputEditText
  lateinit private var saveButton: Button

  //region Activity

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_tag_details)

    val tagId = intent.getStringExtra(IntentExtras.TAG_ID)

    initialiseUI(tagId, savedInstanceState)
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.activity_tag_details_menu, menu)

    if (!tag.existsInDatabase()) {
      menu.findItem(R.id.action_delete).isEnabled = false
    }

    return super.onCreateOptionsMenu(menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.action_delete -> {
        showDeleteTagDialog(tag)
        return true
      }
      else -> {
        return super.onOptionsItemSelected(item)
      }
    }
  }

  override fun onSaveInstanceState(outState: Bundle) {
    outState.putString(SavedInstanceState.NAME, nameInput.text.toString())

    super.onSaveInstanceState(outState)
  }

  //endregion

  //region Data

  private fun getData(tagId: String): Observable<Tag> {
    return TagService.getById(tagId)
  }

  private fun updateTagFromControls(): Unit {
    tag.name = nameInput.editableText.toString()
  }

  private fun saveTag(tag: Tag): Unit {
    tag.save().subscribe(
      {
        this.tag = it
        updateUI()
        showToast(R.string.save_toast)
      },
      {
        when (it) {
          is TagNameTakenException -> {
            showToast(R.string.tag_name_taken, tag.name!!)
          }
        }
      }
    )
  }

  private fun deleteTag(tag: Tag, deleteFlashcards: Boolean): Unit {
    val observable = if (deleteFlashcards)
      TagService.deleteWithFlashcards(tag)
    else
      TagService.delete(tag)

    observable.subscribe {
      showToast(R.string.delete_toast)
      NavUtils.navigateUpFromSameTask(this)
    }
  }

  //endregion

  //region UI

  private fun initialiseUI(tagId: String?, savedInstanceState: Bundle?): Unit {
    if (tagId != null) {
      getData(tagId).subscribe {
        tag = mergeModelAndSavedInstanceState(it, savedInstanceState)
        updateUI()
      }
    } else {
      tag = mergeModelAndSavedInstanceState(
        Tag(null),
        savedInstanceState
      )
    }

    initialiseToolbar()

    initialiseControls()
  }

  private fun initialiseToolbar(): Unit {
    val toolbar = findViewById(R.id.tag_details_toolbar) as Toolbar
    setSupportActionBar(toolbar)

    val actionBar = supportActionBar
    actionBar!!.setDisplayShowTitleEnabled(false)
    actionBar.setDisplayHomeAsUpEnabled(true)
  }

  private fun initialiseControls(): Unit {
    nameInput = findViewById(R.id.tag_details_name) as EnhancedTextInputEditText
    nameInput.addRequiredValidator(getString(R.string.validation_required))
    nameInput.addMaxLengthValidator(40, { actualLength, maxLength ->
      getString(R.string.validation_max_length, actualLength, maxLength)
    })
    nameInput.addTextChangedListener { updateButton() }

    saveButton = findViewById(R.id.tag_details_save_button) as Button

    saveButton.setOnClickListener {
      updateTagFromControls()

      saveTag(tag)
    }
  }

  private fun updateUI(): Unit {
    updateControls()

    updateButton()
  }

  private fun updateControls(): Unit {
    nameInput.setText(tag.name)
  }

  private fun updateButton(): Unit {
    saveButton.isEnabled = isValid()
  }

  private fun mergeModelAndSavedInstanceState(
    tag: Tag, savedInstanceState: Bundle?): Tag {
    savedInstanceState?.let {
      tag.name = savedInstanceState.getString(SavedInstanceState.NAME)
    }

    return tag
  }

  private fun isValid() = nameInput.isValid()

  private fun showToast(messageStringId: Int, vararg values: String) {
    val toastMessage = getString(messageStringId, *values)
    val duration = Toast.LENGTH_SHORT

    val toast = Toast.makeText(this, toastMessage, duration)
    toast.show()
  }

  private fun showDeleteTagDialog(tag: Tag): Unit {
    AlertDialog.Builder(this)
      .setTitle(getString(R.string.delete_tag_dialog_title))
      .setMessage(getString(R.string.delete_tag_dialog_message))
      .setPositiveButton(
        getString(R.string.delete_tag_dialog_yes),
        { di, i -> deleteTag(this.tag, true) }
      )
      .setNegativeButton(
        getString(R.string.delete_tag_dialog_no),
        { di, i -> deleteTag(this.tag, false) }
      )
      .setNeutralButton(
        getString(R.string.delete_tag_dialog_cancel),
        { di, i -> }
      )
      .create()
      .show()
  }

  //endregion

  companion object {
    fun startActivity(context: Context) {
      val intent = Intent(context, TagDetailsActivity::class.java)

      context.startActivity(intent)
    }

    fun startActivity(context: Context, tag: Tag) {
      val intent = Intent(context, TagDetailsActivity::class.java)

      intent.putExtra(IntentExtras.TAG_ID, tag.id)

      context.startActivity(intent)
    }

    object IntentExtras {
      val TAG_ID = "TAG_ID"
    }

    object SavedInstanceState {
      val NAME = "NAME"
    }
  }
}
