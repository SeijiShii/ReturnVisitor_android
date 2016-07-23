package net.c_kogyo.returnvisitor.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import net.c_kogyo.returnvisitor.R;
import net.c_kogyo.returnvisitor.data.Place;
import net.c_kogyo.returnvisitor.data.Visit;
import net.c_kogyo.returnvisitor.dialog.PlaceDialog;
import net.c_kogyo.returnvisitor.service.FetchAddressIntentService;

import static com.google.android.gms.auth.account.WorkAccount.API;

/**
 * Created by SeijiShii on 2016/07/20.
 */

public class RecordVisitActivity extends AppCompatActivity {

    private Visit mVisit;
    private Place mPlace;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.record_visit_activity);

        mVisit = new Visit();

        initBroadcastingForAddress();
        initPlace();

        initPlaceText();


    }

    private void initPlace() {

        Intent intent = getIntent();
        double latitude = intent.getDoubleExtra(MapActivity.LATITUDE, 1000);
        double longitude = intent.getDoubleExtra(MapActivity.LONGITUDE, 1000);

        if ( latitude < 1000 && longitude < 1000 ) {
            mPlace = new Place(new LatLng(latitude, longitude));
            mVisit.setPlaceId(mPlace.getId());

            startFetchAddressIntentService();

        }
    }

    public static final String LAT_LNG_EXTRA = "lat_lng_extra";

    private void startFetchAddressIntentService() {

        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(LAT_LNG_EXTRA, mPlace.getLatLng());
        startService(intent);

        // ドラッグで家を動かした後の処理も考えておく
        // addressTextをnullにすればもう一度リクエストする
    }

    private void initBroadcastingForAddress() {

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String addressString = intent.getStringExtra(FetchAddressIntentService.ADDRESS_DATA);
                mPlace.setAddress(addressString);
                placeText.setText(mPlace.getAddress());
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(FetchAddressIntentService.SEND_ADDRESS));

    }

    private TextView placeText;
    private void initPlaceText() {

        placeText = (TextView) findViewById(R.id.place_text);
        setPlaceText();
        placeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlaceDialog.getInstance(RecordVisitActivity.this, mPlace, new OnPlaceOkListener()).show(getFragmentManager(), "");
            }
        });
    }

    public class OnPlaceOkListener {

        public void onPlaceOk() {
            setPlaceText();
        }

    }

    private void setPlaceText() {
        if (mPlace != null) {
            if (!mPlace.getName().equals("")) {
                placeText.setText(mPlace.getName());
            } else if (mPlace.getAddress() != null){
                placeText.setText(mPlace.getAddress());
            }
        }
    }
}
