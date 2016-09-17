package net.c_kogyo.returnvisitor.data;

import net.c_kogyo.returnvisitor.service.TimeCountService;

import java.util.Calendar;

/**
 * Created by SeijiShii on 2016/08/14.
 */

public class Work extends TimePeriodDataItem {

    public static final String WORK = "work";

    public Work(Calendar startTime) {
        super();

        this.start = (Calendar) startTime.clone();
    }

    public Work(){
        super();
    }

    @Override
    public String getIdHeader() {
        return WORK;
    }

    public boolean isTimeCounting() {

        Work workingWork = TimeCountService.getWork();
        if (workingWork == null) {
            return false;
        }

        if (this.getId().equals(workingWork.getId())) {
            return TimeCountService.isTimeCounting();
        }
        return false;
    }

    public boolean isVisitInWork(Visit visit) {

        return this.getStart().before(visit.getStart()) && this.getEnd().after(visit.getStart());

    }
}
