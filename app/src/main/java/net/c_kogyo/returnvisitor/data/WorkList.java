package net.c_kogyo.returnvisitor.data;

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

}
