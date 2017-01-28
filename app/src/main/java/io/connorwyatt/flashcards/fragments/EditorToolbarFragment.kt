/*
 * Copyright (c) 2016-2017 Connor Wyatt <connorwyatt1@gmail.com>.
 *
 * This file can not be copied and/or distributed without the express permission of Connor Wyatt.
 */

package io.connorwyatt.flashcards.fragments

import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import com.android.colorpicker.ColorPickerDialog
import io.connorwyatt.flashcards.R
import io.connorwyatt.flashcards.enums.EditorToolbarAction
import io.reactivex.Observable

class EditorToolbarFragment : Fragment() {
  private val listeners = mutableListOf<(EditorToolbarAction, Any?) -> Unit>()

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

  fun addListener(listener: (EditorToolbarAction, Any?) -> Unit): () -> Unit {
    listeners.add(listener)

    return { listeners.removeAll(listOf(listener)) }
  }

  private fun initialiseButtons(viewGroup: ViewGroup): Unit {
    buttonActions.forEach {
      val (id, action, dataFn) = it

      val button = viewGroup.findViewById(id) as ImageButton

      button.setOnClickListener {
        if (dataFn != null) {
          dataFn().subscribe {
            callListeners(action, it)
          }
        } else {
          callListeners(action, null)
        }
      }
    }
  }

  private fun callListeners(action: EditorToolbarAction, data: Any?): Unit {
    listeners.forEach { it(action, data) }
  }

  private fun selectColor(colors: IntArray): Observable<Int> {
    return Observable.create { observer ->
      val dialog = ColorPickerDialog()

      val convertedColors = convertColorResources(activity, colors)

      dialog.initialize(
        R.string.color_picker_title,
        convertedColors,
        0,
        4,
        2
      )

      dialog.show(fragmentManager, "colorSelect")

      dialog.setOnColorSelectedListener {
        dialog.dismiss()
        observer.onNext(it)
        observer.onComplete()
      }
    }
  }

  private fun convertColorResources(context: Context, colorResources: IntArray): IntArray {
    return colorResources.map {
      ContextCompat.getColor(context, it)
    }.toIntArray()
  }

  private val buttonActions = listOf<Triple<Int, EditorToolbarAction, (() -> Observable<out Any>)?>>(
    Triple(R.id.fragment_editor_toolbar_bold, EditorToolbarAction.BOLD, null),
    Triple(R.id.fragment_editor_toolbar_italic, EditorToolbarAction.ITALIC, null),
    Triple(R.id.fragment_editor_toolbar_underline, EditorToolbarAction.UNDERLINE, null),
    Triple(R.id.fragment_editor_toolbar_strikethrough, EditorToolbarAction.STRIKETHROUGH, null),
    Triple(R.id.fragment_editor_toolbar_align_left, EditorToolbarAction.ALIGN_LEFT, null),
    Triple(R.id.fragment_editor_toolbar_align_center, EditorToolbarAction.ALIGN_CENTER, null),
    Triple(R.id.fragment_editor_toolbar_align_right, EditorToolbarAction.ALIGN_RIGHT, null),
    Triple(R.id.fragment_editor_toolbar_bullet_list, EditorToolbarAction.BULLET_LIST, null),
    Triple(R.id.fragment_editor_toolbar_number_list, EditorToolbarAction.NUMBER_LIST, null),
    Triple(R.id.fragment_editor_toolbar_indent, EditorToolbarAction.INDENT, null),
    Triple(R.id.fragment_editor_toolbar_outdent, EditorToolbarAction.OUTDENT, null),
    Triple(R.id.fragment_editor_toolbar_text_color, EditorToolbarAction.TEXT_COLOR,
           { selectColor(textColorPalette) }),
    Triple(R.id.fragment_editor_toolbar_background_color, EditorToolbarAction.BACKGROUND_COLOR,
           { selectColor(backgroundColorPalette) })
  )

  companion object {
    private val textColorPalette = intArrayOf(
      R.color.colorPaletteTextBlack,
      R.color.colorPaletteTextDarkGrey,
      R.color.colorPaletteTextMediumGrey,
      R.color.colorPaletteTextLightGrey,
      R.color.colorPaletteTextWhite,
      R.color.colorPaletteTextGreen,
      R.color.colorPaletteTextYellow,
      R.color.colorPaletteTextRed,
      R.color.colorPaletteTextBlue,
      R.color.colorPaletteTextPink,
      R.color.colorPaletteTextPurple,
      R.color.colorPaletteTextOrange,
      R.color.colorPaletteTextCyan
    )

    private val backgroundColorPalette = intArrayOf(
      R.color.colorPaletteBackgroundGreen,
      R.color.colorPaletteBackgroundYellow,
      R.color.colorPaletteBackgroundRed,
      R.color.colorPaletteBackgroundBlue,
      R.color.colorPaletteBackgroundPink,
      R.color.colorPaletteBackgroundPurple,
      R.color.colorPaletteBackgroundOrange,
      R.color.colorPaletteBackgroundCyan
    )
  }
}
