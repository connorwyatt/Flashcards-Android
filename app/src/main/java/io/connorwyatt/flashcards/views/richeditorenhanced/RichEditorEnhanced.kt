package io.connorwyatt.flashcards.views.richeditorenhanced

import android.content.Context
import android.util.AttributeSet
import io.connorwyatt.flashcards.R
import jp.wasabeef.richeditor.RichEditor

class RichEditorEnhanced : RichEditor {
  constructor(context: Context) : super(context)

  constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

  constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int)
    : super(context, attrs, defStyleAttr)

  init {
    setEditorFontColor(R.color.colorPaletteTextBlack)
  }
}
