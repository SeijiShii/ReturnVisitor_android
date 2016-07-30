package net.c_kogyo.returnvisitor.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.support.annotation.NonNull;

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

    public abstract String getIdHeader();

    public String toStringForSearch(Context context){

        return name + " " + note + " ";

    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTimeStamp(Calendar timeStamp) {
        this.timeStamp = timeStamp;
    }

    public <T extends BaseDataItem>boolean equals(T o) {
        return this.getId().equals(o.getId());
    }

    public HashMap<String, Object> toMap() {

        HashMap<String, Object> map = new HashMap<>();

        map.put(ID, this.id);
        map.put(NAME, this.name);
        map.put(NOTE, this.note);
        map.put(TIME_STAMP, this.timeStamp.getTimeInMillis());

        // 目視したときわかりやすいように。読み出しはしない。
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy,MM,dd, E, HH:mm:ss", Locale.JAPAN);
        map.put(TIME_STAMP_STRING, sdf.format(this.timeStamp.getTime()));

        return map;
    }

    public void setMap(@NonNull HashMap<String, Object> map){

        this.id = map.get(ID).toString();
        this.name = map.get(NAME).toString();
        this.note = map.get(NOTE).toString();
        this.timeStamp = Calendar.getInstance();
        this.timeStamp.setTimeInMillis(Long.valueOf(map.get(TIME_STAMP).toString()));
    }

}
