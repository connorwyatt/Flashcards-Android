package io.connorwyatt.flashcards.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import io.connorwyatt.flashcards.R;
import io.connorwyatt.flashcards.activities.FlashcardTestActivity;
import io.connorwyatt.flashcards.interfaces.IPerformanceBreakdown;

public class FlashcardTestSummaryFragment extends Fragment {
    private ViewGroup viewGroup;
    private IPerformanceBreakdown performanceBreakdown;
    private IPerformanceBreakdown.OnPerformanceBreakdownChangeListener changeListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        viewGroup = (ViewGroup) inflater
                .inflate(R.layout.fragment_flashcard_test_summary_card, container, false);

        FlashcardTestFragment testFragment = ((FlashcardTestActivity) getActivity())
                .getFlashcardTestFragment();

        performanceBreakdown = testFragment.getPerformanceBreakdown();

        setUpView();

        return viewGroup;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        performanceBreakdown.removeOnPerformanceBreakdownChangeListener(changeListener);
    }

    private void setUpView() {
        updateValues();

        changeListener = new IPerformanceBreakdown.OnPerformanceBreakdownChangeListener() {
            @Override
            public void onChange() {
                updateValues();
            }
        };
        performanceBreakdown.addOnPerformanceBreakdownChangeListener(changeListener);

        Button finishButton = (Button) viewGroup.findViewById(R.id
                .flashcard_test_summary_finish_button);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
    }

    private void updateValues() {
        ((TextView) viewGroup.findViewById(R.id.flashcard_test_summary_positive_percent))
                .setText(getString(R.string.percentage, toPercent(performanceBreakdown
                        .getPositivePercent())));
        ((TextView) viewGroup.findViewById(R.id.flashcard_test_summary_positive_count))
                .setText(String.valueOf(performanceBreakdown.getPositiveCount()));
        ((TextView) viewGroup.findViewById(R.id.flashcard_test_summary_neutral_percent))
                .setText(getString(R.string.percentage, toPercent(performanceBreakdown
                        .getNeutralPercent())));
        ((TextView) viewGroup.findViewById(R.id.flashcard_test_summary_neutral_count))
                .setText(String.valueOf(performanceBreakdown.getNeutralCount()));
        ((TextView) viewGroup.findViewById(R.id.flashcard_test_summary_negative_percent))
                .setText(getString(R.string.percentage, toPercent(performanceBreakdown
                        .getNegativePercent())));
        ((TextView) viewGroup.findViewById(R.id.flashcard_test_summary_negative_count))
                .setText(String.valueOf(performanceBreakdown.getNegativeCount()));
        ((TextView) viewGroup.findViewById(R.id.flashcard_test_summary_skip_count)).setText
                (getResources().getQuantityString(R.plurals.flashcard_test_summary_skip_count,
                        performanceBreakdown.getSkipCount(), performanceBreakdown.getSkipCount()));
    }

    private long toPercent(double decimal) {
        return Math.round(decimal * 100);
    }
}
