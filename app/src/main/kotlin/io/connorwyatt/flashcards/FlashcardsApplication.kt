/*
 * Copyright (c) 2016-2017 Connor Wyatt <connorwyatt1@gmail.com>.
 *
 * This file can not be copied and/or distributed without the express permission of Connor Wyatt.
 */

package io.connorwyatt.flashcards

import android.app.Application
import com.google.firebase.database.FirebaseDatabase

class FlashcardsApplication : Application() {
  override fun onCreate() {
    super.onCreate()

    initialise()
  }

  private fun initialise(): Unit {
    FirebaseDatabase.getInstance().setPersistenceEnabled(true)
  }
}
