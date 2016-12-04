package io.connorwyatt.flashcards.activities;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import io.connorwyatt.flashcards.R;
import io.connorwyatt.flashcards.fragments.FlashcardTestFragment;

public class FlashcardTestActivity extends AppCompatActivity {
    private static String FRAGMENT_TAG = "DATA";
    private FlashcardTestFragment flashcardTestFragment;

    public FlashcardTestFragment getFlashcardTestFragment() {
        return flashcardTestFragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard_test);

        FragmentManager fm = getFragmentManager();
        flashcardTestFragment = (FlashcardTestFragment) fm.findFragmentByTag(FRAGMENT_TAG);

        if (flashcardTestFragment == null) {
            flashcardTestFragment = new FlashcardTestFragment();

            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.flashcard_test_frame, flashcardTestFragment, FRAGMENT_TAG)
                    .commit();
        }
    }

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, FlashcardTestActivity.class);

        context.startActivity(intent);
    }

    public static void startActivityWithCategoryFilter(Context context, long categoryId) {
        Bundle extras = new Bundle();
        extras.putLong(EXTRA_KEYS.CATEGORY_ID, categoryId);

        Intent intent = new Intent(context, FlashcardTestActivity.class);
        intent.putExtras(extras);

        context.startActivity(intent);
    }

    public static class EXTRA_KEYS {
        public static String CATEGORY_ID = "CATEGORY_ID";
    }
}
