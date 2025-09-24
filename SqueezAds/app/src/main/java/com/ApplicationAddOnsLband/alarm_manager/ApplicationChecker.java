package com.ApplicationAddOnsLband.alarm_manager;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
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
import com.ApplicationAddOnsLband.utils.AlenkaMediaPreferences;
import com.ApplicationAddOnsLband.utils.SharedPreferenceUtil;

import java.util.List;

public class ApplicationChecker extends Service {

    private Handler mHandler = null;

    private HandlerThread mHandlerThread = null;

    private static int CHECK_TIME = 300000;

    private static final int NOTIF_ID = 1;
    private static final String NOTIF_CHANNEL_ID = "Channel_Id_7";

    static final String TAG = ApplicationChecker.class.getSimpleName();

    public ApplicationChecker() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        // code to execute when the service is first created
        super.onCreate();
        Log.d(TAG, "Service Started.");
        mHandlerThread = new HandlerThread("HandlerThread");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent broadcastIntent = new Intent(this, ApplicationChecker.class);
        sendBroadcast(broadcastIntent);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startid)

    {
         startForeground();
        mHandler.postDelayed(runnable,CHECK_TIME);
        return START_STICKY;
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {

           // ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
           // List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfo = am.getRunningAppProcesses();
            String strtuptype= SharedPreferenceUtil.getStringPreference(ApplicationChecker.this, AlenkaMediaPreferences.Startup);

          //  if (runningAppProcessInfo != null){
             //   Log.d(TAG,"Currently running activities" + runningAppProcessInfo.size());
                boolean isApplicationRunning = false;
                String appRunningStatus = "App is in state ";

                if(isAppRunning(getApplicationContext(),"com.SbitSignage")){
                    Log.d(TAG,"App is in running state = ");
                    isApplicationRunning = true;
                    appRunningStatus += "Running";
                } else {
                    appRunningStatus += "Not Running";
                }

              //  Utilities.showToast(ApplicationChecker.this,appRunningStatus);

                if (!isApplicationRunning){
                   // if(strtuptype.equals("Auto")) {
                        Context ctx = ApplicationChecker.this; // or you can replace **'this'** with your **ActivityName.this**
                        Intent i = ctx.getPackageManager().getLaunchIntentForPackage("com.SbitSignage");
                        ctx.startActivity(i);
                  //  }

                }
         //   }

            mHandler.postDelayed(runnable,CHECK_TIME);
        }
    };

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



  /*  protected Boolean isAppRunning()
    {
        ActivityManager activityManager = (ActivityManager) getBaseContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);


        if (tasks == null){
            return true;
        }

        for (ActivityManager.RunningTaskInfo task : tasks) {
            if (Splash_Activity.class.getCanonicalName().equalsIgnoreCase(task.baseActivity.getClassName()) ||
                    HomeActivity.class.getCanonicalName().equalsIgnoreCase(task.baseActivity.getClassName()))
                return true;
        }

        return false;
    }  */


    private void startForeground() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            String NOTIFICATION_CHANNEL_ID = getPackageName();
            String channelName = "Audio background service";
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_LOW);
            NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);

            Notification notification = builder.setOngoing(true)
                    .setContentTitle("signage service")
                    .setContentText("Running in the background.")
                    .setSmallIcon(R.drawable.hotellogincolor)
                    .setPriority(Notification.PRIORITY_DEFAULT)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .build();
            startForeground(2, notification);
        }else{
            startForeground(1, new Notification());
        }

    }

    public  void createnotific()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NOTIF_CHANNEL_ID, "Sevice", importance);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }




}

