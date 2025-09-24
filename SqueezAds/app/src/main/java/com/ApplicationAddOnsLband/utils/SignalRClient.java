package com.ApplicationAddOnsLband.utils;


import android.content.Context;
import android.util.Log;

import com.ApplicationAddOnsLband.activities.HomeActivity;
import com.google.gson.JsonObject;
import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;

import org.json.JSONObject;

public class SignalRClient
{
    private static final String TAG = SignalRClient.class.getSimpleName();

    HubConnection hubConnection;
    String signalid="";
    Context context;

    public SignalRClient(String url,Context ctx)
    {
        this.context=ctx;
        this.hubConnection = HubConnectionBuilder.create(url).build();
        handleIncomingMethods();
        start();
    }

    public void handleIncomingMethods()
    {
        String token = SharedPreferenceUtil.getStringPreference(context, Constants.TOKEN_ID);

        this.hubConnection.on("WelcomeMethodName", (data) -> { // OK
            if(!data.isEmpty()) {
                signalid = data;
                this.hubConnection.invoke("GetDataFromClient", token, signalid);
            }

            Log.d(TAG, data);

        }, String.class);

        this.hubConnection.on("privateMessageMethodName", (data) -> { // OK
            if (data != null) {
                String sd=data.toString();

                try {
                    JSONObject jsonObj = new JSONObject(sd);
                    String id = jsonObj.getString("id");
                    String datatype = jsonObj.getString("type");
                    String Url=jsonObj.getString("url");
                    String AlbumId=jsonObj.getString("albumid");
                    int repeat=jsonObj.getInt("repeat");
                    String filesize=jsonObj.getString("filesize");
                    String titlename=jsonObj.getString("title");
                    String ArtistId=jsonObj.getString("artistid");
                    String Artistname=jsonObj.getString("artistname");
                    String type = jsonObj.getString("playType");
                    String rebootparam=jsonObj.getString("playerrestart");
                    String cat = jsonObj.getString("category");


                    if (type.equalsIgnoreCase("Next")) {
                       //  Utilities.showToast(context,"Request Received");
                        HomeActivity.getInstance().playnextsongfromweb(id,Url,AlbumId,ArtistId,titlename,Artistname,repeat,Long.parseLong(filesize),cat);
                    }


                    if (type.equalsIgnoreCase("Stop")) {
                        //  Utilities.showToast(context,"Request Received");
                        HomeActivity.getInstance().stopCurrentSong(cat);
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

            Log.d(TAG, data.toString());

        }, JsonObject.class);



    }

    public void start()

    {
        this.hubConnection.start().blockingAwait();
        int i=0;
    }

    public void stop()

    {
        this.hubConnection.stop().blockingAwait();
    }
}