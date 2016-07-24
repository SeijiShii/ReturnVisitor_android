package net.c_kogyo.returnvisitor.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import net.c_kogyo.returnvisitor.R;
import net.c_kogyo.returnvisitor.data.Person;

/**
 * Created by SeijiShii on 2016/07/23.
 */

public class PersonDialog extends DialogFragment {

    private static Person mPerson;
    private Context mContext;

    public static PersonDialog getInstance(Person person) {

        mPerson = person;

        return new PersonDialog();

    }

    private View v;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        v = LayoutInflater.from(getActivity()).inflate(R.layout.person_dialog, null);
        builder.setView(v);

        builder.setPositiveButton(R.string.ok_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setNegativeButton(R.string.cancel_text, null);

        return builder.create();
    }
}
