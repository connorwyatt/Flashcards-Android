/*
 * Copyright (c) 2016-2017 Connor Wyatt <connorwyatt1@gmail.com>.
 *
 * This file can not be copied and/or distributed without the express permission of Connor Wyatt.
 */

package io.connorwyatt.flashcards

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.View
import io.connorwyatt.flashcards.activities.FlashcardTestActivity.Companion.Order
import kotlinx.android.synthetic.main.dialog_flashcard_test_options.*
import io.connorwyatt.flashcards.R.string as stringResources

class FlashcardTestOptionsDialogFragment() : DialogFragment() {

  private var continueAction: ((Configuration) -> Unit)? = null
  private var cancelAction: (() -> Unit)? = null

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val builder = AlertDialog.Builder(activity)

    val layoutInflater = activity.layoutInflater

    builder.setTitle(getString(stringResources.test_options))

    builder.setView(layoutInflater.inflate(R.layout.dialog_flashcard_test_options, null))

      .setPositiveButton(getString(stringResources.continue_to_test)) { dialog, _ ->
        val configuration = getConfiguration()

        continueAction?.invoke(configuration)

        dialog.dismiss()
      }

      .setNegativeButton(getString(stringResources.cancel)) { dialog, _ ->
        cancelAction?.invoke()

        dialog.dismiss()
      }

    val dialog = builder.create()

    dialog.setOnShowListener {
      initialiseUI()
    }

    return dialog
  }

  fun setContinueAction(action: (Configuration) -> Unit): Unit {
    continueAction = action
  }

  fun setCancelAction(action: () -> Unit): Unit {
    cancelAction = action
  }

  private fun initialiseUI(): Unit {
    dialog.order_radio_group.check(R.id.order_radio_random)

    dialog.limit_value_layout.visibility = View.GONE

    dialog.limit_checkbox.setOnClickListener {
      dialog.limit_value_layout.visibility = if (dialog.limit_checkbox.isChecked) View.VISIBLE else View.GONE
    }
  }

  private fun getConfiguration(): Configuration {
    val testOrder = when (dialog.order_radio_group.checkedRadioButtonId) {
      R.id.order_radio_random -> Order.RANDOM
      R.id.order_radio_worst_to_best -> Order.WORST_TO_BEST
      else -> Order.RANDOM
    }

    val limitEnabled = dialog.limit_checkbox.isChecked

    val limit = if (limitEnabled) {
      dialog.limit_value_layout.editText?.text.toString().toIntOrNull()
    } else {
      null
    }

    return Configuration(testOrder, limit)
  }

  companion object {
    data class Configuration(val order: Order, val limit: Int?)
  }

}
