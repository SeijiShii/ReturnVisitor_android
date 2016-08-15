package net.c_kogyo.returnvisitor.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import net.c_kogyo.returnvisitor.R;
import net.c_kogyo.returnvisitor.data.RVData;
import net.c_kogyo.returnvisitor.data.Work;

import java.util.Calendar;

/**
 * Created by SeijiShii on 2016/08/13.
 */

public class TimeCountService extends Service {

    private static boolean timeCounting;
    private long startTime;
    private Work mWork;

    private LocalBroadcastManager broadcastManager;

    public static final String TIME_COUNTING_ACTION = TimeCountService.class.getName() + "_time_counting_action";
    public static final String START_TIME = TimeCountService.class.getName() + "_start_time";
    public static final String DURATION = TimeCountService.class.getName() + "_duration";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        broadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
    }


    private long duration;
    private int minCounter;
    private Handler countStopHandler;

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {

        timeCounting = true;
        minCounter = 0;
        countStopHandler = new Handler();

        startTime = Calendar.getInstance().getTimeInMillis();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (timeCounting) {

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        //
                    }

                    duration = Calendar.getInstance().getTimeInMillis() - startTime;
                    final Intent timeBroadCastIntent = new Intent();
                    timeBroadCastIntent.setAction(TIME_COUNTING_ACTION);
                    timeBroadCastIntent.putExtra(START_TIME, startTime);
                    timeBroadCastIntent.putExtra(DURATION, duration);


                    broadcastManager.sendBroadcast(timeBroadCastIntent);

                    // TODO 約1分ごとに保存するようにする
                    minCounter++;
                    if (minCounter > 100) {

                        if (mWork == null) {

                            Calendar startCal = Calendar.getInstance();
                            startCal.setTimeInMillis(startTime);
                            mWork = new Work(startCal);
                        }

                        mWork.setEnd(Calendar.getInstance());

                        RVData.getInstance().workList.addOrSet(mWork);
                        minCounter = 0;
                    }

                }

                countStopHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        Toast.makeText(TimeCountService.this, R.string.logout_time_stop, Toast.LENGTH_SHORT).show();
                    }
                });

                stopSelf();
            }
        }).start();

        return START_STICKY;
    }

    public static boolean isTimeCounting() {
        return timeCounting;
    }

    public static void stopTimeCount() {
        timeCounting = false;
    }
}
