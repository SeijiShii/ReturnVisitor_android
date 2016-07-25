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

        placeList = new DataList<>(Place.class);
        personList = new DataList<>(Person.class);
        visitList = new DataList<>(Visit.class);
    }
}
