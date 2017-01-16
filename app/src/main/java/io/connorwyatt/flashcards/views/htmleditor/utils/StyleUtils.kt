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

object StyleUtils {
  fun bold(spannable: Spannable, selection: Range): Unit {
    addSpan(spannable, selection, { StyleSpan(Typeface.BOLD) }, {
      spannable.getSpans(selection.start - 1, selection.end + 1, StyleSpan::class.java)
        .filter { it.style == Typeface.BOLD }
    })
  }

  fun italic(spannable: Spannable, selection: Range): Unit {
    addSpan(spannable, selection, { StyleSpan(Typeface.ITALIC) }, {
      spannable.getSpans(selection.start - 1, selection.end + 1, StyleSpan::class.java)
        .filter { it.style == Typeface.ITALIC }
    })
  }

  fun underline(spannable: Spannable, selection: Range): Unit {
    addSpan(spannable, selection, { UnderlineSpan() }, {
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
  private fun <T> addSpan(spannable: Spannable, selection: Range, what: () -> Any,
                          getSpans: () -> List<T>): Unit {
    var isRemove = false
    var newSpanStart = selection.start
    var newSpanEnd = selection.end

    getSpans().forEach {
      val spanStart = spannable.getSpanStart(it)
      val spanEnd = spannable.getSpanEnd(it)

      if (spanStart <= selection.end || spanEnd >= selection.start) {
        if (spanStart < newSpanStart) newSpanStart = spanStart
        if (spanEnd > newSpanEnd) newSpanEnd = spanEnd
      }

      if (spanStart <= selection.start && spanEnd >= selection.end) {
        isRemove = true
        newSpanStart = spanStart
        newSpanEnd = spanEnd
      }

      spannable.removeSpan(it)
    }

    if (isRemove) {
      if (newSpanStart != selection.start)
        spannable.setSpan(what(), newSpanStart, selection.start, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
      if (selection.end != newSpanEnd)
        spannable.setSpan(what(), selection.end, newSpanEnd, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
    } else {
      spannable.setSpan(what(), newSpanStart, newSpanEnd, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
    }
  }
}
