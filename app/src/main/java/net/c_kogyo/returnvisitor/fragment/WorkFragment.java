package net.c_kogyo.returnvisitor.fragment;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;

import net.c_kogyo.returnvisitor.R;
import net.c_kogyo.returnvisitor.activity.Constants;
import net.c_kogyo.returnvisitor.activity.RecordVisitActivity;
import net.c_kogyo.returnvisitor.data.RVData;
import net.c_kogyo.returnvisitor.data.Visit;
import net.c_kogyo.returnvisitor.data.VisitList;
import net.c_kogyo.returnvisitor.data.Work;
import net.c_kogyo.returnvisitor.view.BaseAnimateView;
import net.c_kogyo.returnvisitor.view.VisitCell;
import net.c_kogyo.returnvisitor.view.WorkView;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by SeijiShii on 2016/09/17.
 */

public class WorkFragment extends Fragment {

    private Calendar mDate;
    private static OnAllItemRemoveListener mOnAllItemRemoveListener;

    public static WorkFragment newInstance(Calendar date, OnAllItemRemoveListener onAllItemRemoveListener) {

        mOnAllItemRemoveListener = onAllItemRemoveListener;

        WorkFragment workFragment = new WorkFragment();

        Bundle arg = new Bundle();
        Intent intent = new Intent();
        intent.putExtra(Constants.DATE_LONG, date.getTimeInMillis());
        arg.putParcelable(Constants.DATE_LONG, intent);
        workFragment.setArguments(arg);

        return workFragment;
    }

    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setDate();

        view = inflater.inflate(R.layout.work_fragment, container, false);

        initContainer();

        return view;
    }

    private void setDate() {

        mDate = Calendar.getInstance();

        Intent intent = getArguments().getParcelable(Constants.DATE_LONG);

        if (intent == null) return;
        Long dLong =  intent.getLongExtra(Constants.DATE_LONG, 0);

        if (dLong == 0) return;
        mDate.setTimeInMillis(dLong);

    }

    private LinearLayout container;
    private void initContainer() {

        container = (LinearLayout) view.findViewById(R.id.container);

        ArrayList<VisitCell> visitCells = initVisitCells();
        ArrayList<WorkView> workViews = initWorkViews();

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

    private ArrayList<VisitCell> initVisitCells() {

        ArrayList<VisitCell> visitCells = new ArrayList<>();
        ArrayList<Visit> visitsOutOfWork = RVData.getInstance().visitList.getVisitsInDayOutOfWork(mDate);

        for (Visit visit : visitsOutOfWork) {

            visitCells.add(new VisitCell(visit, getActivity(), BaseAnimateView.InitialHeightCondition.VIEW_HEIGHT){
                @Override
                public void onLongClick(Visit visit) {
                    startRecordVisitForEdit(visit);
                }
            });
        }

        return visitCells;
    }

    private ArrayList<WorkView> initWorkViews() {

        ArrayList<WorkView> workViews = new ArrayList<>();
        ArrayList<Work> works = RVData.getInstance().workList.getWorksOfDay(mDate);

        for (Work work : works) {

            workViews.add(instantiateWorkView(work, BaseAnimateView.InitialHeightCondition.VIEW_HEIGHT));
        }
        return workViews;
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

    private void insertVisitCellAndInflate(Visit visit) {

        VisitCell visitCell = new VisitCell(visit, getActivity(), BaseAnimateView.InitialHeightCondition.FROM_0){
            @Override
            public void onLongClick(Visit visit) {
                startRecordVisitForEdit(visit);
            }
        };

        //WorkViewまたはcontainerの適切なほうに挿入する
        WorkView workView = getWorkViewOfVisit(visit);
        if (workView != null) {
            workView.addVisitCell(visit);
        } else {
            container.addView(visitCell, getInsertPosition(visit.getStart()));
        }
    }

    private void addVisitCells(ArrayList<Visit> visits) {

        for (Visit visit : visits) {
            insertVisitCellAndInflate(visit);
        }
    }

    private void startRecordVisitForEdit(Visit visit) {

        Intent editVisitIntent = new Intent(getActivity(), RecordVisitActivity.class);
        editVisitIntent.setAction(Constants.RecordVisitActions.EDIT_VISIT_ACTION);
        editVisitIntent.putExtra(Visit.VISIT, visit.getId());

        startActivityForResult(editVisitIntent, Constants.RecordVisitActions.EDIT_VISIT_REQUEST_CODE);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == Constants.RecordVisitActions.EDIT_VISIT_REQUEST_CODE) {

            if (resultCode == Constants.RecordVisitActions.DELETE_VISIT_RESULT_CODE) {
                // VisitCellが削除されたとき

                String visitId = data.getStringExtra(Visit.VISIT);
                if (visitId == null) return;

                final VisitCell visitCell = getVisitCell(visitId, true);
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
                                verifyItemRemains();
                            }

                            @Override
                            public void onAnimationCancel(Animator animator) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animator) {

                            }
                        }, 5);
            } else if (resultCode == Constants.RecordVisitActions.VISIT_CHANGED_RESULT_CODE) {
                // VisitCellの内容が変更されたとき

                String visitId = data.getStringExtra(Visit.VISIT);
                if (visitId == null) return;

                Visit visit = RVData.getInstance().visitList.getById(visitId);
                if (visit == null) return;

                VisitCell visitCell = getVisitCell(visitId, true);
                if (visitCell == null) return;

                visitCell.updataData(visit);
                // VisitCellの時間を変化させたときに適正なポジションにあるかどうかをVerifyする必要あり
                // 必要なら位置を変更する
                moveVisitCellIfNeeded(visitCell);

            }

        } else if (requestCode == Constants.RecordVisitActions.NEW_VISIT_REQUEST_CODE) {

            if (resultCode == Constants.RecordVisitActions.VISIT_ADDED_RESULT_CODE) {

                // 追加されたのはWorkかVisitか
                String workId = data.getStringExtra(Work.WORK);
                if (workId != null) {
                    Work work = RVData.getInstance().workList.getById(workId);
                    if (work != null) {
                        addWorkViewAndInflate(work);
                    }
                }

                String visitId = data.getStringExtra(Visit.VISIT);
                if (visitId != null) {
                    Visit visit = RVData.getInstance().visitList.getById(visitId);
                    if (visit != null) {
                        insertVisitCellAndInflate(visit);
                    }
                }
            }
        }
    }

    private VisitCell getVisitCell(String visitId, boolean fromDeep) {

        for ( int i = 0 ; i < container.getChildCount() ; i++ ) {

            View view = container.getChildAt(i);

            if (view instanceof VisitCell) {

                VisitCell visitCell = (VisitCell) view;
                if (visitCell.getVisit().getId().equals(visitId)) {
                    return visitCell;
                }
            } else if (view instanceof WorkView) {

                if (fromDeep){

                    WorkView workView = (WorkView) view;
                    VisitCell visitCell = workView.getVisitCell(visitId);

                    if (visitCell != null) {
                        return visitCell;
                    }
                }
            }
        }
        return null;
    }

    private WorkView getWorkView(String workId) {

        for ( int i = 0 ; i < container.getChildCount() ; i++ ) {

            View view = container.getChildAt(i);

            if (view instanceof WorkView) {

                WorkView workView = (WorkView) view;

                if (workView.getWork().getId().equals(workId)) {
                    return workView;
                }
            }
        }
        return null;
    }

    private void moveVisitCellIfNeeded(final VisitCell visitCell) {
        // 必要ならVisitCellの位置を変更する
        // このVisitCellをふくむべきWorkViewは存在するか
        if (getWorkViewOfVisit(visitCell.getVisit()) != null) {
            // 存在した
            WorkView workView = getWorkViewOfVisit(visitCell.getVisit());
            workView.addVisitCell(visitCell.getVisit());

            visitCell.changeViewHeight(BaseAnimateView.AnimateCondition.FROM_HEIGHT_TO_O,
                    true,
                    new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {

                            ViewParent parent = visitCell.getParent();
                            LinearLayout linearLayout = (LinearLayout) parent;
                            linearLayout.removeView(visitCell);
                            verifyItemRemains();

                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {

                        }
                    }, 5);

        } else {
            // このVisitCellを含むべきWorkViewは存在しなかった
            insertVisitCellAndInflate(visitCell.getVisit());

            visitCell.changeViewHeight(BaseAnimateView.AnimateCondition.FROM_HEIGHT_TO_O,
                    true,
                    new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {

                            ViewParent parent = visitCell.getParent();
                            LinearLayout linearLayout = (LinearLayout) parent;
                            linearLayout.removeView(visitCell);
                            verifyItemRemains();
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {

                        }
                    }, 5);
        }
    }

    /**
     *
     * @param visit
     * @return もしそのVisitを含むべきWorkViewがあれば返す。なければNULL
     */
    @Nullable
    private WorkView getWorkViewOfVisit(Visit visit) {

        // 現状containerにあるWorkViewsを検証する

        for ( int i = 0 ; i < container.getChildCount() ; i++ ) {

            View view = container.getChildAt(i);
            if (view instanceof WorkView) {

                WorkView workView = (WorkView) view;
                Work work = workView.getWork();

                if (work.isVisitInWork(visit)) {
                    return workView;
                }
            }
        }
        return null;
    }

    private void removeWorkViews(ArrayList<Work> works) {

        for (Work work : works) {

            WorkView workView = getWorkView(work.getId());

            if (workView != null) {
                workView.compressAndRemove();
            }

        }
    }

    private void removeVisitCells(ArrayList<Visit> visits) {

        for (Visit visit : visits) {
            removeVisitCell(visit);
        }
    }

    private void removeVisitCell(Visit visit) {

        final VisitCell visitCell = getVisitCell(visit.getId(), false);

        if (visitCell == null) return;

        visitCell.changeViewHeight(BaseAnimateView.AnimateCondition.FROM_HEIGHT_TO_O,
                true,
                new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        ViewParent parent = visitCell.getParent();
                        LinearLayout linearLayout = (LinearLayout) parent;
                        linearLayout.removeView(visitCell);
                        verifyItemRemains();

                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                }, 5);

    }

    public Calendar getDate() {
        return mDate;
    }


    private WorkView instantiateWorkView(Work work, BaseAnimateView.InitialHeightCondition condition) {

        return new WorkView(work, getActivity(), condition) {
            @Override
            public void postCompress(WorkView workView, ArrayList<Visit> visitsExpelled) {

                container.removeView(workView);
                addVisitCells(visitsExpelled);
                verifyItemRemains();
            }

            @Override
            public void onVisitCellLongClick(Visit visit) {

                startRecordVisitForEdit(visit);
            }

            @Override
            public void onTimeChange(WorkView workView, VisitList.VisitsMoved visitsMoved, ArrayList<Work> worksRemoved) {

                removeWorkViews(worksRemoved);
                addVisitCells(visitsMoved.visitsExpelled);
                removeVisitCells(visitsMoved.visitsSwallowed);
            }
        };
    }

    public void addWorkViewAndInflate(Work work) {

        int pos = getInsertPosition(work.getStart());
        container.addView(instantiateWorkView(work, BaseAnimateView.InitialHeightCondition.FROM_0), pos);

    }

    private void verifyItemRemains() {

        if (container.getChildCount() <= 0) {
            mOnAllItemRemoveListener.onAllItemRemoved(mDate);
        }

    }

    public interface OnAllItemRemoveListener {
        void onAllItemRemoved(Calendar date);
    }

}
