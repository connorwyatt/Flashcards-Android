package io.connorwyatt.flashcards;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

import io.connorwyatt.flashcards.adapters.FlashcardCardListAdapter;
import io.connorwyatt.flashcards.data.Flashcard;
import io.connorwyatt.flashcards.data.FlashcardDataSource;

public class FlashcardCardList extends AppCompatActivity {
    private FlashcardCardListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard_card_list);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.flashcard_card_list_recycler_view);

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
}
