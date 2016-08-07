package net.c_kogyo.returnvisitor.view;

import android.content.Context;
import android.view.View;
import android.widget.Button;

import net.c_kogyo.returnvisitor.R;
import net.c_kogyo.returnvisitor.data.Tag;

/**
 * Created by SeijiShii on 2016/08/06.
 */

public class TagViewInTagDialog extends TagView {

    public TagViewInTagDialog(Tag tag, Context context, OnRemoveClickListener listener) {
        super(tag, context, false, listener);

        initRemoveButton(!tag.isMutated());

    }


}
