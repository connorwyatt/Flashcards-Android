package io.connorwyatt.flashcards.views.directionalviewpager

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet

class DirectionalViewPager : ViewPager
{
    var allowLeftSwipe = true
    private val onPageSkipListeners: MutableList<(Any) -> Unit> = mutableListOf()

    constructor(context: Context) : super(context)
    {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    {
        init()
    }

    fun addOnPageSkipListener(listener: (Any) -> Unit)
    {
        onPageSkipListeners.add(listener)
    }

    fun removeOnPageSkipListener(listener: (Any) -> Unit)
    {
        onPageSkipListeners.remove(listener)
    }

    fun notifySkipped(skippedItem: Any)
    {
        onPageSkipListeners.forEach { it.invoke(skippedItem) }
    }

    private fun init()
    {
        addOnPageChangeListener(PageChangeListener(this))
    }
}
