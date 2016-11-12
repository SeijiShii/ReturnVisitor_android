package net.c_kogyo.returnvisitor.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import net.c_kogyo.returnvisitor.R;
import net.c_kogyo.returnvisitor.activity.Constants;
import net.c_kogyo.returnvisitor.data.AggregationOfMonth;

import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by SeijiShii on 2016/09/26.
 */

public class MailReport {

    public static void exportToMail(Context context, Calendar month) {

        AggregationOfMonth ofMonth = new AggregationOfMonth(month);

        SharedPreferences prefs = context.getSharedPreferences(Constants.SharedPrefTags.RETURN_VISITOR_SHARED_PREFS, MODE_PRIVATE);
        String name = prefs.getString(Constants.SharedPrefTags.USER_NAME, null);
        if (name == null) name = "";

        String subject = context.getString(R.string.service_report) + ": " + DateTimeText.getMonthText(month);

        String message = name + "\n"
                + context.getString(R.string.month)         + ": " + DateTimeText.getMonthText(month) + "\n"
                + context.getString(R.string.placement)     + ": " + ofMonth.getPlacementCount() + "\n"
                + context.getString(R.string.video)         + ": " + ofMonth.getShowVideoCount() + "\n"
                + context.getString(R.string.time)          + ": " + ofMonth.getHours() + "\n"
                + context.getString(R.string.rv_count)      + ": " + ofMonth.getRvCount() + "\n"
                + context.getString(R.string.bible_study)   + ": " + ofMonth.getBsCount() + "\n"
                + context.getString(R.string.comment)       + ": " ;

        Intent mailIntent = new Intent(Intent.ACTION_SENDTO);
        mailIntent.setData(Uri.parse("mailto:"));
        mailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        mailIntent.putExtra(Intent.EXTRA_TEXT, message);

        context.startActivity(mailIntent);
    }
}
