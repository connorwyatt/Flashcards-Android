package io.connorwyatt.flashcards;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

import io.connorwyatt.flashcards.adapters.FlashcardCardListAdapter;
import io.connorwyatt.flashcards.data.Flashcard;

public class FlashcardCardList extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard_card_list);

        recyclerView = (RecyclerView) findViewById(R.id.flashcard_card_list_recycler_view);

        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        ArrayList<Flashcard> flashcards = new ArrayList<>();

        adapter = new FlashcardCardListAdapter(flashcards);
        recyclerView.setAdapter(adapter);
    }

    public void addNewFlashcard(View view) {
        Intent intent = new Intent(this, FlashcardDetails.class);
        startActivity(intent);
    }
}
