package net.c_kogyo.returnvisitor.data;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by 56255 on 2016/07/19.
 */
public class Visit extends TimePeriodDataItem{

    public static final String VISIT = "visit";
    public static final String PLACE_ID = "place_id";
    public static final String PERSON_IDS = "person_ids";
    public static final String PLACEMENTS = "placements";
    public static final String RV_COUNT = "rv_count";

    private String placeId;
    private ArrayList<String> personIds;
    private ArrayList<Placement> placements;
    private int rvCount;

    public Visit() {
        super();

        this.placeId = null;
        this.personIds = new ArrayList<>();
        this.placements = new ArrayList<>();
        this.rvCount = 0;
    }

    @Override
    public String getIdHeader() {
        return VISIT;
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

        //TODO オブジェクトのArrayListをそのままマップできるわけがないよね。

        ArrayList<HashMap<String, Object>> mapList = new ArrayList<>();
        for ( int i = 0 ; i < placements.size() ; i++ ) {
            mapList.add(placements.get(i).toMap());
        }

        map.put(PLACEMENTS, mapList);

        return map;
    }

    public void addPlacement(Placement placement) {
        placements.add(placement);
    }

    public void removePlacement(Placement placement) {
        placements.remove(placement);
    }

    public int getRvCount() {
        return rvCount;
    }

    public void setRvCount(int rvCount) {
        this.rvCount = rvCount;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }
}
