package net.c_kogyo.returnvisitor.data;

import android.content.Context;

import net.c_kogyo.returnvisitor.R;
import net.c_kogyo.returnvisitor.view.CollapseButton;

import java.util.ArrayList;
import java.util.HashMap;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

/**
 * Created by SeijiShii on 2016/08/01.
 */

public abstract class TagList extends DataList<Tag> {


    private String[] defaultTagArray;

    public TagList() {
        super(Tag.class);
    }

    public void setDefaultTagArray(Context context) {
        defaultTagArray = context.getResources().getStringArray(R.array.tag_item_array);
    }

    public void addDefaultTagsIfNeeded() {
        // 初期タグの追加でFirebaseに追加されすぎてしまう件
        // Firebaseからロードが終わってから、なければ追加するようにする
        for (String tagString : defaultTagArray) {

            if (!hasSameName(tagString)) {
                Tag tag = new Tag(tagString, true);
                addOrSet(tag);
            }
        }
    }

    public ArrayList<Tag> getAll() {
        return list;
    }

    public boolean hasSameName(String name) {

        for (Tag tag : list) {
            if (tag.name.equals(name)){
                return true;
            }
        }
        return false;
    }

    public void loadFromHashMap(HashMap<String, Object> map) {

        Object o = map.get(Tag.class.getSimpleName());

        HashMap<String, Object> map1 = (HashMap<String, Object> ) o;

        list.clear();

        if (map1 == null) return;

        for (Object data : map1.values()) {

            HashMap<String, Object> dataMap = (HashMap<String, Object>) data;
            Tag tag = getInstanceFromMap(dataMap);

            if (!hasSameName(tag.getName())) {
                list.add(tag);
            }
        }

    }
}
