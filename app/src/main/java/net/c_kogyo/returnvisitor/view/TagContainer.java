package net.c_kogyo.returnvisitor.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;

import net.c_kogyo.returnvisitor.data.RVData;
import net.c_kogyo.returnvisitor.data.Tag;

import java.util.ArrayList;

/**
 * Created by SeijiShii on 2016/07/31.
 */

public class TagContainer extends LinearLayout {

    private static final int lineHeight = 75;

    private ArrayList<String> mIds;

    private int viewWidth;
    private ArrayList<TagView> tagViews;
    private ArrayList<LinearLayout> lineViews;
    private OnTagRemoveListener mListener;


    public TagContainer(Context context, ArrayList<String> tagIds, OnTagRemoveListener listener) {
        super(context);

        mIds = tagIds;
        mListener = listener;

        initCommon();

    }

    public TagContainer(Context context, AttributeSet attrs) {
        super(context, attrs);

        initCommon();
    }

    private void initCommon() {

        initTestData();

        viewWidth = 0;
        tagViews = new ArrayList<>();
        lineViews = new ArrayList<>();

        this.setOrientation(VERTICAL);

        if (mIds.size() <= 0) return;

        addLine();

        drawOnceForMeasure();

    }

    private void drawOnceForMeasure() {

        // 見えないようにする
        this.setVisibility(INVISIBLE);

        // すべてのTagを横一列に配置してみる
        for ( String id : mIds ) {

            final Tag tag = RVData.tagList.getById(id);
            if (tag != null) {

                TagView tagView = new TagView(tag, getContext(), true, new TagView.PostRemoveListener() {
                    @Override
                    public void postRemove(TagView tagView) {

                        mIds.remove(tagView.getTag().getId());
                        tagViews.remove(tagView);

                        //フェードインアウトするようなアニメーション
                        // カスケードでシシオドシ的な実装

                        ValueAnimator animator = ValueAnimator.ofFloat(1, 0);
                        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                TagContainer.this.setAlpha((float) valueAnimator.getAnimatedValue());
                            }
                        });
                        animator.setDuration(500);
                        animator.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animator) {


                            }

                            @Override
                            public void onAnimationEnd(Animator animator) {

                                redrawTagViews();
                                ValueAnimator animator1 = ValueAnimator.ofFloat(0, 1);
                                animator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                        TagContainer.this.setAlpha((float) valueAnimator.getAnimatedValue());
                                    }
                                });
                                animator1.setDuration(500);
                                animator1.start();

                            }

                            @Override
                            public void onAnimationCancel(Animator animator) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animator) {

                            }
                        });
                        animator.start();

                        if (mListener != null) {
                            mListener.onTagRemove(tagView.getTag());
                        }
                    }
                });

                tagViews.add(tagView);
                lineViews.get(0).addView(tagView);

            }

        }

        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (TagContainer.this.getWidth() <= 0) {
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        //
                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            redrawTagViews();
                        }
                    });

                }
            }
        }).start();
    }

    private void addLine() {
        LinearLayout line = new LinearLayout(getContext());
        line.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, lineHeight));
        line.setOrientation(HORIZONTAL);
        lineViews.add(line);
        this.addView(line);
    }

    private void redrawTagViews() {

        viewWidth = getWidth();
        setVisibility(VISIBLE);

        // 一旦すべてリセット

        this.removeAllViews();
        lineViews = new ArrayList<>();

        addLine();

        int widthSum = 0;
        for ( TagView tagView : tagViews ) {

            ViewParent parent = tagView.getParent();
            if (parent != null) {
                ((LinearLayout) parent).removeView(tagView);
            }


            if ( widthSum + tagView.getMeasuredViewWidth() > viewWidth  ) {

                widthSum = 0;
                addLine();
            }

            // 一番下の列の一番右に追加
            lineViews.get(lineViews.size() - 1).addView(tagView);
            widthSum += tagView.getMeasuredViewWidth();
        }

    }

    private void initTestData() {

        mIds = new ArrayList<>();
        for (Tag tag : RVData.tagList) {

            mIds.add(tag.getId());
        }
    }

    public void setOnTagRemoveListener(OnTagRemoveListener listener) {
        mListener = listener;
    }

    public interface OnTagRemoveListener {
        void onTagRemove(Tag tag);
    }
}
