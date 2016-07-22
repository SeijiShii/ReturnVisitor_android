package net.c_kogyo.returnvisitor.service;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import net.c_kogyo.returnvisitor.Enum.AddressTextLanguage;
import net.c_kogyo.returnvisitor.R;
import net.c_kogyo.returnvisitor.activity.MapActivity;
import net.c_kogyo.returnvisitor.activity.RecordVisitActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by sayjey on 2015/06/16.
 */
public class FetchAddressIntentService extends IntentService {

    private static final String TAG = "FETCH_ADDRESS";
    private static final int DATA_COUNT = 5;

    public static final String SEND_ADDRESS = "send_address";
    public static final String ADDRESS_DATA = "address_data";

    public FetchAddressIntentService() {
        super(null);

    }


    @Override
    protected void onHandleIntent(Intent intent) {

        makeFirstRequest(intent);

//        Geocoder geocoder = new Geocoder(this);
//
//        String errorMessage = "";
//
//        // Get the location passed to this service through an extra.
//        LatLng latLng = intent.getParcelableExtra(
//                RecordVisitActivity.LAT_LNG_EXTRA);
//
//        List<Address> addresses = null;
//
//        try {
//            addresses = geocoder.getFromLocation(
//                    latLng.latitude,
//                    latLng.longitude,
//                    DATA_COUNT);
//        } catch (IOException ioException) {
//            // Catch network or other I/O problems.
//            errorMessage = getString(R.string.service_not_available);
//            Log.e(TAG, errorMessage, ioException);
//        } catch (IllegalArgumentException illegalArgumentException) {
//            // Catch invalid latitude or longitude values.
//            errorMessage = getString(R.string.invalid_lat_long_used);
//            Log.e(TAG, errorMessage + ". " +
//                    "Latitude = " + latLng.latitude +
//                    ", Longitude = " +
//                    latLng.longitude, illegalArgumentException);
//        }
//
//        // Handle case where no address was found.
//        if (addresses == null || addresses.size()  == 0) {
//            if (errorMessage.isEmpty()) {
//                errorMessage = getString(R.string.no_address_found);
//                Log.e(TAG, errorMessage);
//            }
//
//        } else {
//
//            Address address;
//            String addressString;
//            for (int i = 0 ; i < DATA_COUNT ; i++) {
//
//                address = addresses.get(i);
//
//                ArrayList<String> addressFragments = new ArrayList<String>();
//
//                for(int j = 0; j < address.getMaxAddressLineIndex(); j++) {
//                    addressFragments.add(address.getAddressLine(j));
//                }
//
//
//                addressString = TextUtils.join(" ",addressFragments);
//
//                if (!addressString.equals("")) {
//                    Intent sendAddressIntent = new Intent(SEND_ADDRESS);
//                    sendAddressIntent.putExtra(ADDRESS_DATA, addressString);
//
//                    LocalBroadcastManager.getInstance(this).sendBroadcast(sendAddressIntent);
//
//                    break;
//
//                }
//            }
//        }
    }

    private void makeFirstRequest(Intent intent) {

        Geocoder geocoder = new Geocoder(this);

        String errorMessage = "";

        // Get the location passed to this service through an extra.
        LatLng latLng = intent.getParcelableExtra(RecordVisitActivity.LAT_LNG_EXTRA);

        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(
                    latLng.latitude,
                    latLng.longitude,
                    DATA_COUNT);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            errorMessage = getString(R.string.service_not_available);
            Log.e(TAG, errorMessage, ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            errorMessage = getString(R.string.invalid_lat_long_used);
            Log.e(TAG, errorMessage + ". " +
                    "Latitude = " + latLng.latitude +
                    ", Longitude = " +
                    latLng.longitude, illegalArgumentException);
        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size()  == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = getString(R.string.no_address_found);
                Log.e(TAG, errorMessage);
            }

        } else {

            Address address;

            if (MapActivity.addressTextLang == AddressTextLanguage.USE_GPS_LOCALE) {
                address = addresses.get(0);
                String countryCode = address.getCountryCode();
                String langCode = null;

                Locale[] locales = Locale.getAvailableLocales();
                for (Locale localeIn : locales) {
                    if (countryCode.equalsIgnoreCase(localeIn.getCountry())) {
                        langCode = localeIn.getLanguage();
                        break;
                    }
                }

                Locale locale = new Locale(langCode, countryCode);

                geocoder = new Geocoder(this, locale);

            }
            makeSecondRequest(intent, geocoder);
        }
    }

    private void makeSecondRequest(Intent intent, Geocoder geocoder) {

        String errorMessage = "";

        // Get the location passed to this service through an extra.
        LatLng latLng = intent.getParcelableExtra(
                RecordVisitActivity.LAT_LNG_EXTRA);

        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(
                    latLng.latitude,
                    latLng.longitude,
                    DATA_COUNT);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            errorMessage = getString(R.string.service_not_available);
            Log.e(TAG, errorMessage, ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            errorMessage = getString(R.string.invalid_lat_long_used);
            Log.e(TAG, errorMessage + ". " +
                    "Latitude = " + latLng.latitude +
                    ", Longitude = " +
                    latLng.longitude, illegalArgumentException);
        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size()  == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = getString(R.string.no_address_found);
                Log.e(TAG, errorMessage);
            }
        } else {

            Address address;
            String addressString;

            for (int i = 0 ; i < DATA_COUNT ; i++) {

                address = addresses.get(i);

                ArrayList<String> addressFragments = new ArrayList<String>();

                for(int j = 0; j <= address.getMaxAddressLineIndex(); j++) {
                    addressFragments.add(address.getAddressLine(j));
                }

                addressString = TextUtils.join(" ",addressFragments);

                if (!addressString.equals("")) {
                    Intent sendAddressIntent = new Intent(SEND_ADDRESS);
                    sendAddressIntent.putExtra(ADDRESS_DATA, addressString);

                    LocalBroadcastManager.getInstance(this).sendBroadcast(sendAddressIntent);

                    break;
                }
            }
        }
    }
}

