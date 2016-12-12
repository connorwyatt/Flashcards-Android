package io.connorwyatt.flashcards.views.directionalviewpager

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet

import java.util.ArrayList

class DirectionalViewPager : ViewPager
{
    val allowLeftSwipe = true
    private val onPageSkipListeners = ArrayList<OnPageSkipListener>()

    constructor(context: Context) : super(context)
    {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    {
        init()
    }

    fun addOnPageSkipListener(listener: OnPageSkipListener)
    {
        onPageSkipListeners.add(listener)
    }

    fun clearOnPageSkipListeners()
    {
        onPageSkipListeners.clear()
    }

    fun removeOnPageSkipListener(listener: OnPageSkipListener)
    {
        onPageSkipListeners.remove(listener)
    }

    fun notifySkipped(skippedItem: Any)
    {
        onPageSkipListeners.forEach { it.onPageSkip(skippedItem) }
    }

    private fun init()
    {
        addOnPageChangeListener(PageChangeListener(this))
    }

    interface OnPageSkipListener
    {
        fun onPageSkip(skippedItem: Any)
    }
}
