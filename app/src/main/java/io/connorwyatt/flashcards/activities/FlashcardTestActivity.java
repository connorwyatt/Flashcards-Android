package io.connorwyatt.flashcards.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import io.connorwyatt.flashcards.utils.ListUtils;
import io.connorwyatt.flashcards.views.directionalviewpager.DirectionalViewPager;

public class FlashcardTestActivity extends AppCompatActivity {
    private int initialCount;
    private PerformanceBreakdown performanceBreakdown = createPerformanceBreakdown();
    private HashMap<Long, FlashcardTest> flashcardTestMap = new HashMap<>();

    public PerformanceBreakdown getPerformanceBreakdown() {
        return performanceBreakdown;
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.flashcard_test_confirmation_title)
                .setMessage(R.string.flashcard_test_confirmation_message)
                .setPositiveButton(R.string.flashcard_test_confirmation_yes, new DialogInterface
                        .OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FlashcardTestActivity.this.finish();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard_test);

        setUpViewPager();
    }

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, FlashcardTestActivity.class);

        context.startActivity(intent);
    }

    public static void startActivityWithCategoryFilter(Context context, long categoryId) {
        Bundle extras = new Bundle();
        extras.putLong(EXTRA_KEYS.CATEGORY_ID, categoryId);

        Intent intent = new Intent(context, FlashcardTestActivity.class);
        intent.putExtras(extras);

        context.startActivity(intent);
    }

    public void updateFlashcardTest(FlashcardTest flashcardTest) {
        flashcardTestMap.put(flashcardTest.getFlashcardId(), flashcardTest);
    }

    private void setUpViewPager() {
        List<Flashcard> flashcards;
        Intent intent = getIntent();

        FlashcardDataSource fds = new FlashcardDataSource(this);
        fds.open();
        if (intent.hasExtra(EXTRA_KEYS.CATEGORY_ID)) {
            flashcards = fds.getByCategory(intent.getLongExtra(EXTRA_KEYS.CATEGORY_ID, -1));
        } else {
            flashcards = fds.getAll();
        }
        fds.close();

        initialCount = flashcards.size();

        FlashcardTestPagerAdapter adapter = new FlashcardTestPagerAdapter(
                getFragmentManager(), flashcards);

        DirectionalViewPager viewPager = (DirectionalViewPager) findViewById(R.id
                .activity_flashcard_test_view_pager);
        viewPager.setAdapter(adapter);

        viewPager.setAllowLeftSwipe(false);
        viewPager.addOnPageSkipListener(new DirectionalViewPager.OnPageSkipListener() {
            @Override
            public void onPageSkip(Object skippedItem) {
                Flashcard flashcard = ((Flashcard) skippedItem);

                if (!flashcardTestMap.containsKey(flashcard.getId())) {
                    String skipMessage = getString(R.string.flashcard_test_skip_toast, flashcard
                            .getTitle());
                    Toast.makeText(FlashcardTestActivity.this, skipMessage, Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }

    private PerformanceBreakdown createPerformanceBreakdown() {
        return new PerformanceBreakdown() {
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
                return getTotal() - getSkipCount();
            }

            @Override
            public int getSkipCount() {
                return initialCount - flashcardTestMap.size();
            }

            @Override
            public double getSkipPercent() {
                return getPercentage(getSkipCount(), getTotal());
            }

            @Override
            public int getTotal() {
                return initialCount;
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
