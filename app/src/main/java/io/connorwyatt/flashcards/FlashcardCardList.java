package io.connorwyatt.flashcards;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.List;

import io.connorwyatt.flashcards.adapters.CategoryNameArrayAdapter;
import io.connorwyatt.flashcards.adapters.FlashcardCardListAdapter;
import io.connorwyatt.flashcards.data.Category;
import io.connorwyatt.flashcards.data.CategoryDataSource;
import io.connorwyatt.flashcards.data.Flashcard;
import io.connorwyatt.flashcards.data.FlashcardDataSource;

public class FlashcardCardList extends AppCompatActivity {
    private FlashcardCardListAdapter adapter;
    private Category allCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard_card_list);

        setUpToolbar();

        setUpListRecyclerView();
    }

    private void setUpListRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(
                R.id.flashcard_card_list_recycler_view);

        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new FlashcardCardListAdapter();
        adapter.setOnCardClickListener(new FlashcardCardListAdapter.OnCardClickListener() {
            @Override
            public void onClick(Flashcard flashcard) {
                navigateToFlashcardDetails(flashcard);
            }
        });

        updateAdapterData();

        recyclerView.setAdapter(adapter);
    }

    private void setUpSpinner() {
        List<Category> categories = getAllCategories();

        Spinner spinner = (Spinner) findViewById(R.id.flashcard_card_list_spinner);
        ArrayAdapter<Category> spinnerAdapter = new CategoryNameArrayAdapter(
                getSupportActionBar().getThemedContext(), categories);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position,
                                       long id) {
                Category category = (Category) adapterView.getItemAtPosition(position);

                if (category == allCategory) {
                    adapter.removeFilter();
                } else {
                    adapter.applyCategoryFilter(category.getId());
                }
            }

            @Override public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spinner.setAdapter(spinnerAdapter);
    }

    private void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.flashcard_details_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);

        setUpSpinner();
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateAdapterData();
    }

    public void addNewFlashcard(View view) {
        navigateToFlashcardDetails(null);
    }

    private void updateAdapterData() {
        FlashcardDataSource fds = new FlashcardDataSource(this);
        fds.open();
        List<Flashcard> flashcards = fds.getAll();
        fds.close();

        adapter.setItems(flashcards);
    }

    private void navigateToFlashcardDetails(Flashcard flashcard) {
        Bundle extras = new Bundle();

        if (flashcard != null) {
            extras.putLong(FlashcardDetails.INTENT_EXTRAS.FLASHCARD_ID, flashcard.getId());
        }

        startFlashcardDetailsActivity(extras);
    }

    private void startFlashcardDetailsActivity(Bundle extras) {
        Intent intent = new Intent(this, FlashcardDetails.class);
        intent.putExtras(extras);
        startActivity(intent);
    }

    private List<Category> getAllCategories() {
        CategoryDataSource cds = new CategoryDataSource(this);
        cds.open();
        List<Category> categories = cds.getAll();
        cds.close();

        if (allCategory == null) {
            allCategory = new Category();
            allCategory.setName(getString(R.string.flashcard_cards_list_all_category));
        }

        categories.add(0, allCategory);

        return categories;
    }
}
