package io.connorwyatt.flashcards.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.connorwyatt.flashcards.R;
import io.connorwyatt.flashcards.activities.FlashcardTestActivity;
import io.connorwyatt.flashcards.data.datasources.FlashcardTestDataSource;
import io.connorwyatt.flashcards.data.entities.FlashcardTest;

public class FlashcardTestCardFragment extends Fragment {
    private FlashcardTest flashcardTest = new FlashcardTest();
    private boolean isFlipped = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_flashcard_test_card,
                container, false);

        Fragment cardFragment = !isFlipped ? new CardFrontFragment() : new CardBackFragment();

        getChildFragmentManager()
                .beginTransaction()
                .add(R.id.flashcard_test_card_frame, cardFragment)
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

    private void saveFlashcardTest() {
        FlashcardTestDataSource ftds = new FlashcardTestDataSource(getActivity());
        ftds.open();
        flashcardTest = ftds.save(flashcardTest);
        ftds.close();

        FlashcardTestFragment flashcardTestFragment = ((FlashcardTestActivity) getActivity())
                .getFlashcardTestFragment();

        flashcardTestFragment.updateFlashcardTest(flashcardTest);
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
            super.onCreateView(inflater, container, savedInstanceState);

            CardView card = (CardView) inflater.inflate(R.layout.fragment_flashcard_test_card_front,
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
        private FlashcardTestCardFragment testCardFragment;
        private List<ImageButton> buttons = new ArrayList<>();
        private ImageButton currentlySelectedButton;
        private CardView cardView;

        private void setButtonClickHandlers() {
            for (final ImageButton currentButton : buttons) {
                currentButton.setImageAlpha(100);
                currentButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onButtonClick(currentButton);
                    }
                });
            }
        }

        private void onButtonClick(ImageButton button) {
            int buttonId = button.getId();

            switch (buttonId) {
                case R.id.flashcard_test_card_positive_button:
                    testCardFragment.flashcardTest.setRatingPositive();
                    break;
                case R.id.flashcard_test_card_neutral_button:
                    testCardFragment.flashcardTest.setRatingNeutral();
                    break;
                case R.id.flashcard_test_card_negative_button:
                    testCardFragment.flashcardTest.setRatingNegative();
                    break;
            }

            testCardFragment.saveFlashcardTest();

            setCurrentButton();
        }

        private void setCurrentButton() {
            ImageButton button = null;

            switch (testCardFragment.flashcardTest.getRating()) {
                case POSITIVE:
                    button = (ImageButton) cardView.findViewById(R.id
                            .flashcard_test_card_positive_button);
                    break;
                case NEUTRAL:
                    button = (ImageButton) cardView.findViewById(R.id
                            .flashcard_test_card_neutral_button);
                    break;
                case NEGATIVE:
                    button = (ImageButton) cardView.findViewById(R.id
                            .flashcard_test_card_negative_button);
                    break;
            }

            if (currentlySelectedButton != null) {
                currentlySelectedButton.setImageAlpha(100);
            }

            currentlySelectedButton = button;

            currentlySelectedButton.setImageAlpha(255);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            super.onCreateView(inflater, container, savedInstanceState);

            testCardFragment = (FlashcardTestCardFragment) getParentFragment();

            cardView = (CardView) inflater.inflate(R.layout.fragment_flashcard_test_card_back,
                    container, false);

            Bundle arguments = testCardFragment.getArguments();
            String titleText = arguments.getString(FlashcardTestCardFragment.ARGUMENT_KEYS.TITLE);
            String textText = arguments.getString(FlashcardTestCardFragment.ARGUMENT_KEYS.TEXT);

            testCardFragment.flashcardTest.setFlashcardId(arguments.getLong
                    (FlashcardTestCardFragment.ARGUMENT_KEYS.ID));

            TextView title = (TextView) cardView.findViewById(R.id.flashcard_test_card_title);
            title.setText(titleText);
            TextView text = (TextView) cardView.findViewById(R.id.flashcard_test_card_text);
            text.setText(textText);

            buttons.add((ImageButton) cardView.findViewById(R.id
                    .flashcard_test_card_negative_button));
            buttons.add((ImageButton) cardView.findViewById(R.id
                    .flashcard_test_card_neutral_button));
            buttons.add((ImageButton) cardView.findViewById(R.id
                    .flashcard_test_card_positive_button));

            setButtonClickHandlers();

            if (testCardFragment.flashcardTest.getRating() != null) {
                setCurrentButton();
            }

            return cardView;
        }
    }
}
