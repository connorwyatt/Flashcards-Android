package io.connorwyatt.flashcards.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.connorwyatt.flashcards.R;
import io.connorwyatt.flashcards.data.datasources.FlashcardDataSource;
import io.connorwyatt.flashcards.data.entities.Category;
import io.connorwyatt.flashcards.data.entities.Flashcard;

public class FlashcardDetailsActivity extends AppCompatActivity {
    private Flashcard flashcard;
    private TextInputLayout titleLayout;
    private TextInputEditText title;
    private boolean titleTouched = false;
    private TextInputLayout textLayout;
    private TextInputEditText text;
    private boolean textTouched = false;
    private TextInputEditText categories;
    private Button saveButton;

    private String getTextError() {
        String value = text.getText().toString();

        if (value.length() == 0) {
            return getString(R.string.validation_required);
        } else {
            return null;
        }
    }

    private String getTitleError() {
        String value = title.getText().toString();

        if (value.length() == 0) {
            return getString(R.string.validation_required);
        } else if (value.length() > 80) {
            return getString(R.string.validation_max_length, value.length(), 80);
        } else {
            return null;
        }
    }

    private boolean isCreate() {
        return flashcard == null;
    }

    private boolean isValid() {
        return getTitleError() == null && getTextError() == null;
    }

    private void setViewFromFlashcard(Flashcard flashcard) {
        title.setText(flashcard.getTitle());
        text.setText(flashcard.getText());
        categories.setText(flashcard.getCategoriesString());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.flashcard_details_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        title = (TextInputEditText) findViewById(R.id.flashcard_details_title);
        titleLayout = (TextInputLayout) findViewById(R.id.flashcard_details_title_layout);
        text = (TextInputEditText) findViewById(R.id.flashcard_details_text);
        textLayout = (TextInputLayout) findViewById(R.id.flashcard_details_text_layout);
        categories = (TextInputEditText) findViewById(R.id.flashcard_details_categories);
        saveButton = (Button) findViewById(R.id.flashcard_details_save_button);

        setInputListeners();

        Intent intent = getIntent();

        if (intent.hasExtra(INTENT_EXTRAS.FLASHCARD_ID)) {
            long id = intent.getLongExtra(INTENT_EXTRAS.FLASHCARD_ID, -1);

            FlashcardDataSource fds = new FlashcardDataSource(this);
            fds.open();
            Flashcard dbFlashcard = fds.getById(id);
            fds.close();

            if (dbFlashcard != null) {
                flashcard = dbFlashcard;

                setViewFromFlashcard(flashcard);
            }
        }

        updateButton();
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

        showToast(R.string.save_toast);

        setViewFromFlashcard(flashcard);
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

    private void setInputListeners() {
        title.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocused) {
                if (!isFocused) {
                    titleTouched = true;

                    String error = getTitleError();

                    if (error != null) {
                        titleLayout.setError(error);
                    } else {
                        titleLayout.setErrorEnabled(false);
                    }
                }
            }
        });

        text.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocused) {
                if (!isFocused) {
                    textTouched = true;

                    String error = getTextError();

                    if (error != null) {
                        textLayout.setError(error);
                    } else {
                        textLayout.setErrorEnabled(false);
                    }
                }
            }
        });

        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                titleTouched = true;
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String error = getTitleError();

                if (error != null) {
                    titleLayout.setError(error);
                } else {
                    titleLayout.setErrorEnabled(false);
                }

                updateButton();
            }
        });

        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                textTouched = true;
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String error = getTextError();

                if (error != null) {
                    textLayout.setError(error);
                } else {
                    textLayout.setErrorEnabled(false);
                }

                updateButton();
            }
        });
    }

    private void updateButton() {
        if (isValid()) {
            saveButton.setEnabled(true);
        } else {
            saveButton.setEnabled(false);
        }
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

    public static class INTENT_EXTRAS {
        public static String FLASHCARD_ID = "FLASHCARD_ID";
    }
}
