package net.c_kogyo.returnvisitor.data;

import android.content.Context;

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
        this.magCategory = null;
        this.number = null;

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
     * @return Calendar only if Category = MAGAZINE & MagazineCategory = STUDY_WATCHTOWER
     * else return NULL
     */
    public Calendar getNumberCalendar() {

        if (this.category == Category.MAGAZINE && this.magCategory == MagazineCategory.STUDY_WATCHTOWER)
            return number;
        return null;
    }

    /**
     *
     * @return int number [1 - 6] if Category == MAGAZINE except STUDY
     * else return 0
     */
    public int getNumber() {

        if (this.category != Category.MAGAZINE) return 0;

        if (this.magCategory == MagazineCategory.STUDY_WATCHTOWER) return 0;

        int month = number.get(Calendar.MONTH);

        return (month + 1) / 2;

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
}
