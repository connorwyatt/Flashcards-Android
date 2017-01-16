/*
 * Copyright (c) 2016-2017 Connor Wyatt <connorwyatt1@gmail.com>.
 *
 * This file can not be copied and/or distributed without the express permission of Connor Wyatt.
 */

package io.connorwyatt.flashcards.views.htmleditor

import android.content.Context
import android.util.AttributeSet
import io.connorwyatt.flashcards.views.htmleditor.utils.StyleUtils
import io.connorwyatt.flashcards.views.textinput.EnhancedTextInputEditText

class HTMLEditor : EnhancedTextInputEditText {
  //region Constructors

  constructor(context: Context) : super(context)

  constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

  constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) :
    super(context, attrs, defStyleAttr)

  //endregion

  //region Actions

  fun dispatchEditorAction(editorAction: HTMLEditorAction): Unit {
    if (!isFocused) {
      return
    }

    val range = getRange()

    when (editorAction) {
      HTMLEditorAction.BOLD -> StyleUtils.bold(editableText, range)
      HTMLEditorAction.ITALIC -> StyleUtils.italic(editableText, range)
      HTMLEditorAction.UNDERLINE -> StyleUtils.underline(editableText, range)
    }
  }

  //endregion

  //region Selection

  private fun getRange(): Range {
    var start = selectionStart
    var end = selectionEnd

    if (start > end) {
      start = selectionEnd
      end = selectionStart
    }

    return Range(start, end)
  }

  //endregion
}
