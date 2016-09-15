package net.c_kogyo.returnvisitor.data;

import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.c_kogyo.returnvisitor.R;
import net.c_kogyo.returnvisitor.activity.MapActivity;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by SeijiShii on 2016/07/24.
 */
public class RVData {

    private static RVData instance = new RVData();

    private static OnDataChangedListener mOnDataChangedListener;
    private static OnDataLoadedListener mOnDataLoadedListener;

    public void initWithListenersAndLoad(
            Context context,
            OnDataLoadedListener onDataLoadedListener,
            OnDataChangedListener onDataChangedListener) {


        tagList.setDefaultTagArray(context);

        mOnDataLoadedListener = onDataLoadedListener;
        mOnDataChangedListener = onDataChangedListener;

        loadData();

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

    }

    private void loadData() {

        FirebaseUser user = MapActivity.firebaseAuth.getCurrentUser();
        if (user == null) return;

        String userId = MapActivity.firebaseAuth.getCurrentUser().getUid();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(userId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                HashMap<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
                loadFromHashMap(map);

                mOnDataLoadedListener.onDataLoaded();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void loadFromHashMap(HashMap<String, Object> map) {

        placeList.loadFromHashMap(map, Place.class);
        personList.loadFromHashMap(map, Person.class);
        visitList.loadFromHashMap(map, Visit.class);
        tagList.loadFromHashMap(map, Tag.class);
        workList.loadFromHashMap(map, Work.class);

        placementCompList.loadFromHashMap(map);
        noteCompleteList.loadFromHashMap(map);

        tagList.addDefaultTagsIfNeeded();

        placeList.addChildEventListener();
        personList.addChildEventListener();
        visitList.addChildEventListener();
        tagList.addChildEventListener();
        workList.addChildEventListener();

        placementCompList.addChildEventListener();
        noteCompleteList.addChildEventListener();

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

    public interface OnDataChangedListener {
        void onDataChanged(Class clazz);
    }

    public interface OnDataLoadedListener {
        void onDataLoaded();
    }

}
