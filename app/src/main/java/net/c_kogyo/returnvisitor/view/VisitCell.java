package net.c_kogyo.returnvisitor.view;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import net.c_kogyo.returnvisitor.R;
import net.c_kogyo.returnvisitor.activity.Constants;
import net.c_kogyo.returnvisitor.data.Visit;
import net.c_kogyo.returnvisitor.util.DateTimeText;

/**
 * Created by SeijiShii on 2016/09/11.
 */

public class VisitCell extends BaseAnimateView {

    private Visit mVisit;
    private Context mContext;

    public VisitCell(Visit visit, Context context, InitialHeightCondition initCondition) {
        super(context, initCondition, R.layout.visit_cell);

        mContext = context;
        mVisit = visit;

        initMarker();
        initTimeText();
        initDataText();
    }

    @Override
    public int getViewHeight() {
        return mContext.getResources().getDimensionPixelSize(R.dimen.visit_cell_height);
    }

    @Override
    public void onUpdateHeight() {

    }

    private void initMarker() {

        ImageView marker = (ImageView) getViewById(R.id.marker);
        marker.setBackgroundResource(Constants.markerRes[mVisit.getInterest().num()]);
    }

    private void initTimeText() {

        TextView timeText = (TextView) getViewById(R.id.time_text);
        timeText.setText(DateTimeText.getTimeText(mVisit.getStart()));
    }

    private void initDataText() {

        TextView dataText = (TextView) getViewById(R.id.data_text);
        dataText.setText(mVisit.toDataString(mContext));
    }

    public Visit getVisit() {
        return mVisit;
    }
}
