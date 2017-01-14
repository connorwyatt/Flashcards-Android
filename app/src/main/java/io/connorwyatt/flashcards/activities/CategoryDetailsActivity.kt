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
import io.connorwyatt.flashcards.data.entities.Category
import io.connorwyatt.flashcards.data.services.CategoryService
import io.connorwyatt.flashcards.exceptions.CategoryNameTakenException
import io.connorwyatt.flashcards.views.textinput.EnhancedTextInputEditText
import io.reactivex.Observable

class CategoryDetailsActivity : BaseActivity() {
  lateinit private var category: Category
  lateinit private var nameInput: EnhancedTextInputEditText
  lateinit private var saveButton: Button

  //region Activity

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_category_details)

    initialiseUI(intent.getStringExtra(IntentExtras.CATEGORY_ID))
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.activity_category_details_menu, menu)

    if (!category.existsInDatabase()) {
      menu.findItem(R.id.action_delete).isEnabled = false
    }

    return super.onCreateOptionsMenu(menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.action_delete -> {
        showDeleteCategoryDialog(category)
        return true
      }
      else -> {
        return super.onOptionsItemSelected(item)
      }
    }
  }

  //endregion

  //region Data

  private fun getData(categoryId: String): Observable<Category> {
    return CategoryService.getById(categoryId)
  }

  private fun updateCategoryFromControls(): Unit {
    category.name = nameInput.editableText.toString()
  }

  private fun saveCategory(category: Category): Unit {
    category.save().subscribe(
      {
        this.category = it
        updateUI()
        showToast(R.string.save_toast)
      },
      {
        when (it) {
          is CategoryNameTakenException -> {
            showToast(R.string.category_name_taken, category.name!!)
          }
        }
      }
    )
  }

  private fun deleteCategory(category: Category, deleteFlashcards: Boolean): Unit {
    val observable = if (deleteFlashcards)
      CategoryService.deleteWithFlashcards(category)
    else
      CategoryService.delete(category)

    observable.subscribe {
      showToast(R.string.delete_toast)
      NavUtils.navigateUpFromSameTask(this)
    }
  }

  //endregion

  //region UI

  private fun initialiseUI(categoryId: String?): Unit {
    if (categoryId != null) {
      getData(categoryId).subscribe {
        category = it
        updateUI()
      }
    } else {
      category = Category(null)
    }

    initialiseToolbar()

    initialiseControls()
  }

  private fun initialiseToolbar(): Unit {
    val toolbar = findViewById(R.id.category_details_toolbar) as Toolbar
    setSupportActionBar(toolbar)

    val actionBar = supportActionBar
    actionBar!!.setDisplayShowTitleEnabled(false)
    actionBar.setDisplayHomeAsUpEnabled(true)
  }

  private fun initialiseControls(): Unit {
    nameInput = findViewById(R.id.category_details_name) as EnhancedTextInputEditText
    nameInput.addRequiredValidator(getString(R.string.validation_required))
    nameInput.addMaxLengthValidator(40, { actualLength, maxLength ->
      getString(R.string.validation_max_length, actualLength, maxLength)
    })
    nameInput.addTextChangedListener { updateButton() }

    saveButton = findViewById(R.id.category_details_save_button) as Button

    saveButton.setOnClickListener {
      updateCategoryFromControls()

      saveCategory(category)
    }
  }

  private fun updateUI(): Unit {
    updateControls()

    updateButton()
  }

  private fun updateControls(): Unit {
    nameInput.setText(category.name)
  }

  private fun updateButton(): Unit {
    saveButton.isEnabled = isValid()
  }

  private fun isValid() = nameInput.isValid()

  private fun showToast(messageStringId: Int, vararg values: String) {
    val toastMessage = getString(messageStringId, *values)
    val duration = Toast.LENGTH_SHORT

    val toast = Toast.makeText(this, toastMessage, duration)
    toast.show()
  }

  private fun showDeleteCategoryDialog(category: Category): Unit {
    AlertDialog.Builder(this)
      .setTitle(getString(R.string.delete_category_dialog_title))
      .setMessage(getString(R.string.delete_category_dialog_message))
      .setPositiveButton(
        getString(R.string.delete_category_dialog_yes),
        { di, i -> deleteCategory(this.category, true) }
      )
      .setNegativeButton(
        getString(R.string.delete_category_dialog_no),
        { di, i -> deleteCategory(this.category, false) }
      )
      .setNeutralButton(
        getString(R.string.delete_category_dialog_cancel),
        { di, i -> }
      )
      .create()
      .show()
  }

  //endregion

  companion object {
    fun startActivity(context: Context) {
      val intent = Intent(context, CategoryDetailsActivity::class.java)

      context.startActivity(intent)
    }

    fun startActivity(context: Context, category: Category) {
      val intent = Intent(context, CategoryDetailsActivity::class.java)

      intent.putExtra(IntentExtras.CATEGORY_ID, category.id)

      context.startActivity(intent)
    }

    object IntentExtras {
      val CATEGORY_ID = "CATEGORY_ID"
    }
  }
}
