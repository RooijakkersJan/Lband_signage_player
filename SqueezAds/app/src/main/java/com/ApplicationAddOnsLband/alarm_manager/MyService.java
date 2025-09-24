package com.ApplicationAddOnsLband.alarm_manager;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;


import com.ApplicationAddOnsLband.R;
import com.ApplicationAddOnsLband.activities.HomeActivity;

import java.util.List;

public class MyService extends Service {
    private String TAG = "MyService";
    public static boolean isServiceRunning;
    private String CHANNEL_ID = "NOTIFICATION_CHANNEL";
    private Handler mHandler = null;
    private HandlerThread mHandlerThread = null;


    public MyService() {
        Log.d(TAG, "constructor called");
        isServiceRunning = false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate called");
        createNotificationChannel();
        isServiceRunning = true;
        mHandlerThread = new HandlerThread("HandlerThread");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand called");
        mHandler.postDelayed(runnable,200000);

        Intent notificationIntent = new Intent(this, HomeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Service is Running")
                .setContentText("DA")
                .setSmallIcon(R.drawable.smclogin)
                .setContentIntent(pendingIntent)
              //  .setColor(getResources().getColor(R.color.colorPrimary))
                .build();

        startForeground(1, notification);
        return START_STICKY;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String appName = getString(R.string.app_name);
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    appName,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy called");
        isServiceRunning = false;
        stopForeground(true);
        mHandler.removeCallbacks(runnable);
        // call MyReceiver which will restart this service via a worker
        Intent broadcastIntent = new Intent(this, MyReceiver.class);
        sendBroadcast(broadcastIntent);

        super.onDestroy();
    }


    Runnable runnable = new Runnable() {
        @Override
        public void run() {

            boolean isApplicationRunning = false;
            if(isAppOnForeground(getApplicationContext(),"com.ApplicationAddOnsLband")){

                Log.d(TAG,"App is in running state = ");
                isApplicationRunning = true;
            } else {
                isApplicationRunning = false;
            }
           // Utilities.showToast(MyService.this,"status="+isApplicationRunning);


            if (!isApplicationRunning){

                // if(strtuptype.equals("Auto")) {
                Context ctx = MyService.this; // or you can replace **'this'** with your **ActivityName.this**
                Intent i = ctx.getPackageManager().getLaunchIntentForPackage("com.ApplicationAddOnsLband");
                ctx.startActivity(i);
                //  }

            }
            //   }

            mHandler.postDelayed(runnable,150000);
        }
    };


    private boolean isAppOnForeground(Context context,String packageName ) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
       // final String packageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAppRunning(final Context context, final String packageName) {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        if (procInfos != null)
        {
            for (final ActivityManager.RunningAppProcessInfo processInfo : procInfos) {
                if (processInfo.processName.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }



}