package net.c_kogyo.returnvisitor.data;

import android.content.Context;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by 56255 on 2016/07/19.
 */
public class Visit extends TimePeriodDataItem{

    public static final String VISIT_ID = "visit_id";
    public static final String PLACE_ID = "place_id";
    public static final String PERSON_IDS = "person_ids";

    private String placeId;
    private ArrayList<String> personIds;

    public Visit() {
        super();

        this.placeId = null;
        this.personIds = new ArrayList<>();
    }

    @Override
    public String getIdHeader() {
        return VISIT_ID;
    }

    @Override
    public String toStringForSearch(Context context) {
        return null;
    }

    @Override
    public HashMap<String, Object> toMap() {

        HashMap<String, Object> map = super.toMap();

        map.put(PLACE_ID, placeId);
        map.put(PERSON_IDS, personIds);

        return map;
    }
}
