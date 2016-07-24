package net.c_kogyo.returnvisitor.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import net.c_kogyo.returnvisitor.R;
import net.c_kogyo.returnvisitor.activity.RecordVisitActivity;
import net.c_kogyo.returnvisitor.data.Visit;

/**
 * Created by SeijiShii on 2016/07/23.
 */

public class SeenPersonDialog extends DialogFragment {

    private Context mContext;
    private static Visit mVisit;
    private static RecordVisitActivity.SeenPersonDialogListener mListener;

    public static SeenPersonDialog getInstance(Visit visit, RecordVisitActivity.SeenPersonDialogListener listener) {

        mListener = listener;
        mVisit = visit;

        return new SeenPersonDialog();
    }

    private View v;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mContext = getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.seen_person_dialog);

        v = LayoutInflater.from(getActivity()).inflate(R.layout.seen_person_dialog, null);
        builder.setView(v);

        builder.setPositiveButton(R.string.ok_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mListener.onOkCLick();
            }
        });

        builder.setNegativeButton(R.string.cancel_text, null);

        initNewPersonButton();

        return builder.create();
    }

    private void initNewPersonButton() {

        Button addPersonButton = (Button) v.findViewById(R.id.add_person_button);
        addPersonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PersonDialog.getInstance(null, mVisit).show(getFragmentManager(), null);
            }
        });
    }
}
