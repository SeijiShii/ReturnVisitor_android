package net.c_kogyo.returnvisitor.data;

/**
 * Created by SeijiShii on 2016/07/24.
 */
public class RVData {
    private static RVData ourInstance = new RVData();

    public static RVData getInstance() {
        return ourInstance;
    }

    public static DataList<Place> placeList;
    public static DataList<Person> personList;
    public static DataList<Visit> visitList;

    private RVData() {

        placeList = new DataList<Place>(Place.class){
            @Override
            public void onDataChanged(Place data) {

            }
        };
        personList = new DataList<Person>(Person.class) {
            @Override
            public void onDataChanged(Person data) {

            }
        };
        visitList = new DataList<Visit>(Visit.class) {
            @Override
            public void onDataChanged(Visit data) {

            }
        };
    }

    public interface OnDataChangeListener {
        public abstract void onDataChange();
    }
}
