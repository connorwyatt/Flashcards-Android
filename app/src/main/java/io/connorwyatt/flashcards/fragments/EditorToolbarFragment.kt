/*
 * Copyright (c) 2016-2017 Connor Wyatt <connorwyatt1@gmail.com>.
 *
 * This file can not be copied and/or distributed without the express permission of Connor Wyatt.
 */

package io.connorwyatt.flashcards.fragments

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import io.connorwyatt.flashcards.R
import io.connorwyatt.flashcards.enums.EditorToolbarAction

class EditorToolbarFragment : Fragment() {
  private val listeners = mutableListOf<(EditorToolbarAction) -> Unit>()

  //region Fragment

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                            savedInstanceState: Bundle?): View {
    super.onCreateView(inflater, container, savedInstanceState)

    val viewGroup = inflater?.inflate(
      R.layout.fragment_editor_toolbar, container, false) as ViewGroup

    initialiseButtons(viewGroup)

    return viewGroup
  }

  //endregion

  fun addListener(listener: (EditorToolbarAction) -> Unit): () -> Unit {
    listeners.add(listener)

    return { listeners.removeAll(listOf(listener)) }
  }

  private fun initialiseButtons(viewGroup: ViewGroup): Unit {
    buttonActions.forEach {
      val (id, action) = it

      val button = viewGroup.findViewById(id) as ImageButton

      button.setOnClickListener { callListeners(action) }
    }
  }

  private fun callListeners(action: EditorToolbarAction): Unit {
    listeners.forEach { it(action) }
  }

  companion object {
    private val buttonActions = mapOf<Int, EditorToolbarAction>(
      Pair(R.id.fragment_editor_toolbar_bold, EditorToolbarAction.BOLD),
      Pair(R.id.fragment_editor_toolbar_italic, EditorToolbarAction.ITALIC),
      Pair(R.id.fragment_editor_toolbar_underline, EditorToolbarAction.UNDERLINE),
      Pair(R.id.fragment_editor_toolbar_strikethrough, EditorToolbarAction.STRIKETHROUGH),
      Pair(R.id.fragment_editor_toolbar_align_left, EditorToolbarAction.ALIGN_LEFT),
      Pair(R.id.fragment_editor_toolbar_align_center, EditorToolbarAction.ALIGN_CENTER),
      Pair(R.id.fragment_editor_toolbar_align_right, EditorToolbarAction.ALIGN_RIGHT)
    )
  }
}
