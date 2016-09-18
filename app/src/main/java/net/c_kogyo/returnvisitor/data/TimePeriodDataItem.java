package net.c_kogyo.returnvisitor.data;

import android.app.TimePickerDialog;
import android.content.Context;

import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by 56255 on 2016/07/19.
 */
public class TimePeriodDataItem extends BaseDataItem{

    public static final String START = "start";
    public static final String END = "end";

    protected Calendar start;
    protected Calendar end;

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

    @Override
    public void setMap(HashMap<String, Object> map) {
        super.setMap(map);

        this.start = Calendar.getInstance();
        this.start.setTimeInMillis(Long.valueOf(map.get(START).toString()));

        this.end = Calendar.getInstance();
        this.end.setTimeInMillis(Long.valueOf(map.get(END).toString()));
    }

    public long getDuration() {

        return end.getTimeInMillis() - start.getTimeInMillis();

    }

    @Override
    protected Object clone() throws CloneNotSupportedException {

        TimePeriodDataItem item = (TimePeriodDataItem) super.clone();

        item.start = (Calendar) this.start.clone();
        item.end = (Calendar) this.end.clone();

        return item;
    }
}
