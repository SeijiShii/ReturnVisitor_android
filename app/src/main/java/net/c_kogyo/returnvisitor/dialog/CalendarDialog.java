package net.c_kogyo.returnvisitor.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.c_kogyo.returnvisitor.R;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by SeijiShii on 2016/09/19.
 */

public class CalendarDialog extends DialogFragment {

    private static final String CALENDAR_DEBUG_TAG = "CalendarDebugTAG";

    private static Calendar mDate;
    private Context mContext;

    public static CalendarDialog newInstance(Calendar date) {

        mDate = date;

        Bundle args = new Bundle();

        CalendarDialog fragment = new CalendarDialog();
        fragment.setArguments(args);
        return fragment;
    }

    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = LayoutInflater.from(getActivity()).inflate(R.layout.calendar_dialog, null);

        initDateText();
        initCalendarPager();
        initDayRow();
        initLeftButton();
        initRightButton();

        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mContext = getActivity();
        return new Dialog(mContext);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        WindowManager.LayoutParams layoutParams = getDialog().getWindow().getAttributes();

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        layoutParams.width = (int) (metrics.widthPixels * 0.8);
        layoutParams.height = (int) (metrics.heightPixels * 0.8);

        getDialog().getWindow().setAttributes(layoutParams);
    }

    private TextView monthText;
    private void initDateText() {

        monthText = (TextView) view.findViewById(R.id.month_text);
        updateMonthText();
    }

    private void updateMonthText() {

        String monthString = android.text.format.DateFormat.format("yyyy, MMM", mDate).toString();
        monthText.setText(monthString);
    }

    private LinearLayout dayRow;
    private void initDayRow() {

        dayRow = (LinearLayout) view.findViewById(R.id.day_row);

        Calendar dayCal = Calendar.getInstance();
        dayCal.setTimeInMillis(0);
        dayCal.add(Calendar.DAY_OF_MONTH, 5);

        TextView[] dayCells = new TextView[7];
        for ( int i = 0 ; i < 7 ; i++ ) {

            dayCells[i] = new TextView(getActivity());
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

        calendarPager = (ViewPager) view.findViewById(R.id.calendar_pager);
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

        Button leftButton = (Button) view.findViewById(R.id.left_button);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                calendarPager.setCurrentItem(calendarPager.getCurrentItem() - 1, true);
            }
        });
    }

    private void initRightButton () {

        Button rightButton = (Button) view.findViewById(R.id.right_button);
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

            CalendarView calendarView = new CalendarView(mContext, getMonth(position));
            container.addView(calendarView);
            return calendarView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
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

                CalendarCell cell = new CalendarCell(getActivity(), dayCal);
//                rows.get(rows.size() - 1).addView(cell);

                ((LinearLayout) this.getChildAt(rowNum)).addView(cell);
            }

            for (int i = dayCal.get(Calendar.DAY_OF_WEEK) + 1 ; i <= 7 ; i++ ) {
                addBlank();
            }
        }

        private void addNewRow() {

            LinearLayout row = new LinearLayout(getActivity());
            LayoutParams rowParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
            rowParams.weight = 1;

            row.setOrientation(HORIZONTAL);
            row.setLayoutParams(rowParams);
//            row.setBackgroundColor(Color.BLUE);

            this.addView(row);
            rowNum++;
        }

        private void addBlank() {

            View blank = new View(getActivity());
            LinearLayout.LayoutParams blankParams = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
            blankParams.weight = 1;
            blank.setLayoutParams(blankParams);

//            blank.setBackgroundColor(Color.GREEN);

            ((LinearLayout) this.getChildAt(rowNum)).addView(blank);
        }


    }

    class CalendarCell extends LinearLayout{

        private Calendar mDate;

        public CalendarCell(Context context, Calendar date) {

            super(context);

            mDate = date;

            initCommon();

        }

        public CalendarCell(Context context, AttributeSet attrs) {
            super(context, attrs);
        }


        private void initCommon() {

            LinearLayout.LayoutParams params = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
            params.weight = 1;

            this.setLayoutParams(params);

            this.setOrientation(VERTICAL);

//            this.setBackgroundColor(Color.YELLOW);

            initDayText();

        }

        private void initDayText() {

            TextView dayText = new TextView(getActivity());

            int dayNum = mDate.get(Calendar.DAY_OF_MONTH);

            dayText.setText(String.valueOf(dayNum));
            dayText.setTextColor(Color.GRAY);
            dayText.setTextSize(15f);
            dayText.setGravity(Gravity.CENTER);

            LinearLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
            params.weight = 1;

            dayText.setLayoutParams(params);

            this.addView(dayText);

        }
    }

}
