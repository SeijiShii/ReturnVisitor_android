package net.c_kogyo.returnvisitor.data;

import android.content.Context;
import android.util.Log;

import net.c_kogyo.returnvisitor.R;

import java.util.Arrays;

/**
 * Created by SeijiShii on 2016/07/24.
 */
public class RVData {

    private static RVData instance = new RVData();

    private static OnDataChangedListener mOnDataChangedListener;
    private static OnDataReadyListener mOnDataReadyListener;

    public static void setListeners(OnDataReadyListener onDataReadyListener,
                                    OnDataChangedListener onDataChangedListener) {

        mOnDataReadyListener = onDataReadyListener;
        mOnDataChangedListener = onDataChangedListener;

    }
    public void setCompleteListSeed(Context context) {

        String[] nameSeedList = context.getResources().getStringArray(R.array.complete_array);
        instance.placementCompList.getList().addAll(Arrays.asList(nameSeedList));
    }

    public void setDefaultTag(Context context) {
        tagList.setDefaultTagArray(context);
    }

    public PlaceList placeList;
    public PersonList personList;
    public VisitList visitList;
    public TagList tagList;
    public WorkList workList;

    public CompleteList placementCompList;
    public CompleteList noteCompleteList;

    private boolean isPlaceLoaded = false;
    private boolean isPersonLoaded = false;
    private boolean isVisitLoaded = false;

    public static RVData getInstance(){
        return instance;
    }

    private RVData() {

        placeList = new PlaceList(){
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

        personList = new PersonList() {
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

        visitList = new VisitList() {
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

        tagList = new TagList() {
            @Override
            public void onDataChanged(Tag data) {
                if ( mOnDataChangedListener != null ) {
                    mOnDataChangedListener.onDataChanged(Place.class);
                }
            }
        };

        workList = new WorkList() {
            @Override
            public void onDataChanged(Work data) {
                if ( mOnDataChangedListener != null ) {
                    mOnDataChangedListener.onDataChanged(Work.class);
                }
            }

            @Override
            public void onDataLoaded() {

            }
        };

        placementCompList = new CompleteList("PlacementCompleteList");
        noteCompleteList = new CompleteList("NoteCompleteList");



    }

    public void clearFromLocal() {

        placeList.clearFromLocal();
        personList.clearFromLocal();
        visitList.clearFromLocal();
        tagList.clearFromLocal();
        workList.clearFromLocal();
        placementCompList.clearFromLocal();
        noteCompleteList.clearFromLocal();

    }

    public void setListenerAndLoadData() {

        placeList.setListenerAndLoadData();
        personList.setListenerAndLoadData();
        visitList.setListenerAndLoadData();
        tagList.setListenerAndLoadData();
        workList.setListenerAndLoadData();

        placementCompList.setListenerAndLoadData();
        noteCompleteList.setListenerAndLoadData();

        new Thread(new Runnable() {
            @Override
            public void run() {

                while ( !isPlaceLoaded || !isPersonLoaded || !isVisitLoaded ) {
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
