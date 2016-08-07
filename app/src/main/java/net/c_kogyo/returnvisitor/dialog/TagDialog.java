package net.c_kogyo.returnvisitor.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import net.c_kogyo.returnvisitor.R;
import net.c_kogyo.returnvisitor.data.RVData;
import net.c_kogyo.returnvisitor.data.Tag;
import net.c_kogyo.returnvisitor.view.TagView;
import net.c_kogyo.returnvisitor.view.TagViewInTagDialog;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

/**
 * Created by SeijiShii on 2016/08/06.
 */

public class TagDialog extends DialogFragment {

    private static OnTagSelectedListener mListener;
    private static ArrayList<String> mCaneledTagIds;

    public static TagDialog newInstance(OnTagSelectedListener listener, ArrayList<String> caneledTagIds) {

        mListener = listener;
        mCaneledTagIds = caneledTagIds;

        return new TagDialog();
    }

    private View v;
    private ArrayList<Tag> mTags;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.tag);

        v = LayoutInflater.from(getActivity()).inflate(R.layout.tag_dialog, null);
        builder.setView(v);

        initSearchText();
        initTagList();

        builder.setNegativeButton(R.string.cancel_text, null);

        return builder.create();
    }

    private EditText searchText;
    private void initSearchText() {

        searchText = (EditText) v.findViewById(R.id.search_text);
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                updateTagLinear(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private LinearLayout tagLinear;
    private void initTagList() {

        tagLinear = (LinearLayout) v.findViewById(R.id.tag_list);

        updateTagLinear(null);

    }

    private void updateTagLinear(String searchWord) {

        tagLinear.removeAllViews();

        mTags = new ArrayList<>(RVData.tagList.getAll());

        // すでにPersonについているタグのIDが渡されてくるのでそのタグを削除する
        ArrayList<Tag> tagsToRemove = new ArrayList<>();
        for (Tag tag : mTags) {

            if (mCaneledTagIds.contains(tag.getId())) {
                tagsToRemove.add(tag);
            }
        }
        mTags.removeAll(tagsToRemove);

        if (searchWord != null) {
            if (searchWord.length() > 0 && !isStringAllBlank(searchWord)) {

                String[] words = searchWord.split(" ");
                ArrayList<Tag> tags = new ArrayList<>();

                for ( Tag tag : mTags ) {

                    for (String word : words) {

                        if (StringUtils.containsIgnoreCase(tag.getName(), word)) {
                            tags.add(tag);
                        }
                    }
                }
                mTags = new ArrayList<>(tags);
            }
        }

        for (final Tag tag : mTags) {

            final TagViewInTagDialog tagView = new TagViewInTagDialog(tag, getActivity(),
                    new TagView.OnRemoveClickListener() {
                        @Override
                        public void onRemoveClick(TagView tagView) {

                        }
                    });

            tagView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {

                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        tagView.setAlpha(0.5f);
                        return true;
                    } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        tagView.setAlpha(1f);
                        mListener.onTagSelect(tag);
                        dismiss();
                        return true;
                    }
                    return false;
                }
            });

            tagLinear.addView(tagView);
        }
    }

    private void deleteTagComfirm() {


    }

    private boolean isStringAllBlank(String string) {

        return string.trim().length() <= 0;

    }

    public interface OnTagSelectedListener{
        void onTagSelect(Tag tag);
    }
}
