package net.c_kogyo.returnvisitor.data;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.c_kogyo.returnvisitor.activity.MapActivity;

import java.util.ArrayList;

/**
 * Created by SeijiShii on 2016/07/31.
 */

public class
CompleteList{

    private ArrayList<String> list;
    private String mName;
    private DatabaseReference reference;

    CompleteList(String nodeName) {
        list = new ArrayList<>();
        mName = nodeName;

        String userId = MapActivity.firebaseAuth.getCurrentUser().getUid();

        reference = FirebaseDatabase.getInstance().getReference()
                .child(userId)
                .child(mName);

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                addToList(dataSnapshot.getValue().toString());

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

    public void addToList(String data) {
        if (list.contains(data)) return;
        list.add(data);
    }

    public void addToBoth(String data) {

        addToList(data);

        reference.setValue(list);
    }

    public ArrayList<String> getList() {
        return list;
    }
}
