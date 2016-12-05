package io.connorwyatt.flashcards.views.progressbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.ColorRes;
import android.util.AttributeSet;
import android.view.View;

import io.connorwyatt.flashcards.R;

public class ProgressBar extends View {
    private Paint paint;
    private double progress;
    private int barColor;
    private int unfilledBarColor;

    public ProgressBar(Context context) {
        super(context);

        init(null);
    }

    public ProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(attrs);
    }

    public ProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(attrs);
    }

    public ProgressBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init(attrs);
    }

    public void setBarColor(@ColorRes int color) {
        barColor = color;
    }

    /**
     * A method for setting the progress of the progress bar via a decimal between 0 and 1.
     * If a value greater than 1 or less than 0 is passed, it will be clamped.
     *
     * @param progress A decimal value between 0 and 1 to set the progress to.
     */
    public void setProgress(double progress) {
        this.progress = clampProgress(progress, 0, 1);
        invalidate();
    }

    public void setUnfilledBarColor(@ColorRes int color) {
        unfilledBarColor = color;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();

        int barWidth = (int) (width * progress);

        paint.setColor(barColor);
        canvas.drawRect(0, 0, barWidth, height, paint);

        paint.setColor(unfilledBarColor);
        canvas.drawRect(barWidth, 0, width, height, paint);
    }

    private double clampProgress(double progress, double min, double max) {
        if (progress > max) {
            return max;
        } else if (progress < min) {
            return min;
        }

        return progress;
    }

    private void init(AttributeSet attributeSet) {
        paint = new Paint();

        TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attributeSet,
                R.styleable.ProgressBar, 0, 0);
        try {
            setBarColor(typedArray.getColor(R.styleable.ProgressBar_barColor, Color.BLUE));
            setUnfilledBarColor(typedArray.getColor(R.styleable.ProgressBar_unfilledBarColor,
                    Color.LTGRAY));
            setProgress(typedArray.getFloat(R.styleable.ProgressBar_progress, 0));
        } finally {
            typedArray.recycle();
        }
    }
}
