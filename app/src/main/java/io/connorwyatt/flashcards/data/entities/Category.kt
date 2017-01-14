/*
 * Copyright (c) 2016-2017 Connor Wyatt <connorwyatt1@gmail.com>.
 *
 * This file can not be copied and/or distributed without the express permission of Connor Wyatt.
 */

package io.connorwyatt.flashcards.data.entities

import com.google.firebase.database.DataSnapshot
import io.connorwyatt.flashcards.data.services.CategoryService
import io.reactivex.Observable

class Category(data: DataSnapshot?) : BaseEntity(data)
{
    var name: String? = null

    init
    {
        val values = data?.value as? Map<*, *>

        name = values?.get(PropertyKeys.name) as String?
    }

    override fun serialise(): MutableMap<String, Any?>
    {
        val serialisedEntity = super.serialise()

        serialisedEntity.put(PropertyKeys.name, name)

        return serialisedEntity
    }

    override fun getType() = "category"

    fun save(): Observable<Category>
    {
        return CategoryService.save(this)
    }

    fun delete(): Observable<Any?>
    {
        return CategoryService.delete(this)
    }

    companion object
    {
        object PropertyKeys
        {
            val name = "name"
        }
    }
}
