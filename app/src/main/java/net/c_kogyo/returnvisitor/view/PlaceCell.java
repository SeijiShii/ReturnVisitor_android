package net.c_kogyo.returnvisitor.view;

import android.animation.Animator;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import net.c_kogyo.returnvisitor.R;
import net.c_kogyo.returnvisitor.activity.Constants;
import net.c_kogyo.returnvisitor.data.Person;
import net.c_kogyo.returnvisitor.data.Place;

/**
 * Created by SeijiShii on 2016/07/24.
 */

public class PlaceCell extends BaseAnimateView {

    private Place mPlace;
    private Context mContext;

    public PlaceCell(Context context,
                     Place place,
                     InitialHeightCondition initCondition,
                     PostRemoveAnimationListener listener) {
        super(context, initCondition, R.layout.place_cell, 3);

        mContext = context;
        mPlace = place;

        initRaterMark();
        initDataText();
        setPlace(null);
        initRemoveButton(listener);

    }

    private ImageView raterMark;
    private void initRaterMark() {

        raterMark = (ImageView) getViewById(R.id.rater_mark);

    }

    private TextView dataText;
    @Override
    public int getViewHeight() {

        return 50;
    }


    private void initDataText() {

        dataText = (TextView) getViewById(R.id.data_text);
    }

    @Override
    public void onUpdateHeight() {

    }

    public Place getPlace() {
        return mPlace;
    }

    public void setPlace(Place place) {

        if (place != null) {
            mPlace = place;
        }

        raterMark.setBackgroundResource(Constants.buttonRes[mPlace.getInterest().num()]);
        dataText.setText(mPlace.toString());

    }

    private void initRemoveButton(final PostRemoveAnimationListener listener) {

        Button removeButton = (Button) getViewById(R.id.remove_button);

        if (listener == null) {
            removeButton.setVisibility(INVISIBLE);
        } else {
            removeButton.setVisibility(VISIBLE);
            removeButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {

                    PlaceCell.this.changeViewHeight(AnimateCondition.FROM_HEIGHT_TO_O,
                            true,
                            new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animator) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animator) {

                                    listener.postAnimation(PlaceCell.this);
                                }

                                @Override
                                public void onAnimationCancel(Animator animator) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animator) {

                                }
                            },
                    5);
                }
            });
        }
    }

    public interface PostRemoveAnimationListener {
        void postAnimation(PlaceCell cell);
    }


}
