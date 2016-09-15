package net.c_kogyo.returnvisitor.data;

import net.c_kogyo.returnvisitor.util.CalendarUtil;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by SeijiShii on 2016/07/28.
 */

public abstract class VisitList extends DataList<Visit> {

    VisitList() {
        super(Visit.class);
    }

    private ArrayList<Visit> getVisitsOfDay(Calendar date) {

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
        return visits;

    }

    public ArrayList<Visit> getVisitsInDayOutOfWork(Calendar date) {

        ArrayList<Visit> visitsOfDayOutOfWork = getVisitsOfDay(date);

        visitsOfDayOutOfWork.removeAll(getVisitsInDayInWork(date));

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

}
