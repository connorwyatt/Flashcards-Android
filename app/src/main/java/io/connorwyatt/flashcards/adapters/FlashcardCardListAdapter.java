package io.connorwyatt.flashcards.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import io.connorwyatt.flashcards.R;
import io.connorwyatt.flashcards.data.Flashcard;

public class FlashcardCardListAdapter extends RecyclerView.Adapter<FlashcardCardListAdapter.FlashcardCardViewHolder> {
    List<Flashcard> flashcards;

    public FlashcardCardListAdapter(List<Flashcard> flashcards) {
        this.flashcards = flashcards;
    }

    public static class FlashcardCardViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView title;
        TextView text;

        FlashcardCardViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.flashcard_card);
            title = (TextView) itemView.findViewById(R.id.flashcard_card_title);
            text = (TextView) itemView.findViewById(R.id.flashcard_card_text);

        }
    }

    @Override
    public int getItemCount() {
        return flashcards.size();
    }

    @Override
    public FlashcardCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext()).inflate(R.layout.flashcard_card, parent, false);
        return new FlashcardCardViewHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(FlashcardCardViewHolder holder, int position) {
        Flashcard currentFlashcard = flashcards.get(position);

        holder.title.setText(currentFlashcard.getTitle());
        holder.text.setText(currentFlashcard.getText());
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
