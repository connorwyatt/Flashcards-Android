package io.connorwyatt.flashcards.data.entities

class Timestamps(created: Long?, lastModified: Long?)
{
    var created: Long
        private set
    var lastModified: Long
        private set

    init
    {
        this.created = created ?: System.currentTimeMillis()
        this.lastModified = lastModified ?: this.created
    }
}
