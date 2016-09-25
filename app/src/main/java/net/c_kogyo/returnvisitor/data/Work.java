package net.c_kogyo.returnvisitor.data;

import android.support.annotation.Nullable;

import net.c_kogyo.returnvisitor.service.TimeCountService;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by SeijiShii on 2016/08/14.
 */

public class Work extends TimePeriodDataItem {

    public static final String WORK = "work";

    public Work(Calendar startAndEndTime) {
        super();

        this.start = (Calendar) startAndEndTime.clone();
        this.end = (Calendar) startAndEndTime.clone();
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

    public VisitList.VisitsMoved setTimes(Calendar start, Calendar end) {

        Work workBefore = null;
        try {
            workBefore = (Work) this.clone();
        }catch (CloneNotSupportedException e) {
            //
        }

        super.setStart(start);
        super.setEnd(end);

        RVData.getInstance().workList.addOrSet(this);

        return new VisitList.VisitsMoved( workBefore, this);
    }



    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }


}
