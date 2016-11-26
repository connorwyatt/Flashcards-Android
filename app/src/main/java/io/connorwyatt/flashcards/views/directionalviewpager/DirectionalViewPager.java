package io.connorwyatt.flashcards.views.directionalviewpager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

import java.util.ArrayList;

public class DirectionalViewPager extends ViewPager {
    private boolean allowLeftSwipe = true;
    private ArrayList<OnPageSkipListener> onPageSkipListeners = new ArrayList<>();

    public DirectionalViewPager(Context context) {
        super(context);
        init();
    }

    public DirectionalViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public boolean getAllowLeftSwipe() {
        return allowLeftSwipe;
    }

    public void setAllowLeftSwipe(boolean allowLeftSwipe) {
        this.allowLeftSwipe = allowLeftSwipe;
    }

    public void addOnPageSkipListener(OnPageSkipListener listener) {
        if (listener != null) {
            onPageSkipListeners.add(listener);
        }
    }

    public void clearOnPageSkipListeners() {
        onPageSkipListeners.clear();
    }

    public void removeOnPageSkipListener(OnPageSkipListener listener) {
        onPageSkipListeners.remove(listener);
    }

    public void notifySkipped(Object skippedItem) {
        for (OnPageSkipListener listener : onPageSkipListeners) {
            listener.onPageSkip(skippedItem);
        }
    }

    private void init() {
        addOnPageChangeListener(new PageChangeListener(this));
    }

    public interface OnPageSkipListener {
        void onPageSkip(Object skippedItem);
    }
}
