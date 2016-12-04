package io.connorwyatt.flashcards.interfaces;

public interface IPerformanceBreakdown {
    int getNegativeCount();

    double getNegativePercent();

    int getNeutralCount();

    double getNeutralPercent();

    int getPositiveCount();

    double getPositivePercent();

    int getRatedTotal();

    int getSkipCount();

    double getSkipPercent();

    int getTotal();

    void addOnPerformanceBreakdownChangeListener(OnPerformanceBreakdownChangeListener listener);

    void removeOnPerformanceBreakdownChangeListener(OnPerformanceBreakdownChangeListener listener);

    void clearOnPerformanceBreakdownChangeListener();

    interface OnPerformanceBreakdownChangeListener {
        void onChange();
    }
}
