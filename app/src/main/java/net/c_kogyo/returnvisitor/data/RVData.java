package net.c_kogyo.returnvisitor.data;

import android.util.Log;

/**
 * Created by SeijiShii on 2016/07/24.
 */
public class RVData {
    private static RVData ourInstance = new RVData();
    private static OnDataChangedListener mOnDataChangedListener;
    private static OnDataReadyListener mOnDataReadyListener;

    public static void setListeners(OnDataReadyListener onDataReadyListener, OnDataChangedListener onDataChangedListener) {

        mOnDataReadyListener = onDataReadyListener;
        mOnDataChangedListener = onDataChangedListener;

    }

    public static RVData getInstance() {
        return ourInstance;
    }

    public static DataList<Place> placeList;
    public static DataList<Person> personList;
    public static DataList<Visit> visitList;

    private boolean isPlaceLoaded = false;
    private boolean isPersonLoaded = false;
    private boolean isVisitLoaded = false;

    private RVData() {

        placeList = new DataList<Place>(Place.class){
            @Override
            public void onDataChanged(Place data) {

                if ( mOnDataChangedListener != null ) {
                    mOnDataChangedListener.onDataChanged(Place.class);
                }
            }

            @Override
            public void onDataLoaded() {
                isPlaceLoaded = true;
            }
        };
        personList = new DataList<Person>(Person.class) {
            @Override
            public void onDataChanged(Person data) {

                if ( mOnDataChangedListener != null ) {
                    mOnDataChangedListener.onDataChanged(Person.class);
                }
            }

            @Override
            public void onDataLoaded() {
                isPersonLoaded = true;
            }
        };
        visitList = new DataList<Visit>(Visit.class) {
            @Override
            public void onDataChanged(Visit data) {

                if ( mOnDataChangedListener != null ) {
                    mOnDataChangedListener.onDataChanged(Visit.class);
                }
            }

            @Override
            public void onDataLoaded() {
                isVisitLoaded = true;
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {

                while ( !isPlaceLoaded || !isPersonLoaded || !isVisitLoaded) {
                    try {
                        Thread.sleep(50);
                        Log.d("RVData", "WAIT!");
                    } catch (InterruptedException e) {
                        Log.d("RVData", e.getMessage());
                    }
                }
                if (mOnDataReadyListener != null) {
                    mOnDataReadyListener.onDataReady();
                }
            }
        }).start();

    }

    public interface OnDataChangedListener {
        void onDataChanged(Class clazz);
    }

    public interface OnDataReadyListener {
        void onDataReady();
    }
}
