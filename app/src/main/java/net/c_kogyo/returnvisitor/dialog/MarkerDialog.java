package net.c_kogyo.returnvisitor.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import net.c_kogyo.returnvisitor.R;


/**
 * Created by SeijiShii on 2016/07/27.
 */

public class MarkerDialog extends DialogFragment {

    private View v;
    private AlertDialog dialog;

    public static MarkerDialog getInstance() {
        return new MarkerDialog();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        v = LayoutInflater.from(getActivity()).inflate(R.layout.marker_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v);

        initRecordButton();
        initDeleteButton();
        initCancelButton();

        dialog = builder.create();
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.y = 200;

        dialog.getWindow().setAttributes(params);

        // 背景のオーバレイを透明にする
        dialog.getWindow().setFlags(0, WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        // 背景画像の下に白い枠が出ないように
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);


        return dialog;
    }

    private void initRecordButton() {

        Button recordButton = (Button) v.findViewById(R.id.record_visit_button);
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void initCancelButton() {

        Button cancelButton = (Button) v.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dismiss();
            }
        });
    }

    private void initDeleteButton() {

        Button deleteButton = (Button) v.findViewById(R.id.delete_place_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder1  = new AlertDialog.Builder(getActivity());
                builder1.setTitle(R.string.delete);
                builder1.setMessage(R.string.delete_place_message);
                builder1.setNegativeButton(R.string.cancel_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialog.dismiss();
                    }
                });
                builder1.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialog.dismiss();
                    }
                });
                builder1.create().show();
            }
        });
    }
}
