package io.connorwyatt.flashcards;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import io.connorwyatt.flashcards.data.FlashcardTest;
import io.connorwyatt.flashcards.data.FlashcardTestDataSource;

import java.util.ArrayList;
import java.util.List;

public class FlashcardTestCardFragment extends Fragment {
    private List<ImageButton> buttons = new ArrayList<>();
    private FlashcardTest flashcardTest = new FlashcardTest();
    private ImageButton currentlySelectedButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.flashcard_test_card, container, false);

        TextView title = (TextView) viewGroup.findViewById(R.id.flashcard_test_card_title);
        TextView text = (TextView) viewGroup.findViewById(R.id.flashcard_test_card_text);

        buttons.add((ImageButton) viewGroup.findViewById(R.id.flashcard_test_card_negative_button));
        buttons.add((ImageButton) viewGroup.findViewById(R.id.flashcard_test_card_neutral_button));
        buttons.add((ImageButton) viewGroup.findViewById(R.id.flashcard_test_card_positive_button));

        setButtonClickHandlers();

        Bundle arguments = getArguments();

        flashcardTest.setFlashcardId(arguments.getLong(ARGUMENT_KEYS.ID));
        title.setText(arguments.getString(ARGUMENT_KEYS.TITLE));
        text.setText(arguments.getString(ARGUMENT_KEYS.TEXT));

        return viewGroup;
    }

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
                flashcardTest.setRatingPositive();
                break;
            case R.id.flashcard_test_card_neutral_button:
                flashcardTest.setRatingNeutral();
                break;
            case R.id.flashcard_test_card_negative_button:
                flashcardTest.setRatingNegative();
                break;
        }

        if (currentlySelectedButton != null) {
            currentlySelectedButton.setImageAlpha(100);
        }

        currentlySelectedButton = button;

        currentlySelectedButton.setImageAlpha(255);

        FlashcardTestDataSource ftds = new FlashcardTestDataSource(getActivity());
        ftds.open();
        flashcardTest = ftds.save(flashcardTest);
        ftds.close();
    }

    public static class ARGUMENT_KEYS {
        public static String ID = "ID";
        public static String TITLE = "TITLE";
        public static String TEXT = "TEXT";
    }
}
