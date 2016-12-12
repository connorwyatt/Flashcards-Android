package io.connorwyatt.flashcards.interfaces

interface IPerformanceBreakdown
{
    val negativeCount: Int

    val negativePercent: Double

    val neutralCount: Int

    val neutralPercent: Double

    val positiveCount: Int

    val positivePercent: Double

    val ratedTotal: Int

    val skipCount: Int

    val skipPercent: Double

    val total: Int

    fun addOnPerformanceBreakdownChangeListener(listener: OnPerformanceBreakdownChangeListener)

    fun removeOnPerformanceBreakdownChangeListener(listener: OnPerformanceBreakdownChangeListener)

    fun clearOnPerformanceBreakdownChangeListener()

    interface OnPerformanceBreakdownChangeListener
    {
        fun onChange()
    }
}
