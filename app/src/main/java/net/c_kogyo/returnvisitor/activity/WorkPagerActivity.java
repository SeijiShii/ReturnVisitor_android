package net.c_kogyo.returnvisitor.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import net.c_kogyo.returnvisitor.R;
import net.c_kogyo.returnvisitor.data.RVData;
import net.c_kogyo.returnvisitor.fragment.WorkFragment;
import net.c_kogyo.returnvisitor.util.CalendarUtil;
import net.c_kogyo.returnvisitor.util.DateTimeText;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by SeijiShii on 2016/09/17.
 */

public class WorkPagerActivity extends AppCompatActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.work_pager_activity);

        initToolBar();

        initPager();

        initButtons();

    }

    private Toolbar toolbar;
    private void initToolBar(){

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
//        toolbar.inflateMenu(R.menu.return_visitor_menu);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.work_visit_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
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


    private ViewPager pager;
    private DatePagerAdapter datePagerAdapter;
    private void initPager() {

        pager = (ViewPager) findViewById(R.id.view_pager);

        Calendar date = getDate();
        datePagerAdapter = new DatePagerAdapter(getSupportFragmentManager());

        pager.setAdapter(datePagerAdapter);


    }

    private Calendar getDate() {

        Calendar date = Calendar.getInstance();

        long dLong = getIntent().getLongExtra(Constants.DATE_LONG, 0);
        if (dLong != 0) {
            date.setTimeInMillis(dLong);
        }
        return date;

    }

    private Button leftButton;
    private Button rightButton;
    private TextView dateText;

    private void initButtons() {

        leftButton = (Button) findViewById(R.id.left_button);
        rightButton = (Button) findViewById(R.id.right_button);
        dateText = (TextView) findViewById(R.id.date_text);

        updateButtons();

        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar date = datePagerAdapter.getDate(pager.getCurrentItem());
                new DatePickerDialog(WorkPagerActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

                        Calendar daySet = Calendar.getInstance();
                        daySet.set(Calendar.YEAR, i);
                        daySet.set(Calendar.MONTH, i1);
                        daySet.set(Calendar.DAY_OF_MONTH, i2);

                        if (datePagerAdapter.containsDate(daySet)) {

                            pager.setCurrentItem(datePagerAdapter.getPosition(daySet), true);
                            updateButtons();
                        }
                    }
                }, date.get(Calendar.YEAR),
                        date.get(Calendar.MONTH),
                        date.get(Calendar.DAY_OF_MONTH)).show();

            }
        });
    }

    private void updateLeftButton() {

        if (pager.getCurrentItem() > 0) {
            leftButton.setVisibility(View.VISIBLE);

            leftButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    pager.setCurrentItem(pager.getCurrentItem() - 1, true);

                    updateButtons();

                }
            });

        } else {
            leftButton.setVisibility(View.INVISIBLE);
        }
    }

    private void updateRightButton() {

        if (pager.getCurrentItem() < datePagerAdapter.getCount() - 1) {

            rightButton.setVisibility(View.VISIBLE);

            rightButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pager.setCurrentItem(pager.getCurrentItem() + 1, true);

                    updateButtons();
                }
            });

        } else {
            rightButton.setVisibility(View.INVISIBLE);
        }

    }

    private void updateDateText() {

        DateFormat format = android.text.format.DateFormat.getDateFormat(this);
        String dateString = format.format(datePagerAdapter.getDate(pager.getCurrentItem()).getTime());

        dateText.setText(dateString);

    }

    private void updateButtons() {

        updateLeftButton();
        updateRightButton();
        updateDateText();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        datePagerAdapter.getItem(pager.getCurrentItem()).onActivityResult(requestCode, resultCode, data);
    }

    class DatePagerAdapter extends FragmentStatePagerAdapter {

        private ArrayList<Calendar> mDates;

        public DatePagerAdapter(FragmentManager fm) {
            super(fm);

            setDates();

        }

        @Override
        public Fragment getItem(int position) {

            return WorkFragment.newInstance(mDates.get(position));
        }

        @Override
        public int getCount() {
            return mDates.size();
        }

        public int getPosition(Calendar date) {

            for ( int i = 0 ; i < mDates.size() ; i++ ) {

                Calendar date1 = mDates.get(i);

                if (CalendarUtil.isSameDay(date, date1)) {
                    return i;
                }
            }

            return -1;
        }

        private void setDates() {

            ArrayList<Calendar> datesOfVisit = RVData.getInstance().visitList.getDates();
            ArrayList<Calendar> datesOfWork = RVData.getInstance().workList.getDates();
            ArrayList<Calendar> datesDoubled = new ArrayList<>();

            for (Calendar date0 : datesOfVisit) {
                for (Calendar date1 : datesOfWork) {

                    if (CalendarUtil.isSameDay(date0, date1)) {
                        datesDoubled.add(date1);
                    }
                }
            }

            datesOfWork.removeAll(datesDoubled);
            datesOfVisit.addAll(datesOfWork);

            Collections.sort(datesOfVisit, new Comparator<Calendar>() {
                @Override
                public int compare(Calendar calendar, Calendar t1) {
                    return calendar.compareTo(t1);
                }
            });

            mDates = new ArrayList<>(datesOfVisit);
        }

        public Calendar getDate(int pos) {
            return mDates.get(pos);
        }

        public boolean containsDate(Calendar date) {

            for (Calendar date1 : mDates) {
                if (CalendarUtil.isSameDay(date, date1)) {
                    return true;
                }
            }
            return false;
        }

    }
}
