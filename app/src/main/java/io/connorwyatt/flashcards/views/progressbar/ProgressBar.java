package io.connorwyatt.flashcards.views.progressbar;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import io.connorwyatt.flashcards.R;

public class ProgressBar extends View {
    private Paint paint;
    private double progress;
    private int barColor;
    private int unfilledBarColor;
    private ValueAnimator animator;

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

    public void setBarColor(int color) {
        barColor = color;
        invalidate();
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

    public void setUnfilledBarColor(int color) {
        unfilledBarColor = color;
        invalidate();
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

    public void setProgress(final double progress, boolean animate) {
        if (!animate) {
            setProgress(progress);
        } else {
            final double oldProgress = this.progress;
            double targetProgress = clampProgress(progress, 0, 1);
            final double difference = targetProgress - oldProgress;

            if (animator != null) {
                animator.cancel();
            }

            animator = ValueAnimator.ofFloat(0, 1);

            animator.setDuration(700);

            animator.setInterpolator(new AccelerateDecelerateInterpolator());

            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    double animatedValue = (double) (float) animation.getAnimatedValue();
                    setProgress(oldProgress + animatedValue * difference, false);
                }
            });

            if (!animator.isStarted()) {
                animator.start();
            }
        }
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
