package net.c_kogyo.returnvisitor.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import net.c_kogyo.returnvisitor.R;
import net.c_kogyo.returnvisitor.data.Person;
import net.c_kogyo.returnvisitor.data.Placement;
import net.c_kogyo.returnvisitor.data.RVData;
import net.c_kogyo.returnvisitor.data.Visit;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by SeijiShii on 2016/08/07.
 */

public class PlacementActivity extends AppCompatActivity {

    private String[] categoryArray, magazineArray;
    private static Placement mPlacement;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // staticなので確実にヌリファイしておく
        mPlacement = null;

        categoryArray = getResources().getStringArray(R.array.placement_array);
        magazineArray = getResources().getStringArray(R.array.magazine_array);

        initPlacement();

        setContentView(R.layout.placement_activity);

        initToolBar();
        initCategoryText();
        initMagazineCategoryContainer();
        initMagazineNumberContainer();
        initNameText();

        initOkButton();
        initCancelButton();
    }

    private void initPlacement() {

        Intent intent = getIntent();
        String catString = intent.getStringExtra(Placement.PLACEMENT_CATEGORY);

        if ( catString == null ) {
            catString = Placement.Category.OTHER.toString();
        }

        Placement.Category category = Placement.Category.valueOf(catString);
        mPlacement = new Placement(category);
    }

    private void initToolBar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        if (toolbar != null) {

            toolbar.setTitle(R.string.placement);
        }
    }

    private void initCategoryText() {

        TextView categoryText = (TextView) findViewById(R.id.category_text);
        categoryText.setText(categoryArray[mPlacement.getCategory().num()]);
    }

    private void initMagazineCategoryContainer() {

        RelativeLayout magazineCategoryContainer = (RelativeLayout) findViewById(R.id.magazine_category_container);
        if (mPlacement.getCategory() != Placement.Category.MAGAZINE) {
            magazineCategoryContainer.setVisibility(View.INVISIBLE);
            magazineCategoryContainer.getLayoutParams().height = 0;

        } else {
            magazineCategoryContainer.setVisibility(View.VISIBLE);
            initMagazineCategorySpinner();
        }
    }

    private void initMagazineCategorySpinner(){

        Spinner magazineCategorySpinner = (Spinner) findViewById(R.id.magazine_category_spinner);
        ArrayAdapter<String> magazineAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, magazineArray);
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

        LinearLayout magazineNumberContainer = (LinearLayout) findViewById(R.id.magazine_number_container);
        if ( mPlacement.getCategory() != Placement.Category.MAGAZINE ) {

            magazineNumberContainer.setVisibility(View.INVISIBLE);
            magazineNumberContainer.getLayoutParams().height = 0;

        } else {

            magazineNumberContainer.setVisibility(View.VISIBLE);
            initMagazineNumberSpinner();
        }
    }

    private Spinner magazineNumberSpinner;
    private void initMagazineNumberSpinner() {

        final ArrayList<Pair<Calendar, String>> numberList = Placement.getMagazineNumberArrayList(mPlacement.getMagCategory(), this);
        ArrayList<String> numStringList = new ArrayList<>();
        for (Pair<Calendar, String> item : numberList) {

            numStringList.add(item.second);
        }

        magazineNumberSpinner = (Spinner) findViewById(R.id.magazine_number_spinner);
        ArrayAdapter<String> magNumAdapter = new ArrayAdapter<>(this,
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

        AutoCompleteTextView nameText = (AutoCompleteTextView) findViewById(R.id.name_text);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                RVData.placementCompList.getList());
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

    private void initOkButton() {

        Button okButton = (Button) findViewById(R.id.ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setResult(Constants.PlacementCode.PLACEMENT_ADDED_RESULT_CODE);

                finish();
            }
        });
    }

    private void initCancelButton() {

        Button cancelButton = (Button) findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setResult(Constants.PlacementCode.PLACEMENT_CANCELED_RESULT_CODE);

                mPlacement = null;
                finish();
            }
        });
    }

    public static Placement getPlacement() {

        Placement placement = mPlacement;
        mPlacement =null;

        return placement;
    }
}

