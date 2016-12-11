package io.connorwyatt.flashcards.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.List;

import io.connorwyatt.flashcards.R;
import io.connorwyatt.flashcards.adapters.CategoryNameArrayAdapter;
import io.connorwyatt.flashcards.adapters.FlashcardCardListAdapter;
import io.connorwyatt.flashcards.data.entities.Category;
import io.connorwyatt.flashcards.data.entities.Flashcard;
import io.connorwyatt.flashcards.data.services.CategoryService;
import io.connorwyatt.flashcards.data.services.FlashcardService;

public class FlashcardCardListActivity extends AppCompatActivity {
    private FlashcardCardListAdapter adapter;
    private Category allCategory;
    private Category filterCategory;

    private List<Category> getAllCategories() {
        CategoryService categoryService = new CategoryService(this);
        List<Category> categories = categoryService.getAll();

        if (allCategory == null) {
            allCategory = new Category();
            allCategory.setName(getString(R.string.flashcard_cards_list_all_category));
        }

        categories.add(0, allCategory);

        return categories;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard_card_list);

        setUpToolbar();

        setUpListRecyclerView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_flashcard_card_list_menu, menu);

        if (adapter.getItemCount() == 0) {
            menu.findItem(R.id.action_test).setEnabled(false);
            menu.findItem(R.id.action_test).getIcon().mutate().setAlpha(100);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_test:
                if (allCategory == filterCategory) {
                    FlashcardTestActivity.startActivity(this);
                } else {
                    FlashcardTestActivity.startActivityWithCategoryFilter(this, filterCategory
                            .getId());
                }
                return true;
            case R.id.action_navigate_to_categories:
                CategoriesActivity.Activities.startActivity(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateAdapterData();
    }

    public void addNewFlashcard(View view) {
        FlashcardDetailsActivity.startActivity(this);
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
                FlashcardDetailsActivity.startActivityWithFlashcard(
                        FlashcardCardListActivity.this, flashcard);
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

                filterCategory = category;

                if (category == allCategory) {
                    adapter.removeFilter();
                } else {
                    adapter.applyCategoryFilter(category.getId());
                }

                invalidateOptionsMenu();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spinner.setAdapter(spinnerAdapter);
    }

    private void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.flashcard_card_list_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);

        setUpSpinner();
    }

    private void updateAdapterData() {
        FlashcardService flashcardService = new FlashcardService(this);
        List<Flashcard> flashcards = flashcardService.getAll();

        adapter.setItems(flashcards);
        invalidateOptionsMenu();
    }
}
