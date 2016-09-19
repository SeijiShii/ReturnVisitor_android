package net.c_kogyo.returnvisitor.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import net.c_kogyo.returnvisitor.R;
import net.c_kogyo.returnvisitor.activity.Constants;
import net.c_kogyo.returnvisitor.activity.PersonActivity;
import net.c_kogyo.returnvisitor.data.Person;
import net.c_kogyo.returnvisitor.data.Place;
import net.c_kogyo.returnvisitor.data.RVData;
import net.c_kogyo.returnvisitor.view.BaseAnimateView;
import net.c_kogyo.returnvisitor.view.PersonCell;
import net.c_kogyo.returnvisitor.view.PlaceCell;

import java.util.ArrayList;

/**
 * Created by SeijiShii on 2016/09/18.
 */

public class SelectPlaceDialog extends DialogFragment {

    private static final String PERSON_IDS = "person_ids";

    private static OnPlaceSelectedListener mOnPlaceSelectedListener;

    public static SelectPlaceDialog newInstance(ArrayList<String> personIds, OnPlaceSelectedListener onPlaceSelectedListener) {

        mOnPlaceSelectedListener = onPlaceSelectedListener;

        Bundle arg = new Bundle();
        arg.putStringArrayList(PERSON_IDS, personIds);

        SelectPlaceDialog dialog = new SelectPlaceDialog();
        dialog.setArguments(arg);

        return dialog;
    }

    private ArrayList<String> mPersonIds;
    private ArrayList<Place> mPlaces;

    private View view;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mPersonIds = getArguments().getStringArrayList(PERSON_IDS);

        view = LayoutInflater.from(getActivity()).inflate(R.layout.select_place_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.place);
        builder.setView(view);
        builder.setNegativeButton(R.string.cancel_text, null);

        initSearchText();
        initPlaceContainer();

        refreshSuggestedPlaces(null);

        return builder.create();
    }

    private void initSearchText() {

        EditText searchText = (EditText) view.findViewById(R.id.search_text);
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                refreshSuggestedPlaces(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

//    private ListView personList;
//    private void initPersonList() {
//
//        personList = (ListView) v.findViewById(R.id.persons_list);
//        personList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                mSelectedListener.onSelected(places.get(i).getId());
//                dismiss();
//            }
//        });
//
//    }

    private LinearLayout placeContainer;
    private void initPlaceContainer() {

        placeContainer = (LinearLayout) view.findViewById(R.id.place_container);
    }


    /**
     *
     * @param searchString nullまたは空白ならdefaultをセット
     */
    private void refreshSuggestedPlaces(String searchString) {

        if (searchString == null || isStringAllBlank(searchString)) {

            mPlaces = RVData.getInstance().placeList.getListByPersonIds(mPersonIds);

        } else {
            mPlaces = RVData.getInstance().placeList.getSearchedPlaces(searchString, getActivity());

        }

        setPlaceCells();
    }

    private boolean isStringAllBlank(String string) {

        return string.trim().length() <= 0;

    }

    private void setPlaceCells() {

        placeContainer.removeAllViews();

        for ( Place place : mPlaces) {

            final PlaceCell cell = new PlaceCell(getActivity(), place, BaseAnimateView.InitialHeightCondition.VIEW_HEIGHT, null);
            placeContainer.addView(cell);

            cell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mOnPlaceSelectedListener.onSelected(cell.getPlace());

                    dismiss();
                }
            });

            // タッチ時に半透明にする　振り分けや伝播の仕方を間違えると他のリスナが呼ばれない
            cell.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {

                    cell.setAlpha(0.5f);
                    return false;
                }
            });

        }
    }

    public interface OnPlaceSelectedListener {

        void onSelected(Place place);
    }

}
