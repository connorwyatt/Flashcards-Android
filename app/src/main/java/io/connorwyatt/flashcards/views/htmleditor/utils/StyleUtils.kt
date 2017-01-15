/*
 * Copyright (c) 2016-2017 Connor Wyatt <connorwyatt1@gmail.com>.
 *
 * This file can not be copied and/or distributed without the express permission of Connor Wyatt.
 */

package io.connorwyatt.flashcards.views.htmleditor.utils

import android.text.Spannable
import io.connorwyatt.flashcards.views.htmleditor.Selection

object StyleUtils {
  fun style(spannable: Spannable, selection: Selection, style: Any): Unit {
    spannable.setSpan(style, selection.start, selection.end, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
  }
}
