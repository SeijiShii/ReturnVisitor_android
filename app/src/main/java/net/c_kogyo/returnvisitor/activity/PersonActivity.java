package net.c_kogyo.returnvisitor.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import net.c_kogyo.returnvisitor.R;
import net.c_kogyo.returnvisitor.data.Person;
import net.c_kogyo.returnvisitor.data.RVData;
import net.c_kogyo.returnvisitor.data.Tag;
import net.c_kogyo.returnvisitor.dialog.TagDialog;
import net.c_kogyo.returnvisitor.view.TagContainer;

import static net.c_kogyo.returnvisitor.activity.Constants.buttonRes;
import static net.c_kogyo.returnvisitor.data.Person.Sex.FEMALE;
import static net.c_kogyo.returnvisitor.data.Person.Sex.MALE;

/**
 * Created by SeijiShii on 2016/08/06.
 */

public class PersonActivity extends AppCompatActivity {

    private Person mPerson;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initPerson();

        setContentView(R.layout.person_activity);


    }

    @Override
    protected void onResume() {
        super.onResume();

        initToolBar();

        initNameText();
        initSexRadio();
        initAgeSpinner();

        initInterestStateText();
        initInterestRater();

        initNoteText();

        initAddTagButton();
        initTagContainer();

        initOkButton();
        initCancelButton();
        initDeleteButton();
    }

    private void initPerson() {

        Intent intent = getIntent();
        String personId = intent.getStringExtra(Person.PERSON);
        if (personId == null) {
            mPerson = new Person();
        } else {
            mPerson = RVData.getInstance().personList.getById(personId);
            if (mPerson == null) {
                mPerson = new Person();
            }
        }

    }

    private void initToolBar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        if (toolbar != null) {

            toolbar.setTitle(R.string.person);
            toolbar.setTitleTextColor(Color.WHITE);
        }
    }

    private EditText nameText;
    private void initNameText() {

        nameText = (EditText) findViewById(R.id.name_text);

        if ( mPerson != null ) {
            nameText.setText(mPerson.getName());
        }

    }

    private RadioButton maleButton, femaleButton;
    private void initSexRadio() {

        maleButton = (RadioButton) findViewById(R.id.male_button);
        femaleButton = (RadioButton) findViewById(R.id.female_button);

        if (mPerson != null) {

            if (mPerson.getSex() == MALE) {
                maleButton.setChecked(true);
            } else if (mPerson.getSex() == FEMALE) {
                femaleButton.setChecked(true);
            }
        }

        maleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b) {
                    mPerson.setSex(MALE);
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

        ageSpinner = (Spinner) findViewById(R.id.age_spinner);

        ArrayAdapter<CharSequence> adapter
                = ArrayAdapter.createFromResource(this,
                R.array.age_array,
                android.R.layout.simple_spinner_item);

        ageSpinner.setAdapter(adapter);

        if (mPerson != null) {
            ageSpinner.setSelection(mPerson.getAge().num());
        }

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

    private Button[] raterButtons;
    private void initInterestRater() {

        LinearLayout raterContainer = (LinearLayout) findViewById(R.id.rater_container);
        raterContainer.removeAllViews();

        raterButtons = new Button[8];
        for ( int i = 0 ; i < 7 ; i++ ) {
            raterButtons[i] = new Button(this);
            raterButtons[i].setBackgroundResource(buttonRes[0]);
            raterButtons[i].setTag(i);

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(40, 40);

            raterButtons[i].setLayoutParams(params);
            raterContainer.addView(raterButtons[i]);

            if ( i < 6 ) {
                View view = new View(this);
                FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(15, 30);
                view.setLayoutParams(params2);
                raterContainer.addView(view);
            }
            raterButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int tag = Integer.parseInt(view.getTag().toString());
                    Person.Interest interest = Person.Interest.getEnum(tag);

                    mPerson.setInterest(interest);
                    refreshRater(interest);

                }
            });
        }
        refreshRater(mPerson.getInterest());

    }

    private void refreshRater(Person.Interest interest) {

        int num = interest.num();

        interestStateText.setText(getResources().getStringArray(R.array.interest_array)[mPerson.getInterest().num()]);

        for (int i = 0 ; i <= num ; i++) {
            raterButtons[i].setBackgroundResource(Constants.buttonRes[num]);
        }

        for (int i = num + 1 ; i < 7 ; i++ ) {
            raterButtons[i].setBackgroundResource(Constants.buttonRes[0]);
        }
    }

    private TextView interestStateText;
    private void initInterestStateText() {

        interestStateText = (TextView) findViewById(R.id.interest_state_text);

    }

    private AutoCompleteTextView noteText;
    private void initNoteText() {

        noteText = (AutoCompleteTextView) findViewById(R.id.note_text);
        noteText.setText(mPerson.getNote());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, RVData.getInstance().noteCompleteList.getList());
        noteText.setAdapter(adapter);
        noteText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                mPerson.setNote(editable.toString());
            }
        });
    }

    private void initOkButton() {

        Button okButton = (Button) findViewById(R.id.ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mPerson.setName(nameText.getText().toString());
                mPerson.setNote(noteText.getText().toString());

                if (getIntent().getIntExtra(Constants.REQUEST_CODE, 0) == Constants.PersonCode.ADD_PERSON_REQUEST_CODE) {
                    RVData.getInstance().personList.addOrSet(mPerson);

                    RVData.getInstance().noteCompleteList.addToBoth(mPerson.getNote());

                    Intent intent = new Intent();
                    intent.putExtra(Person.PERSON, mPerson.getId());
                    setResult(Constants.PersonCode.PERSON_ADDED_RESULT_CODE, intent);
                } else if (getIntent().getIntExtra(Constants.REQUEST_CODE, 0) == Constants.PersonCode.EDIT_PERSON_REQUEST_CODE) {

                    RVData.getInstance().personList.addOrSet(mPerson);

                    RVData.getInstance().noteCompleteList.addToBoth(mPerson.getNote());

                    Intent intent = new Intent();
                    intent.putExtra(Person.PERSON, mPerson.getId());
                    setResult(Constants.PersonCode.PERSON_EDITED_RESULT_CODE, intent);

                }

                finish();
            }
        });
    }

    private void initCancelButton() {

        Button cancelButton = (Button) findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initDeleteButton() {

        Button deleteButton = (Button) findViewById(R.id.delete_button);
        if (RVData.getInstance().personList.contains(mPerson)) {
            deleteButton.setVisibility(View.VISIBLE);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        } else {
            deleteButton.setVisibility(View.INVISIBLE);
        }
    }

    private void initAddTagButton() {

        Button addTagButton = (Button) findViewById(R.id.add_tag_button);
        addTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TagDialog.newInstance(new TagDialog.OnTagSelectedListener() {
                    @Override
                    public void onTagSelect(Tag tag) {

                        tagContainer.addTag(tag);
                        mPerson.addTagIds(tag.getId());
                    }
                },
                mPerson.getTagIds())
                        .show(getFragmentManager(), null);
            }
        });
    }

    private TagContainer tagContainer;
    private void initTagContainer() {

        tagContainer = (TagContainer) findViewById(R.id.tag_container);
        tagContainer.setTagIds(mPerson.getTagIds(), true);
        tagContainer.setOnTagRemoveListener(new TagContainer.OnTagRemoveListener() {
            @Override
            public void onTagRemove(Tag tag) {
                mPerson.getTagIds().remove(tag.getId());
            }
        });
    }
}
