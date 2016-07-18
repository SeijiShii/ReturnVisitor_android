package net.c_kogyo.returnvisitor;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


/**
 * Created by sayjey on 2015/07/17.
 */
public abstract class BaseDataItem  implements Cloneable{

    public static final String TIME_STAMP   = "time_stamp";
    public static final String TIME_STAMP_STRING = "time_stamp_string";

    protected String id;
    protected Calendar timeStamp;

    BaseDataItem(){

        this.timeStamp = Calendar.getInstance();
        this.id = generateNewId();
    }

    BaseDataItem(JSONObject object) {

        this();

        try {
            if (object.has(getIdHeader())) {
                this.id = object.getString(getIdHeader());
            } else {
                this.id = "";
            }

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

    @Override
    protected Object clone() throws CloneNotSupportedException {

        BaseDataItem item = (BaseDataItem) super.clone();

        item.id = this.id;
        item.timeStamp = (Calendar) this.timeStamp.clone();

        return item;
    }

    public JSONObject getJSONObject() {

        JSONObject object = new JSONObject();

        try {
            object.put(getIdHeader(), this.id);
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

    abstract public Person.Interest getInterest();

    abstract public String toStringForSearch(Context context);

    public <T extends BaseDataItem>boolean equals(T o) {
        return this.getId().equals(o.getId());
    }
}
