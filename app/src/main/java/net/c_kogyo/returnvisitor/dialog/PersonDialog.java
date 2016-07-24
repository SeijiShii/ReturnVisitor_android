package net.c_kogyo.returnvisitor.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import net.c_kogyo.returnvisitor.R;
import net.c_kogyo.returnvisitor.data.Person;
import net.c_kogyo.returnvisitor.data.Visit;

import java.util.ArrayList;

/**
 * Created by SeijiShii on 2016/07/23.
 */

public class PersonDialog extends DialogFragment {

    private static Person mPerson;
    private static Visit mVisit;
    private Context mContext;

    public static PersonDialog getInstance(Person person, Visit visit) {

        mPerson = person;

        if ( mPerson == null ) mPerson = new Person();

        mVisit = visit;

        return new PersonDialog();

    }

    private View v;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mContext = getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        v = LayoutInflater.from(getActivity()).inflate(R.layout.person_dialog, null);
        builder.setView(v);

        builder.setPositiveButton(R.string.ok_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setNegativeButton(R.string.cancel_text, null);

        initNameText();
        initSexRadio();
        initAgeSpinner();
        initInterestRater();

        return builder.create();
    }

    private EditText nameText;
    private void initNameText() {

        nameText = (EditText) v.findViewById(R.id.name_text);

        if ( mPerson != null ) {
            nameText.setText(mPerson.getName());
        }

    }

    private RadioButton maleButton, femaleButton;
    private void initSexRadio() {

        maleButton = (RadioButton) v.findViewById(R.id.male_button);
        femaleButton = (RadioButton) v.findViewById(R.id.female_button);

        maleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b) {
                    mPerson.setSex(Person.Sex.MALE);
                    femaleButton.setChecked(false);
                }
            }
        });

        femaleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b) {
                    mPerson.setSex(Person.Sex.FEMALE);
                    maleButton.setChecked(false);
                }
            }
        });

    }

    private Spinner ageSpinner;
    private void initAgeSpinner() {

        ageSpinner = (Spinner) v.findViewById(R.id.age_spinner);

        ArrayAdapter<CharSequence> adapter
                = ArrayAdapter.createFromResource(mContext,
                                                    R.array.age_array,
                                                    android.R.layout.simple_spinner_item);

        ageSpinner.setAdapter(adapter);
        ageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mPerson.setAge(Person.Age.getEnum(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private int[] buttonRes = {
            R.mipmap.button_gray,
            R.mipmap.button_red,
            R.mipmap.button_purple,
            R.mipmap.button_blue,
            R.mipmap.button_green,
            R.mipmap.button_yellow,
            R.mipmap.button_orange
    } ;


    private void initInterestRater() {

        LinearLayout raterContainer = (LinearLayout) v.findViewById(R.id.rater_container);

        final Button[] raterButtons = new Button[8];
        for ( int i = 0 ; i < 7 ; i++ ) {
            raterButtons[i] = new Button(mContext);
            raterButtons[i].setBackgroundResource(buttonRes[0]);
            raterButtons[i].setTag(i);

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(40, 40);

            raterButtons[i].setLayoutParams(params);
            raterContainer.addView(raterButtons[i]);

            if ( i < 6 ) {
                View view = new View(mContext);
                FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(15, 30);
                view.setLayoutParams(params2);
                raterContainer.addView(view);
            }


            raterButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int tag = Integer.parseInt(view.getTag().toString());

                    for (int i = 0 ; i <= tag ; i++) {
                        raterButtons[i].setBackgroundResource(buttonRes[tag]);
                    }

                    for (int i = tag + 1 ; i < 7 ; i++ ) {
                        raterButtons[i].setBackgroundResource(buttonRes[0]);
                    }
                }
            });
        }



    }
}
