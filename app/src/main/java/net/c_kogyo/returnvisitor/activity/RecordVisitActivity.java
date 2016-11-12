package net.c_kogyo.returnvisitor.activity;

import android.animation.Animator;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.maps.model.LatLng;

import net.c_kogyo.returnvisitor.R;
import net.c_kogyo.returnvisitor.data.Person;
import net.c_kogyo.returnvisitor.data.Place;
import net.c_kogyo.returnvisitor.data.Placement;
import net.c_kogyo.returnvisitor.data.RVData;
import net.c_kogyo.returnvisitor.data.Visit;
import net.c_kogyo.returnvisitor.dialog.PlaceDialog;
import net.c_kogyo.returnvisitor.dialog.PlacementSelectDialog;
import net.c_kogyo.returnvisitor.dialog.SelectPersonDialog;
import net.c_kogyo.returnvisitor.dialog.SelectPlaceDialog;
import net.c_kogyo.returnvisitor.service.FetchAddressIntentService;
import net.c_kogyo.returnvisitor.view.BaseAnimateView;
import net.c_kogyo.returnvisitor.view.PersonCell;
import net.c_kogyo.returnvisitor.view.PlacementCell;
import net.c_kogyo.returnvisitor.view.RefreshableCounterView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by SeijiShii on 2016/07/20.
 */

public class RecordVisitActivity extends AppCompatActivity {

    // Visitが保存されるタイミングはOKが押されるとき
    private Visit mVisit;
    private Place mPlace;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.record_visit_activity);

        onActions();

        initBroadcastingForAddress();

        initToolBar();
        initPlaceText();
        initDateText();
        initTimeText();
        initPersonContainer();
        initRCountCounter();
        initBSSwitch();
        initPlacementContainer();
        initNoteText();

        initOkButton();
        initCancelButton();
        initDeleteButton();

    }

    private void onActions() {

        Intent intent = getIntent();
        String action = intent.getAction();
        String placeId = intent.getStringExtra(Place.PLACE);

        switch (action) {
            case Constants.RecordVisitActions.NEW_PLACE_ACTION:

                // 地図上のロングクリックから来た場合

                mVisit = new Visit();

                double latitude = intent.getDoubleExtra(Constants.SharedPrefTags.LATITUDE, 1000);
                double longitude = intent.getDoubleExtra(Constants.SharedPrefTags.LONGITUDE, 1000);

                if ( latitude < 1000 && longitude < 1000 ) {
                    mPlace = new Place(new LatLng(latitude, longitude));
                    mVisit.setPlaceId(mPlace.getId());

                    startFetchAddressIntentService();

                }

                break;

            case Constants.RecordVisitActions.NEW_VISIT_ACTION_WITH_PLACE:

                // マーカのクリックから来た場合
                mVisit = new Visit();

                mPlace = RVData.getInstance().placeList.getById(placeId);
                mVisit.setPlaceId(mPlace.getId());

                break;

            case Constants.RecordVisitActions.EDIT_VISIT_ACTION:

                // Visitだけで来た

                String visitId = intent.getStringExtra(Visit.VISIT);

                mVisit = RVData.getInstance().visitList.getById(visitId);

                mPlace = RVData.getInstance().placeList.getById(mVisit.getPlaceId());

                break;

            case Constants.RecordVisitActions.NEW_VISIT_ACTION_NO_PLACE:

                // WorkActivityなど場所指定なしで来た場合

                mVisit = new Visit();

                // 特にDateが送られてこなければ今日の日付になる
                long dLong = intent.getLongExtra(Constants.DATE_LONG, 0);
                if (dLong != 0) {

                    Calendar setDate = Calendar.getInstance();
                    setDate.setTimeInMillis(dLong);

                    mVisit.getStart().set(Calendar.YEAR, setDate.get(Calendar.YEAR));
                    mVisit.getStart().set(Calendar.MONTH, setDate.get(Calendar.MONTH));
                    mVisit.getStart().set(Calendar.DAY_OF_MONTH, setDate.get(Calendar.DAY_OF_MONTH));

                    mVisit.setEnd((Calendar) mVisit.getStart().clone());
                }

                break;
        }

    }

    public static final String LAT_LNG_EXTRA = "lat_lng_extra";

    private void startFetchAddressIntentService() {

        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(LAT_LNG_EXTRA, mPlace.getLatLng());
        startService(intent);

        // ドラッグで家を動かした後の処理も考えておく
        // addressTextをnullにすればもう一度リクエストする
    }

    private void initBroadcastingForAddress() {

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String addressString = intent.getStringExtra(FetchAddressIntentService.ADDRESS_DATA);
                mPlace.setAddress(addressString);
                placeText.setText(mPlace.getAddress());
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(FetchAddressIntentService.SEND_ADDRESS));

    }

    private void initToolBar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        if (toolbar != null) {

            toolbar.setTitle(R.string.record_visit);
            toolbar.setTitleTextColor(Color.WHITE);
        }
    }

    private TextView placeText;
    private void initPlaceText() {

        placeText = (TextView) findViewById(R.id.place_text);
        updatePlaceText();
        placeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mPlace == null) {

                    SelectPlaceDialog.newInstance(mVisit.getPersonIds(), new SelectPlaceDialog.OnPlaceSelectedListener() {
                        @Override
                        public void onSelected(Place place) {

                            mPlace = place;
                            mVisit.setPlaceId(place.getId());
                            updatePlaceText();
                        }
                    }).show(getFragmentManager(), null);

                } else {
                    PlaceDialog.getInstance(RecordVisitActivity.this, mPlace, new PlaceDialog.OnOkClickListener() {
                        @Override
                        public void onOkClick(Place place) {
                            updatePlaceText();
                        }
                    }).show(getFragmentManager(), null);
                }
            }
        });
    }

    private void updatePlaceText() {

        if (mPlace != null) {
            placeText.setText(mPlace.toString());
        }
    }

    private TextView dateText;
    private void initDateText() {

        dateText = (TextView) findViewById(R.id.month_text);

        DateFormat format = android.text.format.DateFormat.getDateFormat(getApplicationContext());
        String dateString = format.format(mVisit.getStart().getTime());
        dateText.setText(dateString);

        initDatePicker();

    }

    private void initDatePicker() {

        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(RecordVisitActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

                                mVisit.getStart().set(Calendar.YEAR, i);
                                mVisit.getStart().set(Calendar.MONTH, i1);
                                mVisit.getStart().set(Calendar.DAY_OF_MONTH, i2);
                                initDateText();
                            }
                        },
                        mVisit.getStart().get(Calendar.YEAR),
                        mVisit.getStart().get(Calendar.MONTH),
                        mVisit.getStart().get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private TextView timeText;
    private void initTimeText() {

        timeText = (TextView) findViewById(R.id.time_text);

        DateFormat format = android.text.format.DateFormat.getTimeFormat(getApplicationContext());
        String timeString = format.format(mVisit.getStart().getTime());
        timeText.setText(timeString);

        initTimePicker();

    }

    private void initTimePicker() {

        timeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new TimePickerDialog(RecordVisitActivity.this,
                        new TimePickerDialog.OnTimeSetListener(){
                            @Override
                            public void onTimeSet(TimePicker timePicker, int i, int i1) {

                                mVisit.getStart().set(Calendar.HOUR_OF_DAY, i);
                                mVisit.getStart().set(Calendar.MINUTE, i1);
                                initTimeText();
                            }
                        },
                        mVisit.getStart().get(Calendar.HOUR_OF_DAY),
                        mVisit.getStart().get(Calendar.MINUTE),
                        true).show();
            }
        });

    }

    private LinearLayout personContainer;
    private void initPersonContainer() {

        personContainer = (LinearLayout) findViewById(R.id.person_container);
        personContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SelectPersonDialog.getInstance(mVisit,
                        mPlace,
                        new SelectPersonDialog.OnPersonSelectedListener() {
                            @Override
                            public void onSelected(String personId) {
                                mVisit.addPersonId(personId);
                                updatePersonContainer();
                                rvCountCounter.setCount(mVisit.refreshRVCount(RecordVisitActivity.this));
                            }
                        }).show(getFragmentManager(), null);

            }
        });

        //会えた人を列挙する
        for ( String id : mVisit.getPersonIds() ) {

            Person person = RVData.getInstance().personList.getById(id);
            if (person != null) {

                PersonCell cell = new PersonCell(this,
                        person,
                        BaseAnimateView.InitialHeightCondition.VIEW_HEIGHT,
                        new PersonCell.PostRemoveAnimationListener() {
                            @Override
                            public void postAnimation(PersonCell cell1) {
                                removePersonCellFromContainer(cell1);
                            }
                        });
                personContainer.addView(cell);
            }
        }
    }

    private void updatePersonContainer() {

        // SeenPersonDialogから帰ってきたときの処理

        ArrayList<String> addedIds = new ArrayList<>(mVisit.getPersonIds());
        addedIds.removeAll(getPersonIdsInContainer());

        ArrayList<String> removedIds = new ArrayList<>(getPersonIdsInContainer());
        removedIds.removeAll(mVisit.getPersonIds());

        for ( String id : addedIds ) {

            Person person = RVData.getInstance().personList.getById(id);
            if (person != null) {

                PersonCell cell = new PersonCell(this,
                        person,
                        BaseAnimateView.InitialHeightCondition.FROM_0,
                        new PersonCell.PostRemoveAnimationListener() {
                            @Override
                            public void postAnimation(PersonCell cell1) {
                                removePersonCellFromContainer(cell1);
                            }
                        });
                personContainer.addView(cell);
            }
        }

        for ( String id : removedIds ) {

            for ( int i = 0 ; i < personContainer.getChildCount() ; i++ ) {

                final PersonCell cell = (PersonCell) personContainer.getChildAt(i);
                if (cell.getPerson().getId().equals(id)) {
                    cell.changeViewHeight(BaseAnimateView.AnimateCondition.FROM_HEIGHT_TO_O, true,
                            new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animator) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animator) {

                                    personContainer.removeView(cell);

                                }

                                @Override
                                public void onAnimationCancel(Animator animator) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animator) {

                                }
                            }, 5);
                }
            }
        }

        updatePersonTouchText();
        updateBSSwitch();

    }

    private void updatePersonTouchText() {

        // PersonがいればTouch Hereが消えるようにする
        TextView touchText = (TextView) findViewById(R.id.person_touch_text);
        if (mVisit.getPersonIds().size() > 0) {
            touchText.setVisibility(View.INVISIBLE);
        } else {
            touchText.setVisibility(View.VISIBLE);
        }
    }

    private void removePersonCellFromContainer(PersonCell cell) {
        personContainer.removeView(cell);
        mVisit.getPersonIds().remove(cell.getPerson().getId());
        updatePersonTouchText();
        rvCountCounter.setCount(mVisit.refreshRVCount(this));
        updateBSSwitch();
    }

    private ArrayList<String> getPersonIdsInContainer() {

        ArrayList<String> ids = new ArrayList<>();

        for ( int i = 0 ; i < personContainer.getChildCount() ; i++ ) {

            PersonCell cell = (PersonCell) personContainer.getChildAt(i);
            ids.add(cell.getPerson().getId());
        }
        return ids;
    }

    //再訪問数
    private RefreshableCounterView rvCountCounter;
    private void initRCountCounter() {

        rvCountCounter = (RefreshableCounterView) findViewById(R.id.rv_count_text);
        rvCountCounter.setCount(mVisit.refreshRVCount(this));
        rvCountCounter.setOnRefreshPressedListener(new RefreshableCounterView.OnRefreshPressedListener() {
            @Override
            public void onRefreshPress(RefreshableCounterView refreshableCounterView) {

                refreshableCounterView.setCount(mVisit.refreshRVCount(RecordVisitActivity.this));
            }
        });
    }

    private SwitchCompat bsSwitch;
    private RelativeLayout bsContainer;
    private void initBSSwitch() {

        bsContainer = (RelativeLayout) findViewById(R.id.bs_container);
        bsSwitch = (SwitchCompat) findViewById(R.id.is_bs_switch);
        updateBSSwitch();
    }

    private void updateBSSwitch() {

        if (mVisit.canBeBS(this)) {
            bsSwitch.setVisibility(View.VISIBLE);
            bsContainer.getLayoutParams().height = 60;
            bsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    mVisit.setBS(b);
                }
            });
        } else {
            bsSwitch.setVisibility(View.INVISIBLE);
            bsContainer.getLayoutParams().height = 0;
        }
    }

    // Placement Frame
    private LinearLayout placementContainer;
    private TextView placementTouchText;
    private void initPlacementContainer() {

        placementContainer = (LinearLayout) findViewById(R.id.placement_container);
        placementTouchText = (TextView) findViewById(R.id.placement_touch_text);
        updatePlacementTouchText();

        placementContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PlacementSelectDialog.getInstance().show(getFragmentManager(), null);
            }
        });
    }

    private void updatePlacementTouchText() {

        if (mVisit.getPlacements().size() > 0) {
            placementTouchText.setVisibility(View.INVISIBLE);
        } else {
            placementTouchText.setVisibility(View.VISIBLE);
        }
    }

    private void initNoteText() {

        AutoCompleteTextView noteText = (AutoCompleteTextView) findViewById(R.id.visit_note_text);
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

                mVisit.setNote(editable.toString());
            }
        });
    }

    private void initOkButton() {

        Button okButton = (Button) findViewById(R.id.ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 場所NULLでも登録できるように
                if (mPlace != null) {
                    mPlace.addPersonIds(mVisit.getPersonIds());
                    RVData.getInstance().placeList.addOrSet(mPlace);
                }

                mVisit.setRvCount(rvCountCounter.getCount());

                RVData.getInstance().visitList.addOrSet(mVisit);
                RVData.getInstance().noteCompleteList.addToBoth(mVisit.getNote());

                switch (getIntent().getAction()) {

                    case Constants.RecordVisitActions.EDIT_VISIT_ACTION:

                        Intent intent = new Intent();
                        intent.putExtra(Visit.VISIT, mVisit.getId());
                        setResult(Constants.RecordVisitActions.VISIT_CHANGED_RESULT_CODE, intent);

                        break;

                    case  Constants.RecordVisitActions.NEW_VISIT_ACTION_NO_PLACE:
                        // finish()後の戻り先。MapActivityまたはWorkActivity
                        Intent intent1 = new Intent();
                        intent1.putExtra(Visit.VISIT, mVisit.getId());
                        setResult(Constants.RecordVisitActions.VISIT_ADDED_RESULT_CODE, intent1);

                        break;
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

        final Button deleteButton = (Button) findViewById(R.id.delete_button);
        if ( getIntent().getAction().equals(Constants.RecordVisitActions.EDIT_VISIT_ACTION)) {
            deleteButton.setVisibility(View.VISIBLE);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    deleteConfirm();
                }
            });
        } else {
            deleteButton.setVisibility(View.INVISIBLE);
        }
    }

    private void deleteConfirm() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        String deleteVisitTitle = getString(R.string.visit);
        deleteVisitTitle = getString(R.string.delete_title, deleteVisitTitle);
        builder.setTitle(deleteVisitTitle);

        String deleteVisitMessage = getString(R.string.visit);
        deleteVisitMessage = getString(R.string.delete_message, deleteVisitMessage);
        builder.setMessage(deleteVisitMessage);

        builder.setNegativeButton(R.string.cancel_text, null);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                RVData.getInstance().visitList.removeFromBoth(mVisit);

                Intent deleteVisitIntent = new Intent();
                deleteVisitIntent.putExtra(Visit.VISIT, mVisit.getId());

                setResult(Constants.RecordVisitActions.DELETE_VISIT_RESULT_CODE, deleteVisitIntent);
                finish();
            }
        });

        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.PersonCode.ADD_PERSON_REQUEST_CODE) {
            if (resultCode == Constants.PersonCode.PERSON_ADDED_RESULT_CODE) {

                String id = data.getStringExtra(Person.PERSON);
                if (id != null) {
                    mVisit.addPersonId(id);
                    updatePersonContainer();

                    if  (mPlace != null) {
                        mPlace.addPersonId(id);
                    }
                    rvCountCounter.setCount(mVisit.refreshRVCount(this));

                }

            }
        } else if (requestCode == Constants.PersonCode.EDIT_PERSON_REQUEST_CODE) {
            if (resultCode == Constants.PersonCode.PERSON_EDITED_RESULT_CODE) {
                String id = data.getStringExtra(Person.PERSON);
                if (id != null) {
                    mVisit.addPersonId(id);
                    updatePersonContainer();
                    if  (mPlace != null) {
                        mPlace.addPersonId(id);
                    }
                    rvCountCounter.setCount(mVisit.refreshRVCount(this));


                }
            }
        } else if (requestCode == Constants.PlacementCode.PLACEMENT_REQUEST_CODE) {
            if (resultCode == Constants.PlacementCode.PLACEMENT_ADDED_RESULT_CODE) {

                Placement placement = PlacementActivity.getPlacement();
                mVisit.addPlacement(placement);
                addPlacementCell(placement);

            }
        }
    }

    private void addPlacementCell(Placement placement) {

        PlacementCell placementCell
                = new PlacementCell(placement,
                    this,
                    BaseAnimateView.InitialHeightCondition.FROM_0,
                    new PlacementCell.PostRemoveAnimationListener() {
                        @Override
                        public void postAnimation(PlacementCell cell) {
                            placementContainer.removeView(cell);
                            mVisit.removePlacement(cell.getPlacement());
                            updatePlacementTouchText();
                        }
                    });
        placementContainer.addView(placementCell);
        updatePlacementTouchText();
    }


}
