package net.c_kogyo.returnvisitor.data;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by sayjey on 2015/06/18.
 */
public class Person extends BaseDataItem implements Cloneable{

    public static final String PERSON       = "person";

    public static final String SEX      = "sex";
    public static final String AGE      = "age";
    public static final String INTEREST = "interest";
    public static final String TAG_IDS  = "tag_ids";
    public static final String PLACE_IDS  = "place_ids";

    public enum Sex {
        SEX_UNKNOWN(0),
        MALE(1),
        FEMALE(2);

        private final int num;

        Sex(int num) {
            this.num = num;
        }

        public static Sex getEnum (int num) {

            Sex[] enumArray = Sex.values();

            for (Sex sex : enumArray) {

                if (sex.num() == num) return sex;

            }

            return null;
        }

        public int num(){
            return num;
        }
    }

    public enum Age {

        AGE_UNKNOWN(0),
        AGE__10(1),
        AGE_11_20(2),
        AGE_21_30(3),
        AGE_31_40(4),
        AGE_41_50(5),
        AGE_51_60(6),
        AGE_61_70(7),
        AGE_71_(8);

        final int num;

        Age(int num) {
            this.num = num;
        }

        public static Age getEnum(int num) {

            Age[] enumArray = Age.values();

            for (Age age : enumArray) {

                if (age.num() == num) return age;

            }

            return null;
        }

        public int num() {return num;}

    }

    public enum Interest {

        NONE(0),
        REFUSED(1),
        INDIFFERENT(2),
        FAIR(3),
        KIND(4),
        INTERESTED(5),
        STRONGLY_INTERESTED(6);

        final int num;

        Interest(int num) {
            this.num = num;
        }

        public static Interest getEnum(int num) {

            Interest[] enumArray = Interest.values();

            for (Interest interest : enumArray) {

                if (interest.num() == num) return interest;

            }

            return null;
        }

        public int num() {return num;}

    }

    private Sex sex;
    private Age age;
    private Interest interest;

    // タグは個人につけるもの
    private ArrayList<String> tagIds;

    private ArrayList<String> placeId;

    public Person() {
        initCommon();
    }

    private void initCommon() {
        this.sex = Sex.SEX_UNKNOWN;
        this.age = Age.AGE_UNKNOWN;
        this.interest = Interest.NONE;
        this.tagIds = new ArrayList<>();
        this.placeId= new ArrayList<>();
    }

//    public Person(JSONObject object) {
//        super(object);
//        initCommon();
//
//        try {
//            if (object.has(SEX))            this.sex         = Sex.valueOf(object.get(SEX).toString());
//            if (object.has(AGE))            this.age         = Age.valueOf(object.get(AGE).toString());
//            if (object.has(INTEREST))       this.interest    = Interest.valueOf(object.get(INTEREST).toString());
//
//            if (object.has(TAG_IDS)) {
//                this.tagIds = new ArrayList<>();
//                JSONArray array = object.getJSONArray(TAG_IDS);
//                for ( int i = 0 ; i < array.length() ; i++ ) {
//                    this.tagIds.add(array.getString(i));
//                }
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

    public Sex getSex() {
        return sex;
    }

    public Age getAge() {
        return age;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public void setAge(Age age) {
        this.age = age;
    }

    public Interest getInterest() {
        return interest;
    }

    public void setInterest(Interest interest) {
        this.interest = interest;
    }

    public ArrayList<String> getTagIds() {
        return tagIds;
    }

    public void setTagIds(ArrayList<String> tagIds) {
        this.tagIds = tagIds;
    }

    public void addTagIds(String tagId) {
        this.tagIds.add(tagId);
    }

    public void removeTagId(String tagId) {
        this.tagIds.remove(tagId);
    }



    @Override
    public String getIdHeader() {
        return PERSON;
    }

    //    public String getSexString(Context context) {
//
//        String[] sexStringArray = context.getResources().getStringArray(R.array.sex_array);
//        return sexStringArray[sex.num()];
//
//    }

//    public String getAgeString(Context context) {
//
//        String[] ageStringArray = context.getResources().getStringArray(R.array.age_array);
//        return ageStringArray[age.num()];
//
//    }

    @Override
    protected Object clone() throws CloneNotSupportedException {

        Person person = (Person) super.clone();

        person.sex  = this.sex;
        person.age  = this.age;
        person.interest = this.interest;
        person.tagIds = new ArrayList<>(this.tagIds);
        person.placeId = new ArrayList<>(this.placeId);

        return person;
    }

//    @Override
//    public JSONObject getJSONObject() {
//
//        JSONObject object = super.getJSONObject();
//
//        try {
//            object.put(SEX, sex);
//            object.put(AGE, age);
//            object.put(INTEREST, interest);
//
//            JSONArray array = new JSONArray();
//            for ( int i = 0 ; i < this.tagIds.size() ; i++ ) {
//                array.put(this.tagIds.get(i));
//            }
//            object.put(TAG_IDS, array);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        return object;
//    }

    @Override
    public String toStringForSearch(Context context) {
        return null;
    }

    @Override
    public HashMap<String, Object> toMap() {

        HashMap<String, Object> map = super.toMap();

        map.put(SEX, sex);
        map.put(AGE, age);
        map.put(INTEREST, interest);

        map.put(TAG_IDS, tagIds);
        map.put(PLACE_IDS, placeId);

        return map;
    }
}
