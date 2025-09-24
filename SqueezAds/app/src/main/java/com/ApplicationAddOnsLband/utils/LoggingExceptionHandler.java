package com.ApplicationAddOnsLband.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


import com.ApplicationAddOnsLband.activities.Splash_Activity;
import com.ApplicationAddOnsLband.application.AlenkaMedia;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by love on 19/5/18.
 */

public class LoggingExceptionHandler implements Thread.UncaughtExceptionHandler {

    private final static String TAG = LoggingExceptionHandler.class.getSimpleName();
    private final static String ERROR_FILE = Exception.class.getSimpleName() + ".error";

    private final Context context;
    private final Thread.UncaughtExceptionHandler rootHandler;

    public LoggingExceptionHandler(Context context) {
        this.context = context;
        rootHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(final Thread thread, final Throwable ex) {

        try {
            Log.d(TAG, "called for " + ex.getClass());
            String stackTrace = Log.getStackTraceString(ex);
          //  String classstack=ex.getStackTrace()[0].getClassName();
           // String methodstack=ex.getStackTrace()[0].getMethodName();

            final JSONObject jsonObject = new JSONObject();

            try {
                Calendar calendar;
                calendar =Calendar.getInstance();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MMM/yyyy hh:mm:ss aa", Locale.US);
                String crash_date_time = simpleDateFormat.format(calendar.getTime());
                jsonObject.put("CrashDateTime",crash_date_time);
                jsonObject.put("TokenId", SharedPreferenceUtil.getStringPreference(context,Constants.TOKEN_ID));
                jsonObject.put("CrashLog",stackTrace);

                /*jsonObject.put("MethodName","LoggingException");
                jsonObject.put("ClassName","");
                jsonObject.put("ParametersUsed","");
                jsonObject.put("SeverityId","5");
                jsonObject.put("TokenId", SharedPreferenceUtil.getStringPreference(context,Constants.TOKEN_ID));
                jsonObject.put("ApplicationId","4");
                jsonObject.put("ErrorMessage",stackTrace);*/


            }catch (Exception e){
                e.printStackTrace();
                Log.e(TAG, "uncaughtException: "+e.getMessage());
            }

            saveCrashLogForNextTime(jsonObject.toString(),thread, ex);


        } catch (Exception e) {
            Log.e(TAG, "Exception Logger failed!", e);
        }
    }

    private void saveCrashLogForNextTime(String jsonString, Thread thread, final Throwable ex){

        SharedPreferenceUtil.setStringPreference(context,Constants.CRASH_MESSAGE,jsonString);
        displayToastAndCrashApplication(thread, ex);
    }

    private void displayToastAndCrashApplication(Thread thread, final Throwable ex){

        Intent intent = new Intent(context, Splash_Activity.class);
        intent.putExtra("crash", true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(AlenkaMedia.getInstance().getBaseContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT |PendingIntent.FLAG_IMMUTABLE);

        AlarmManager mgr = (AlarmManager) AlenkaMedia.getInstance().getBaseContext().getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 10000, pendingIntent);

        System.exit(2);

        /*new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                // we cant start a dialog here, as the context is maybe just a background activity ...
                Toast.makeText(context, ex.getMessage() + " Application will close!", Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }.start();

        try {
            Thread.sleep(4000); // Let the Toast display before app will get shutdown
        } catch (InterruptedException e) {
            // Ignored.
        }

        rootHandler.uncaughtException(thread, ex);*/
    }

    private static String getExceptionDetails(Exception e) {

        StackTraceElement[] stackTraceElement = e.getStackTrace();

        String fileName = "";
        String methodName = "";
        int lineNumber = 0;

        try {

            for (int i = 0; i < stackTraceElement.length; i++) {

                    fileName = stackTraceElement[i].getFileName();
                    methodName = stackTraceElement[i].getMethodName();
                    lineNumber = stackTraceElement[i].getLineNumber();


            }
        } catch (Exception e2) {
        }


        return fileName + ":" + methodName + "():line "
                + String.valueOf(lineNumber);
    }
}