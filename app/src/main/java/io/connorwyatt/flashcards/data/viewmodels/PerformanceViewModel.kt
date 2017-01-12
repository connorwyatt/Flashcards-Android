package io.connorwyatt.flashcards.data.viewmodels

import io.connorwyatt.flashcards.enums.Rating

data class PerformanceViewModel(private val ratings: List<Rating>, val total: Int)
{
    val negativeCount: Int by lazy { ratings.count { it == Rating.NEGATIVE } }

    val negativePercent: Double by lazy { negativeCount.toDouble() / ratedCount }

    val neutralCount: Int by lazy { ratings.count { it == Rating.NEUTRAL } }

    val neutralPercent: Double by lazy { neutralCount.toDouble() / ratedCount }

    val positiveCount: Int by lazy { ratings.count { it == Rating.POSITIVE } }

    val positivePercent: Double by lazy { positiveCount.toDouble() / ratedCount }

    val ratedCount: Int by lazy { ratings.size }

    val ratedPercent: Double by lazy { ratedCount.toDouble() / total }

    val skipCount: Int by lazy { total - ratedCount }

    val skipPercent: Double by lazy { skipCount.toDouble() / total }
}
