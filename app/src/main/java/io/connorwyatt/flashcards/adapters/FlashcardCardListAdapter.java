package io.connorwyatt.flashcards.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.internal.util.Predicate;

import java.util.ArrayList;
import java.util.List;

import io.connorwyatt.flashcards.R;
import io.connorwyatt.flashcards.data.Category;
import io.connorwyatt.flashcards.data.Flashcard;
import io.connorwyatt.flashcards.utils.ListUtils;

public class FlashcardCardListAdapter extends RecyclerView.Adapter<FlashcardCardListAdapter
        .FlashcardCardViewHolder> {
    private List<Flashcard> flashcards;
    private List<Flashcard> viewFlashcards;
    private long categoryFilter;
    private OnCardClickListener onCardClickListener;

    public FlashcardCardListAdapter() {
    }

    @Override
    public int getItemCount() {
        return viewFlashcards.size();
    }

    @Override
    public FlashcardCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.flashcard_card, parent, false);
        return new FlashcardCardViewHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(final FlashcardCardViewHolder holder, int position) {
        Flashcard currentFlashcard = viewFlashcards.get(position);

        if (onCardClickListener != null) {
            final FlashcardCardListAdapter context = this;
            holder.cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.onCardClickListener
                            .onClick(viewFlashcards.get(holder.getAdapterPosition()));
                }
            });
        }

        holder.title.setText(currentFlashcard.getTitle());
        holder.text.setText(currentFlashcard.getText());
        holder.categories.setText(currentFlashcard.getCategoriesString());
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void setItems(List<Flashcard> flashcards) {
        this.flashcards = flashcards;
        updateViewFlashcards();
    }

    private void updateViewFlashcards() {
        viewFlashcards = flashcards;

        viewFlashcards = applyFilters(viewFlashcards);

        notifyDataSetChanged();
    }

    private List<Flashcard> applyFilters(List<Flashcard> flashcards) {
        List<Flashcard> filteredList = new ArrayList<>(flashcards);

        if (categoryFilter > 0) {
            filteredList = ListUtils.filter(filteredList, new Predicate<Flashcard>() {
                @Override public boolean apply(Flashcard flashcard) {
                    return ListUtils.contains(flashcard.getCategories(), new Predicate<Category>() {
                        @Override public boolean apply(Category category) {
                            return category.getId() == categoryFilter;
                        }
                    });
                }
            });
        }

        return filteredList;
    }

    public void applyCategoryFilter(long categoryId) {
        categoryFilter = categoryId;
        updateViewFlashcards();
    }

    public void removeFilter() {
        categoryFilter = 0;
        updateViewFlashcards();
    }

    public void setOnCardClickListener(OnCardClickListener onCardClickListener) {
        this.onCardClickListener = onCardClickListener;
    }

    public interface OnCardClickListener {
        void onClick(Flashcard flashcard);
    }

    public static class FlashcardCardViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView title;
        TextView text;
        TextView categories;

        FlashcardCardViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.flashcard_card);
            title = (TextView) itemView.findViewById(R.id.flashcard_card_title);
            text = (TextView) itemView.findViewById(R.id.flashcard_card_text);
            categories = (TextView) itemView.findViewById(R.id.flashcard_card_categories);
        }
    }
}
