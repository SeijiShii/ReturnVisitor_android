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
    public static final String PLACEMNETS = "placements";

    private String placeId;
    private ArrayList<String> personIds;
    private ArrayList<Placement> placements;

    public Visit() {
        super();

        this.placeId = null;
        this.personIds = new ArrayList<>();
        this.placements = new ArrayList<>();
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

        map.put(PLACEMNETS, placements);

        return map;
    }

    public void addPlacement(Placement placement) {
        placements.add(placement);
    }

    public void removePlacement(Placement placement) {
        placements.remove(placement);
    }
}
