package net.c_kogyo.returnvisitor.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.Button;

import net.c_kogyo.returnvisitor.R;

/**
 * Created by SeijiShii on 2016/08/12.
 */

public class CollapseButton extends Button {

    private int collapseHeight, extractHeight;

    public CollapseButton(Context context, int collapseHeight, int extractHeight) {
        super(context);

        this.collapseHeight = collapseHeight;
        this.extractHeight = extractHeight;
    }

    public CollapseButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyAttr(context, attrs);
    }

    public CollapseButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyAttr(context, attrs);
    }

    private void applyAttr(Context context, AttributeSet attrs) {

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CollapseButton, 0, 0);
        try {
            collapseHeight = array.getDimensionPixelSize(R.styleable.CollapseButton_CollapseHeight, 0);
            extractHeight = array.getDimensionPixelSize(R.styleable.CollapseButton_ExtractHeight, 0);
        } finally {
            array.recycle();
        }

    }

    public void animateHeight(boolean extract) {

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
                CollapseButton.this.getLayoutParams().height = (int) valueAnimator.getAnimatedValue();
                requestLayout();
            }
        });
        animator.setDuration(Math.abs(targetHeight - originalHeight) * 3);
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
