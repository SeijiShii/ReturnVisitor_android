package net.c_kogyo.returnvisitor.data;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.c_kogyo.returnvisitor.activity.MapActivity;

import java.util.ArrayList;

/**
 * Created by SeijiShii on 2016/07/24.
 */

public class DataList<T extends BaseDataItem>{

    private ArrayList<T> list;
    private Class<T> klass;

    DataList(final Class<T> klass) {
        list = new ArrayList<>();
        this.klass = klass;

        String userId = MapActivity.firebaseAuth.getCurrentUser().getUid();
        String className = klass.getSimpleName();

        FirebaseDatabase.getInstance().getReference()
                .child(userId)
                .child(className)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        T data = dataSnapshot.getValue(klass);
                        list.add(data);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    public void set(T data) {

        int index = indexOf(data);

        if ( index < 0 ) {
            list.add(data);
        } else {
            list.set(index, data);
        }

        String userId = MapActivity.firebaseAuth.getCurrentUser().getUid();
        String className = klass.getSimpleName();

        FirebaseDatabase.getInstance().getReference()
                .child(userId)
                .child(className)
                .child(data.getId())
                .setValue(data.toMap());
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


}
