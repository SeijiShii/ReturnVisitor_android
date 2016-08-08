package net.c_kogyo.returnvisitor.data;

import android.content.Context;
import android.support.annotation.NonNull;

import net.c_kogyo.returnvisitor.R;

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

    public Tag(String tagName, boolean isMutated) {

        super();
        this.isMutated = isMutated;
        this.name = tagName;
    }

    public Tag() {
        super();
    }

    public Tag(HashMap<String, Object> map) {

        super();
        setMap(map);
    }


    // Mutateされると削除できなくなります
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
//    protected Object clone() throws CloneNotSupportedException {
//        return super.clone();
//    }

    @Override
    public HashMap<String, Object> toMap() {

        HashMap<String, Object> map = super.toMap();

        map.put(IS_MUTATED, this.isMutated);

        return map;
    }

    @Override
    public void setMap(@NonNull HashMap<String, Object> map) {
        super.setMap(map);

        this.isMutated = Boolean.valueOf(map.get(IS_MUTATED).toString());
    }

    public void setMutated(boolean mutated) {
        isMutated = mutated;
    }

    public boolean isRVTag(Context context) {

        String rvString = context.getResources().getStringArray(R.array.tag_item_array)[1];

        return this.name.equals(rvString);
    }

    public boolean isBibleStudyTag(Context context) {

        String bsString = context.getResources().getStringArray(R.array.tag_item_array)[0];

        return this.name.equals(bsString);

    }

    public boolean isMRTag(Context context) {

        String mrString = context.getResources().getStringArray(R.array.tag_item_array)[2];

        return this.name.equals(mrString);

    }
}
