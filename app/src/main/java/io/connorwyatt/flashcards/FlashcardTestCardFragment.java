package io.connorwyatt.flashcards;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FlashcardTestCardFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater
                .inflate(R.layout.flashcard_test_card, container, false);

        TextView title = (TextView) viewGroup.findViewById(R.id.flashcard_test_card_title);
        TextView text = (TextView) viewGroup.findViewById(R.id.flashcard_test_card_text);

        title.setText(getArguments().getString(ARGUMENT_KEYS.TITLE));
        text.setText(getArguments().getString(ARGUMENT_KEYS.TEXT));

        return viewGroup;
    }

    public static class ARGUMENT_KEYS {
        public static String TITLE = "TITLE";
        public static String TEXT = "TEXT";
    }
}
