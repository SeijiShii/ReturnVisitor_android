package net.c_kogyo.returnvisitor.dialog;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import net.c_kogyo.returnvisitor.R;
import net.c_kogyo.returnvisitor.activity.RecordVisitActivity;
import net.c_kogyo.returnvisitor.data.Place;

import org.w3c.dom.Text;

/**
 * Created by SeijiShii on 2016/07/21.
 */

public class PlaceDialog extends DialogFragment {

    private static Context mContext;
    private static Place mPlace;
    private static PlaceDialog.OnOkClickListener mLlstener;

    public static PlaceDialog getInstance(Context context, Place place, PlaceDialog.OnOkClickListener listener){

        mContext = context;
        mPlace = place;
        mLlstener = listener;
        return new PlaceDialog();
    }

    private View v;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.place);

        v = LayoutInflater.from(mContext).inflate(R.layout.place_dialog, null);
        builder.setView(v);

        initNameText();
        initAddressText();
        initCoordinateText();

        builder.setPositiveButton(R.string.ok_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                mPlace.setName(nameText.getText().toString());
                mPlace.setAddress(addressText.getText().toString());
                mLlstener.onOkClick(mPlace);
            }
        });
        builder.setNegativeButton(R.string.cancel_text, null);

        return builder.create();
    }

    private EditText nameText;
    private void initNameText() {

        nameText = (EditText) v.findViewById(R.id.name_text);
        nameText.setText(mPlace.getName());

    }

    private EditText addressText;
    private void initAddressText() {

        addressText = (EditText) v.findViewById(R.id.address_text);

        if (mPlace.getAddress() != null)
            addressText.setText(mPlace.getAddress());

    }

    private void initCoordinateText() {

        TextView latText = (TextView) v.findViewById(R.id.lat_text);
        String latS = mContext.getResources().getString(R.string.latitude, String.valueOf(mPlace.getLatLng().latitude));
        latText.setText(latS);

        TextView lngText = (TextView) v.findViewById(R.id.lng_text);
        String lngS = mContext.getResources().getString(R.string.longitude, String.valueOf(mPlace.getLatLng().longitude));
        lngText.setText(lngS);
    }

    public interface OnOkClickListener {

        public void onOkClick(Place place);
    }
}
