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
    private PostRemoveListener mListener;
    private int measuredWidth;

    public TagView(Tag tag, Context context, boolean showRemoveButton, PostRemoveListener listener) {
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

    private void initRemoveButton(boolean showRemoveButton) {

        Button removeButton = (Button) findViewById(R.id.remove_button);

        if (showRemoveButton) {

            removeButton.setVisibility(VISIBLE);
            removeButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {

                    ValueAnimator animator = ValueAnimator.ofFloat(1f, 0f);
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

                            mListener.postRemove(TagView.this);
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
            });

        } else {

            removeButton.setVisibility(INVISIBLE);
        }
    }


    public interface PostRemoveListener {
        void postRemove(TagView tagView);
    }

    public int getViewWidth() {
        return measuredWidth;
    }
}