package net.c_kogyo.returnvisitor.data;

import android.support.annotation.Nullable;

import net.c_kogyo.returnvisitor.util.CalendarUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by SeijiShii on 2016/07/28.
 */

public abstract class VisitList extends DataList<Visit> {

    VisitList() {
        super(Visit.class);
    }

    public ArrayList<Visit> getVisitsOfDay(Calendar date) {

        ArrayList<Visit> visits = new ArrayList<>();

        for (Visit visit : list) {

            if (CalendarUtil.isSameDay(date, visit.getStart())) {

                visits.add(visit);
            }
        }
        return visits;
    }

    public ArrayList<Visit> getVisitsInWork(Work work) {

        ArrayList<Visit> visits = new ArrayList<>();

        for (Visit visit : list) {

            if (work.start.before(visit.start) && work.end.after(visit.start)) {

                visits.add(visit);
            }
        }

        Collections.sort(visits, new Comparator<Visit>() {
            @Override
            public int compare(Visit visit, Visit t1) {
                return visit.getStart().compareTo(t1.getStart());
            }
        });
        return visits;

    }

    public ArrayList<Visit> getVisitsInDayOutOfWork(Calendar date) {

        ArrayList<Visit> visitsOfDayOutOfWork = getVisitsOfDay(date);

        visitsOfDayOutOfWork.removeAll(getVisitsInDayInWork(date));

        Collections.sort(visitsOfDayOutOfWork, new Comparator<Visit>() {
            @Override
            public int compare(Visit visit, Visit t1) {
                return visit.getStart().compareTo(t1.getStart());
            }
        });


        return visitsOfDayOutOfWork;
    }

    private ArrayList<Visit> getVisitsInDayInWork(Calendar date) {

        ArrayList<Visit> visitsOfDayInWork = new ArrayList<>();
        ArrayList<Work> worksInDay = RVData.getInstance().workList.getWorksOfDay(date);

        for (Work work : worksInDay) {

            visitsOfDayInWork.addAll(getVisitsInWork(work));

        }
        return visitsOfDayInWork;

    }

    public static class VisitsMoved {

        public ArrayList<Visit> visitsSwallowed;
        public ArrayList<Visit> visitsExpelled;

        public VisitsMoved(@Nullable Work workBefore, Work workAfter) {

            if (workBefore == null) {
                visitsSwallowed = new ArrayList<>();
                visitsExpelled = new ArrayList<>();
            } else {

                try {

                    Work workBefore1 = (Work) workBefore.clone();
                    Work workAfter1 = (Work) workAfter.clone();

                    visitsSwallowed = RVData.getInstance().visitList.getVisitsInWork(workAfter1);
                    visitsSwallowed.removeAll(RVData.getInstance().visitList.getVisitsInWork(workBefore1));

                    visitsExpelled = RVData.getInstance().visitList.getVisitsInWork(workBefore1);
                    visitsExpelled.removeAll(RVData.getInstance().visitList.getVisitsInWork(workAfter1));

                } catch (CloneNotSupportedException e) {

                    visitsSwallowed = new ArrayList<>();
                    visitsExpelled = new ArrayList<>();
                }
            }

        }
    }

    public ArrayList<Calendar> getDates() {

        ArrayList<Calendar> dates = new ArrayList<>();

        for (Visit visit : list) {
            dates.add(visit.start);
        }

        ArrayList<Calendar> datesToRemove = new ArrayList<>();

        for (int i = 0 ; i < dates.size() - 1 ; i++ ) {

            Calendar date0 = dates.get(i);

            for ( int j = i + 1 ; j < dates.size() ; j++ ) {

                Calendar date1 = dates.get(j);

                if (CalendarUtil.isSameDay(date0, date1)) {

                    datesToRemove.add(date1);
                }
            }
        }
        dates.removeAll(datesToRemove);
        return dates;
    }

    public Visit getFirstVisit() {

        ArrayList<Visit> list1  = new ArrayList<>(list);
        Collections.sort(list1, new Comparator<Visit>() {
            @Override
            public int compare(Visit visit, Visit t1) {
                return visit.getStart().compareTo(t1.getStart());
            }
        });
        return list1.get(0);
    }

    public Visit getLastVisit() {

        ArrayList<Visit> list1  = new ArrayList<>(list);
        Collections.sort(list1, new Comparator<Visit>() {
            @Override
            public int compare(Visit visit, Visit t1) {
                return t1.getStart().compareTo(visit.getStart());
            }
        });
        return list1.get(0);
    }
}
