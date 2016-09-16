package net.c_kogyo.returnvisitor.data;

import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.c_kogyo.returnvisitor.activity.MapActivity;
import net.c_kogyo.returnvisitor.util.CalendarUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by SeijiShii on 2016/07/24.
 */


public abstract class DataList<T extends BaseDataItem> implements Iterable<T>{

    public static final String DATA_LIST_TAG = "DataList";

    protected ArrayList<T> list;
    private Class<T> klass;
//    private DatabaseReference reference;

    DataList(final Class<T> clazz) {
        list = new ArrayList<>();
        this.klass = clazz;
    }

//    public void setChildEventListener() {
//        FirebaseUser user = MapActivity.firebaseAuth.getCurrentUser();
//        if (user == null) return;
//
//        String userId = MapActivity.firebaseAuth.getCurrentUser().getUid();
//
//        reference = FirebaseDatabase.getInstance().getReference()
//                .child(userId)
//                .child(klass.getSimpleName());
//
//        reference.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//
//    }

    public void addChildEventListener() {

        FirebaseUser user = MapActivity.firebaseAuth.getCurrentUser();
        if (user == null) return;

        String userId = MapActivity.firebaseAuth.getCurrentUser().getUid();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(userId)
                .child(klass.getSimpleName());

        reference.addChildEventListener(new ChildEventListener() {

            // 呼ばれるタイミング
            // リスナがセットされた直後
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                // 呼ばれるタイミング
                // クライアント側でデータを追加したとき
                // リモート側でデータが追加されたときにクライアントにそれを通知する

                T data1 = getInstance(dataSnapshot);
                if ( data1 == null ) return;

                addToListIfNotContained(data1);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                Log.d(DATA_LIST_TAG, "Child Changed!");
                // 呼ばれるタイミング
                // addOrSet(data)の後
                // リモート側で変更されたらPushされてくる
                T data1 = getInstance(dataSnapshot);
                if ( data1 == null ) return;

                setToList(data1);
                // ローカル側で変更された場合は結果2回setすることになるけど仕方ないと思う
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                Log.d(DATA_LIST_TAG, "Child Removed!");
                String id = dataSnapshot.getKey();
                removeByIdIfContained(id);

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private T getInstance(DataSnapshot dataSnapshot) {

        Object o = dataSnapshot.getValue();
        HashMap<String, Object> map = (HashMap<String, Object>) o;

        return getInstanceFromMap(map);

    }

    public T getInstanceFromMap(HashMap<String, Object> map) {

        T data1 = null;

        try {
            data1 =  klass.newInstance();

            if ( map != null ) {
                data1.setMap(map);
            }
        } catch (IllegalAccessException e) {
            //
        } catch (InstantiationException e) {
            //
        }
        return data1;
    }

    private void addToListIfNotContained(T data) {

        if ( indexOf(data) < 0 ) {
            list.add(data);
        }
    }

    public void addOrSet(T data) {

        if (indexOf(data) < 0) {
            list.add(data);
        } else {
            list.set(indexOf(data), data);

        }

        FirebaseUser user = MapActivity.firebaseAuth.getCurrentUser();
        if (user == null) return;

        String userId = MapActivity.firebaseAuth.getCurrentUser().getUid();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(userId)
                .child(klass.getSimpleName());

        DatabaseReference node = reference.child(data.getId());
        node.setValue(data.toMap());
        // この後onChildChangedが呼ばれる
    }

    private void setToList(T data) {

        list.set(indexOf(data), data);
    }

    private int indexOf(T data) {

        for ( int i = 0 ; i < list.size() ; i++ ) {

            T data1 = list.get(i);

            if (data.equals(data1)) {
                return i;
            }
        }
        return -1;

    }

    public boolean contains(T data) {
        return indexOf(data) >= 0;
    }

    public T getById(String id) {

        for ( T data : list ) {
            if (data.getId().equals(id)) {
                return data;
            }
        }
        return null;
    }

    public void removeFromBoth(T data) {

        list.remove(data);

        FirebaseUser user = MapActivity.firebaseAuth.getCurrentUser();
        if (user == null) return;

        String userId = MapActivity.firebaseAuth.getCurrentUser().getUid();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(userId)
                .child(klass.getSimpleName());

        DatabaseReference node = reference.child(data.getId());
        node.setValue(null);
        // この後onChildRemovedが呼ばれる
    }

    private void remove(T data) {
        list.remove(data);
    }

    public void removeById(String id) {

        T data = getById(id);
        if ( data == null ) return;

        remove(data);
    }

    private void removeByIdIfContained(String id) {

        T data = getById(id);
        if ( data == null ) return;

        list.remove(data);
        onDataChanged(data);
    }

    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }

    public ArrayList<T> getList(ArrayList<String> ids) {

        ArrayList<T> arrayList = new ArrayList<>();
        for ( T item : list ) {

            if (ids.contains(item.id)) {
                arrayList.add(item);
            }
        }
        return arrayList;
    }

    public void clearFromLocal() {
        list.clear();

    }

    public ArrayList<T> getList() {
        return list;
    }

    public abstract void onDataChanged(T data);

    public void loadFromHashMap(HashMap<String, Object> map, Class<T> clazz) {

        String className = clazz.getSimpleName();
        Object o = map.get(className);

        HashMap<String, Object> map1 = (HashMap<String, Object> ) o;

        list.clear();

        if (map1 == null) return;

        for (Object data : map1.values()) {

            HashMap<String, Object> dataMap = (HashMap<String, Object>) data;
            list.add(getInstanceFromMap(dataMap));
        }

    }

}

