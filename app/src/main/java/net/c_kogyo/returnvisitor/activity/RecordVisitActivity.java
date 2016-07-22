package net.c_kogyo.returnvisitor.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;

import net.c_kogyo.returnvisitor.R;
import net.c_kogyo.returnvisitor.data.Place;
import net.c_kogyo.returnvisitor.data.Visit;

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

        initPlace();


    }

    private void initPlace() {

        Intent intent = getIntent();
        double latitude = intent.getDoubleExtra(MapActivity.LATITUDE, 1000);
        double longitude = intent.getDoubleExtra(MapActivity.LONGITUDE, 1000);

        if ( latitude < 1000 && longitude < 1000 ) {
            mPlace = new Place(new LatLng(latitude, longitude));
            mVisit.setPlaceId(mPlace.getId());
        }
    }

    private void reverseGeocoding() {

    }
}
