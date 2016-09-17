package net.c_kogyo.returnvisitor.activity;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import net.c_kogyo.returnvisitor.R;
import net.c_kogyo.returnvisitor.data.RVData;
import net.c_kogyo.returnvisitor.data.Visit;
import net.c_kogyo.returnvisitor.data.Work;
import net.c_kogyo.returnvisitor.view.BaseAnimateView;
import net.c_kogyo.returnvisitor.view.VisitCell;
import net.c_kogyo.returnvisitor.view.WorkView;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by SeijiShii on 2016/08/21.
 */

public class WorkActivity extends AppCompatActivity {

    private Calendar mDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.work_activity);

        setDate();
        initToolBar();
        initContainer();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setDate() {

        mDate = Calendar.getInstance();

        long dLong =  getIntent().getLongExtra(Constants.DATE_LONG, 0);
        if (dLong != 0) {
            mDate.setTimeInMillis(dLong);
        }

    }

    private Toolbar toolbar;
    private void initToolBar(){

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
//        toolbar.inflateMenu(R.menu.return_visitor_menu);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.work_visit_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        boolean result = true;

        switch (id) {
            case android.R.id.home:
                finish();
                break;
            default:
                result = super.onOptionsItemSelected(item);
        }

        return result;

    }

    private LinearLayout container;
    private void initContainer() {

        container = (LinearLayout) findViewById(R.id.container);

        initVisitCells();
        initWorkViews();

        int visitCounter = 0;
        int workCounter = 0;

        while (visitCounter < visitCells.size() && workCounter < workViews.size()) {

            if (visitCells.get(visitCounter).getVisit().getStart().before(workViews.get(workCounter).getWork().getStart())) {

                container.addView(visitCells.get(visitCounter));
                visitCounter++;

            } else {

                container.addView(workViews.get(workCounter));
                workCounter++;
            }
        }

        if (visitCounter < visitCells.size()) {

            for (int i = visitCounter ; i< visitCells.size() ; i++ ) {
                container.addView(visitCells.get(i));
            }


        } else if (workCounter < workViews.size()) {

            for (int i = workCounter ; i < workViews.size() ; i++ ) {
                container.addView(workViews.get(i));
            }
        }


    }

    ArrayList<VisitCell> visitCells;
    private void initVisitCells() {

        visitCells = new ArrayList<>();
        ArrayList<Visit> visitsOutOfWork = RVData.getInstance().visitList.getVisitsInDayOutOfWork(mDate);

        for (Visit visit : visitsOutOfWork) {

            visitCells.add(new VisitCell(visit, this, BaseAnimateView.InitialHeightCondition.VIEW_HEIGHT){
                @Override
                public void onLongClick(Visit visit) {
                    startRecordVisitForEdit(visit);
                }
            });
        }
    }

    ArrayList<WorkView> workViews;
    private void initWorkViews() {

        workViews = new ArrayList<>();
        ArrayList<Work> works = RVData.getInstance().workList.getWorksOfDay(mDate);

        for (Work work : works) {

            workViews.add(new WorkView(work, this, BaseAnimateView.InitialHeightCondition.VIEW_HEIGHT){

                @Override
                public void postCompress(WorkView workView, ArrayList<Visit> visitsExpelled) {

                    container.removeView(workView);
                    addVisitCells(visitsExpelled);
                }

                @Override
                public void onVisitCellLongClick(Visit visit) {
                    startRecordVisitForEdit(visit);
                }
            });
        }
    }

    private int getInsertPosition(Calendar time) {

        for ( int i = 0 ; i < container.getChildCount() ; i++ ) {

            Calendar time1 = null;

            View view = container.getChildAt(i);
            if (view instanceof VisitCell) {

                VisitCell visitCell = (VisitCell) view;
                Visit visit = visitCell.getVisit();
                time1 = visit.getStart();

            } else if (view instanceof WorkView) {

                WorkView workView = (WorkView) view;
                Work work = workView.getWork();
                time1 = work.getStart();
            }

            if (time1 != null) {

                if (time.before(time1)) {
                    return i;
                }
            }
        }
        return container.getChildCount();
    }

    private void insertVisitCell(Visit visit) {

        VisitCell visitCell = new VisitCell(visit, this, BaseAnimateView.InitialHeightCondition.FROM_0){
            @Override
            public void onLongClick(Visit visit) {
                startRecordVisitForEdit(visit);
            }
        };
        container.addView(visitCell, getInsertPosition(visit.getStart()));
    }

    private void addVisitCells(ArrayList<Visit> visits) {

        for (Visit visit : visits) {
            insertVisitCell(visit);
        }
    }

    private void startRecordVisitForEdit(Visit visit) {

        Intent editVisitIntent = new Intent(this, RecordVisitActivity.class);
        editVisitIntent.setAction(Constants.RecordVisitActions.EDIT_VISIT_ACTION);
        editVisitIntent.putExtra(Visit.VISIT, visit.getId());

        startActivityForResult(editVisitIntent, Constants.RecordVisitActions.EDIT_VISIT_REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == Constants.RecordVisitActions.EDIT_VISIT_REQUEST_CODE) {

            if (resultCode == Constants.RecordVisitActions.DELETE_VISIT_RESULT_CODE) {

                String visitId = data.getStringExtra(Visit.VISIT);
                if (visitId == null) return;

                final VisitCell visitCell = getVisitCell(visitId);
                if (visitCell == null) return;

                visitCell.changeViewHeight(BaseAnimateView.AnimateCondition.FROM_HEIGHT_TO_O,
                        true,
                        new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animator) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animator) {

                                container.removeView(visitCell);
                            }

                            @Override
                            public void onAnimationCancel(Animator animator) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animator) {

                            }
                        }, 5);
            } else if (resultCode == Constants.RecordVisitActions.VISIT_CHANGED_RESULT_CODE) {

                String visitId = data.getStringExtra(Visit.VISIT);
                if (visitId == null) return;

                Visit visit = RVData.getInstance().visitList.getById(visitId);
                if (visit == null) return;

                VisitCell visitCell = getVisitCell(visitId);
                if (visitCell == null) return;

                visitCell.updataData(visit);

            }

        }
    }

    private VisitCell getVisitCell(String visitId) {

        for ( int i = 0 ; i < container.getChildCount() ; i++ ) {

            View view = container.getChildAt(i);

            if (view instanceof VisitCell) {

                VisitCell visitCell = (VisitCell) view;
                if (visitCell.getVisit().getId().equals(visitId)) {
                    return visitCell;
                }
            }
        }
        return null;
    }
}
