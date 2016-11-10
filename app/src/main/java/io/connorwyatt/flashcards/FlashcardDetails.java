package io.connorwyatt.flashcards;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import io.connorwyatt.flashcards.data.Flashcard;
import io.connorwyatt.flashcards.data.FlashcardDataSource;

public class FlashcardDetails extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard_details);
    }

    public void save(View view) {
        EditText title = (EditText) findViewById(R.id.flashcard_details_title);
        EditText text = (EditText) findViewById(R.id.flashcard_details_text);

        Flashcard flashcard = new Flashcard();
        flashcard.setTitle(title.getText().toString());
        flashcard.setText(text.getText().toString());

        FlashcardDataSource fds = new FlashcardDataSource(this);
        fds.open();

        fds.createFlashcard(flashcard);

        fds.close();
    }
}
