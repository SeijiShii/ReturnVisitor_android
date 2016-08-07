package net.c_kogyo.returnvisitor.activity;

import android.animation.Animator;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.maps.model.LatLng;

import net.c_kogyo.returnvisitor.R;
import net.c_kogyo.returnvisitor.data.Person;
import net.c_kogyo.returnvisitor.data.Place;
import net.c_kogyo.returnvisitor.data.Placement;
import net.c_kogyo.returnvisitor.data.RVData;
import net.c_kogyo.returnvisitor.data.Tag;
import net.c_kogyo.returnvisitor.data.Visit;
import net.c_kogyo.returnvisitor.dialog.PlaceDialog;
import net.c_kogyo.returnvisitor.dialog.PlacementDialog;
import net.c_kogyo.returnvisitor.dialog.PlacementSelectDialog;
import net.c_kogyo.returnvisitor.dialog.SelectPersonDialog;
import net.c_kogyo.returnvisitor.service.FetchAddressIntentService;
import net.c_kogyo.returnvisitor.view.BaseAnimateView;
import net.c_kogyo.returnvisitor.view.PersonCell;
import net.c_kogyo.returnvisitor.view.PlacementCell;
import net.c_kogyo.returnvisitor.view.TagContainer;
import net.c_kogyo.returnvisitor.view.TagView;

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
    private ArrayList<String> createdPersonIds;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.record_visit_activity);

        mVisit = new Visit();
        createdPersonIds = new ArrayList<>();

        initBroadcastingForAddress();
        initPlace();

        initToolBar();
        initPlaceText();
        initDateText();
        initTimeText();
        initPersonContainer();
        initPlacementContainer();
        initNoteText();

        initOkButton();
        initCancelButton();
        initDeleteButton();


    }

    private void initPlace() {

        Intent intent = getIntent();

        String placeId = intent.getStringExtra(Place.PLACE);
        if (placeId != null) {
            // マーカのクリックから来た場合
            mPlace = RVData.getInstance().placeList.getById(placeId);
            mVisit.setPlaceId(mPlace.getId());
        } else {
            // 地図上のロングクリックから来た場合
            double latitude = intent.getDoubleExtra(MapActivity.LATITUDE, 1000);
            double longitude = intent.getDoubleExtra(MapActivity.LONGITUDE, 1000);

            if ( latitude < 1000 && longitude < 1000 ) {
                mPlace = new Place(new LatLng(latitude, longitude));
                mVisit.setPlaceId(mPlace.getId());

                startFetchAddressIntentService();

            }
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
        }
    }

    private TextView placeText;
    private void initPlaceText() {

        placeText = (TextView) findViewById(R.id.place_text);
        setPlaceText();
        placeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlaceDialog.getInstance(RecordVisitActivity.this, mPlace, new PlaceDialog.OnOkClickListener() {
                    @Override
                    public void onOkClick(Place place) {
                        setPlaceText();
                    }
                }).show(getFragmentManager(), "");
            }
        });
    }

    private void setPlaceText() {
        if (mPlace != null) {
            if (!mPlace.getName().equals("")) {
                placeText.setText(mPlace.getName());
            } else if (mPlace.getAddress() != null){
                placeText.setText(mPlace.getAddress());
            }
        }
    }

    private TextView dateText;
    private void initDateText() {

        dateText = (TextView) findViewById(R.id.date_text);

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
                        createdPersonIds,
                        new SelectPersonDialog.OnPersonSelectedListener() {
                            @Override
                            public void onSelected(String personId) {
                                mVisit.addPersonId(personId);
                                updatePersonContainer();
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
    }

    private ArrayList<String> getPersonIdsInContainer() {

        ArrayList<String> ids = new ArrayList<>();

        for ( int i = 0 ; i < personContainer.getChildCount() ; i++ ) {

            PersonCell cell = (PersonCell) personContainer.getChildAt(i);
            ids.add(cell.getPerson().getId());
        }
        return ids;
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

                PlacementSelectDialog.getInstance(new PlacementDialog.OnAddPlacementListener() {
                    @Override
                    public void onAdd(Placement placement) {
                        mVisit.addPlacement(placement);
                        placementContainer.addView(new PlacementCell(placement,
                                RecordVisitActivity.this, BaseAnimateView.InitialHeightCondition.FROM_0,
                                new PlacementCell.PostRemoveAnimationListener() {
                            @Override
                            public void postAnimation(PlacementCell cell) {
                                placementContainer.removeView(cell);
                                mVisit.removePlacement(cell.getPlacement());
                                updatePlacementTouchText();
                            }
                        }));
                        updatePlacementTouchText();
                    }
                }).show(getFragmentManager(), null);
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
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, RVData.noteCompleteList.getList());
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

                mPlace.addPersonIds(mVisit.getPersonIds());

                RVData.visitList.add(mVisit);
                RVData.placeList.add(mPlace);
                RVData.noteCompleteList.addToBoth(mVisit.getNote());

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
        if (RVData.getInstance().visitList.contains(mVisit)) {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.PersonCode.ADD_PERSON_REQUEST_CODE) {
            if (resultCode == Constants.PersonCode.PERSON_ADDED_RESULT_CODE) {

                String id = data.getStringExtra(Person.PERSON);
                if (id != null) {
                    mVisit.addPersonId(id);
                    updatePersonContainer();
                    createdPersonIds.add(id);
                }


            }
        }
    }

    //TODO RecordVisitに再訪問数を追加
}
