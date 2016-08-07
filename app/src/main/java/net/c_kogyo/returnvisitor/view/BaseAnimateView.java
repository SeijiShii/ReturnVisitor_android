package net.c_kogyo.returnvisitor.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 * Created by sayjey on 2015/07/10.
 */
public abstract class BaseAnimateView extends FrameLayout{

    private LinearLayout view;

    public enum AnimateCondition {
        NONE,
        FROM_0_TO_HEIGHT,
        FROM_HEIGHT_TO_O,
        CHANGE_HEIGHT,
    }

    public enum InitialHeightCondition {
        FROM_0,
        VIEW_HEIGHT
    }



    public BaseAnimateView(Context context, InitialHeightCondition initCondition, int baseViewResId) {
        super(context);

        view = (LinearLayout) LayoutInflater.from(context).inflate(baseViewResId, null);
        this.addView(view);

        if (initCondition == InitialHeightCondition.FROM_0) {

            this.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0));
            animatePostDrawn();
        }
//        else {
//            this.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getViewHeight()));
//        }
    }

    public View getViewById(int resId) {
        return view.findViewById(resId);
    }


    public void animatePostDrawn() {

        this.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0));
        final Handler handler = new Handler();

        new Thread(new Runnable() {
            @Override
            public void run() {

                while (BaseAnimateView.this.getWidth() <= 0) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        //
                    }
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        changeViewHeight(AnimateCondition.FROM_0_TO_HEIGHT, true, null, 3);
                    }
                });
            }
        }).start();

    }

    abstract public int getViewHeight();

    public void changeViewHeight(AnimateCondition condition, boolean toAnimate, Animator.AnimatorListener animatorListener, int multi) {

        int originHeight = 0;
        int targetHeight = 0;

        switch (condition) {
            case FROM_0_TO_HEIGHT:
                originHeight = 0;
                targetHeight = getViewHeight();
                break;
            case FROM_HEIGHT_TO_O:
                originHeight = getViewHeight();
                targetHeight = 0;
                break;
            case CHANGE_HEIGHT:
                originHeight = getMeasuredHeight();
                targetHeight = getViewHeight();
                break;
        }

        if (!toAnimate) {
            BaseAnimateView.this.getLayoutParams().height = targetHeight;
            BaseAnimateView.this.requestLayout();
        } else {

            ValueAnimator animator = ValueAnimator.ofInt(originHeight, targetHeight);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    BaseAnimateView.this.getLayoutParams().height = (int) animation.getAnimatedValue();
                    BaseAnimateView.this.requestLayout();
                    onUpdateHeight();
                }
            });

            int duration = Math.abs(targetHeight - originHeight) * multi;
            animator.setDuration(duration);

            if (animatorListener != null) animator.addListener(animatorListener);

            animator.start();
        }
    }

    public abstract void onUpdateHeight();



}
