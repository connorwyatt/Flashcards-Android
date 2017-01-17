/*
 * Copyright (c) 2016-2017 Connor Wyatt <connorwyatt1@gmail.com>.
 *
 * This file can not be copied and/or distributed without the express permission of Connor Wyatt.
 */

package io.connorwyatt.flashcards.views.htmleditor

/**
 * An enum for configuring the selection behaviour for styles.
 */
enum class SelectionBehaviour {
  /**
   * The selection will not be expanded.
   */
  NO_EXPAND,
  /**
   * The selection will be expanded to include the rest of the words at the start and end of the
   * selection.
   */
  EXPAND_TO_WORD,
  /**
   * The selection will be expanded to include the rest of the paragraphs at the start and end of
   * the selection.
   */
  EXPAND_TO_PARAGRAPH
}
