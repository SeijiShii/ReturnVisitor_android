package net.c_kogyo.returnvisitor.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.c_kogyo.returnvisitor.R;
import net.c_kogyo.returnvisitor.data.RVData;
import net.c_kogyo.returnvisitor.data.Visit;
import net.c_kogyo.returnvisitor.data.Work;
import net.c_kogyo.returnvisitor.service.TimeCountService;
import net.c_kogyo.returnvisitor.util.CalendarUtil;
import net.c_kogyo.returnvisitor.util.DateTimeText;

import java.util.ArrayList;

/**
 * Created by SeijiShii on 2016/09/11.
 */

public class WorkView extends BaseAnimateView {

    private static final String WORK_VIEW_TEST_TAG = "WorkViewTest";

    private Context mContext;
    private Work mWork;
    private ArrayList<Visit> visitsInWork;
    private LocalBroadcastManager broadcastManager;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(TimeCountService.TIME_COUNTING_ACTION)) {

                updateDurationText(intent);

            } else if (intent.getAction().equals(TimeCountService.STOP_TIME_COUNT_ACTION)) {
                updateStopCountButton();
            }
        }
    };

    public WorkView(Work work, Context context, InitialHeightCondition initCondition) {
        super(context, initCondition, R.layout.work_view);

        mContext = context;
        mWork = work;

        broadcastManager = LocalBroadcastManager.getInstance(mContext);
        IntentFilter timeCountFilter = new IntentFilter(TimeCountService.TIME_COUNTING_ACTION);
        IntentFilter stopTimeFilter = new IntentFilter(TimeCountService.STOP_TIME_COUNT_ACTION);

        broadcastManager.registerReceiver(receiver, timeCountFilter);
        broadcastManager.registerReceiver(receiver, stopTimeFilter);

        initStartText();
        initDurationText();
        initEndText();
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

    private TextView durationText;
    private void initDurationText() {

        durationText = (TextView) getViewById(R.id.time_text);
        updateDurationText(null);
    }

    private void updateDurationText(@Nullable Intent intent) {

        if (mWork.isTimeCounting() && intent != null) {

            long duration = intent.getLongExtra(TimeCountService.DURATION, 0);
            String durationString = DateTimeText.getDurationString(duration);
            Log.d(WORK_VIEW_TEST_TAG, "Duration updated: " + durationString);

            durationText.setText(durationString);

        } else {

            String timeString = DateTimeText.getDurationString(mWork.getDuration());
            timeString = mContext.getString(R.string.time_text, timeString);
            durationText.setText(timeString);
        }

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

    private Button stopCountButton;
    private void initStopCountButton() {

        stopCountButton = (Button) getViewById(R.id.stop_time_count_button);
        updateStopCountButton();
    }

    private void updateStopCountButton() {

        if (mWork.isTimeCounting()) {
            stopCountButton.setVisibility(VISIBLE);
            stopCountButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {

                    TimeCountService.stopTimeCount();

                }
            });
        } else {
            stopCountButton.setVisibility(INVISIBLE);
        }
    }

}
