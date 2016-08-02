package net.c_kogyo.returnvisitor.data;

import android.content.Context;
import android.util.Log;

import com.facebook.internal.CollectionMapper;

import net.c_kogyo.returnvisitor.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

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
    public static void setCompleteListSeed(Context context) {

        String[] nameSeedList = context.getResources().getStringArray(R.array.complete_array);
        placementCompList.getList().addAll(Arrays.asList(nameSeedList));
    }

    public static void initTagList(Context context) {
        tagList = new TagList(context) {
            @Override
            public void onDataChanged(Tag data) {
                if ( mOnDataChangedListener != null ) {
                    mOnDataChangedListener.onDataChanged(Place.class);
                }
            }

            @Override
            public void onDataLoaded() {
            }
        };
    }

    public static RVData getInstance() {
        return ourInstance;
    }

    public static PlaceList placeList;
    public static PersonList personList;
    public static VisitList visitList;
    public static TagList tagList;

    public static CompleteList placementCompList;
    public static CompleteList noteCompleteList;

    private boolean isPlaceLoaded = false;
    private boolean isPersonLoaded = false;
    private boolean isVisitLoaded = false;

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


        placementCompList = new CompleteList("PlacementCompleteList");
        noteCompleteList = new CompleteList("NoteCompleteList");

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
