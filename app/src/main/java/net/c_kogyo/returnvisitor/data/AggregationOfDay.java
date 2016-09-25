package net.c_kogyo.returnvisitor.data;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by SeijiShii on 2016/09/24.
 */

public class AggregationOfDay extends AggregationBase{

    private ArrayList<Work> worksOfDay;
    private ArrayList<Visit> visitsOfDay;
    private ArrayList<Visit> bsVisitsOfDay;

    public AggregationOfDay(Calendar date) {

        super();

        bsVisitsOfDay = new ArrayList<>();

        worksOfDay = RVData.getInstance().workList.getWorksOfDay(date);
        for (Work work : worksOfDay) {
            time += work.getDuration();
        }


        visitsOfDay = RVData.getInstance().visitList.getVisitsOfDay(date);
        for (Visit visit : visitsOfDay) {

            placementCount += visit.getPlacementCount();
            showVideoCount += visit.getShowVideoCount();
            rvCount += visit.getRvCount();

            if (visit.isBS()) {
                bsVisitsOfDay.add(visit);
            }
        }

    }

    public boolean hasWorkOrVisit() {
        return worksOfDay.size() > 0 || visitsOfDay.size() > 0;
    }

    public ArrayList<Visit> getBsVisitsOfDay() {
        return bsVisitsOfDay;
    }
}
