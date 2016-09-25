package net.c_kogyo.returnvisitor.data;

import android.content.Context;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.c_kogyo.returnvisitor.R;
import net.c_kogyo.returnvisitor.activity.MapActivity;
import net.c_kogyo.returnvisitor.util.CalendarUtil;

import java.lang.annotation.Retention;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Created by SeijiShii on 2016/07/24.
 */
public class RVData {

    private static RVData instance = new RVData();
    private static boolean isFirstCallbackAdded;

    private static OnDataChangedListener mOnDataChangedListener;
    private static OnDataLoadedListener mOnDataLoadedListener;

    public void initWithListenersAndLoad(
            Context context,
            OnDataLoadedListener onDataLoadedListener,
            OnDataChangedListener onDataChangedListener) {


        tagList.setDefaultTagArray(context);

        mOnDataLoadedListener = onDataLoadedListener;
        mOnDataChangedListener = onDataChangedListener;

        loadDataIfNeeded();

        placementCompList.setCompleteSeed(context, R.array.complete_array);

    }

    public PlaceList placeList;
    public PersonList personList;
    public VisitList visitList;
    public TagList tagList;
    public WorkList workList;

    public CompleteList placementCompList;
    public CompleteList noteCompleteList;

    public static RVData getInstance(){
        return instance;
    }

    private RVData() {

        isFirstCallbackAdded = false;

        placeList = new PlaceList(){
            @Override
            public void onDataChanged(Place data) {

                if ( mOnDataChangedListener != null ) {
                    mOnDataChangedListener.onDataChanged(Place.class);
                }
            }

        };

        personList = new PersonList() {
            @Override
            public void onDataChanged(Person data) {

                if ( mOnDataChangedListener != null ) {
                    mOnDataChangedListener.onDataChanged(Person.class);
                }
            }

        };

        visitList = new VisitList() {
            @Override
            public void onDataChanged(Visit data) {

                if ( mOnDataChangedListener != null ) {
                    mOnDataChangedListener.onDataChanged(Visit.class);
                }
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

        isFirstCallbackAdded = false;

    }

    private void loadDataIfNeeded() {

        if (isFirstCallbackAdded) return;

        FirebaseUser user = MapActivity.firebaseAuth.getCurrentUser();
        if (user == null) return;

        String userId = MapActivity.firebaseAuth.getCurrentUser().getUid();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(userId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                MapActivity.showTimeLog("Firebase callback called");

                HashMap<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
                loadFromHashMap(map);

                // その他のリスナをつけるのは最初のロードが終わってから～
                tagList.addDefaultTagsIfNeeded();

                placeList.addChildEventListener();
                personList.addChildEventListener();
                visitList.addChildEventListener();
                tagList.addChildEventListener();
                workList.addChildEventListener();

                placementCompList.addChildEventListener();
                noteCompleteList.addChildEventListener();

                mOnDataLoadedListener.onDataLoaded();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        isFirstCallbackAdded = true;
        MapActivity.showTimeLog("Firebase Callback added");


    }

    private void loadFromHashMap(HashMap<String, Object> map) {

        placeList.loadFromHashMap(map, Place.class);
        personList.loadFromHashMap(map, Person.class);
        visitList.loadFromHashMap(map, Visit.class);
        workList.loadFromHashMap(map, Work.class);

        tagList.loadFromHashMap(map);

        placementCompList.loadFromHashMap(map);
        noteCompleteList.loadFromHashMap(map);

    }

    private ArrayList<Calendar> getDatesWithData() {

        ArrayList<Calendar> datesOfVisit = visitList.getDates();
        ArrayList<Calendar> datesOfWork = workList.getDates();
        ArrayList<Calendar> datesDoubled = new ArrayList<>();

        for (Calendar date0 : datesOfVisit) {
            for (Calendar date1 : datesOfWork) {

                if (CalendarUtil.isSameDay(date0, date1)) {
                    datesDoubled.add(date1);
                }
            }
        }

        datesOfWork.removeAll(datesDoubled);
        datesOfVisit.addAll(datesOfWork);

        Collections.sort(datesOfVisit, new Comparator<Calendar>() {
            @Override
            public int compare(Calendar calendar, Calendar t1) {
                return calendar.compareTo(t1);
            }
        });

        return new ArrayList<>(datesOfVisit);
    }

    public boolean theDayHasData(Calendar date) {

        for (Calendar date1 : getDatesWithData()) {
            if (CalendarUtil.isSameDay(date1, date)) {
                return true;
            }
        }
        return false;
    }

    //    public void setListenerAndLoadData() {
//
//        placeList.setListenerAndLoadData();
//        personList.setListenerAndLoadData();
//        visitList.setListenerAndLoadData();
//        tagList.setListenerAndLoadData();
//        workList.setListenerAndLoadData();
//
//        placementCompList.setListenerAndLoadData();
//        noteCompleteList.setListenerAndLoadData();
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//                while ( !isPlaceLoaded || !isPersonLoaded || !isVisitLoaded ) {
//                    try {
//                        Thread.sleep(50);
//                        Log.d("RVData", "WAIT!");
//                    } catch (InterruptedException e) {
//                        Log.d("RVData", e.getMessage());
//                    }
//                }
//                if (mOnDataReadyListener != null) {
//                    mOnDataReadyListener.onDataReady();
//                }
//            }
//        }).start();
//    }

    public ArrayList<AggregationOfDay> getAggregatedDays() {

        ArrayList<AggregationOfDay> aggregationOfDays = new ArrayList<>();

        for (Calendar date : getDatesWithData()) {

            aggregationOfDays.add(new AggregationOfDay(date));
        }
        return aggregationOfDays;
    }

    public interface OnDataChangedListener {
        void onDataChanged(Class clazz);
    }

    public interface OnDataLoadedListener {
        void onDataLoaded();
    }

}
