package net.c_kogyo.returnvisitor.data;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by SeijiShii on 2016/09/24.
 */

public class AggregationOfDay {

    private long time;
    private int placementCount;
    private int showVideoCount;
    private int rvCount;

    public AggregationOfDay(Calendar date) {

        ArrayList<Work> worksOfDay = RVData.getInstance().workList.getWorksOfDay(date);
        time = 0;
        for (Work work : worksOfDay) {
            time += work.getDuration();
        }


        ArrayList<Visit> visitsOfDay = RVData.getInstance().visitList.getVisitsOfDay(date);
        placementCount = 0;
        showVideoCount = 0;
        rvCount = 0;

        for (Visit visit : visitsOfDay) {

            placementCount += visit.getPlacementCount();
            showVideoCount += visit.getShowVideoCount();
            rvCount += visit.getRvCount();
        }

    }

    public long getTime() {
        return time;
    }

    public int getPlacementCount() {
        return placementCount;
    }

    public int getShowVideoCount() {
        return showVideoCount;
    }

    public int getRvCount() {
        return rvCount;
    }

}
