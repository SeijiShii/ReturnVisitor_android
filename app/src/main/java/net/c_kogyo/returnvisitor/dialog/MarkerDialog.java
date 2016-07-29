package net.c_kogyo.returnvisitor.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import net.c_kogyo.returnvisitor.R;
import net.c_kogyo.returnvisitor.data.Place;
import net.c_kogyo.returnvisitor.data.RVData;


/**
 * Created by SeijiShii on 2016/07/27.
 */

public class MarkerDialog extends DialogFragment {

    private View v;
    private AlertDialog dialog;
    static private Place mPlace;
    static private OnPlaceRemoveListener mRemoveListener;
    static private OnVisitRecordClickListener mRecordListener;

    public static MarkerDialog getInstance(Place place,
                                           OnVisitRecordClickListener recordListener,
                                           OnPlaceRemoveListener removeListener) {

        mPlace = place;
        mRecordListener = recordListener;
        mRemoveListener = removeListener;

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

                mRecordListener.onVisitRecordClick(mPlace);
                dismiss();
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
                        deletePlace();
                    }
                });
                builder1.create().show();
            }
        });
    }

    private void deletePlace() {

        //TODO 実際のdelete処理を実装
        // Personからその場所のidを削除
        // その場所のidを持つVisitを削除
        // と思ったけど上記2つはやらなくてもよいと思う。単に「不明な場所になるだけ

        // Placeを削除
        RVData.getInstance().placeList.removeFromBoth(mPlace);
        mRemoveListener.onPlaceRemoved(mPlace);

    }

    public interface OnVisitRecordClickListener {
        void onVisitRecordClick(Place place);
    }

    public interface OnPlaceRemoveListener {
        void onPlaceRemoved(Place place);
    }

}
