package net.c_kogyo.returnvisitor.view;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.c_kogyo.returnvisitor.R;
import net.c_kogyo.returnvisitor.activity.Constants;
import net.c_kogyo.returnvisitor.activity.RecordVisitActivity;
import net.c_kogyo.returnvisitor.data.Visit;
import net.c_kogyo.returnvisitor.util.DateTimeText;

/**
 * Created by SeijiShii on 2016/09/11.
 */

public abstract class VisitCell extends BaseAnimateView {

    private Visit mVisit;
    private Context mContext;

    public VisitCell(Visit visit, Context context, InitialHeightCondition initCondition) {
        super(context, initCondition, R.layout.visit_cell, 5);

        mContext = context;
        mVisit = visit;

        initMarker();
        initTimeText();
        initDataText();

        updateData();

        this.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                VisitCell.this.onLongClick(mVisit);

                return true;
            }
        });
    }

    @Override
    public int getViewHeight() {
        return mContext.getResources().getDimensionPixelSize(R.dimen.visit_cell_height);
    }

    @Override
    public void onUpdateHeight() {

    }

    private ImageView marker;
    private void initMarker() {

        marker = (ImageView) getViewById(R.id.marker);
    }

    private TextView timeText;
    private void initTimeText() {

        timeText = (TextView) getViewById(R.id.time_text);
    }

    private TextView dataText;
    private void initDataText() {

        dataText = (TextView) getViewById(R.id.data_text);
    }

    public Visit getVisit() {
        return mVisit;
    }

    public abstract void onLongClick(Visit visit);

    private void updateData() {

        marker.setBackgroundResource(Constants.markerRes[mVisit.getInterest().num()]);
        timeText.setText(DateTimeText.getTimeText(mVisit.getStart()));
        dataText.setText(mVisit.toDataString(mContext));
    }

    public void updataData(Visit visit) {

        mVisit = visit;
        updateData();
    }
}
