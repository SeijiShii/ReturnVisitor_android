package net.c_kogyo.returnvisitor;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by sayjey on 2015/06/18.
 */
public class Person extends BaseDataItem implements Cloneable{

    public static final String HOUSEHOLDER_ID       = "householder_id";

    public static final String NAME     = "name";
    public static final String SEX      = "sex";
    public static final String AGE      = "age";
    public static final String NOTE     = "note";
    public static final String INTEREST = "interest";

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

    private String name;
    private Sex sex;
    private Age age;
    private String note;
    private Interest interest;

    public Person() {
        initCommon();
    }

    private void initCommon() {
        this.name = "";
        this.sex = Sex.SEX_UNKNOWN;
        this.age = Age.AGE_UNKNOWN;
        this.interest = Interest.NONE;
        this.note = "";
    }

    public Person(JSONObject object) {
        super(object);
        initCommon();

        try {
            if (object.has(NAME))           this.name        = object.getString(NAME);
            if (object.has(SEX))            this.sex         = Sex.valueOf(object.get(SEX).toString());
            if (object.has(AGE))            this.age         = Age.valueOf(object.get(AGE).toString());
            if (object.has(INTEREST))       this.interest    = Interest.valueOf(object.get(INTEREST).toString());
            if (object.has(NOTE))           this.note        = object.getString(NOTE);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public Sex getSex() {
        return sex;
    }

    public Age getAge() {
        return age;
    }

    public String getNote() {
        return note;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public void setAge(Age age) {
        this.age = age;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public Interest getInterest() {
        return interest;
    }

    public void setInterest(Interest interest) {
        this.interest = interest;
    }

    @Override
    public String getIdHeader() {
        return HOUSEHOLDER_ID;
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

        person.name = this.name;
        person.sex  = this.sex;
        person.age  = this.age;
        person.note = this.note;
        person.interest = this.interest;

        return person;
    }

    @Override
    public JSONObject getJSONObject() {

        JSONObject object = super.getJSONObject();

        try {
            object.put(NAME, name);
            object.put(SEX, sex);
            object.put(AGE, age);
            object.put(INTEREST, interest);
            object.put(NOTE, note);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object;
    }

    @Override
    public String toStringForSearch(Context context) {
        return null;
    }


}
