package net.c_kogyo.returnvisitor.view;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.c_kogyo.returnvisitor.R;
import net.c_kogyo.returnvisitor.data.RVData;
import net.c_kogyo.returnvisitor.data.Visit;
import net.c_kogyo.returnvisitor.data.Work;
import net.c_kogyo.returnvisitor.util.DateTimeText;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by SeijiShii on 2016/09/11.
 */

public class WorkView extends BaseAnimateView {

    private Context mContext;
    private Work mWork;
    private ArrayList<Visit> visitsInWork;

    public WorkView(Work work, Context context, InitialHeightCondition initCondition) {
        super(context, initCondition, R.layout.work_view);

        mContext = context;
        mWork = work;

        initStartText();
        initTimeText();
        initEndText();
        initEditButton();
        initStopCountButton();

        initVisitCellContainer();
    }

    @Override
    public int getViewHeight() {

        int workCellHeight = mContext.getResources().getDimensionPixelSize(R.dimen.work_cell_height);
        int visitCellHeight = mContext.getResources().getDimensionPixelSize(R.dimen.visit_cell_height);

        return workCellHeight * 2 + visitCellHeight * visitsInWork.size();
    }

    @Override
    public void onUpdateHeight() {

    }

    private void initStartText() {

        TextView startText = (TextView) getViewById(R.id.start_time_text);
        String startString = DateTimeText.getTimeText(mWork.getStart());
        startString = mContext.getString(R.string.start_time_text, startString);

        startText.setText(startString);

    }

    private void initTimeText() {

        TextView timeText = (TextView) getViewById(R.id.time_text);

        String timeString = DateTimeText.getDurationString(mWork.getDuration());
        timeString = mContext.getString(R.string.time_text, timeString);

        timeText.setText(timeString);

    }

    private void initEndText() {

        TextView endText = (TextView) getViewById(R.id.end_time_text);

        String endString;

        if (mWork.isTimeCounting()) {
            endString = mContext.getString(R.string.time_counting);
        } else {
            endString = DateTimeText.getTimeText(mWork.getEnd());
        }

        endString = mContext.getString(R.string.end_time_text, endString);
        endText.setText(endString);

    }

    private LinearLayout visitCellContainer;
    private void initVisitCellContainer() {

        visitCellContainer = (LinearLayout) getViewById(R.id.visit_cell_container);

        visitsInWork = RVData.getInstance().visitList.getVisitsInWork(mWork);

        for (Visit visit : visitsInWork) {

            visitCellContainer.addView(new VisitCell(visit, mContext, InitialHeightCondition.VIEW_HEIGHT));
        }
    }

    public Work getWork() {
        return mWork;
    }

    private void initEditButton() {

        Button editButton = (Button) getViewById(R.id.edit_button);

        if (mWork.isTimeCounting()) {
            editButton.setVisibility(INVISIBLE);
        } else {
            editButton.setVisibility(VISIBLE);

            editButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }
    }

    private void initStopCountButton() {

        Button stopCountButton = (Button) getViewById(R.id.stop_time_count_button);
        if (mWork.isTimeCounting()) {
            stopCountButton.setVisibility(VISIBLE);
            stopCountButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        } else {
            stopCountButton.setVisibility(INVISIBLE);
        }
    }


}
