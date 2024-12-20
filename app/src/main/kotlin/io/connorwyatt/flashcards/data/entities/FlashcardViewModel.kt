/*
 * Copyright (c) 2016-2017 Connor Wyatt <connorwyatt1@gmail.com>.
 *
 * This file can not be copied and/or distributed without the express permission of Connor Wyatt.
 */

package io.connorwyatt.flashcards.data.entities

import com.google.firebase.database.DataSnapshot

class Flashcard(data: DataSnapshot?) : BaseEntity(data) {
  var title: String? = null
  var text: String? = null

  init {
    val values = data?.value as? Map<*, *>

    title = values?.get(PropertyKeys.title) as String?
    text = values?.get(PropertyKeys.text) as String?
  }

  override fun serialise(): MutableMap<String, Any?> {
    val serialisedEntity = super.serialise()

    serialisedEntity.put(PropertyKeys.title, title)
    serialisedEntity.put(PropertyKeys.text, text)

    return serialisedEntity
  }

  override fun getType() = "flashcard"

  companion object {
    object PropertyKeys {
      val title = "title"
      val text = "text"
    }
  }
}
