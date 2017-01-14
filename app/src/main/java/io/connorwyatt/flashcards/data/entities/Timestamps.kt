/*
 * Copyright (c) 2016-2017 Connor Wyatt <connorwyatt1@gmail.com>.
 *
 * This file can not be copied and/or distributed without the express permission of Connor Wyatt.
 */

package io.connorwyatt.flashcards.data.entities

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Timestamps(created: Long?, lastModified: Long?) {
  var created: Long?
    private set
  var lastModified: Long?
    private set

  private val dateFormatter = SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.ENGLISH)

  init {
    this.created = created
    this.lastModified = lastModified
  }

  fun createdNow(): Unit {
    created = System.currentTimeMillis()
    lastModified = created
  }

  fun modifiedNow(): Unit {
    lastModified = System.currentTimeMillis()
  }

  override fun toString(): String {
    val created = this.created
    val lastModified = this.lastModified

    if (created != null && lastModified != null) {
      return "Created on ${dateFormatter.format(Date(created))}, " +
             "Last Modified on ${dateFormatter.format(Date(lastModified))}."
    } else {
      return "Not yet created."
    }
  }
}
