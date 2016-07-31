package net.c_kogyo.returnvisitor.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
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

/**
 * Created by SeijiShii on 2016/07/30.
 */

public class PlacementDialog extends DialogFragment {

    private String[] categoryArray, magazineArray;


    private static Placement.Category mCategory;
    static public PlacementDialog getInstance(Placement.Category category) {

        mCategory = category;

        return new PlacementDialog();
    }

    private View v;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        categoryArray = getActivity().getResources().getStringArray(R.array.placement_array);
        magazineArray = getActivity().getResources().getStringArray(R.array.magazine_array);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.placement);
        builder.setPositiveButton(R.string.ok_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

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
        categoryText.setText(categoryArray[mCategory.num()]);
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
            initMagazineNumberText();
        }
     }

    private TextView magazineNumberText;
    private void initMagazineNumberText() {

        magazineNumberText = (TextView) v.findViewById(R.id.magazine_number_text);
        magazineNumberText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void initNameText() {

        AutoCompleteTextView nameText = (AutoCompleteTextView) v.findViewById(R.id.name_text);
    }
}
