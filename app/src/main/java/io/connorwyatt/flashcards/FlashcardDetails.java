package io.connorwyatt.flashcards;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.connorwyatt.flashcards.data.Category;
import io.connorwyatt.flashcards.data.Flashcard;
import io.connorwyatt.flashcards.data.FlashcardDataSource;

public class FlashcardDetails extends AppCompatActivity {
    private Flashcard flashcard;
    private AutoCompleteTextView title;
    private MultiAutoCompleteTextView text;
    private MultiAutoCompleteTextView categories;

    public static class INTENT_EXTRAS {
        public static String FLASHCARD_ID = "FLASHCARD_ID";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.flashcard_details_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        title = (AutoCompleteTextView) findViewById(R.id.flashcard_details_title);
        text = (MultiAutoCompleteTextView) findViewById(R.id.flashcard_details_text);
        categories = (MultiAutoCompleteTextView) findViewById(R.id.flashcard_details_categories);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_flashcard_details_menu, menu);

        if (isCreate()) {
            menu.findItem(R.id.action_delete).setEnabled(false);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                delete();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    public void onSaveClick(View view) {
        save();
    }

    private void save() {
        Flashcard flashcardToSave = isCreate() ? new Flashcard() : flashcard;

        flashcardToSave.setTitle(title.getText().toString());
        flashcardToSave.setText(text.getText().toString());
        flashcardToSave.setCategories(processCategoriesString(categories.getText().toString()));

        FlashcardDataSource fds = new FlashcardDataSource(this);
        fds.open();
        flashcard = fds.save(flashcardToSave);
        fds.close();

        showToast(R.string.flashcard_details_save_toast);

        this.invalidateOptionsMenu();
    }

    private void delete() {
        FlashcardDataSource fds = new FlashcardDataSource(this);
        fds.open();
        fds.deleteById(flashcard.getId());
        fds.close();

        showToast(R.string.flashcard_details_delete_toast);

        NavUtils.navigateUpFromSameTask(this);
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
        categories.setText(stringifyCategories(flashcard.getCategories()));

        if (shouldFocus) {
            title.requestFocus();
        }
    }

    private boolean isCreate() {
        return flashcard == null;
    }

    private List<Category> processCategoriesString(String categoriesString) {
        List<Category> categories = new ArrayList<>();

        String[] categoryNames = categoriesString.split(",");

        for (String categoryName : categoryNames) {
            String trimmedString = categoryName.trim();

            if (trimmedString.length() > 0) {
                Category category = new Category();
                category.setName(categoryName.trim());
                categories.add(category);
            }
        }

        return categories;
    }

    private String stringifyCategories(List<Category> categories) {
        String categoriesString = "";

        for (Category category : categories) {
            if (categoriesString.length() != 0) {
                categoriesString = categoriesString.concat(", ");
            }

            categoriesString = categoriesString.concat(category.getName());
        }

        return categoriesString;
    }
}
