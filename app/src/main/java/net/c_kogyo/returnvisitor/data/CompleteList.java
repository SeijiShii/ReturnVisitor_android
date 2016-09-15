package net.c_kogyo.returnvisitor.data;

import android.content.Context;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.c_kogyo.returnvisitor.R;
import net.c_kogyo.returnvisitor.activity.MapActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by SeijiShii on 2016/07/31.
 */

public class
CompleteList{

    private ArrayList<String> list;
    private String mNodeName;

    CompleteList(String nodeName) {
        list = new ArrayList<>();
        mNodeName = nodeName;

    }

    public void setCompleteSeed(Context context, int arrayResId) {

        String[] nameSeedList = context.getResources().getStringArray(arrayResId);

        for (String seed : nameSeedList) {
            addToBoth(seed);
        }

    }

    public void loadFromHashMap(HashMap<String, Object> map) {

        list = new ArrayList<>();

        for (Object o : map.entrySet()) {

            String s = String.valueOf(o);
            list.add(s);
        }
    }

    public void addChildEventListener() {

        FirebaseUser user = MapActivity.firebaseAuth.getCurrentUser();
        if (user == null) return;

        String userId = user.getUid();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(userId)
                .child(mNodeName);

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

        FirebaseUser user = MapActivity.firebaseAuth.getCurrentUser();
        if (user == null) return;

        String userId = user.getUid();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(userId)
                .child(mNodeName);

        reference.setValue(list);
    }

    public ArrayList<String> getList() {
        return list;
    }

    public void clearFromLocal() {
        list.clear();
    }
}
