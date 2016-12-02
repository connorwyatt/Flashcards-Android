package io.connorwyatt.flashcards;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

public class FlashcardTestCardFragment extends Fragment {
    private boolean isFlipped = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.flashcard_test_card,
                container, false);

        getChildFragmentManager()
                .beginTransaction()
                .add(R.id.flashcard_test_card_frame, new CardFrontFragment())
                .commit();

        return viewGroup;
    }

    private void flipCard() {
        if (!isFlipped) {
            getChildFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.animator.card_flip_in,
                            R.animator.card_flip_out)
                    .replace(R.id.flashcard_test_card_frame, new CardBackFragment())
                    .commit();

            isFlipped = !isFlipped;
        }
    }

    public static class ARGUMENT_KEYS {
        public static String ID = "ID";
        public static String TITLE = "TITLE";
        public static String TEXT = "TEXT";
    }

    public static class CardFrontFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            CardView card = (CardView) inflater.inflate(R.layout.flashcard_test_card_front,
                    container, false);

            final FlashcardTestCardFragment parentFragment = (FlashcardTestCardFragment)
                    getParentFragment();

            Bundle arguments = parentFragment.getArguments();
            String titleText = arguments.getString(FlashcardTestCardFragment.ARGUMENT_KEYS.TITLE);

            TextView title = (TextView) card.findViewById(R.id.flashcard_test_card_title);
            title.setText(titleText);

            ImageButton flipButton = (ImageButton) card.findViewById(R.id
                    .flashcard_test_card_flip_button);
            flipButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    parentFragment.flipCard();
                }
            });

            return card;
        }
    }

    public static class CardBackFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            CardView card = (CardView) inflater.inflate(R.layout.flashcard_test_card_back,
                    container, false);

            final FlashcardTestCardFragment parentFragment = (FlashcardTestCardFragment)
                    getParentFragment();

            Bundle arguments = parentFragment.getArguments();
            String titleText = arguments.getString(FlashcardTestCardFragment.ARGUMENT_KEYS.TITLE);
            String textText = arguments.getString(FlashcardTestCardFragment.ARGUMENT_KEYS.TEXT);

            TextView title = (TextView) card.findViewById(R.id.flashcard_test_card_title);
            title.setText(titleText);
            TextView text = (TextView) card.findViewById(R.id.flashcard_test_card_text);
            text.setText(textText);

            return card;
        }
    }
}
