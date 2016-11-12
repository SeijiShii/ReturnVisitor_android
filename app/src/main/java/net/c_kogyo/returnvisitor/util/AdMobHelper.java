package net.c_kogyo.returnvisitor.util;

import android.app.Activity;
import android.content.Context;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import net.c_kogyo.returnvisitor.R;

/**
 * Created by SeijiShii on 2016/09/26.
 */

public class AdMobHelper {

    public static void setAdView(Context context) {

        MobileAds.initialize(context, context.getString(R.string.ad_id));

        AdView mAdView = (AdView) ((Activity)context).findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }
}
