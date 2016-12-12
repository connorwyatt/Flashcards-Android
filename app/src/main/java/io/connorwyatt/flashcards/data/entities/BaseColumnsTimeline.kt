package io.connorwyatt.flashcards.data.entities

import android.provider.BaseColumns

interface BaseColumnsTimeline : BaseColumns
{
    companion object
    {
        val _CREATED_ON = "_created_on"
        val _LAST_MODIFIED_ON = "_last_modified_on"
    }
}
