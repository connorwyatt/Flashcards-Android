/*
 * Copyright (c) 2016-2017 Connor Wyatt <connorwyatt1@gmail.com>.
 *
 * This file can not be copied and/or distributed without the express permission of Connor Wyatt.
 */

package io.connorwyatt.flashcards.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import io.connorwyatt.flashcards.R
import io.connorwyatt.flashcards.data.entities.Tag
import io.connorwyatt.flashcards.fragments.FlashcardTestFragment

class FlashcardTestActivity : BaseActivity() {
  var flashcardTestFragment: FlashcardTestFragment? = null
    private set

  //region Activity

  override fun onCreate(savedInstanceState: Bundle?): Unit {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_flashcard_test)

    val tagId = if (intent.hasExtra(IntentExtras.TAG_ID)) intent.getStringExtra(IntentExtras.TAG_ID) else null
    val order = Order.values()[intent.getIntExtra(IntentExtras.ORDER, -1)]
    val limit = if (intent.hasExtra(IntentExtras.LIMIT)) intent.getIntExtra(IntentExtras.LIMIT, -1) else null

    initialiseFragment(tagId, order, limit)
  }

  override fun onBackPressed() {
    flashcardTestFragment!!.onBackPressed { super.onBackPressed() }
  }

  //endregion

  //region UI

  private fun initialiseFragment(tagId: String?, order: Order, limit: Int?): Unit {
    var fragment = fragmentManager.findFragmentByTag(FRAGMENT_TAG) as FlashcardTestFragment?

    if (fragment == null) {
      fragment = FlashcardTestFragment()

      val arguments = Bundle()

      arguments.putString(FlashcardTestFragment.Companion.ArgumentKeys.TAG_ID, tagId)
      arguments.putInt(FlashcardTestFragment.Companion.ArgumentKeys.ORDER, order.ordinal)
      arguments.putInt(FlashcardTestFragment.Companion.ArgumentKeys.LIMIT, limit ?: -1)

      fragment.arguments = arguments

      fragmentManager
        .beginTransaction()
        .add(R.id.flashcard_test_frame, fragment, FRAGMENT_TAG)
        .commit()
    }

    flashcardTestFragment = fragment
  }

  //endregion

  companion object {
    private val FRAGMENT_TAG = "DATA"

    fun startActivity(context: Context, configuration: Configuration) {
      val (tag, order, limit) = configuration

      val extras = Bundle()
      tag?.let { extras.putString(IntentExtras.TAG_ID, it.id) }
      extras.putInt(IntentExtras.ORDER, order.ordinal)
      limit?.let { extras.putInt(IntentExtras.LIMIT, it) }

      val intent = Intent(context, FlashcardTestActivity::class.java)
      intent.putExtras(extras)

      context.startActivity(intent)
    }

    data class Configuration(val tag: Tag?, val order: Order, val limit: Int?)

    enum class Order {
      RANDOM,
      WORST_TO_BEST
    }

    object IntentExtras {
      val TAG_ID = "TAG_ID"
      val ORDER = "ORDER"
      val LIMIT = "LIMIT"
    }
  }
}
