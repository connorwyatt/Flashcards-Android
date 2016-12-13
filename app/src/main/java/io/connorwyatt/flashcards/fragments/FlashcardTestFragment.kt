package io.connorwyatt.flashcards.fragments

import android.app.AlertDialog
import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import io.connorwyatt.flashcards.R
import io.connorwyatt.flashcards.adapters.FlashcardTestPagerAdapter
import io.connorwyatt.flashcards.data.entities.Flashcard
import io.connorwyatt.flashcards.data.entities.FlashcardTest
import io.connorwyatt.flashcards.data.services.FlashcardService
import io.connorwyatt.flashcards.interfaces.IPerformanceBreakdown
import io.connorwyatt.flashcards.utils.ListUtils
import io.connorwyatt.flashcards.views.directionalviewpager.DirectionalViewPager
import io.connorwyatt.flashcards.views.progressbar.ProgressBar
import java.util.ArrayList
import java.util.HashMap

class FlashcardTestFragment : Fragment()
{
    val performanceBreakdown = createPerformanceBreakdown()
    private var initialCount: Int = 0
    private val flashcardTestMap = HashMap<Long, FlashcardTest>()
    private val skippedFlashcards = ArrayList<Long>()
    private var flashcardTestPagerAdapter: FlashcardTestPagerAdapter? = null
    private var progressBar: ProgressBar? = null
    private var flashcards: MutableList<Flashcard>? = null
    private val changeListeners = ArrayList<IPerformanceBreakdown.OnPerformanceBreakdownChangeListener>()

    private val completedCardsCount: Int
        get() = performanceBreakdown.ratedTotal + performanceBreakdown.skipCount

    private fun setUpProgressBar(viewGroup: ViewGroup)
    {
        progressBar = viewGroup.findViewById(R.id
                                                 .flashcard_test_progress_bar) as ProgressBar
        val viewPager = viewGroup.findViewById(R.id
                                                   .flashcard_test_view_pager) as DirectionalViewPager

        viewPager.addOnPageSkipListener(
            object : DirectionalViewPager.OnPageSkipListener
            {
                override fun onPageSkip(skippedItem: Any)
                {
                    updateProgressBar()
                }
            }
        )

        updateProgressBar()
    }

    private fun setUpViewPager(viewGroup: ViewGroup)
    {
        val viewPager = viewGroup.findViewById(R.id
                                                   .flashcard_test_view_pager) as DirectionalViewPager
        viewPager.adapter = flashcardTestPagerAdapter

        viewPager.allowLeftSwipe = false
        viewPager.addOnPageSkipListener(
            object : DirectionalViewPager.OnPageSkipListener
            {
                override fun onPageSkip(skippedItem: Any)
                {
                    val flashcard = skippedItem as Flashcard

                    if (!flashcardTestMap.containsKey(flashcard.id))
                    {
                        skippedFlashcards.add(flashcard.id!!)
                        val skipMessage = getString(R.string.flashcard_test_skip_toast,
                                                    flashcard
                                                        .title)
                        Toast.makeText(activity,
                                       skipMessage,
                                       Toast.LENGTH_SHORT)
                            .show()

                        dispatchOnPerformanceBreakdownChangeEvent()
                    }
                }
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        retainInstance = true

        val intent = activity.intent

        val flashcardService = FlashcardService(activity)
        if (intent.hasExtra(EXTRA_KEYS.CATEGORY_ID))
        {
            flashcards = flashcardService
                .getByCategory(intent.getLongExtra(EXTRA_KEYS.CATEGORY_ID, -1)).toMutableList()
        }
        else
        {
            flashcards = flashcardService.getAll().toMutableList()
        }

        initialCount = flashcards!!.size
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup,
                              savedInstanceState: Bundle?): View
    {
        super.onCreateView(inflater, container, savedInstanceState)

        val viewGroup = inflater.inflate(R.layout.fragment_flashcard_test,
                                         container, false) as ViewGroup

        flashcardTestPagerAdapter = FlashcardTestPagerAdapter(fragmentManager, flashcards!!)

        setUpViewPager(viewGroup)

        setUpProgressBar(viewGroup)

        return viewGroup
    }

    fun onBackPressed(runnable: Runnable)
    {
        val totalCompleted = performanceBreakdown.ratedTotal + performanceBreakdown
            .skipCount
        val isComplete = totalCompleted >= initialCount

        if (isComplete)
        {
            runnable.run()
        }
        else
        {
            AlertDialog.Builder(activity)
                .setTitle(R.string.flashcard_test_confirmation_title)
                .setMessage(R.string.flashcard_test_confirmation_message)
                .setPositiveButton(R.string.flashcard_test_confirmation_yes) { dialogInterface, i -> runnable.run() }
                .setNegativeButton(R.string.flashcard_test_confirmation_no) { dialogInterface, i -> }
                .create()
                .show()
        }
    }

    fun updateFlashcardTest(flashcardTest: FlashcardTest)
    {
        flashcardTestMap.put(flashcardTest.flashcardId!!, flashcardTest)
        dispatchOnPerformanceBreakdownChangeEvent()
    }

    private fun updateProgressBar()
    {
        val percent = completedCardsCount.toDouble() / initialCount.toDouble()

        progressBar!!.setProgress(percent, true)
    }

    private fun dispatchOnPerformanceBreakdownChangeEvent()
    {
        for (changeListener in changeListeners)
        {
            changeListener.onChange()
        }
    }

    private fun createPerformanceBreakdown(): IPerformanceBreakdown
    {
        return object : IPerformanceBreakdown
        {
            override val negativeCount: Int
                get() = getRatingCount(FlashcardTest.Rating.NEGATIVE)

            override val negativePercent: Double
                get() = getPercentage(negativeCount, ratedTotal)

            override val neutralCount: Int
                get() = getRatingCount(FlashcardTest.Rating.NEUTRAL)

            override val neutralPercent: Double
                get() = getPercentage(neutralCount, ratedTotal)

            override val positiveCount: Int
                get() = getRatingCount(FlashcardTest.Rating.POSITIVE)

            override val positivePercent: Double
                get() = getPercentage(positiveCount, ratedTotal)

            override val ratedTotal: Int
                get() = flashcardTestMap.size

            override val skipCount: Int
                get() = skippedFlashcards.size

            override val skipPercent: Double
                get() = getPercentage(skipCount, total)

            override val total: Int
                get() = initialCount

            override fun addOnPerformanceBreakdownChangeListener(listener: IPerformanceBreakdown.OnPerformanceBreakdownChangeListener)
            {
                changeListeners.add(listener)
            }

            override fun removeOnPerformanceBreakdownChangeListener(listener: IPerformanceBreakdown.OnPerformanceBreakdownChangeListener)
            {
                changeListeners.remove(listener)
            }

            override fun clearOnPerformanceBreakdownChangeListener()
            {
                changeListeners.clear()
            }

            private fun getRatingCount(rating: FlashcardTest.Rating): Int
            {
                return ListUtils.filter(ArrayList(flashcardTestMap.values)) { flashcardTest -> flashcardTest.rating === rating }.size
            }

            private fun getPercentage(count: Int, total: Int): Double
            {
                var percentage = count.toDouble() / total.toDouble()

                if (java.lang.Double.isNaN(percentage)) percentage = 0.0

                return percentage
            }
        }
    }

    object EXTRA_KEYS
    {
        var CATEGORY_ID = "CATEGORY_ID"
    }
}
