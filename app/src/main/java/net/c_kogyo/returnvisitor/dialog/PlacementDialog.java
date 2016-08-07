package net.c_kogyo.returnvisitor.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import net.c_kogyo.returnvisitor.R;
import net.c_kogyo.returnvisitor.data.Placement;
import net.c_kogyo.returnvisitor.data.RVData;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by SeijiShii on 2016/07/30.
 */

public class PlacementDialog extends DialogFragment {

    private String[] categoryArray, magazineArray;
    private Placement mPlacement;
    private static OnAddPlacementListener mListener;

    private static Placement.Category mCategory;
    static public PlacementDialog getInstance(Placement.Category category, OnAddPlacementListener listener) {

        mCategory = category;
        mListener = listener;

        return new PlacementDialog();
    }

    private View v;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        categoryArray = getActivity().getResources().getStringArray(R.array.placement_array);
        magazineArray = getActivity().getResources().getStringArray(R.array.magazine_array);

        mPlacement = new Placement(mCategory);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.placement);
        builder.setPositiveButton(R.string.ok_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                mListener.onAdd(mPlacement);
                RVData.placementCompList.addToBoth(mPlacement.getName());
            }
        });
        builder.setNegativeButton(R.string.cancel_text, null);

        v = LayoutInflater.from(getActivity()).inflate(R.layout.placement_dialog, null);

        initCategoryText();
        initMagazineCategoryContainer();
        initMagazineNumberContainer();
        initNameText();

        builder.setView(v);
        return builder.create();
    }

    private void initCategoryText() {

        TextView categoryText = (TextView) v.findViewById(R.id.category_text);
        categoryText.setText(categoryArray[mPlacement.getCategory().num()]);
    }

    private void initMagazineCategoryContainer() {

        RelativeLayout magazineCategoryContainer = (RelativeLayout) v.findViewById(R.id.magazine_category_container);
        if (mCategory != Placement.Category.MAGAZINE) {
            magazineCategoryContainer.setVisibility(View.INVISIBLE);
            magazineCategoryContainer.getLayoutParams().height = 0;

        } else {
            magazineCategoryContainer.setVisibility(View.VISIBLE);
            initMagazineCategorySpinner();
        }
    }

    private void initMagazineCategorySpinner(){

        Spinner magazineCategorySpinner = (Spinner) v.findViewById(R.id.magazine_category_spinner);
        ArrayAdapter<String> magazineAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, magazineArray);
        magazineCategorySpinner.setAdapter(magazineAdapter);
        magazineCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                mPlacement.setMagCategory(Placement.MagazineCategory.getEnum(i));
                initMagazineNumberSpinner();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void initMagazineNumberContainer() {

        LinearLayout magazineNumberContainer = (LinearLayout) v.findViewById(R.id.magazine_number_container);
        if ( mCategory != Placement.Category.MAGAZINE ) {

            magazineNumberContainer.setVisibility(View.INVISIBLE);
            magazineNumberContainer.getLayoutParams().height = 0;

        } else {

            magazineNumberContainer.setVisibility(View.VISIBLE);
            initMagazineNumberSpinner();
        }
     }


    private Spinner magazineNumberSpinner;
    private void initMagazineNumberSpinner() {

        final ArrayList<Pair<Calendar, String>> numberList = Placement.getMagazineNumberArrayList(mPlacement.getMagCategory(), getActivity());
        ArrayList<String> numStringList = new ArrayList<>();
        for (Pair<Calendar, String> item : numberList) {

            numStringList.add(item.second);
        }

        magazineNumberSpinner = (Spinner) v.findViewById(R.id.magazine_number_spinner);
        ArrayAdapter<String> magNumAdapter = new ArrayAdapter<>(getActivity(),
                                                                android.R.layout.simple_list_item_1,
                                                                numStringList);
        magazineNumberSpinner.setAdapter(magNumAdapter);
        magazineNumberSpinner.setSelection(magNumAdapter.getCount() - 4);
        magazineNumberSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                mPlacement.setNumber(numberList.get(i).first);

//                Toast.makeText(getActivity(),
//                        Placement.getNumberString(mPlacement.getNumber(), mPlacement.getMagCategory(), getActivity()),
//                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

    private void initNameText() {

        AutoCompleteTextView nameText = (AutoCompleteTextView) v.findViewById(R.id.name_text);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1,
                RVData.getInstance().placementCompList.getList());
        nameText.setAdapter(adapter);
        nameText.setThreshold(1);

        nameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                mPlacement.setName(editable.toString());
            }
        });


    }

    public interface OnAddPlacementListener {
        void onAdd(Placement placement);
    }

    //TODO PlacementDialogもAutoCompleteTextの関係で不具合が生じているのでActivityに換装する。
}
