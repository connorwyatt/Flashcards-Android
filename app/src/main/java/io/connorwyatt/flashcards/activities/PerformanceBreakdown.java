package io.connorwyatt.flashcards.activities;

interface PerformanceBreakdown {
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
