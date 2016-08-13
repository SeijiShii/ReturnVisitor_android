package net.c_kogyo.returnvisitor.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import net.c_kogyo.returnvisitor.R;

/**
 * Created by SeijiShii on 2016/08/13.
 */

public class HeightChangeFrameLayout extends FrameLayout {

    private int collapseHeight, extractHeight;

    public HeightChangeFrameLayout(Context context, int collapseHeight, int extractHeight) {
        super(context);

        this.collapseHeight = collapseHeight;
        this.extractHeight = extractHeight;
    }

    public HeightChangeFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        applyAttr(context, attrs);
    }

    public HeightChangeFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        applyAttr(context, attrs);
    }

    private void applyAttr(Context context, AttributeSet attrs) {

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CollapseButton, 0, 0);
        try {
            collapseHeight = array.getDimensionPixelSize(R.styleable.HeightChangeFrameLayout_CollapseHeight, 0);
            extractHeight = array.getDimensionPixelSize(R.styleable.HeightChangeFrameLayout_ExtractHeight, 0);
        } finally {
            array.recycle();
        }

    }

    public void animateHeight(boolean extract, Animator.AnimatorListener animatorListener) {

        int originalHeight, targetHeight;

        originalHeight = this.getLayoutParams().height;

        if (extract) {

            targetHeight = extractHeight;

        } else {

            targetHeight = collapseHeight;

        }
        if (originalHeight == targetHeight) return;

        ValueAnimator animator = ValueAnimator.ofInt(originalHeight, targetHeight);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                HeightChangeFrameLayout.this.getLayoutParams().height = (int) valueAnimator.getAnimatedValue();
                requestLayout();
            }
        });
        animator.setDuration(Math.abs(targetHeight - originalHeight) * 3);

        if (animatorListener != null) {

            animator.addListener(animatorListener);
        }

        animator.start();
    }


    public void setHeight(boolean extract) {

        if (extract) {
            this.getLayoutParams().height = extractHeight;
        } else {
            this.getLayoutParams().height = collapseHeight;
        }
    }
}
