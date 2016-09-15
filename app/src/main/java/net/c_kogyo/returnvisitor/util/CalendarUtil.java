package net.c_kogyo.returnvisitor.util;

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
}
