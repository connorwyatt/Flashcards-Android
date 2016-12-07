package io.connorwyatt.flashcards.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.internal.util.Predicate;

import java.util.ArrayList;
import java.util.List;

import io.connorwyatt.flashcards.R;
import io.connorwyatt.flashcards.data.entities.Category;
import io.connorwyatt.flashcards.data.entities.Flashcard;
import io.connorwyatt.flashcards.services.FlashcardTestService;
import io.connorwyatt.flashcards.utils.ListUtils;
import io.connorwyatt.flashcards.views.progressbar.ProgressBar;

public class FlashcardCardListAdapter extends RecyclerView.Adapter<FlashcardCardListAdapter
        .FlashcardCardViewHolder> {
    private List<Flashcard> flashcards;
    private List<Flashcard> viewFlashcards;
    private long categoryFilter;
    private OnCardClickListener onCardClickListener;

    public FlashcardCardListAdapter() {
    }

    public void setItems(List<Flashcard> flashcards) {
        this.flashcards = flashcards;
        updateViewFlashcards();
    }

    public void setOnCardClickListener(OnCardClickListener onCardClickListener) {
        this.onCardClickListener = onCardClickListener;
    }

    @Override
    public FlashcardCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_flashcard_card, parent, false);
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

        Context context = holder.layout.getContext();

        FlashcardTestService fts = new FlashcardTestService(context);
        Double averageRating = fts.getAverageRatingForFlashcard(currentFlashcard.getId());

        if (averageRating == null) {
            holder.rating.setProgress(0);
        } else {
            Integer color = null;

            if (averageRating > 2.0 / 3.0) {
                int colorId = R.color.colorPositive;
                color = ContextCompat.getColor(context, colorId);
            } else if (averageRating < 1.0 / 3.0) {
                int colorId = R.color.colorNegative;
                color = ContextCompat.getColor(context, colorId);
            } else {
                int colorId = R.color.colorNeutral;
                color = ContextCompat.getColor(context, colorId);
            }

            int backgroundColor = ColorUtils.setAlphaComponent(color, 128);

            holder.rating.setBarColor(color);
            holder.rating.setUnfilledBarColor(backgroundColor);
            holder.rating.setProgress(averageRating);
        }

        holder.title.setText(currentFlashcard.getTitle());
        holder.text.setText(currentFlashcard.getText());

        String categoriesString = currentFlashcard.getCategoriesString();
        if (categoriesString.length() > 0) {
            holder.categories.setText(categoriesString);
        } else {
            holder.bodyLayout.removeView(holder.categories);
        }
    }

    @Override
    public int getItemCount() {
        return viewFlashcards.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void applyCategoryFilter(long categoryId) {
        categoryFilter = categoryId;
        updateViewFlashcards();
    }

    public void removeFilter() {
        categoryFilter = 0;
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
                @Override
                public boolean apply(Flashcard flashcard) {
                    return ListUtils.contains(flashcard.getCategories(), new Predicate<Category>() {
                        @Override
                        public boolean apply(Category category) {
                            return category.getId() == categoryFilter;
                        }
                    });
                }
            });
        }

        return filteredList;
    }

    public interface OnCardClickListener {
        void onClick(Flashcard flashcard);
    }

    public static class FlashcardCardViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        LinearLayout layout;
        LinearLayout bodyLayout;
        ProgressBar rating;
        TextView title;
        TextView text;
        TextView categories;

        FlashcardCardViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.flashcard_card);
            layout = (LinearLayout) itemView.findViewById(R.id.flashcard_card_layout);
            bodyLayout = (LinearLayout) itemView.findViewById(R.id.flashcard_card_body_layout);
            rating = (ProgressBar) itemView.findViewById(R.id.flashcard_card_rating);
            title = (TextView) itemView.findViewById(R.id.flashcard_card_title);
            text = (TextView) itemView.findViewById(R.id.flashcard_card_text);
            categories = (TextView) itemView.findViewById(R.id.flashcard_card_categories);
        }
    }
}
