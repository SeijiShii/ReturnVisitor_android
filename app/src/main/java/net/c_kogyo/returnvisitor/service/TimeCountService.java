package net.c_kogyo.returnvisitor.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import net.c_kogyo.returnvisitor.R;
import net.c_kogyo.returnvisitor.data.RVData;
import net.c_kogyo.returnvisitor.data.Work;
import net.c_kogyo.returnvisitor.util.DateTimeText;

import java.util.Calendar;

import static android.content.Intent.ACTION_DELETE;

/**
 * Created by SeijiShii on 2016/08/13.
 */

public class TimeCountService extends Service {

    private static final int TIME_NOTIFY_ID = 100;

    private static boolean timeCounting;
//    private long startTime;
    private static Work mWork;

    private LocalBroadcastManager broadcastManager;
    private BroadcastReceiver receiver;

    public static final String TIME_COUNTING_ACTION = TimeCountService.class.getName() + "_time_counting_action";

    public static final String START_TIME = TimeCountService.class.getName() + "_start_time";
    public static final String DURATION = TimeCountService.class.getName() + "_duration";

    public static final String START_CHANGE_ACTION = TimeCountService.class.getName() + "_start_change";

    public static final String STOP_TIME_COUNT_ACTION = TimeCountService.class.getName() + "_stop_time_count_action";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        broadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(START_CHANGE_ACTION)) {

                    long startTime = intent.getLongExtra(START_TIME, mWork.getStart().getTimeInMillis());
                    mWork.getStart().setTimeInMillis(startTime);
                }
            }
        };

        broadcastManager.registerReceiver(receiver, new IntentFilter(START_CHANGE_ACTION));
    }

    private long duration;
    private int minCounter;
    private Handler countStopHandler;

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {

        duration = 0;
        timeCounting = true;
        minCounter = 0;
        countStopHandler = new Handler();

        String workId = intent.getStringExtra(Work.WORK);
        if (workId != null) {
            mWork = RVData.getInstance().workList.getById(workId);

            if (mWork == null) {
                stopTimeCount();
            }

        }

//        startTime = Calendar.getInstance().getTimeInMillis();

//        if (mWork == null) {
//
//            // 既にデータに存在するか
//
//            Calendar startCal = Calendar.getInstance();
//            startCal.setTimeInMillis(startTime);
//            mWork = new Work(startCal);
//        }

        initNotification(duration);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (timeCounting) {

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        //
                    }

                    duration = Calendar.getInstance().getTimeInMillis() - mWork.getStart().getTimeInMillis();
                    final Intent timeBroadCastIntent = new Intent();
                    timeBroadCastIntent.setAction(TIME_COUNTING_ACTION);
                    timeBroadCastIntent.putExtra(START_TIME, mWork.getStart().getTimeInMillis());
                    timeBroadCastIntent.putExtra(DURATION, duration);

                    mWork.setEnd(Calendar.getInstance());

                    broadcastManager.sendBroadcast(timeBroadCastIntent);
                    updateNotification(duration);

                    // 約1分ごとに保存するようにする
                    minCounter++;
                    if (minCounter > 50) {

                        mWork.setEnd(Calendar.getInstance());

                        RVData.getInstance().workList.addOrSet(mWork);
                        minCounter = 0;
                    }

                }

                countStopHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        Toast.makeText(TimeCountService.this, R.string.time_stop, Toast.LENGTH_SHORT).show();
                    }
                });

                if (notificationManager != null) {
                    notificationManager.cancel(TIME_NOTIFY_ID);
                }

                mWork = null;

                Intent stopIntent = new Intent(STOP_TIME_COUNT_ACTION);
                broadcastManager.sendBroadcast(stopIntent);

                stopSelf();

            }
        }).start();

        return START_REDELIVER_INTENT;
    }

    public static boolean isTimeCounting() {
        return timeCounting;
    }

    public static Work getWork() {
        return mWork;
    }

    public static void stopTimeCount() {
        timeCounting = false;
    }


    private NotificationManager notificationManager;
    private NotificationCompat.Builder mBuilder;


    private void initNotification(long duration) {

        String duraString = getString(R.string.duration_text, DateTimeText.getDurationString(duration, true));

        mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.white_rv_icon)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(duraString);


        Intent dummyIntent = new Intent(this, IntentCatcherDummyService.class);
        PendingIntent dummyPendingIntent = PendingIntent.getService(this, 0, dummyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(dummyPendingIntent);

        Intent deleteIntent = new Intent(this, TimeCountService.class);
        deleteIntent.setAction(ACTION_DELETE);
        PendingIntent deletePendingIntent = PendingIntent.getService(this, 0, deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setDeleteIntent(deletePendingIntent);

        // キャンセルできないようにする
        mBuilder.setOngoing(true);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(TIME_NOTIFY_ID, mBuilder.build());
    }

    private void updateNotification(long duration) {

        String duraString = getString(R.string.duration_text, DateTimeText.getDurationString(duration, true));
        mBuilder.setContentText(duraString);

        // キャンセルできないようにする
        mBuilder.setOngoing(true);

        notificationManager.notify(TIME_NOTIFY_ID, mBuilder.build());
    }

    // エラーで停止した後通知が消えない
    // TimeCountがシステム側の都合で停止したとき同じIntentで再開できる仕組みにする
    // TODO Workの生成をMapActivityにしたので要検証

}
