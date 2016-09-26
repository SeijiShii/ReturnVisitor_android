package net.c_kogyo.returnvisitor.util;

import net.c_kogyo.returnvisitor.data.RVData;

import java.util.Calendar;

/**
 * Created by SeijiShii on 2016/08/24.
 */

public class CalendarUtil {

    public static boolean isSameDay(Calendar calendar0, Calendar calendar1) {

        int year0 = calendar0.get(Calendar.YEAR);
        int month0 = calendar0.get(Calendar.MONTH);
        int day0 = calendar0.get(Calendar.DAY_OF_MONTH);

        int year1 = calendar1.get(Calendar.YEAR);
        int month1 = calendar1.get(Calendar.MONTH);
        int day1 = calendar1.get(Calendar.DAY_OF_MONTH);

        return year0 == year1 && month0 == month1 && day0 == day1;
    }

    public static boolean isSameMonth(Calendar calendar1, Calendar calendar2) {

        int year1 = calendar1.get(Calendar.YEAR);
        int month1 = calendar1.get(Calendar.MONTH);

        int year2 = calendar2.get(Calendar.YEAR);
        int month2 = calendar2.get(Calendar.MONTH);

        return year1 == year2 && month1 == month2;
    }


    public static boolean oneIsBeforeTwo(Calendar month1, Calendar month2) {

        int year1 = month1.get(Calendar.YEAR);
        int mMonth1 = month1.get(Calendar.MONTH);

        int year2 = month2.get(Calendar.YEAR);
        int mMonth2 = month2.get(Calendar.MONTH);

        return (year1 < year2) || ( year1 == year2 && mMonth1 <= mMonth2);
    }
}
