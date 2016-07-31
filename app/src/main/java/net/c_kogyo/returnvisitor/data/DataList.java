package net.c_kogyo.returnvisitor.data;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.c_kogyo.returnvisitor.activity.MapActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by SeijiShii on 2016/07/24.
 */


public abstract class DataList<T extends BaseDataItem> implements Iterable<T>{

    public static final String DATA_LIST_TAG = "DataList";

    protected ArrayList<T> list;
    private Class<T> klass;
    private DatabaseReference reference;
    private long childCount = 0;
    private long childCounter = 0;

    DataList(final Class<T> klass) {
        list = new ArrayList<T>();
        this.klass = klass;

        String userId = MapActivity.firebaseAuth.getCurrentUser().getUid();
        final String className = klass.getSimpleName();

        reference = FirebaseDatabase.getInstance().getReference()
                .child(userId)
                .child(className);

        // こちらは一度呼ばれたら取り外されるリスナ。起動時のデータの読み出し等に用いる
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Childリスナではないので指定したノードそのものを読み込む
                // ここではデータ数だけを読み取る
                childCount = dataSnapshot.getChildrenCount();
                if (childCount == 0) {
                    onDataLoaded();
                }

                addChildEventListener();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void addChildEventListener() {

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
                childCounter++;

                if (childCounter >= childCount) {
                    onDataLoaded();
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                Log.d(DATA_LIST_TAG, "Child Changed!");
                // 呼ばれるタイミング
                // set(data)の後
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

    public void add(T data) {

        list.add(data);

        DatabaseReference node = reference.child(data.getId());
        node.setValue(data.toMap());
    }

    public void set(T data) {

        list.set(indexOf(data), data);

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
        return ((ArrayList<T>) list).iterator();
    }

//    public abstract void onDataReady();

    public abstract void onDataChanged(T data);

    public abstract void onDataLoaded();



}

