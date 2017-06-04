/*
 * Copyright (c) 2016-2017 Connor Wyatt <connorwyatt1@gmail.com>.
 *
 * This file can not be copied and/or distributed without the express permission of Connor Wyatt.
 */

package io.connorwyatt.flashcards.data.entities

import com.google.firebase.database.DataSnapshot
import io.connorwyatt.flashcards.data.services.TagService
import io.reactivex.Observable

class Tag(data: DataSnapshot?) : BaseEntity(data) {
  var name: String? = null

  init {
    val values = data?.value as? Map<*, *>

    name = values?.get(PropertyKeys.name) as String?
  }

  override fun serialise(): MutableMap<String, Any?> {
    val serialisedEntity = super.serialise()

    serialisedEntity.put(PropertyKeys.name, name)

    return serialisedEntity
  }

  override fun getType() = "tag"

  fun save(): Observable<Tag> {
    return TagService.save(this)
  }

  fun delete(): Observable<Any?> {
    return TagService.delete(this)
  }

  companion object {
    object PropertyKeys {
      val name = "name"
    }
  }
}
