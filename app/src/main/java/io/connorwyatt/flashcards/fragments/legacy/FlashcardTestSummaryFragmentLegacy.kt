package io.connorwyatt.flashcards.fragments.legacy

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import io.connorwyatt.flashcards.R
import io.connorwyatt.flashcards.activities.legacy.FlashcardTestActivityLegacy
import io.connorwyatt.flashcards.interfaces.IPerformanceBreakdown

@Deprecated("This is considered legacy.")
class FlashcardTestSummaryFragmentLegacy : Fragment()
{
    private var viewGroup: ViewGroup? = null
    private var performanceBreakdown: IPerformanceBreakdown? = null
    private var changeListener: IPerformanceBreakdown.OnPerformanceBreakdownChangeListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup,
                              savedInstanceState: Bundle?): View
    {
        super.onCreateView(inflater, container, savedInstanceState)

        viewGroup = inflater.inflate(
            R.layout.fragment_flashcard_test_summary, container, false) as ViewGroup

        val testFragment = (activity as FlashcardTestActivityLegacy).flashcardTestFragment

        performanceBreakdown = testFragment!!.performanceBreakdown

        setUpView()

        return viewGroup as ViewGroup
    }

    override fun onDestroy()
    {
        super.onDestroy()

        performanceBreakdown!!.removeOnPerformanceBreakdownChangeListener(changeListener!!)
    }

    private fun setUpView()
    {
        updateValues()

        changeListener = object : IPerformanceBreakdown.OnPerformanceBreakdownChangeListener
        {
            override fun onChange()
            {
                updateValues()
            }
        }
        performanceBreakdown!!.addOnPerformanceBreakdownChangeListener(changeListener!!)

        val finishButton = viewGroup!!.findViewById(R.id
                                                        .flashcard_test_summary_finish_button) as Button
        finishButton.setOnClickListener { activity.finish() }
    }

    private fun updateValues()
    {
        (viewGroup!!.findViewById(R.id.flashcard_test_summary_positive_percent) as TextView).text =
            getString(R.string.percentage, toPercent(performanceBreakdown!!.positivePercent))
        (viewGroup!!.findViewById(R.id.flashcard_test_summary_positive_count) as TextView).text =
            performanceBreakdown!!.positiveCount.toString()
        (viewGroup!!.findViewById(R.id.flashcard_test_summary_neutral_percent) as TextView).text =
            getString(R.string.percentage, toPercent(performanceBreakdown!!.neutralPercent))
        (viewGroup!!.findViewById(R.id.flashcard_test_summary_neutral_count) as TextView).text =
            performanceBreakdown!!.neutralCount.toString()
        (viewGroup!!.findViewById(R.id.flashcard_test_summary_negative_percent) as TextView).text =
            getString(R.string.percentage, toPercent(performanceBreakdown!!.negativePercent))
        (viewGroup!!.findViewById(R.id.flashcard_test_summary_negative_count) as TextView).text =
            performanceBreakdown!!.negativeCount.toString()
        (viewGroup!!.findViewById(R.id.flashcard_test_summary_skip_count) as TextView).text =
            resources.getQuantityString(R.plurals.flashcard_test_summary_skip_count,
                                        performanceBreakdown!!.skipCount,
                                        performanceBreakdown!!.skipCount)
    }

    private fun toPercent(decimal: Double): Long
    {
        return Math.round(decimal * 100)
    }
}
