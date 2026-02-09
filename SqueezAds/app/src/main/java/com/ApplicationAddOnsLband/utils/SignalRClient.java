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
                    String txt=jsonObj.getString("alarmText");
                    String audiourl=jsonObj.getString("audioPromoUrl");
                    String logourl=jsonObj.getString("logoPromoUrl");
                    String audioPromoid=jsonObj.getString("audioPromoId");
                    String duration=jsonObj.getString("alarmTextDuration");
                    String promofilesize=jsonObj.getString("audioPromoFileSize");
                    String screencasttype=jsonObj.getString("screencasttype");

                    if (type.equalsIgnoreCase("Stop")) {
                      //  Utilities.showToast(context,"Stop");
                        HomeActivity.getInstance().stopCurrentSong(datatype);
                        return;
                    }


                    if(datatype.equalsIgnoreCase("alarm"))
                    {
                        cat="Normal";
                        HomeActivity.getInstance().playnextsongfromweb(audioPromoid, audiourl, duration, ArtistId, txt, logourl, 0, Long.parseLong(promofilesize), cat,screencasttype);
                        return;
                    }

                    if (datatype.equalsIgnoreCase("Song")) {
                        //  Utilities.showToast(context,"Request Received");
                     //   Utilities.showToast(context,"Play");
                        HomeActivity.getInstance().playnextsongfromweb(id, Url, AlbumId, ArtistId, titlename, Artistname, repeat, Long.parseLong(filesize), cat,"");
                        return;
                    }

                    if ((datatype.equalsIgnoreCase("Publish")) && (type.equalsIgnoreCase("UpdateNow"))) {
                        //Utilities.showToast(MyFirebaseMessagingService.this,"Request fr Publish");
                        HomeActivity.getInstance().updateTokenpublish();
                        return;
                    }

                    if(type.equalsIgnoreCase("Reboot") && rebootparam.equals("1"))
                    {
                        HomeActivity.getInstance().rebootbox();
                        return;
                    }


                    if (type.equalsIgnoreCase("Playlist")) {
                        HomeActivity.getInstance().playplaylistfromwebnow(id);
                        return;
                    }

                    if (type.equalsIgnoreCase("Ads")) {
                        HomeActivity.getInstance().playadvnow(id);
                        return;
                    }

                } catch (Exception e) {
                    e.getCause();
                }

            }

            Log.d(TAG, data.toString());

        }, JsonObject.class);

        hubConnection.onClosed(exception -> {
            try {
                // Re-attempt start
                hubConnection.start().blockingAwait();
            } catch (Exception e) {
                Log.e("SignalR", "Reconnection failed: " + e.getMessage());
            }
        });





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