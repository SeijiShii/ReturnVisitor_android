package net.c_kogyo.returnvisitor.data;

import android.content.Context;

import net.c_kogyo.returnvisitor.R;

import java.util.ArrayList;

/**
 * Created by SeijiShii on 2016/08/01.
 */

public abstract class TagList extends DataList<Tag> {


    private String[] defaultTagArray;

    public TagList(Context context) {
        super(Tag.class);

        defaultTagArray = context.getResources().getStringArray(R.array.tag_item_array);

    }

    public boolean hasSameNamedTag(String name) {

        for (Tag tag : list) {
            if (tag.name.equals(name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onDataLoaded() {

        // TODO 初期タグの追加でFirebaseに追加されすぎてしまう件
        // Firebaseからロードが終わってから、なければ追加するようにする
        for (String tagString : defaultTagArray) {

            if (!hasSameNamedTag(tagString)) {
                Tag tag = new Tag(tagString, true);
                add(tag);
            }
        }
    }

    public ArrayList<Tag> getAll() {
        return list;
    }
}
