package net.c_kogyo.returnvisitor.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import net.c_kogyo.returnvisitor.R;
import net.c_kogyo.returnvisitor.data.Work;

import java.util.Calendar;

/**
 * Created by SeijiShii on 2016/09/18.
 */

public class AddSelectDialog extends DialogFragment {

    private static Calendar mDate;
    private static AddWorkDialog.OnWorkSetListener mOnWorkSetListener;

    public static AddSelectDialog newInstance(Calendar date,
                                              AddWorkDialog.OnWorkSetListener onWorkSetListener) {

        mDate = date;
        mOnWorkSetListener = onWorkSetListener;

        return new AddSelectDialog();
    }

    private View view;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        view = LayoutInflater.from(getActivity()).inflate(R.layout.add_select_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setView(view);

        initAddWorkButton();
        initAddVisitButton();
        initCancelButton();

        return builder.create();
    }

    private void initAddWorkButton() {

        Button addWorkButton = (Button) view.findViewById(R.id.add_work_button);
        addWorkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AddWorkDialog.newInstance(mDate, mOnWorkSetListener).show(getFragmentManager(), null);
                dismiss();
            }
        });

    }

    private void initAddVisitButton() {

        Button addVisitButton = (Button) view.findViewById(R.id.add_visit_button);
        addVisitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dismiss();
            }
        });

    }

    private void initCancelButton() {

        Button cancelButton = (Button) view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }



}
