package net.c_kogyo.returnvisitor.data;

import android.content.Context;
import android.support.v4.util.Pair;

import net.c_kogyo.returnvisitor.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by 56255 on 2016/07/19.
 */
public class Placement extends BaseDataItem {

    public enum Category {

        BIBLE(0),
        BOOK(1),
        TRACT(2),
        MAGAZINE(3),
        WEB_LINK(4),
        SHOW_VIDEO(5),
        OTHER(6);

        private final int num;

        Category(int num) {
            this.num = num;
        }

        public static Category getEnum (int num) {

            Category[] enumArray = Category.values();

            for (Category category : enumArray) {

                if (category.num() == num) return category;

            }
            return null;
        }
        public int num(){
            return num;
        }
    }

    public enum MagazineCategory {

        WATCHTOWER(0),
        STUDY_WATCHTOWER(1),
        AWAKE(2);

        private final int num;

        MagazineCategory(int num) {
            this.num = num;
        }

        public static MagazineCategory getEnum(int num) {

            MagazineCategory[] enumArray = MagazineCategory.values();

            for (MagazineCategory magCategory : enumArray) {

                if (magCategory.num() == num) return magCategory;
            }
            return null;
        }

        public int num(){
            return num;
        }
    }

    public static final String PLACEMENT = "Placement";

    public static final String CATEGORY = "category";
    public static final String MAGAZINE_CATEGORY = "magazine_category";
    public static final String NUMBER = "number";

    private Category category;
    private MagazineCategory magCategory;
    private Calendar number;

    public Placement() {
        super();

        this.category = Category.OTHER;
        this.magCategory = MagazineCategory.WATCHTOWER;
        this.number = Calendar.getInstance();

    }

    public Placement(Category category) {

        this();

        this.category = category;
    }

    @Override
    public String getIdHeader() {
        return PLACEMENT;
    }

    @Override
    public String toStringForSearch(Context context) {
        return null;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public MagazineCategory getMagCategory() {
        return magCategory;
    }

    public void setMagCategory(MagazineCategory magCategory) {
        this.magCategory = magCategory;
    }

    /**
     *
     * @return int number [1 - 6] if Category == MAGAZINE except STUDY
     * else return 0
     */
    public Calendar getNumber() {

        return number;

    }

    @Override
    public HashMap<String, Object> toMap() {

        HashMap<String, Object> map = super.toMap();

        map.put(CATEGORY, category);
        map.put(MAGAZINE_CATEGORY, magCategory);
        map.put(NUMBER, number.getTimeInMillis());

        return map;
  }


    public void setNumber(Calendar number) {
        this.number = number;
    }

    public static String getNumberString(Calendar number, MagazineCategory magCategory, Context context) {

        String magNumString;

        SimpleDateFormat yFormat = new SimpleDateFormat("yyyy");
        String yearString = yFormat.format(number.getTime());

        if (magCategory == MagazineCategory.STUDY_WATCHTOWER) {

            SimpleDateFormat mFormat = new SimpleDateFormat("MMMM");
            String monthString = mFormat.format(number.getTime());

            magNumString = context.getString(R.string.magazine_number_month, monthString, yearString);

        } else {

            String numString = String.valueOf((number.get(Calendar.MONTH) + 1 )/ 2);

            magNumString = context.getString(R.string.magazine_number_number, numString, yearString);
        }
        return magNumString;

    }

    static public ArrayList<Pair<Calendar, String>> getMagazineNumberArrayList(Placement.MagazineCategory magCategory, Context context) {

        ArrayList<Pair<Calendar, String>> list = new ArrayList<>();
        Calendar numberCounter = Calendar.getInstance();
        numberCounter.add(Calendar.MONTH, -11);

        if (magCategory == MagazineCategory.STUDY_WATCHTOWER) {
            // 現在月が12(#11)番目（1年前まで指定可能）　現在月より3つ先まで表示

            for ( int i = 0; i < 15 ; i++ ) {

                Calendar clonedNumber = (Calendar) numberCounter.clone();
                String str = getNumberString(numberCounter, magCategory, context);
                Pair<Calendar, String> pair = new Pair<>(clonedNumber, str);
                list.add(pair);


                numberCounter.add(Calendar.MONTH, 1);
            }

        } else {
            // 現在月が6(#5)番目（1年前まで指定可能）　現在月より3つ先まで表示

            for ( int i = 0; i < 9 ; i++ ) {

                Calendar clonedNumber = (Calendar) numberCounter.clone();
                String str = getNumberString(numberCounter, magCategory, context);
                Pair<Calendar, String> pair = new Pair<>(clonedNumber, str);
                list.add(pair);

                numberCounter.add(Calendar.MONTH, 2);
            }

        }

        return list;
    }
}
