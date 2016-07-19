package net.c_kogyo.returnvisitor.data;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by SeijiShii on 2016/07/17.
 */

public class Place extends BaseDataItem {

    public static final String PLACE = "place";

    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String ADDRESS = "address";

    private LatLng latLng;
    private String address;

    public Place(LatLng latLng) {
        super();
        initCommon();

        this.latLng = latLng;
    }

//    public Place(JSONObject object) {
//        super(object);
//        initCommon();
//
//        try {
//            if (object.has(LATITUDE) && object.has(LONGITUDE)) {
//                double lat = object.getDouble(LATITUDE);
//                double lng = object.getDouble(LONGITUDE);
//                this.latLng = new LatLng(lat, lng);
//            }
//
//            if (object.has(ADDRESS)) this.address = object.getString(ADDRESS);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

    private void initCommon() {
        this.latLng = new LatLng(0, 0);
        this.address = "";
    }

    @Override
    public String getIdHeader() {
        return PLACE;
    }

    public Person.Interest getInterest() {
        return null;
    }

    @Override
    public String toStringForSearch(Context context) {
        return null;
    }

//    @Override
//    public JSONObject getJSONObject() {
//
//        JSONObject object = super.getJSONObject();
//
//        try {
//            object.put(LATITUDE, latLng.latitude);
//            object.put(LONGITUDE, latLng.longitude);
//            object.put(ADDRESS, address);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        return object;
//    }

    @Override
    public HashMap<String, Object> toMap() {

        HashMap<String, Object> map = super.toMap();

        map.put(LATITUDE, latLng.latitude);
        map.put(LONGITUDE, latLng.longitude);
        map.put(ADDRESS, address);

        return map;
    }
}
