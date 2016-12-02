package io.connorwyatt.flashcards;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.List;

import io.connorwyatt.flashcards.adapters.FlashcardTestPagerAdapter;
import io.connorwyatt.flashcards.data.datasources.FlashcardDataSource;
import io.connorwyatt.flashcards.data.entities.Flashcard;
import io.connorwyatt.flashcards.views.directionalviewpager.DirectionalViewPager;

public class FlashcardTest extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard_test);

        setUpViewPager();
    }

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, FlashcardTest.class);

        context.startActivity(intent);
    }

    public static void startActivityWithCategoryFilter(Context context, long categoryId) {
        Bundle extras = new Bundle();
        extras.putLong(EXTRA_KEYS.CATEGORY_ID, categoryId);

        Intent intent = new Intent(context, FlashcardTest.class);
        intent.putExtras(extras);

        context.startActivity(intent);
    }

    private void setUpViewPager() {
        List<Flashcard> flashcards;
        Intent intent = getIntent();

        FlashcardDataSource fds = new FlashcardDataSource(this);
        fds.open();
        if (intent.hasExtra(EXTRA_KEYS.CATEGORY_ID)) {
            flashcards = fds.getByCategory(intent.getLongExtra(EXTRA_KEYS.CATEGORY_ID, -1));
        } else {
            flashcards = fds.getAll();
        }
        fds.close();

        FlashcardTestPagerAdapter adapter = new FlashcardTestPagerAdapter(
                getFragmentManager(), flashcards);

        DirectionalViewPager viewPager = (DirectionalViewPager) findViewById(R.id.activity_flashcard_test_view_pager);
        viewPager.setAdapter(adapter);

        viewPager.setAllowLeftSwipe(false);
        viewPager.addOnPageSkipListener(new DirectionalViewPager.OnPageSkipListener() {
            @Override
            public void onPageSkip(Object skippedItem) {
                String flashcardTitle = ((Flashcard) skippedItem).getTitle();
                String skipMessage = getString(R.string.flashcard_test_skip_toast, flashcardTitle);
                Toast.makeText(FlashcardTest.this, skipMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class EXTRA_KEYS {
        public static String CATEGORY_ID = "CATEGORY_ID";
    }
}
