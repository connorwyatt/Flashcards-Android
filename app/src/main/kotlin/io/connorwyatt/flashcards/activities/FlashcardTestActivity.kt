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
import io.connorwyatt.flashcards.fragments.FlashcardTestFragment

class FlashcardTestActivity : BaseActivity() {
  var flashcardTestFragment: FlashcardTestFragment? = null
    private set

  //region Activity

  override fun onCreate(savedInstanceState: Bundle?): Unit {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_flashcard_test)

    val tagId = intent.getStringExtra(IntentExtras.TAG_ID)

    initialiseFragment(tagId)
  }

  override fun onBackPressed() {
    flashcardTestFragment!!.onBackPressed { super.onBackPressed() }
  }

  //endregion

  //region UI

  private fun initialiseFragment(tagId: String?): Unit {
    var fragment = fragmentManager.findFragmentByTag(FRAGMENT_TAG) as FlashcardTestFragment?

    if (fragment == null) {
      fragment = FlashcardTestFragment()

      val arguments = Bundle()

      arguments.putString(
        FlashcardTestFragment.Companion.ArgumentKeys.TAG_ID, tagId)

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

    fun startActivity(context: Context) {
      val intent = Intent(context, FlashcardTestActivity::class.java)

      context.startActivity(intent)
    }

    fun startActivityWithTagFilter(context: Context, tagId: String) {
      val extras = Bundle()
      extras.putString(IntentExtras.TAG_ID, tagId)

      val intent = Intent(context, FlashcardTestActivity::class.java)
      intent.putExtras(extras)

      context.startActivity(intent)
    }

    object IntentExtras {
      val TAG_ID = "TAG_ID"
    }
  }
}
