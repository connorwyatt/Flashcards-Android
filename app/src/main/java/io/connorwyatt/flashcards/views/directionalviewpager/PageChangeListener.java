package io.connorwyatt.flashcards.views.directionalviewpager;

import android.support.v4.view.ViewPager;
import io.connorwyatt.flashcards.adapters.FlashcardTestPagerAdapter;
import io.connorwyatt.flashcards.data.entities.Flashcard;

class PageChangeListener implements ViewPager.OnPageChangeListener {
    private DirectionalViewPager context;
    private boolean hasPageChanged = false;
    private int previousPage;
    private int currentPage;

    PageChangeListener(DirectionalViewPager context) {
        this.context = context;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        hasPageChanged = true;
        previousPage = currentPage;
        currentPage = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        switch (state) {
            case ViewPager.SCROLL_STATE_IDLE:
                if (hasPageChanged) {
                    if (!context.getAllowLeftSwipe() && currentPage > previousPage) {
                        FlashcardTestPagerAdapter adapter = (FlashcardTestPagerAdapter) context.getAdapter();

                        if (adapter != null) {
                            Flashcard removedFlashcard = adapter.removeItem(previousPage);
                            context.setCurrentItem(previousPage, false);
                            hasPageChanged = false;
                            context.notifySkipped(removedFlashcard);
                        }
                    }
                }

                break;
        }
    }
}
