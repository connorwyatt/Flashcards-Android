/*
 * Copyright (c) 2016-2017 Connor Wyatt <connorwyatt1@gmail.com>.
 *
 * This file can not be copied and/or distributed without the express permission of Connor Wyatt.
 */

package io.connorwyatt.flashcards.views.htmleditor.utils

import android.graphics.Typeface
import android.text.Spannable
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import io.connorwyatt.flashcards.views.htmleditor.Range
import io.connorwyatt.flashcards.views.htmleditor.SelectionBehaviour

object StyleUtils {
  private val paragraphCharacters = listOf('\n')
  private val wordCharacters = paragraphCharacters.plus(' ')

  fun bold(spannable: Spannable, selection: Range): Unit {
    val selectionBehaviour =
      if (selection.size > 0) SelectionBehaviour.NO_EXPAND else SelectionBehaviour.EXPAND_TO_WORD

    addSpan(spannable, selection, selectionBehaviour, { StyleSpan(Typeface.BOLD) }, { selection ->
      spannable.getSpans(selection.start - 1, selection.end + 1, StyleSpan::class.java)
        .filter { it.style == Typeface.BOLD }
    })
  }

  fun italic(spannable: Spannable, selection: Range): Unit {
    val selectionBehaviour =
      if (selection.size > 0) SelectionBehaviour.NO_EXPAND else SelectionBehaviour.EXPAND_TO_WORD

    addSpan(spannable, selection, selectionBehaviour, { StyleSpan(Typeface.ITALIC) }, { selection ->
      spannable.getSpans(selection.start - 1, selection.end + 1, StyleSpan::class.java)
        .filter { it.style == Typeface.ITALIC }
    })
  }

  fun underline(spannable: Spannable, selection: Range): Unit {
    val selectionBehaviour =
      if (selection.size > 0) SelectionBehaviour.NO_EXPAND else SelectionBehaviour.EXPAND_TO_WORD

    addSpan(spannable, selection, selectionBehaviour, { UnderlineSpan() }, { selection ->
      spannable.getSpans(selection.start - 1, selection.end + 1, UnderlineSpan::class.java)
        .toList()
    })
  }

  /**
   * A method for adding spans. This method removes and overlapping, touching and contained spans
   * and replaces them with a single span that encompasses all of the removed spans plus the new
   * one.
   *
   * It the selection is completely contained within a span, the span will be removed.
   */
  private fun <T> addSpan(spannable: Spannable, selection: Range,
                          selectionBehaviour: SelectionBehaviour,
                          what: () -> Any, getSpans: (Range) -> List<T>): Unit {
    val modifiedSelection = expandSelection(spannable, selection, selectionBehaviour)

    var isRemove = false
    var newSpanStart = modifiedSelection.start
    var newSpanEnd = modifiedSelection.end

    getSpans(modifiedSelection).forEach {
      val spanStart = spannable.getSpanStart(it)
      val spanEnd = spannable.getSpanEnd(it)

      if (spanStart <= modifiedSelection.end || spanEnd >= modifiedSelection.start) {
        if (spanStart < newSpanStart) newSpanStart = spanStart
        if (spanEnd > newSpanEnd) newSpanEnd = spanEnd
      }

      if (spanStart <= modifiedSelection.start && spanEnd >= modifiedSelection.end) {
        isRemove = true
        newSpanStart = spanStart
        newSpanEnd = spanEnd
      }

      spannable.removeSpan(it)
    }

    if (isRemove) {
      if (newSpanStart != modifiedSelection.start)
        spannable.setSpan(
          what(), newSpanStart, modifiedSelection.start, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
      if (modifiedSelection.end != newSpanEnd)
        spannable.setSpan(
          what(), modifiedSelection.end, newSpanEnd, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
    } else {
      spannable.setSpan(what(), newSpanStart, newSpanEnd, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
    }
  }

  private fun expandSelection(spannable: Spannable, selection: Range,
                              selectionBehaviour: SelectionBehaviour): Range {
    val characterList =
      when (selectionBehaviour) {
        SelectionBehaviour.EXPAND_TO_WORD -> wordCharacters
        SelectionBehaviour.EXPAND_TO_PARAGRAPH -> paragraphCharacters
        SelectionBehaviour.NO_EXPAND -> null
      } ?: return selection

    var currentCharIndex = selection.start
    var start: Int? = null
    var end: Int? = null

    while (start == null && currentCharIndex > 0) {
      val char = spannable[currentCharIndex - 1]

      if (char in characterList) {
        start = currentCharIndex
      }

      currentCharIndex--
    }

    currentCharIndex = selection.end

    while (end == null && currentCharIndex < spannable.length) {
      val char = spannable[currentCharIndex]

      if (char in characterList) {
        end = currentCharIndex
      }

      currentCharIndex++
    }

    return Range(start ?: 0, end ?: spannable.length)
  }
}
