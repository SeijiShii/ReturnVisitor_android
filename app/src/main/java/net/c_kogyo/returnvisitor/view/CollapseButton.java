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
    }

    public CollapseButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void applyAttr(Context context, AttributeSet attrs) {

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CollapseButton, 0, 0);
        try {
            collapseHeight = array.getInt(R.styleable.CollapseButton_CollapseHeight, 0);
            extractHeight = array.getInt(R.styleable.CollapseButton_ExtractHeight, 0);
        } finally {
            array.recycle();
        }

    }

    public void animateHeight(boolean collapse) {

        int originalHeight, targetHeight;

        originalHeight = this.getLayoutParams().height;

        if (collapse) {

            targetHeight = collapseHeight;

        } else {

            targetHeight = extractHeight;

        }

        ValueAnimator animator = ValueAnimator.ofInt(originalHeight, targetHeight);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                CollapseButton.this.getLayoutParams().height = (int) valueAnimator.getAnimatedValue();
            }
        });
        animator.setDuration(500);
        animator.start();
    }

    private void initCommon() {

    }


}
