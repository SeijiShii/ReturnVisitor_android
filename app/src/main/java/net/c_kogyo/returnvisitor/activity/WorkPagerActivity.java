package net.c_kogyo.returnvisitor.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.c_kogyo.returnvisitor.R;
import net.c_kogyo.returnvisitor.data.RVData;
import net.c_kogyo.returnvisitor.data.Work;
import net.c_kogyo.returnvisitor.dialog.AddSelectDialog;
import net.c_kogyo.returnvisitor.dialog.AddWorkDialog;
import net.c_kogyo.returnvisitor.dialog.CalendarDialog;
import net.c_kogyo.returnvisitor.fragment.WorkFragment;
import net.c_kogyo.returnvisitor.util.CalendarUtil;

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
        pager.setCurrentItem(datePagerAdapter.getClosestPosition(date));

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                updateButtons();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    // 日付指定で遷移するためのプロセス あったわ…
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
        dateText = (TextView) findViewById(R.id.month_text);

        updateButtons();

        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CalendarDialog.newInstance(datePagerAdapter.getDate(pager.getCurrentItem())).show(getFragmentManager(), null);


//                Calendar date = datePagerAdapter.getDate(pager.getCurrentItem());
//                new DatePickerDialog(WorkPagerActivity.this, new DatePickerDialog.OnDateSetListener() {
//                    @Override
//                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
//
//                        Calendar daySet = Calendar.getInstance();
//                        daySet.set(Calendar.YEAR, i);
//                        daySet.set(Calendar.MONTH, i1);
//                        daySet.set(Calendar.DAY_OF_MONTH, i2);
//
//                        if (datePagerAdapter.containsDate(daySet)) {
//
//                            pager.setCurrentItem(datePagerAdapter.getPosition(daySet), true);
//                            updateButtons();
//                        }
//                    }
//                }, date.get(Calendar.YEAR),
//                        date.get(Calendar.MONTH),
//                        date.get(Calendar.DAY_OF_MONTH)).show();

            }
        });

        initAddButton();
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

    private void initAddButton() {

        Button addButton = (Button) findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AddSelectDialog.newInstance(datePagerAdapter.getDate(pager.getCurrentItem()),
                        new AddWorkDialog.OnWorkSetListener() {
                    @Override
                    public void onWorkSet(Work work) {

                        // ここがUIの一番浅い場所なので原初データをいじる
                        RVData.getInstance().workList.addOrSet(work);
                        RVData.getInstance().workList.onChangeTime(work);

                        pager.setCurrentItem(datePagerAdapter.onAddWork(work), true);
                        updateButtons();

                        WorkFragment fragment = (WorkFragment) datePagerAdapter.instantiateItem(pager, datePagerAdapter.getPosition(work.getStart()));

                        fragment.refreshContent();
                    }
                }).show(getFragmentManager(), null);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //TODO ここをgetItemでやるべきか　要検証
        ((WorkFragment) datePagerAdapter.instantiateItem(pager, pager.getCurrentItem())).onActivityResult(requestCode, resultCode, data);
    }

    class DatePagerAdapter extends FragmentStatePagerAdapter {

        private ArrayList<Calendar> mDates;

        public DatePagerAdapter(FragmentManager fm) {
            super(fm);

            setDates();
        }

        @Override
        public Fragment getItem(int position) {

            return WorkFragment.newInstance(mDates.get(position), new WorkFragment.OnAllItemRemoveListener() {
                @Override
                public void onAllItemRemoved(Calendar date) {
                    removeDate(date);
                }
            });
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

        public int onAddWork(Work work) {

            // Workが追加された時点ですでにmDatesにある日付かどうか
            int datePos = getPosition(work.getStart());

            if (datePos >= 0) {
                // 日付がすでにある

            } else {
                // 日付が存在しない(その日にはまだ何のデータもなかった)
                // この日には削除されるWorkも存在しない
                setDates();

                // 気を取り直して…
                datePos = getPosition(work.getStart());
                notifyDataSetChanged();
            }

            return datePos;
        }

        public int getClosestPosition(Calendar date) {

            if (getCount() <= 0) return 0;

            if (getPosition(date) >= 0) {
                return getPosition(date);
            }

            Calendar dateFuture = (Calendar) date.clone();
            Calendar datePast = (Calendar) date.clone();

            while (true) {

                dateFuture.add(Calendar.DAY_OF_MONTH, 1);
                datePast.add(Calendar.DAY_OF_MONTH, -1);

                if (getPosition(datePast) >= 0) {
                    return getPosition(datePast);
                }

                if (getPosition(dateFuture) >= 0) {
                    return getPosition(dateFuture);
                }

            }
        }

        private void removeDate(Calendar date) {

            for (Calendar date1 : mDates) {
                if (CalendarUtil.isSameDay(date, date1)){
                    mDates.remove(date1);
                    break;
                }
            }
            notifyDataSetChanged();
            pager.setCurrentItem(getClosestPosition(date), true);
            updateButtons();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }
}
