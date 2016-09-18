package net.c_kogyo.returnvisitor.dialog;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import net.c_kogyo.returnvisitor.R;
import net.c_kogyo.returnvisitor.data.RVData;
import net.c_kogyo.returnvisitor.data.Work;
import net.c_kogyo.returnvisitor.util.CalendarUtil;
import net.c_kogyo.returnvisitor.util.DateTimeText;

import java.text.DateFormat;
import java.util.Calendar;

/**
 * Created by SeijiShii on 2016/09/18.
 */

public class AddWorkDialog extends DialogFragment {

    private static OnWorkSetListener mOnWorkSetListener;
    private Calendar mFrom, mTo;

    public static AddWorkDialog newInstance(OnWorkSetListener onWorkSetListener) {

        mOnWorkSetListener = onWorkSetListener;

        return new AddWorkDialog();
    }

    private View view;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        initCalendars();

        view = LayoutInflater.from(getActivity()).inflate(R.layout.add_work_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setView(view);

        builder.setTitle(R.string.add_work);

        initFromDateText();
        initFromTimeText();

        initToDataText();
        initToTimeText();

        builder.setNegativeButton(R.string.cancel_text, null);
        builder.setPositiveButton(R.string.ok_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Work work = new Work(mFrom);
                work.setEnd(mTo);

                // UIの深いところで元データをいじっていはいけない。データ変更結果の伝播がうまくいかない。
                // ここではコールバックで返すだけ
                mOnWorkSetListener.onWorkSet(work);
            }
        });

        return builder.create();
    }

    private void initCalendars() {

        mFrom = Calendar.getInstance();

        mTo = (Calendar) mFrom.clone();
        mTo.add(Calendar.MINUTE, 5);

    }

    private TextView fromDateText;
    private void initFromDateText() {

        fromDateText = (TextView) view.findViewById(R.id.from_date_text);

        updateFromDateText();

        fromDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

                        mFrom.set(Calendar.YEAR, i);
                        mFrom.set(Calendar.MONTH, i1);
                        mFrom.set(Calendar.DAY_OF_MONTH, i2);

                        updateFromDateText();
                        validateFromCalendar();

                    }
                },
                        mFrom.get(Calendar.YEAR),
                        mFrom.get(Calendar.MONTH),
                        mFrom.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void updateFromDateText() {

        DateFormat format = android.text.format.DateFormat.getDateFormat(getActivity());
        String dateString = format.format(mFrom.getTime());
        fromDateText.setText(dateString);
    }

    private TextView fromTimeText;
    private void initFromTimeText() {

        fromTimeText = (TextView) view.findViewById(R.id.from_time_text);

        updateFromTimeText();

        fromTimeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {

                        mFrom.set(Calendar.HOUR_OF_DAY, i);
                        mFrom.set(Calendar.MINUTE, i1);
                        updateFromTimeText();
                        validateFromCalendar();
                    }
                },
                        mFrom.get(Calendar.HOUR_OF_DAY),
                        mFrom.get(Calendar.MINUTE),
                        true).show();
            }
        });
    }

    private void updateFromTimeText() {

        fromTimeText.setText(DateTimeText.getTimeText(mFrom));
    }

    private TextView toDateText;
    private void initToDataText() {

        toDateText = (TextView) view.findViewById(R.id.to_date_text);
        updateToDateText();

        toDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

                        mTo.set(Calendar.YEAR, i);
                        mTo.set(Calendar.MONTH, i1);
                        mTo.set(Calendar.DAY_OF_MONTH, i2);

                        updateToDateText();
                        validateToCalendar();
                    }
                },
                mTo.get(Calendar.YEAR),
                mTo.get(Calendar.MONTH),
                mTo.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


    }

    private void updateToDateText() {

        DateFormat format = android.text.format.DateFormat.getDateFormat(getActivity());
        String dateString = format.format(mTo.getTime());
        toDateText.setText(dateString);
    }

    private TextView toTimeText;
    private void initToTimeText() {

        toTimeText = (TextView) view.findViewById(R.id.to_time_text);
        updateToTimeText();
        toTimeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {

                        mTo.set(Calendar.HOUR_OF_DAY, i);
                        mTo.set(Calendar.MINUTE, i1);
                        updateToTimeText();
                        validateToCalendar();

                    }
                },
                        mTo.get(Calendar.HOUR_OF_DAY),
                        mTo.get(Calendar.MINUTE),
                        true).show();

            }
        });
    }

    private void updateToTimeText() {

        toTimeText.setText(DateTimeText.getTimeText(mTo));

    }

    private void validateFromCalendar() {

        if (mFrom.after(mTo)) {

            mTo = (Calendar) mFrom.clone();
            mTo.add(Calendar.MINUTE, 5);

            updateToDateText();
            updateToTimeText();
        }

    }

    private void validateToCalendar() {

        if (mTo.before(mFrom)) {

            mFrom = (Calendar) mTo.clone();
            mFrom.add(Calendar.MINUTE, -5);

            updateFromDateText();
            updateFromTimeText();
        }

    }

    public interface OnWorkSetListener {

        void onWorkSet(Work work);
    }
}
