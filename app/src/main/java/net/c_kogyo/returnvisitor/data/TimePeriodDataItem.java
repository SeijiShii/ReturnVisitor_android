package net.c_kogyo.returnvisitor.data;

import android.content.Context;

import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by 56255 on 2016/07/19.
 */
public class TimePeriodDataItem extends BaseDataItem{

    public static final String VISIT_ID = "visit_id";

    public static final String START = "start";
    public static final String END = "end";

    private Calendar start;
    private Calendar end;

    public TimePeriodDataItem() {

        super();

        this.start  = Calendar.getInstance();
        this.end    = Calendar.getInstance();
    }

    @Override
    public HashMap<String, Object> toMap() {

        HashMap<String, Object> map = super.toMap();

        map.put(START,  start.getTimeInMillis());
        map.put(END,    end.getTimeInMillis());

        return map;
    }

    @Override
    public String getIdHeader() {
        return null;
    }

    @Override
    public String toStringForSearch(Context context) {
        return null;
    }

    public Calendar getStart() {
        return start;
    }

    public void setStart(Calendar start) {
        this.start = start;
    }

    public Calendar getEnd() {
        return end;
    }

    public void setEnd(Calendar end) {
        this.end = end;
    }
}
