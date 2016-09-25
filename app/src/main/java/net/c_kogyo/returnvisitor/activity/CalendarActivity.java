package net.c_kogyo.returnvisitor.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.c_kogyo.returnvisitor.R;
import net.c_kogyo.returnvisitor.data.AggregationOfDay;
import net.c_kogyo.returnvisitor.data.RVData;
import net.c_kogyo.returnvisitor.util.DateTimeText;

import java.util.ArrayList;
import java.util.Calendar;

import static android.view.MotionEvent.ACTION_BUTTON_PRESS;
import static android.view.MotionEvent.ACTION_BUTTON_RELEASE;

/**
 * Created by SeijiShii on 2016/09/23.
 */

public class CalendarActivity extends AppCompatActivity{

    private static final String CALENDAR_DEBUG_TAG = "CalendarDebugTAG";

    private Calendar mDate;
    private ArrayList<Calendar> mDatesWithData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.calendar_activity);


    }

    @Override
    protected void onResume() {
        super.onResume();

        mDatesWithData = RVData.getInstance().getDatesWithData();

        setDate();

        initToolBar();
        initCalendarPager();

        initMonthText();
        initDayRow();
        initLeftButton();
        initRightButton();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        boolean result = true;

        switch (id) {
            case android.R.id.home:
                finish();
                break;
            default:
                result = super.onOptionsItemSelected(item);
        }

        return result;

    }

    private void setDate() {

        mDate = Calendar.getInstance();

        long dLong = getIntent().getLongExtra(Constants.DATE_LONG, 0);
        if (dLong != 0) {
            mDate.setTimeInMillis(dLong);
        }

    }

    private Toolbar toolbar;
    private void initToolBar(){

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
//        toolbar.inflateMenu(R.menu.return_visitor_menu);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private TextView monthText;
    private void initMonthText() {

        monthText = (TextView) findViewById(R.id.month_text);
        updateMonthText();
    }

    private void updateMonthText() {

        String monthString = android.text.format.DateFormat.format("yyyy, MMM", mDate).toString();
        monthText.setText(monthString);
    }

    private LinearLayout dayRow;
    private void initDayRow() {

        dayRow = (LinearLayout) findViewById(R.id.day_row);

        Calendar dayCal = Calendar.getInstance();
        dayCal.setTimeInMillis(0);
        dayCal.add(Calendar.DAY_OF_MONTH, 5);

        TextView[] dayCells = new TextView[7];
        for ( int i = 0 ; i < 7 ; i++ ) {

            dayCells[i] = new TextView(this);
            dayCells[i].setTextColor(Color.WHITE);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
            params.weight = 1;

            dayCells[i].setLayoutParams(params);
            dayCells[i].setGravity(Gravity.CENTER);

            String dayText = DateFormat.format("EEE", dayCal).toString();

            dayCells[i].setText(dayText);


            dayRow.addView(dayCells[i]);

            dayCal.add(Calendar.DAY_OF_MONTH, 1);
        }

    }

    private ViewPager calendarPager;
    private CalendarPagerAdapter adapter;
    private void initCalendarPager() {

        calendarPager = (ViewPager) findViewById(R.id.calendar_pager);
        adapter = new CalendarPagerAdapter();

        calendarPager.setAdapter(adapter);
        calendarPager.setCurrentItem(adapter.getInitialPosition());

        calendarPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                mDate = adapter.getMonth(position);
                updateMonthText();

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void initLeftButton () {

        Button leftButton = (Button) findViewById(R.id.left_button);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                calendarPager.setCurrentItem(calendarPager.getCurrentItem() - 1, true);
            }
        });
    }

    private void initRightButton () {

        Button rightButton = (Button) findViewById(R.id.right_button);
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                calendarPager.setCurrentItem(calendarPager.getCurrentItem() + 1, true);
            }
        });
    }
    class CalendarPagerAdapter extends PagerAdapter {

        private final int MONTH_COUNT =50;

        public CalendarPagerAdapter() {

        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public int getCount() {
            return MONTH_COUNT;
        }

        public Object instantiateItem(ViewGroup container, int position) {

            Log.d(CALENDAR_DEBUG_TAG, "instantiateItem() called: " + getMonth(position).get(Calendar.MONTH));

            CalendarView calendarView = new CalendarView(CalendarActivity.this, getMonth(position));
            container.addView(calendarView);
            return calendarView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
            Log.d(CALENDAR_DEBUG_TAG, "Item destroyed pos: " + position);
        }

        private Calendar getMonth(int pos) {

            Calendar month = Calendar.getInstance();

            month.add(Calendar.MONTH, pos - (MONTH_COUNT / 2));

            return month;
        }

        int getInitialPosition() {
            return MONTH_COUNT / 2;
        }
    }

    class CalendarView extends LinearLayout{

        private Calendar mMonth;

        public CalendarView(Context context, Calendar month) {
            super(context);

            mMonth = month;

            initCommon();
        }

        public CalendarView(Context context, AttributeSet attrs) {
            super(context, attrs);

            initCommon();
        }

        int rowNum;
        private void initCommon() {

            LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            this.setLayoutParams(params);

//            this.setBackgroundColor(Color.RED);

            this.setOrientation(VERTICAL);

//            rows = new ArrayList<>();

            if (mMonth == null) return;

            Calendar dayCal = (Calendar) mMonth.clone();

            Calendar lastDayCal = (Calendar) mMonth.clone();
            lastDayCal.add(Calendar.MONTH, 1);
            lastDayCal.set(Calendar.DAY_OF_MONTH, 1);
            lastDayCal.add(Calendar.DAY_OF_MONTH, -1);

            int dayCount = lastDayCal.get(Calendar.DAY_OF_MONTH);

            Calendar lastMonthCal = (Calendar) mMonth.clone();
            lastMonthCal.set(Calendar.DAY_OF_MONTH, 1);
            lastMonthCal.add(Calendar.DAY_OF_MONTH, -1);
            int lastDay = lastMonthCal.get(Calendar.DAY_OF_WEEK);

            rowNum = -1;

            if (lastDay < 7) {

                addNewRow();
                for (int i = 0 ; i < lastDay ; i++) {

                    addBlank();
                }
            }


            for (int i = 1 ; i <= dayCount ; i++ ) {

                dayCal.set(Calendar.DAY_OF_MONTH, i);

                if (dayCal.get(Calendar.DAY_OF_WEEK) <= 1) {
                    addNewRow();

                }

                CalendarCell cell = new CalendarCell(CalendarActivity.this, dayCal);
//                rows.get(rows.size() - 1).addView(cell);

                ((LinearLayout) this.getChildAt(rowNum)).addView(cell);
            }

            for (int i = dayCal.get(Calendar.DAY_OF_WEEK) + 1 ; i <= 7 ; i++ ) {
                addBlank();
            }
        }

        private void addNewRow() {

            LinearLayout row = new LinearLayout(CalendarActivity.this);
            LayoutParams rowParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
            rowParams.weight = 1;

            row.setOrientation(HORIZONTAL);
            row.setLayoutParams(rowParams);
//            row.setBackgroundColor(Color.BLUE);

            this.addView(row);
            rowNum++;
        }

        private void addBlank() {

            View blank = new View(CalendarActivity.this);
            LinearLayout.LayoutParams blankParams = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
            blankParams.weight = 1;
            blank.setLayoutParams(blankParams);

//            blank.setBackgroundColor(Color.GREEN);

            ((LinearLayout) this.getChildAt(rowNum)).addView(blank);
        }


    }

    class CalendarCell extends FrameLayout{

        private Calendar mDate;
        private AggregationOfDay aggregation;

        public CalendarCell(Context context, Calendar date) {

            super(context);

            mDate = date;

            initCommon();

        }

        public CalendarCell(Context context, AttributeSet attrs) {
            super(context, attrs);
        }


        private void initCommon() {

            aggregation = new AggregationOfDay(mDate);

            View.inflate(CalendarActivity.this, R.layout.calendar_cell, this);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
            params.weight = 1;
            this.setLayoutParams(params);


            initDateText();
            initTimeBar();
            initTimeText();
            initPLCMarker();
            initRVMarker();
            initVideoMarker();

            if (RVData.getInstance().theDayHasData(mDate)) {

                setOnTouchListener(new OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {

                        switch (motionEvent.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                Log.d(CALENDAR_DEBUG_TAG, "Cell pressed down!");
                                CalendarCell.this.setAlpha(0.5f);

                                Intent workIntent = new Intent(CalendarActivity.this, WorkPagerActivity.class);

                                if (getIntent().getAction().equals(Constants.CalendarActions.START_CALENDAR_ACTION)) {

                                    workIntent.putExtra(Constants.DATE_LONG, mDate.getTimeInMillis());
                                    setResult(Constants.CalendarActions.PRESS_DATE_RESULT_CODE, workIntent);
                                    finish();
                                }

                                return true;

                            case MotionEvent.ACTION_UP:

                                Log.d(CALENDAR_DEBUG_TAG, "Cell action up!");
                                CalendarCell.this.setAlpha(1f);

                                return true;
                        }


                        return false;
                    }
                });
            }

        }

        private void initDateText() {

            TextView dateText = (TextView) findViewById(R.id.date_text);

            int dayNum = mDate.get(Calendar.DAY_OF_MONTH);

            dateText.setText(String.valueOf(dayNum));

        }

        private void initTimeText() {

            TextView timeText = (TextView) findViewById(R.id.time_text);

            if (RVData.getInstance().theDayHasData(mDate)) {
//                timeText.setVisibility(VISIBLE);

                long time = RVData.getInstance().workList.getTimeInDay(mDate);
                String timeString = DateTimeText.getDurationString(time, false);

                timeText.setText(timeString);

            } else {
//                timeText.setVisibility(INVISIBLE);
            }

        }

        private void initTimeBar() {

            RelativeLayout timeBar = (RelativeLayout) findViewById(R.id.time_bar);

            if (RVData.getInstance().theDayHasData(mDate)) {
                timeBar.setVisibility(VISIBLE);
            } else {
                timeBar.setVisibility(INVISIBLE);
            }
        }

        private void initPLCMarker() {
            View plcMarker = findViewById(R.id.plc_marker);
            if (aggregation.getPlacementCount() > 0) {
                plcMarker.setVisibility(VISIBLE);
            } else {
                plcMarker.setVisibility(INVISIBLE);
            }
        }

        private void initRVMarker() {
            View rvMarker = findViewById(R.id.rv_marker);
            if (aggregation.getRvCount() > 0) {
                rvMarker.setVisibility(VISIBLE);
            } else {
                rvMarker.setVisibility(INVISIBLE);
            }
        }

        private void initVideoMarker() {
            View videoMarker = findViewById(R.id.video_marker);
            if (aggregation.getShowVideoCount() > 0) {
                videoMarker.setVisibility(VISIBLE);
            } else {
                videoMarker.setVisibility(INVISIBLE);
            }
        }
    }
}
