/*
 * Copyright (c) 2016-2017 Connor Wyatt <connorwyatt1@gmail.com>.
 *
 * This file can not be copied and/or distributed without the express permission of Connor Wyatt.
 */

package io.connorwyatt.flashcards.views.directionalviewpager

import android.support.v4.view.ViewPager
import io.connorwyatt.flashcards.adapters.FlashcardTestPagerAdapter

internal class PageChangeListener(private val context: DirectionalViewPager) : ViewPager.OnPageChangeListener
{
    private var hasPageChanged = false
    private var previousPage: Int = 0
    private var currentPage: Int = 0

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int)
    {
    }

    override fun onPageSelected(position: Int)
    {
        hasPageChanged = true
        previousPage = currentPage
        currentPage = position
    }

    override fun onPageScrollStateChanged(state: Int)
    {
        when (state)
        {
            ViewPager.SCROLL_STATE_IDLE ->
                if (hasPageChanged && !context.allowLeftSwipe && currentPage > previousPage)
                {
                    val adapter = context.adapter as FlashcardTestPagerAdapter

                    val removedFlashcard = adapter.removeItem(previousPage)
                    context.setCurrentItem(previousPage, false)
                    hasPageChanged = false
                    context.notifySkipped(removedFlashcard)
                }
        }
    }
}
