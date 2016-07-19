package net.c_kogyo.returnvisitor.data;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by SeijiShii on 2016/07/18.
 */

public class Tag extends BaseDataItem implements Cloneable {

    public static final String TAG = "tag";
    public static final String IS_MUTATED = "is_mutated";

    private boolean isMutated;

    public Tag(boolean isMutated) {

        this.isMutated = isMutated;
    }

//    public Tag(JSONObject object) {
//        super(object);
//
//        try {
//            if (object.has(IS_MUTATED)) this.isMutated = object.getBoolean(IS_MUTATED);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

    public boolean isMutated() {
        return isMutated;
    }

    @Override
    public String getIdHeader() {
        return TAG;
    }

    @Override
    public String toStringForSearch(Context context) {
        return null;
    }

//    @Override
//    public JSONObject getJSONObject() {
//        JSONObject object = super.getJSONObject();
//
//        try {
//            object.put(IS_MUTATED, this.isMutated);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        return object;
//    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public HashMap<String, Object> toMap() {

        HashMap<String, Object> map = super.toMap();

        map.put(IS_MUTATED, this.isMutated);

        return map;
    }
}
