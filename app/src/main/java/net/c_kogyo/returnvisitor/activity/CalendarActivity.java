package net.c_kogyo.returnvisitor.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import net.c_kogyo.returnvisitor.R;
import net.c_kogyo.returnvisitor.data.AggregationOfDay;
import net.c_kogyo.returnvisitor.data.AggregationOfMonth;
import net.c_kogyo.returnvisitor.util.AdMobHelper;
import net.c_kogyo.returnvisitor.util.DateTimeText;
import net.c_kogyo.returnvisitor.util.MailReport;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.RunnableFuture;

import static android.widget.LinearLayout.HORIZONTAL;

/**
 * Created by SeijiShii on 2016/09/23.
 */

public class CalendarActivity extends AppCompatActivity{

    private static final String CALENDAR_DEBUG_TAG = "CalendarDebugTAG";

    private Calendar mDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.calendar_activity);

        AdMobHelper.setAdView(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        setDate();

        initToolBar();
        initCalendarPager();

        initMonthText();
        initDayRow();
        initLeftButton();
        initRightButton();

        initReportMailButton();

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
        toolbar.setTitle("");

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

    private void initDayRow() {

        LinearLayout dayRow = (LinearLayout) findViewById(R.id.day_row);
        dayRow.removeAllViews();

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

    private void initReportMailButton() {

        Button reportMailButton = (Button) findViewById(R.id.mail_button);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            reportMailButton.setClickable(true);
            reportMailButton.setAlpha(1f);
            reportMailButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    MailReport.exportToMail(CalendarActivity.this, adapter.getMonth(calendarPager.getCurrentItem()));

                }
            });
        } else {

            reportMailButton.setOnClickListener(null);
            reportMailButton.setAlpha(0.5f);
            reportMailButton.setClickable(false);

        }


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

    class CalendarView extends FrameLayout{

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

            inflate(CalendarActivity.this, R.layout.calendar_view, this);

            initCalendarLinear();
            initAggregationLinear();

        }

        private LinearLayout calendarLinear;
        private void initCalendarLinear() {

            calendarLinear = (LinearLayout) findViewById(R.id.calendar_linear);

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

                ((LinearLayout) calendarLinear.getChildAt(rowNum)).addView(cell);
            }

            for (int i = dayCal.get(Calendar.DAY_OF_WEEK) + 1 ; i <= 7 ; i++ ) {
                addBlank();
            }
        }

        private void addNewRow() {

            LinearLayout row = new LinearLayout(CalendarActivity.this);
            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
            rowParams.weight = 1;

            row.setOrientation(HORIZONTAL);
            row.setLayoutParams(rowParams);
//            row.setBackgroundColor(Color.BLUE);

            calendarLinear.addView(row);
            rowNum++;
        }

        private void addBlank() {

            View blank = new View(CalendarActivity.this);
            LinearLayout.LayoutParams blankParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
            blankParams.weight = 1;
            blank.setLayoutParams(blankParams);

//            blank.setBackgroundColor(Color.GREEN);

            ((LinearLayout) calendarLinear.getChildAt(rowNum)).addView(blank);
        }

        private LinearLayout aggregationLinear;
        private AggregationOfMonth ofMonth;
        private void initAggregationLinear() {

            aggregationLinear = (LinearLayout) findViewById(R.id.aggregation_linear);
            ofMonth = new AggregationOfMonth(mMonth);

            initAggregationCells();

        }

        private ArrayList<AggregationCell> aggregationCells;
        private int containerWidth;
        private void initAggregationCells() {

            aggregationCells = new ArrayList<>();

            String timeString = null;
            if (ofMonth.getTime() > 0) {
                timeString = DateTimeText.getDurationString(ofMonth.getTime(), false);

                AggregationCell timeCell
                        = new AggregationCell(getContext(),
                        R.string.time,
                        timeString,
                        R.color.colorPrimary);
                aggregationCells.add(timeCell);
            }

            if (ofMonth.getPlacementCount() > 0) {
                AggregationCell plcCell
                        = new AggregationCell(getContext(),
                        R.string.placement,
                        String.valueOf(ofMonth.getPlacementCount()),
                        R.color.plc_pink);
                aggregationCells.add(plcCell);
            }

            if (ofMonth.getShowVideoCount() > 0) {
                AggregationCell videoCell
                        = new AggregationCell(getContext(),
                        R.string.video,
                        String.valueOf(ofMonth.getShowVideoCount()),
                        R.color.video_blue);
                aggregationCells.add(videoCell);
            }

            if (ofMonth.getRvCount() > 0) {
                AggregationCell rvCell
                        = new AggregationCell(getContext(),
                        R.string.return_visits,
                        String.valueOf(ofMonth.getRvCount()),
                        R.color.rv_blue);
                aggregationCells.add(rvCell);
            }

            if (ofMonth.getBsCount() > 0) {
                AggregationCell bsCell
                        = new AggregationCell(getContext(),
                        R.string.bible_study,
                        String.valueOf(ofMonth.getBsCount()),
                        R.color.colorAccent);
                aggregationCells.add(bsCell);
            }

            aggregationLinear.setOrientation(HORIZONTAL);
            for (AggregationCell cell : aggregationCells) {
                aggregationLinear.addView(cell);
            }

            final android.os.Handler handler = new android.os.Handler();
            new Thread(new Runnable() {
                @Override
                public void run() {

                    while (aggregationLinear.getWidth() <= 0) {
                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException e) {
                            //
                        }
                    }

                    containerWidth = aggregationLinear.getWidth();

                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            for ( AggregationCell cell : aggregationCells ) {

                                cell.renderedWidth = cell.getWidth();

                            }
                            aggregationLinear.removeAllViews();
                            aggregationLinear.setOrientation(LinearLayout.VERTICAL);

                            int widthSum = 0;

                            if (aggregationCells.size() > 0) {
                                addRow();
                            }

                            for (AggregationCell cell : aggregationCells) {

                                if (containerWidth < widthSum + cell.renderedWidth) {
                                    addRow();
                                    widthSum = 0;
                                }

                                ((LinearLayout) (aggregationLinear.getChildAt(aggregationLinear.getChildCount() - 1))).addView(cell);
                                widthSum += cell.renderedWidth;

                            }
                        }
                    });
                }
            }).start();
        }

        private void addRow() {

            LinearLayout row = new LinearLayout(getContext());
            float scale = getResources().getDisplayMetrics().density;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int)(scale * 40));
            row.setLayoutParams(params);

            aggregationLinear.addView(row);
        }


    }

    class CalendarCell extends FrameLayout{

        private Calendar mSingleDay;
        private AggregationOfDay aggregation;

        public CalendarCell(Context context, Calendar singleDay) {

            super(context);
            mSingleDay = singleDay;
            aggregation = new AggregationOfDay(mSingleDay);
            initCommon();

        }

        public CalendarCell(Context context, AttributeSet attrs) {
            super(context, attrs);
        }


        private void initCommon() {

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

            if (aggregation.hasWorkOrVisit()) {

                setOnTouchListener(new OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {

                        switch (motionEvent.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                Log.d(CALENDAR_DEBUG_TAG, "Cell pressed down!");
                                CalendarCell.this.setAlpha(0.5f);

                                Intent workIntent = new Intent(CalendarActivity.this, WorkPagerActivity.class);

                                String action = getIntent().getAction();
                                if (action != null) {

                                    // Workから来ている場合
                                    if (action.equals(Constants.CalendarActions.START_CALENDAR_FROM_WORK_ACTION)) {

                                        workIntent.putExtra(Constants.DATE_LONG, aggregation.getDate().getTimeInMillis());
                                        setResult(Constants.CalendarActions.PRESS_DATE_RESULT_CODE, workIntent);
                                        finish();
                                    } else if (action.equals(Constants.CalendarActions.START_CALENDAR_FROM_MAP_ACTION)) {
                                        // Mapから来ている場合
                                        workIntent.putExtra(Constants.DATE_LONG, aggregation.getDate().getTimeInMillis());
                                        CalendarActivity.this.startActivity(workIntent);
                                        finish();
                                    }

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

            int dayNum = aggregation.getDate().get(Calendar.DAY_OF_MONTH);

            dateText.setText(String.valueOf(dayNum));

        }

        private void initTimeText() {

            TextView timeText = (TextView) findViewById(R.id.time_text);

            if (aggregation.hasWorkOrVisit()) {
//                timeText.setVisibility(VISIBLE);

                long time = aggregation.getTime();
                String timeString = DateTimeText.getDurationString(time, false);

                timeText.setText(timeString);

            } else {
//                timeText.setVisibility(INVISIBLE);
            }

        }

        private void initTimeBar() {

            RelativeLayout timeBar = (RelativeLayout) findViewById(R.id.time_bar);

            if (aggregation.hasWorkOrVisit()) {
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

    class AggregationCell extends FrameLayout {

        private String mTitle;
        private int mColorResId;
        private String mCountString;
        int renderedWidth;

        public AggregationCell(Context context, int titleRes, String countString, int colorResId) {
            super(context);

            mTitle = getContext().getResources().getString(titleRes);
            mColorResId = colorResId;
            mCountString = countString;

            initCommon();
        }


        public AggregationCell(Context context, AttributeSet attrs) {
            super(context, attrs);

            initCommon();
        }

        private void initCommon() {

            inflate(CalendarActivity.this, R.layout.aggregation_cell, this);
            initTitleText();
            initColorBar();

//            float scale = getResources().getDisplayMetrics().density;
//            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int)(30 * scale));
//
//            if (mCountString == null || mCountString.equals("0")) {
//                params.height = 0;
//            }
//            this.setLayoutParams(params);

        }

        private void initTitleText() {

            TextView titleText = (TextView) findViewById(R.id.title_text);

            titleText.setText(mTitle + ": " + mCountString);

        }

        private void initColorBar() {

            View colorBar = findViewById(R.id.color_bar);
            colorBar.setBackgroundColor(ContextCompat.getColor(CalendarActivity.this, mColorResId));

        }



    }
}
