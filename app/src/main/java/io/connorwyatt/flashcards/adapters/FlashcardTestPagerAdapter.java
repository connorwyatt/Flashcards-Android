package io.connorwyatt.flashcards.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import io.connorwyatt.flashcards.FlashcardTestCardFragment;
import io.connorwyatt.flashcards.data.Flashcard;

import java.util.List;

public class FlashcardTestPagerAdapter extends FragmentStatePagerAdapter {
    private List<Flashcard> flashcards;

    public FlashcardTestPagerAdapter(FragmentManager fragmentManager, List<Flashcard> flashcards) {
        super(fragmentManager);

        this.flashcards = flashcards;
    }

    @Override
    public Fragment getItem(int position) {
        Flashcard currentFlashcard = flashcards.get(position);

        Fragment fragment = new FlashcardTestCardFragment();

        Bundle arguments = new Bundle();
        arguments.putLong(FlashcardTestCardFragment.ARGUMENT_KEYS.ID, currentFlashcard.getId());
        arguments.putString(FlashcardTestCardFragment.ARGUMENT_KEYS.TITLE, currentFlashcard.getTitle());
        arguments.putString(FlashcardTestCardFragment.ARGUMENT_KEYS.TEXT, currentFlashcard.getText());
        fragment.setArguments(arguments);

        return fragment;
    }

    @Override
    public int getCount() {
        return flashcards.size();
    }
}
