package net.c_kogyo.returnvisitor.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import net.c_kogyo.returnvisitor.R;
import net.c_kogyo.returnvisitor.data.Tag;

/**
 * Created by SeijiShii on 2016/07/31.
 */

public class TagView extends FrameLayout {

    private Tag mTag;
    private View v;
    private OnRemoveClickListener mListener;
    private int measuredWidth;

    public TagView(Tag tag, Context context, boolean showRemoveButton, OnRemoveClickListener listener) {
        super(context);

        mTag = tag;
        mListener = listener;

        v = LayoutInflater.from(context).inflate(R.layout.tag_view, this);

        initTagText();
        initRemoveButton(showRemoveButton);

        this.measure(0, 0);
        measuredWidth = getMeasuredWidth();
    }

    private void initTagText() {

        TextView tagText = (TextView) v.findViewById(R.id.tag_text);
        tagText.setText(mTag.getName());
    }

    public Tag getTag() {
        return mTag;
    }

    public void initRemoveButton(boolean showRemoveButton) {

        Button removeButton = (Button) findViewById(R.id.remove_button);

        if (showRemoveButton) {

            removeButton.setVisibility(VISIBLE);
            removeButton.getLayoutParams().width = 25;
            removeButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {

                    mListener.onRemoveClick(TagView.this);

                }
            });

        } else {

            removeButton.setVisibility(INVISIBLE);
            removeButton.getLayoutParams().width = 0;
        }
    }

    public int getMeasuredViewWidth() {
        return measuredWidth;
    }

    public void fadeoutView(final PostFadeoutListener fadeoutListener) {

        ValueAnimator animator = ValueAnimator.ofFloat(1, 0);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                TagView.this.setAlpha((float) valueAnimator.getAnimatedValue());
            }
        });
        animator.setDuration(500);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {

                fadeoutListener.postFadeout(TagView.this);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animator.start();
    }

    public interface OnRemoveClickListener {
        void onRemoveClick(TagView tagView);
    }

    public interface PostFadeoutListener {
        void postFadeout(TagView tagView);
    }

}
