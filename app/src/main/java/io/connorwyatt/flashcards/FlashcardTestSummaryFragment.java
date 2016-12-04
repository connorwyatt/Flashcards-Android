package io.connorwyatt.flashcards;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import io.connorwyatt.flashcards.activities.FlashcardTestActivity;
import io.connorwyatt.flashcards.activities.PerformanceBreakdown;

public class FlashcardTestSummaryFragment extends Fragment {
    private FlashcardTestActivity testActivity;

    private void setUpView(ViewGroup viewGroup) {
        PerformanceBreakdown performanceBreakdown = testActivity.getPerformanceBreakdown();

        ((TextView) viewGroup.findViewById(R.id.flashcard_test_summary_positive_percent))
                .setText(String.valueOf(toPercent(performanceBreakdown.getPositivePercent())) +
                        "%");
        ((TextView) viewGroup.findViewById(R.id.flashcard_test_summary_positive_count))
                .setText(String.valueOf(performanceBreakdown.getPositiveCount()));
        ((TextView) viewGroup.findViewById(R.id.flashcard_test_summary_neutral_percent))
                .setText(String.valueOf(toPercent(performanceBreakdown.getNeutralPercent())) + "%");
        ((TextView) viewGroup.findViewById(R.id.flashcard_test_summary_neutral_count))
                .setText(String.valueOf(performanceBreakdown.getNeutralCount()));
        ((TextView) viewGroup.findViewById(R.id.flashcard_test_summary_negative_percent))
                .setText(String.valueOf(toPercent(performanceBreakdown.getNegativePercent())) +
                        "%");
        ((TextView) viewGroup.findViewById(R.id.flashcard_test_summary_negative_count))
                .setText(String.valueOf(performanceBreakdown.getNegativeCount()));
        ((TextView) viewGroup.findViewById(R.id.flashcard_test_summary_skip_count)).setText
                (getResources().getQuantityString(R.plurals.flashcard_test_summary_skip_count,
                        performanceBreakdown.getSkipCount(), performanceBreakdown.getSkipCount()));

        Button finishButton = (Button) viewGroup.findViewById(R.id
                .flashcard_test_summary_finish_button);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testActivity.finish();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater
                .inflate(R.layout.fragment_flashcard_test_summary_card, container, false);

        testActivity = (FlashcardTestActivity)
                FlashcardTestSummaryFragment.this.getActivity();

        setUpView(viewGroup);

        return viewGroup;
    }

    private long toPercent(double decimal) {
        return Math.round(decimal * 100);
    }
}
