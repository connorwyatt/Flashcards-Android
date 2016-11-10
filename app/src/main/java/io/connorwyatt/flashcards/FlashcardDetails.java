package io.connorwyatt.flashcards;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import io.connorwyatt.flashcards.data.Flashcard;
import io.connorwyatt.flashcards.data.FlashcardDataSource;

public class FlashcardDetails extends AppCompatActivity {
    private Flashcard flashcard;
    private EditText title;
    private EditText text;

    public static class INTENT_EXTRAS {
        public static String FLASHCARD_ID = "FLASHCARD_ID";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard_details);

        title = (EditText) findViewById(R.id.flashcard_details_title);
        text = (EditText) findViewById(R.id.flashcard_details_text);

        Intent intent = getIntent();

        if (intent.hasExtra(INTENT_EXTRAS.FLASHCARD_ID)) {
            long id = intent.getLongExtra(INTENT_EXTRAS.FLASHCARD_ID, -1);

            FlashcardDataSource fds = new FlashcardDataSource(this);
            fds.open();
            Flashcard dbFlashcard = fds.getById(id);
            fds.close();

            if (dbFlashcard != null) {
                flashcard = dbFlashcard;

                setViewFromFlashcard(flashcard, true);
            }
        }
    }

    public void save(View view) {
        Flashcard flashcardToSave = flashcard != null ? flashcard : new Flashcard();

        flashcardToSave.setTitle(title.getText().toString());
        flashcardToSave.setText(text.getText().toString());

        FlashcardDataSource fds = new FlashcardDataSource(this);
        fds.open();
        fds.save(flashcardToSave);
        fds.close();

        showToast(R.string.flashcard_details_save_toast);
    }

    private void showToast(int messageStringId) {
        Context context = getApplicationContext();
        CharSequence toastMessage = getString(messageStringId);
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, toastMessage, duration);
        toast.show();
    }

    private void setViewFromFlashcard(Flashcard flashcard, boolean shouldFocus) {
        title.setText(flashcard.getTitle());
        text.setText(flashcard.getText());

        if (shouldFocus) {
            title.requestFocus();
        }
    }
}
