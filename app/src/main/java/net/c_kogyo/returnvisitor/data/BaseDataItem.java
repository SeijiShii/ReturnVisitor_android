package net.c_kogyo.returnvisitor.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


/**
 * Created by sayjey on 2015/07/17.
 */
public abstract class BaseDataItem  implements Cloneable{

    public static final String ID           = "id";
    public static final String NAME         = "name";
    public static final String NOTE         = "note";
    public static final String TIME_STAMP   = "time_stamp";
    public static final String TIME_STAMP_STRING = "time_stamp_string";

    protected String id;
    protected String name;
    protected String note;

    protected Calendar timeStamp;

    BaseDataItem(){

        this.timeStamp = Calendar.getInstance();
        this.id = generateNewId();
        this.name = "";
        this.note = "";
    }

    BaseDataItem(JSONObject object) {

        this();

        try {
            if (object.has(ID)) {
                this.id = object.getString(ID);
            } else {
                this.id = "";
            }

            if (object.has(NAME)) this.name = object.getString(NAME);
            if (object.has(NOTE)) this.note = object.getString(NOTE);

            if (object.has(TIME_STAMP)) {
                this.timeStamp = Calendar.getInstance();
                this.timeStamp.setTimeInMillis(object.getLong(TIME_STAMP));
            } else {
                this.timeStamp = Calendar.getInstance();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * カレンダーのミリ秒を文字列にした末尾に1000までの乱数を加えて初期値を生成する
     * @return String id
     */
    private String generateNewId() {

        long milNum = Calendar.getInstance().getTimeInMillis();
        String sMilNum = String.valueOf(milNum);

        int ranNum = (int)(Math.random() * 10000);
        String sRanNum = String.valueOf(ranNum);

        String mId = sMilNum + sRanNum;

        return getIdHeader() + "_" + mId;
    }

    public String getId() {
        return id;
    }

    public Calendar getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp() {
        timeStamp = Calendar.getInstance();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {

        BaseDataItem item = (BaseDataItem) super.clone();

        item.id = this.id;
        item.name = this.name;
        item.note = this.note;
        item.timeStamp = (Calendar) this.timeStamp.clone();

        return item;
    }

    public JSONObject getJSONObject() {

        JSONObject object = new JSONObject();

        try {
            object.put(ID, this.id);
            object.put(NAME, this.name);
            object.put(NOTE, this.note);
            object.put(TIME_STAMP, this.timeStamp.getTimeInMillis());

            // jsonファイルを目視したときわかりやすいように。読み出しはしない。
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy,MM,dd, E, HH:mm:ss", Locale.JAPAN);
            object.put(TIME_STAMP_STRING, sdf.format(this.timeStamp.getTime()));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    public abstract String getIdHeader();

//    static public boolean isDataType(String idHeader, PendingData data) {
//
//        int idHeaderCount = idHeader.length();
//        String dataId = data.getId().substring(0, idHeaderCount - 1);
//
//        return (dataId.equals(idHeader));
//
//    }

    // for Test
//    public BaseDataItem(int idNum, Calendar timeStampCalendar) {
//
//        id = getIdHeader() + String.format("%018d", idNum);
//        timeStamp = (Calendar) timeStampCalendar.clone();
//    }
//
//    /**
//     * If has period, returns 2 sized array [0] beginning, [1] end
//     * If not returns same object in both
//     * @return
//     */
//    abstract public Calendar[] getPeriod();

    abstract public String toStringForSearch(Context context);

    public <T extends BaseDataItem>boolean equals(T o) {
        return this.getId().equals(o.getId());
    }

    public HashMap<String, Object> toMap() {

        HashMap<String, Object> map = new HashMap<>();

        map.put(ID, this.id);
        map.put(NAME, this.name);
        map.put(NOTE, this.note);
        map.put(TIME_STAMP, this.timeStamp.getTimeInMillis());

        // jsonファイルを目視したときわかりやすいように。読み出しはしない。
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy,MM,dd, E, HH:mm:ss", Locale.JAPAN);
        map.put(TIME_STAMP_STRING, sdf.format(this.timeStamp.getTime()));

        return map;
    }
}
