package io.connorwyatt.flashcards;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import io.connorwyatt.flashcards.activities.FlashcardTestActivity;

public class FlashcardTestSummaryFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater
                .inflate(R.layout.fragment_flashcard_test_summary_card, container, false);

        Button finishButton = (Button) viewGroup.findViewById(R.id
                .flashcard_test_summary_finish_button);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FlashcardTestActivity testActivity = (FlashcardTestActivity)
                        FlashcardTestSummaryFragment.this.getActivity();

                testActivity.finish();
            }
        });

        return viewGroup;
    }
}
