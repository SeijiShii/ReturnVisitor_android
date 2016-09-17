package net.c_kogyo.returnvisitor.view;

import android.animation.Animator;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import net.c_kogyo.returnvisitor.R;
import net.c_kogyo.returnvisitor.data.RVData;
import net.c_kogyo.returnvisitor.data.Visit;
import net.c_kogyo.returnvisitor.data.Work;
import net.c_kogyo.returnvisitor.service.TimeCountService;
import net.c_kogyo.returnvisitor.util.DateTimeText;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by SeijiShii on 2016/09/11.
 */

public abstract class WorkView extends BaseAnimateView {

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
                updateEndButton();
                updateDeleteButton();
            }
        }
    };

    public WorkView(Work work, Context context, InitialHeightCondition initCondition) {
        super(context, initCondition, R.layout.work_view, 5);

        mContext = context;
        mWork = work;

        broadcastManager = LocalBroadcastManager.getInstance(mContext);
        IntentFilter timeCountFilter = new IntentFilter(TimeCountService.TIME_COUNTING_ACTION);
        IntentFilter stopTimeFilter = new IntentFilter(TimeCountService.STOP_TIME_COUNT_ACTION);

        broadcastManager.registerReceiver(receiver, timeCountFilter);
        broadcastManager.registerReceiver(receiver, stopTimeFilter);

        initStartTextButton();
        initDurationText();
        initEndButton();
        initDeleteButton();

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

    private Button startTextButton;
    private void initStartTextButton() {

        startTextButton = (Button) getViewById(R.id.start_time_text);
        updateStartTextButton();

        startTextButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {

                        Calendar setTime = Calendar.getInstance();
                        setTime.set(Calendar.HOUR_OF_DAY, i);
                        setTime.set(Calendar.MINUTE, i1);

                        if (setTime.before(mWork.getEnd())) {
                            mWork.setStart(setTime);
                            updateStartTextButton();
                            updateDurationText(null);
                        }

                    }
                },
                mWork.getStart().get(Calendar.HOUR_OF_DAY),
                mWork.getStart().get(Calendar.MINUTE),
                true).show();
            }
        });

    }

    private void updateStartTextButton() {

        String startString = DateTimeText.getTimeText(mWork.getStart());
        startString = mContext.getString(R.string.start_time_text, startString);

        startTextButton.setText(startString);
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

    private Button endButton;
    private void initEndButton() {

        endButton = (Button) getViewById(R.id.end_time_text);

        updateEndButton();

    }

    private void updateEndButton() {

        String endString;

        if (mWork.isTimeCounting()) {
            endString = mContext.getString(R.string.time_counting_stop);

            endButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    TimeCountService.stopTimeCount();
                }
            });

        } else {
            endString = DateTimeText.getTimeText(mWork.getEnd());

            endButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {

                    new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int i, int i1) {

                            Calendar setTime = Calendar.getInstance();
                            setTime.set(Calendar.HOUR_OF_DAY, i);
                            setTime.set(Calendar.MINUTE, i1);

                            if (setTime.after(mWork.getStart())) {
                                mWork.setEnd(setTime);
                                updateEndButton();
                                updateDurationText(null);
                            }

                        }
                    },
                    mWork.getEnd().get(Calendar.HOUR_OF_DAY),
                    mWork.getEnd().get(Calendar.MINUTE),
                    true).show();

                }
            });
        }

        endString = mContext.getString(R.string.end_time_text, endString);
        endButton.setText(endString);
    }

    private LinearLayout visitCellContainer;
    private void initVisitCellContainer() {

        visitCellContainer = (LinearLayout) getViewById(R.id.visit_cell_container);

        visitsInWork = RVData.getInstance().visitList.getVisitsInWork(mWork);

        for (Visit visit : visitsInWork) {

            visitCellContainer.addView(new VisitCell(visit, mContext, InitialHeightCondition.VIEW_HEIGHT){
                @Override
                public void onLongClick(Visit visit) {
                    onVisitCellLongClick(visit);
                }
            });
        }
    }

    public void addVisitCell(Visit visit) {

        // この引数VISITはすでにRVDataに追加されているものとする

        // 再度読み込むと引数VISITもvisitsInWorkに含まれているはず
        visitsInWork = RVData.getInstance().visitList.getVisitsInWork(mWork);

        // 挿入ポジションを特定
        int pos = visitsInWork.indexOf(visit);

        VisitCell visitCell = new VisitCell(visit, mContext, InitialHeightCondition.FROM_0) {
            @Override
            public void onLongClick(Visit visit) {
                onVisitCellLongClick(visit);
            }
        };

        visitCellContainer.addView(visitCell, pos);

    }

    public Work getWork() {
        return mWork;
    }

    private Button deleteButton;
    private void initDeleteButton() {

        deleteButton = (Button) getViewById(R.id.delete_button);
        updateDeleteButton();
    }

    private void updateDeleteButton() {

        if (mWork.isTimeCounting()) {
            deleteButton.setVisibility(INVISIBLE);
        } else {
            deleteButton.setVisibility(VISIBLE);
            deleteButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteConfirm();
                }
            });
        }
    }

    private void deleteConfirm() {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        String deleteWorkTitle = mContext.getString(R.string.work);
        deleteWorkTitle = mContext.getString(R.string.delete_title, deleteWorkTitle);
        builder.setTitle(deleteWorkTitle);

        String deleteWorkMessage = mContext.getString(R.string.work);
        deleteWorkMessage = mContext.getString(R.string.delete_message, deleteWorkMessage);
        builder.setMessage(deleteWorkMessage);

        builder.setNegativeButton(R.string.cancel_text, null);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                RVData.getInstance().workList.removeFromBoth(mWork);
                WorkView.this.changeViewHeight
                        (AnimateCondition.FROM_HEIGHT_TO_O, true, new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animator) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animator) {

                                postCompress(WorkView.this, visitsInWork);
                            }

                            @Override
                            public void onAnimationCancel(Animator animator) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animator) {

                            }
                        }, 3);
            }
        });

        builder.create().show();
    }

    @Nullable
    public VisitCell getVisitCell(String visitId) {

        for (int i = 0 ; i < visitCellContainer.getChildCount() ; i++ ) {

            VisitCell visitCell = (VisitCell) visitCellContainer.getChildAt(i);

            if (visitCell.getVisit().getId().equals(visitId)) {
                return visitCell;
            }
        }
        return null;
    }

    public abstract void postCompress(WorkView workView, ArrayList<Visit> visitsExpelled);

    public abstract void onVisitCellLongClick(Visit visit);

}
