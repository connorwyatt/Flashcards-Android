package io.connorwyatt.flashcards.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

import java.util.List;

import io.connorwyatt.flashcards.data.entities.Flashcard;
import io.connorwyatt.flashcards.fragments.FlashcardTestCardFragment;
import io.connorwyatt.flashcards.fragments.FlashcardTestSummaryFragment;

public class FlashcardTestPagerAdapter extends FixedFragmentStatePagerAdapter {
    private List<Flashcard> flashcards;

    public FlashcardTestPagerAdapter(FragmentManager fragmentManager, List<Flashcard> flashcards) {
        super(fragmentManager);

        this.flashcards = flashcards;
    }

    private Fragment getSummaryFragment() {
        return new FlashcardTestSummaryFragment();
    }

    @Override
    public int getCount() {
        return flashcards.size() + 1;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Fragment getItem(int position) {
        boolean isFlashcard = position < flashcards.size();

        if (isFlashcard) {
            return getFlashcardFragment(position);
        } else {
            return getSummaryFragment();
        }
    }

    @Override
    public String getFragmentTag(int position) {
        String tag;

        if (position < flashcards.size()) {
            tag = flashcards.get(position).getId().toString();
        } else {
            tag = "summary";
        }

        return tag;
    }

    public Flashcard removeItem(int position) {
        Flashcard removedFlashcard = flashcards.remove(position);
        notifyDataSetChanged();
        return removedFlashcard;
    }

    private Fragment getFlashcardFragment(int position) {
        Flashcard currentFlashcard = flashcards.get(position);

        FlashcardTestCardFragment fragment = new FlashcardTestCardFragment();

        Bundle arguments = new Bundle();
        arguments.putLong(FlashcardTestCardFragment.ARGUMENT_KEYS.ID, currentFlashcard.getId());
        arguments.putString(FlashcardTestCardFragment.ARGUMENT_KEYS.TITLE, currentFlashcard
                .getTitle());
        arguments.putString(FlashcardTestCardFragment.ARGUMENT_KEYS.TEXT, currentFlashcard
                .getText());
        fragment.setArguments(arguments);

        return fragment;
    }
}
