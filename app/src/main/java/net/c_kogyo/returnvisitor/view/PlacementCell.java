package net.c_kogyo.returnvisitor.view;

import android.animation.Animator;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.c_kogyo.returnvisitor.R;
import net.c_kogyo.returnvisitor.data.Place;
import net.c_kogyo.returnvisitor.data.Placement;

/**
 * Created by SeijiShii on 2016/07/31.
 */

public class PlacementCell extends BaseAnimateView {

    private Placement mPlacement;
    private PostRemoveAnimationListener mListener;

    public PlacementCell(Placement placement, Context context, InitialHeightCondition initCondition, PostRemoveAnimationListener listener) {
        super(context, initCondition, R.layout.placement_cell, 5);

        mPlacement = placement;
        mListener = listener;

        initPlacementText(context);
        initRemoveButton();
    }

    private void initPlacementText(Context context) {

        TextView placementText = (TextView) findViewById(R.id.placement_text);
        placementText.setText(mPlacement.toString(context));
    }

    private void initRemoveButton() {

        Button removeButton = (Button) findViewById(R.id.remove_button);
        removeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                PlacementCell.this.changeViewHeight(AnimateCondition.FROM_HEIGHT_TO_O, true,
                        new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animator) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animator) {

                                mListener.postAnimation(PlacementCell.this);
                            }

                            @Override
                            public void onAnimationCancel(Animator animator) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animator) {

                            }
                        },5);
            }
        });
    }

    @Override
    public int getViewHeight() {
        return 50;
    }

    @Override
    public void onUpdateHeight() {

    }

    public interface PostRemoveAnimationListener {
        void postAnimation(PlacementCell cell);
    }

    public Placement getPlacement() {
        return mPlacement;
    }
}
