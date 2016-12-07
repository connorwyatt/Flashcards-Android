package io.connorwyatt.flashcards.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.internal.util.Predicate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.connorwyatt.flashcards.R;
import io.connorwyatt.flashcards.adapters.FlashcardTestPagerAdapter;
import io.connorwyatt.flashcards.data.datasources.FlashcardDataSource;
import io.connorwyatt.flashcards.data.entities.Flashcard;
import io.connorwyatt.flashcards.data.entities.FlashcardTest;
import io.connorwyatt.flashcards.interfaces.IPerformanceBreakdown;
import io.connorwyatt.flashcards.utils.ListUtils;
import io.connorwyatt.flashcards.views.directionalviewpager.DirectionalViewPager;
import io.connorwyatt.flashcards.views.progressbar.ProgressBar;

public class FlashcardTestFragment extends Fragment {
    private int initialCount;
    private IPerformanceBreakdown performanceBreakdown = createPerformanceBreakdown();
    private HashMap<Long, FlashcardTest> flashcardTestMap = new HashMap<>();
    private ArrayList<Long> skippedFlashcards = new ArrayList<>();
    private FlashcardTestPagerAdapter flashcardTestPagerAdapter;
    private ProgressBar progressBar;
    private List<Flashcard> flashcards;
    private ArrayList<IPerformanceBreakdown.OnPerformanceBreakdownChangeListener> changeListeners
            = new ArrayList<>();

    private int getCompletedCardsCount() {
        return performanceBreakdown.getRatedTotal() + performanceBreakdown.getSkipCount();
    }

    public IPerformanceBreakdown getPerformanceBreakdown() {
        return performanceBreakdown;
    }

    private void setUpProgressBar(ViewGroup viewGroup) {
        progressBar = (ProgressBar) viewGroup.findViewById(R.id
                .flashcard_test_progress_bar);
        DirectionalViewPager viewPager = (DirectionalViewPager) viewGroup.findViewById(R.id
                .flashcard_test_view_pager);

        viewPager.addOnPageSkipListener(new DirectionalViewPager.OnPageSkipListener() {
            @Override
            public void onPageSkip(Object skippedItem) {
                updateProgressBar();
            }
        });

        updateProgressBar();
    }

    private void setUpViewPager(ViewGroup viewGroup) {
        DirectionalViewPager viewPager = (DirectionalViewPager) viewGroup.findViewById(R.id
                .flashcard_test_view_pager);
        viewPager.setAdapter(flashcardTestPagerAdapter);

        viewPager.setAllowLeftSwipe(false);
        viewPager.addOnPageSkipListener(new DirectionalViewPager.OnPageSkipListener() {
            @Override
            public void onPageSkip(Object skippedItem) {
                Flashcard flashcard = ((Flashcard) skippedItem);

                if (!flashcardTestMap.containsKey(flashcard.getId())) {
                    skippedFlashcards.add(flashcard.getId());
                    String skipMessage = getString(R.string.flashcard_test_skip_toast, flashcard
                            .getTitle());
                    Toast.makeText(getActivity(), skipMessage, Toast.LENGTH_SHORT)
                            .show();
                    dispatchOnPerformanceBreakdownChangeEvent();
                }
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        Intent intent = getActivity().getIntent();

        FlashcardDataSource fds = new FlashcardDataSource(getActivity());
        fds.open();
        if (intent.hasExtra(EXTRA_KEYS.CATEGORY_ID)) {
            flashcards = fds.getByCategory(intent.getLongExtra(EXTRA_KEYS.CATEGORY_ID, -1));
        } else {
            flashcards = fds.getAll();
        }
        fds.close();

        initialCount = flashcards.size();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_flashcard_test,
                container, false);

        flashcardTestPagerAdapter = new FlashcardTestPagerAdapter(getFragmentManager(), flashcards);

        setUpViewPager(viewGroup);

        setUpProgressBar(viewGroup);

        return viewGroup;
    }

    public void onBackPressed(final Runnable runnable) {
        int totalCompleted = performanceBreakdown.getRatedTotal() + performanceBreakdown
                .getSkipCount();
        boolean isComplete = totalCompleted >= initialCount;

        if (isComplete) {
            runnable.run();
        } else {
            new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.flashcard_test_confirmation_title)
                    .setMessage(R.string.flashcard_test_confirmation_message)
                    .setPositiveButton(R.string.flashcard_test_confirmation_yes, new DialogInterface
                            .OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            runnable.run();
                        }
                    })
                    .setNegativeButton(R.string.flashcard_test_confirmation_no, new DialogInterface
                            .OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    })
                    .create()
                    .show();
        }
    }

    public void updateFlashcardTest(FlashcardTest flashcardTest) {
        flashcardTestMap.put(flashcardTest.getFlashcardId(), flashcardTest);
        dispatchOnPerformanceBreakdownChangeEvent();
    }

    private void updateProgressBar() {
        double percent = (double) getCompletedCardsCount() / (double) initialCount;

        progressBar.setProgress(percent, true);
    }

    private void dispatchOnPerformanceBreakdownChangeEvent() {
        for (IPerformanceBreakdown.OnPerformanceBreakdownChangeListener changeListener :
                changeListeners) {
            changeListener.onChange();
        }
    }

    private IPerformanceBreakdown createPerformanceBreakdown() {
        return new IPerformanceBreakdown() {
            @Override
            public int getNegativeCount() {
                return getRatingCount(FlashcardTest.Rating.NEGATIVE);
            }

            @Override
            public double getNegativePercent() {
                return getPercentage(getNegativeCount(), getRatedTotal());
            }

            @Override
            public int getNeutralCount() {
                return getRatingCount(FlashcardTest.Rating.NEUTRAL);
            }

            @Override
            public double getNeutralPercent() {
                return getPercentage(getNeutralCount(), getRatedTotal());
            }

            @Override
            public int getPositiveCount() {
                return getRatingCount(FlashcardTest.Rating.POSITIVE);
            }

            @Override
            public double getPositivePercent() {
                return getPercentage(getPositiveCount(), getRatedTotal());
            }

            @Override
            public int getRatedTotal() {
                return flashcardTestMap.size();
            }

            @Override
            public int getSkipCount() {
                return skippedFlashcards.size();
            }

            @Override
            public double getSkipPercent() {
                return getPercentage(getSkipCount(), getTotal());
            }

            @Override
            public int getTotal() {
                return initialCount;
            }

            @Override
            public void addOnPerformanceBreakdownChangeListener
                    (OnPerformanceBreakdownChangeListener listener) {
                changeListeners.add(listener);
            }

            @Override
            public void removeOnPerformanceBreakdownChangeListener
                    (OnPerformanceBreakdownChangeListener listener) {
                changeListeners.remove(listener);
            }

            @Override
            public void clearOnPerformanceBreakdownChangeListener() {
                changeListeners.clear();
            }

            private int getRatingCount(final FlashcardTest.Rating rating) {
                return ListUtils.filter(new ArrayList<>(flashcardTestMap.values()), new
                        Predicate<FlashcardTest>() {
                            @Override
                            public boolean apply(FlashcardTest flashcardTest) {
                                return flashcardTest.getRating() == rating;
                            }
                        }).size();
            }

            private double getPercentage(int count, int total) {
                double percentage = ((double) count / (double) total);

                if (Double.isNaN(percentage)) percentage = 0;

                return percentage;
            }
        };
    }

    public static class EXTRA_KEYS {
        public static String CATEGORY_ID = "CATEGORY_ID";
    }
}
