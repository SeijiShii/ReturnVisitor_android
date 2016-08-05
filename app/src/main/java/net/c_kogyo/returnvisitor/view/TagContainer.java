package net.c_kogyo.returnvisitor.view;

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


    public TagContainer(Context context, ArrayList<String> tagIds) {
        super(context);

        mIds = tagIds;

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

                        //TODO もうすこしフェードインアウトするようなアニメーションにできるかも
                        ViewParent parent = tagView.getParent();
                        LinearLayout line = (LinearLayout) parent;
                        line.removeView(tagView);

                        if (line.getChildCount() <= 0) {

                            TagContainer.this.removeView(line);

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
}
