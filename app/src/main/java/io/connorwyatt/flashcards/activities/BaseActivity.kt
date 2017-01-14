/*
 * Copyright (c) 2016-2017 Connor Wyatt <connorwyatt1@gmail.com>.
 *
 * This file can not be copied and/or distributed without the express permission of Connor Wyatt.
 */

package io.connorwyatt.flashcards.activities

import android.support.v7.app.AppCompatActivity
import io.connorwyatt.flashcards.helpers.auth.AuthHelper

open class BaseActivity : AppCompatActivity() {
  protected val auth = AuthHelper.getInstance()

  override fun onStart(): Unit {
    super.onStart()

    if (!auth.isSignedIn) {
      AuthActivity.startActivity(this)
    }
  }

  fun signOut(): Unit {
    auth.signOut()

    recreate()
  }
}
