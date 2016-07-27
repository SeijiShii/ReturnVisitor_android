package net.c_kogyo.returnvisitor.data;

/**
 * Created by SeijiShii on 2016/07/24.
 */
public class RVData {
    private static RVData ourInstance = new RVData();
    private static OnDataChangedListener mOnDataChangedListener;
    private static OnDataReadyListener mOnDataReadyListener;

    public static void init(OnDataReadyListener onDataReadyListener, OnDataChangedListener onDataChangedListener) {
        if (onDataReadyListener != null) {
            mOnDataReadyListener = onDataReadyListener;
        }

        if (onDataChangedListener != null){
            mOnDataChangedListener = onDataChangedListener;
        }
    }

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

                if ( mOnDataChangedListener != null ) {
                    mOnDataChangedListener.onDataChanged(Place.class);
                }


            }

        };
        personList = new DataList<Person>(Person.class) {
            @Override
            public void onDataChanged(Person data) {

                if ( mOnDataChangedListener != null ) {
                    mOnDataChangedListener.onDataChanged(Person.class);
                }
            }
        };
        visitList = new DataList<Visit>(Visit.class) {
            @Override
            public void onDataChanged(Visit data) {

                if ( mOnDataChangedListener != null ) {
                    mOnDataChangedListener.onDataChanged(Visit.class);
                }
            }
        };

        if (mOnDataReadyListener != null) {
            mOnDataReadyListener.onDataReady();
        }
    }

    public interface OnDataChangedListener {
        void onDataChanged(Class clazz);
    }

    public interface OnDataReadyListener {
        void onDataReady();
    }
}
