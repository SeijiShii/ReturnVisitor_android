package net.c_kogyo.returnvisitor.data;

import net.c_kogyo.returnvisitor.util.CalendarUtil;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by SeijiShii on 2016/09/25.
 */

public class AggregationOfMonth extends AggregationBase{

    private Calendar mMonth, thisMonth;
    private ArrayList<AggregationOfDay> days;

    // その月に司会された別個の研究数。
    // 判断基準は「研究」の訪問を何件したか、人の完全重複を削除
    private int bsCount;

    public AggregationOfMonth(Calendar month) {

        thisMonth = (Calendar) month.clone();
        mMonth = (Calendar) month.clone();
        mMonth.set(Calendar.DAY_OF_MONTH,1);

        days = new ArrayList<>();
        bsCount = 0;

        int yearNum = mMonth.get(Calendar.YEAR);
        int monthNum = mMonth.get(Calendar.MONTH);

        while (monthNum == mMonth.get(Calendar.MONTH) && yearNum == mMonth.get(Calendar.YEAR)) {

            AggregationOfDay day = new AggregationOfDay(mMonth);
            if (day.hasWorkOrVisit()) {
                days.add(day);
            }

            mMonth.add(Calendar.DAY_OF_MONTH, 1);
        }

        ArrayList<Visit> bsVisits = new ArrayList<>();
        for (AggregationOfDay day : days) {

            time += day.getTime();
            placementCount += day.getPlacementCount();
            rvCount += day.getRvCount();
            showVideoCount += day.showVideoCount;

            bsVisits.addAll(day.getBsVisitsOfDay());
        }

        ArrayList<Visit> doubledBSVisits = new ArrayList<>();
        for ( int i = 0 ; i < bsVisits.size() - 1; i++ ) {

            Visit visit1 = bsVisits.get(i);

            for ( int j = i + 1 ; j < bsVisits.size() ; j++ ) {

                Visit visit2 = bsVisits.get(j);

                if (visit1.hasSamePersonIds(visit2)) {
                    doubledBSVisits.add(visit2);
                }
            }
        }
        bsVisits.removeAll(doubledBSVisits);
        bsCount = bsVisits.size();

        // 再帰的に過去へさかのぼり余り時間を取得
        addRemainingFromPast();

    }

    public int getBsCount() {
        return bsCount;
    }

    final long hour = 1000 * 60 * 60;
    public long getRemaining() {
        return time - time / hour * hour;
    }

    // TODO 要検証なメソッド
    private void addRemainingFromPast() {

        Calendar firstMonth = RVData.getInstance().getFirstMonth();
        if (CalendarUtil.oneIsBeforeTwo(thisMonth, firstMonth)) return;

        Calendar pastMonth = (Calendar) thisMonth.clone();
        pastMonth.add(Calendar.MONTH, -1);
        AggregationOfMonth aggPastMonth = new AggregationOfMonth(pastMonth);

        time += aggPastMonth.getRemaining();


    }

    public int getHours() {

        return (int) (time / hour);

    }
}
