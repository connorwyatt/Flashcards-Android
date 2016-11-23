package io.connorwyatt.flashcards;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

import io.connorwyatt.flashcards.adapters.FlashcardTestPagerAdapter;
import io.connorwyatt.flashcards.data.entities.Flashcard;
import io.connorwyatt.flashcards.data.datasources.FlashcardDataSource;

public class FlashcardTest extends AppCompatActivity {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard_test);

        setUpViewPager();
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
                getSupportFragmentManager(), flashcards);

        ViewPager viewPager = (ViewPager) findViewById(R.id.activity_flashcard_test_view_pager);
        viewPager.setAdapter(adapter);
    }

    public static class EXTRA_KEYS {
        public static String CATEGORY_ID = "CATEGORY_ID";
    }
}
