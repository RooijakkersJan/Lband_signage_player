package com.ApplicationAddOnsLband.utils;

import android.content.Context;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ApplicationAddOnsLband.activities.HomeActivity;

import org.json.JSONObject;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    //  HomeActivity hm=new HomeActivity();
    private static final String TAG = "FCM Service";
    private Context context;

    @Override
    public void onNewToken(String s) {
        Log.e("NEW_TOKEN", s);
       // SharedPreferenceUtil.setStringPreference(MyFirebaseMessagingService.this, AlenkaMediaPreferences.Firebaseserver,s);
       // Utilities.showToast(MyFirebaseMessagingService.this,"s");
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO: Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated.

        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
          //Utilities.showToast(MyFirebaseMessagingService.this,"request Received");

        final String body = remoteMessage.getNotification().getBody();
        //Toast.makeText(MyFirebaseMessagingService.this," Body => " + body,Toast.LENGTH_LONG).show();

       // RemoteMessage.Notification notification = new NotificationCompat.bu

        /* Handler mHandler = new Handler(getMainLooper());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                String str1 = body;
                Toast.makeText(MyFirebaseMessagingService.this," body "+ str1,Toast.LENGTH_LONG).show();
            }
        });*/

        
        if (body != null) {
            try {
                JSONObject jsonObj = new JSONObject(body);
                String id = jsonObj.getString("id");
                String datatype = jsonObj.getString("type");
                String Url=jsonObj.getString("url");
                String AlbumId=jsonObj.getString("albumid");
                int repeat=jsonObj.getInt("Repeat");
                String filesize=jsonObj.getString("Filesize");
                String titlename=jsonObj.getString("title");
                String ArtistId=jsonObj.getString("artistid");
                String Artistname=jsonObj.getString("artistname");
                String type = jsonObj.getString("PlayType");
                String rebootparam=jsonObj.getString("reboot");
                String cat = jsonObj.getString("category");


                if (type.equalsIgnoreCase("Next")) {
                    // Utilities.showToast(MyFirebaseMessagingService.this,"Request Received");
                    HomeActivity.getInstance().playnextsongfromweb(id,Url,AlbumId,ArtistId,titlename,Artistname,repeat,Long.parseLong(filesize),cat);
                }

                if ((datatype.equalsIgnoreCase("Publish")) && (type.equalsIgnoreCase("UpdateNow"))) {
                    //Utilities.showToast(MyFirebaseMessagingService.this,"Request fr Publish");
                    HomeActivity.getInstance().updateTokenpublish();
                }

                if(type.equalsIgnoreCase("Reboot") && rebootparam.equals("1"))
                {
                    HomeActivity.getInstance().rebootbox();

                }

                if (type.equalsIgnoreCase("Song")) {
                    //Toast.makeText(MyFirebaseMessagingService.this,"Hit api",Toast.LENGTH_LONG).show();
                    // HomeActivity.getInstance().playsongfromweb(id,Url,AlbumId,ArtistId,titlename,Artistname);
                }

                if (type.equalsIgnoreCase("Playlist")) {
                    HomeActivity.getInstance().playplaylistfromwebnow(id);
                }

                if (type.equalsIgnoreCase("Ads")) {
                    HomeActivity.getInstance().playadvnow(id);
                }

            } catch (Exception e) {
                e.getCause();
            }

        }

        MyNotificationManager.getInstance(this);

    }
}
