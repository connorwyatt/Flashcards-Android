package io.connorwyatt.flashcards.activities;

public interface PerformanceBreakdown {
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
}
