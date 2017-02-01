/*
 * Copyright (c) 2016-2017 Connor Wyatt <connorwyatt1@gmail.com>.
 *
 * This file can not be copied and/or distributed without the express permission of Connor Wyatt.
 */

package io.connorwyatt.flashcards.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import io.connorwyatt.flashcards.R
import io.connorwyatt.flashcards.enums.EditorToolbarAction
import io.connorwyatt.flashcards.fragments.EditorToolbarFragment
import io.connorwyatt.flashcards.views.richeditorenhanced.RichEditorEnhanced

class FlashcardContentsEditorActivity : BaseActivity() {
  lateinit private var editor: RichEditorEnhanced

  //region Activity

  override fun onCreateAfterAuth(savedInstanceState: Bundle?) {
    setContentView(R.layout.activity_flashcard_contents_editor)

    initialiseUI(savedInstanceState)
  }

  override fun onBackPressed() {
    val bundle = Bundle()
    bundle.putString(bundleKeys.HTML, editor.html)

    val intent = Intent()
    intent.putExtras(bundle)

    setResult(0, intent)
    finish()
  }

  override fun onOptionsItemSelected(item: MenuItem?): Boolean {
    return when (item?.itemId) {
      android.R.id.home -> {
        onBackPressed()
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }

  override fun onSaveInstanceState(outState: Bundle) {
    outState.putString(bundleKeys.HTML, editor.html)

    super.onSaveInstanceState(outState)
  }

  //endregion

  private fun initialiseUI(savedInstanceState: Bundle?): Unit {
    initialiseToolbar()

    initialiseEditor(savedInstanceState)
  }

  private fun initialiseToolbar(): Unit {
    val toolbar = findViewById(R.id.flashcard_contents_editor_toolbar) as Toolbar
    setSupportActionBar(toolbar)

    val actionBar = supportActionBar
    actionBar!!.setDisplayHomeAsUpEnabled(true)
    actionBar.setDisplayShowTitleEnabled(false)
  }

  private fun initialiseEditor(savedInstanceState: Bundle?): Unit {
    editor = findViewById(R.id.flashcard_contents_editor_editor) as RichEditorEnhanced

    editor.setPadding(16, 8, 16, 8)

    intent.getStringExtra(bundleKeys.HTML)

    if (savedInstanceState?.getString(bundleKeys.HTML) != null) {
      editor.html = savedInstanceState!!.getString(bundleKeys.HTML)
    } else if (intent.getStringExtra(bundleKeys.HTML) != null) {
      editor.html = intent.getStringExtra(bundleKeys.HTML)
    }

    val fragment = EditorToolbarFragment()

    fragmentManager
      .beginTransaction()
      .add(R.id.flashcard_contents_editor_frame, fragment)
      .commit()

    fragment.addListener { action, data ->
      applyStyle(action, data)
    }
  }

  private fun applyStyle(editorAction: EditorToolbarAction, data: Any?): Unit {
    when (editorAction) {
      EditorToolbarAction.BOLD -> editor.setBold()
      EditorToolbarAction.ITALIC -> editor.setItalic()
      EditorToolbarAction.UNDERLINE -> editor.setUnderline()
      EditorToolbarAction.STRIKETHROUGH -> editor.setStrikeThrough()
      EditorToolbarAction.ALIGN_LEFT -> editor.setAlignLeft()
      EditorToolbarAction.ALIGN_CENTER -> editor.setAlignCenter()
      EditorToolbarAction.ALIGN_RIGHT -> editor.setAlignRight()
      EditorToolbarAction.BULLET_LIST -> editor.setBullets()
      EditorToolbarAction.NUMBER_LIST -> editor.setNumbers()
      EditorToolbarAction.INDENT -> editor.setIndent()
      EditorToolbarAction.OUTDENT -> editor.setOutdent()
      EditorToolbarAction.TEXT_COLOR -> editor.setTextColor(data as Int)
      EditorToolbarAction.BACKGROUND_COLOR -> editor.setTextBackgroundColor(data as Int)
    }
  }

  companion object {
    private object bundleKeys {
      val HTML = "HTML"
      val REQUEST_CODE = "REQUEST_CODE"
    }

    fun startActivityForResult(context: Activity, responseCode: Int, initialHtml: String): Unit {
      val intent = Intent(context, FlashcardContentsEditorActivity::class.java)

      val bundle = Bundle()
      bundle.putString(bundleKeys.HTML, initialHtml)
      bundle.putInt(bundleKeys.REQUEST_CODE, responseCode)

      intent.putExtras(bundle)

      context.startActivityForResult(intent, responseCode, bundle)
    }
  }
}