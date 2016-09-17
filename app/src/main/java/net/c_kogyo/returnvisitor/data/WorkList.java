package net.c_kogyo.returnvisitor.data;

import android.support.annotation.Nullable;

import net.c_kogyo.returnvisitor.util.CalendarUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by SeijiShii on 2016/08/14.
 */

public abstract class WorkList extends DataList<Work> {

    public WorkList() {
        super(Work.class);
    }

    public ArrayList<Work> getWorksOfDay(Calendar date) {

        ArrayList<Work> works = new ArrayList<>();

        for (Work work : list) {

            if (CalendarUtil.isSameDay(date, work.start) || CalendarUtil.isSameDay(date, work.end)) {
                works.add(work);
            }
        }

        Collections.sort(works, new Comparator<Work>() {
            @Override
            public int compare(Work work, Work t1) {
                return work.getStart().compareTo(t1.getStart());
            }
        });

        return works;
    }

    @Nullable
    public Work getWorkOfVisit(Visit visit) {

        for (Work work : list) {
            if (work.isVisitInWork(visit)) {
                return work;
            }
        }
        return null;
    }

    //TODO 要素の時間が変更されたとき前後に向かって調整するメソッド

    /**
     *
     * @param work 時間の変更された
     * @return 調整の結果削除されたWorkのリスト
     */
    public ArrayList<Work> onChangeTime(Work work) {

        ArrayList<Work> removedWork = new ArrayList<>();

        // 念のため存在チェック
        if (!list.contains(work)) return removedWork;

        // すべてのリストを開始時間で整列
        Collections.sort(list, new Comparator<Work>() {
            @Override
            public int compare(Work work, Work t1) {
                return work.getStart().compareTo(t1.getStart());
            }
        });

        // 対象の要素のindexを取得
        int index = list.indexOf(work);

        // 過去に向かってさかのぼり
        for ( int i = index - 1 ; i >= 0 ; i-- ) {

            Work work1 = list.get(i);

            if (work.getStart().before(work1.getStart())) {
                removedWork.add(work1);
            } else if (work.getStart().before(work1.getEnd())) {
                work.setStart(work1.getStart());
                removedWork.add(work1);
            } else {
                break;
            }
        }

        // 未来にむかって!!
        for (int i = index + 1 ; i < list.size() ; i++ ) {

            Work work1 = list.get(i);

            if (work.getEnd().after(work1.getEnd())) {
                removedWork.add(work1);
            } else if (work.getEnd().after(work1.getStart())) {
                work.setEnd(work1.getEnd());
                removedWork.add(work1);
            } else {
                break;
            }
        }

        list.removeAll(removedWork);

        return removedWork;

    }
}
