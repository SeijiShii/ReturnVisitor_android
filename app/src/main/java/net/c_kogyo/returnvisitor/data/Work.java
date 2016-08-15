package net.c_kogyo.returnvisitor.data;

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

    @Override
    public String getIdHeader() {
        return WORK;
    }
}
