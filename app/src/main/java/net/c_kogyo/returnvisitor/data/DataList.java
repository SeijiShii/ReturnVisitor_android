package net.c_kogyo.returnvisitor.data;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.c_kogyo.returnvisitor.activity.MapActivity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by SeijiShii on 2016/07/24.
 */

public abstract class DataList<T extends BaseDataItem>{

    private ArrayList<T> list;
    private Class<T> klass;
    private DatabaseReference reference;

    DataList(final Class<T> klass) {
        list = new ArrayList<>();
        this.klass = klass;

        String userId = MapActivity.firebaseAuth.getCurrentUser().getUid();
        String className = klass.getSimpleName();

        reference = FirebaseDatabase.getInstance().getReference()
                .child(userId)
                .child(className);

        // TODO　リスト全体を読みだす処理

    }

    public void add(T data) {

        list.add(data);

        DatabaseReference node = reference.child(data.getId());

        node.setValue(data.toMap());
        node.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Object o = dataSnapshot.getValue();
                HashMap<String, Object> map = (HashMap<String, Object>) o;
                T data1 = null;

                try {
                    data1 =  klass.newInstance();
                    data1.setMap(map);

                    set(data1);
                    onDataChanged(data1);

                } catch (IllegalAccessException e) {
                    //
                } catch (InstantiationException e) {
                    //
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void set(T data) {

        list.set(indexOf(data), data);
    }

    public int indexOf(T data) {

        for ( int i = 0 ; i < list.size() ; i++ ) {

            T data1 = list.get(i);

            if (data.equals(data1)) {
                return i;
            }
        }
        return -1;

    }

    public T getById(String id) {

        for ( T data : list ) {
            if (data.getId().equals(id)) {
                return data;
            }
        }
        return null;
    }

    public abstract void onDataChanged(T data);

}

