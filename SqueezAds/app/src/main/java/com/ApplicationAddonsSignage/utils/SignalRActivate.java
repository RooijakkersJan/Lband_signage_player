package com.ApplicationAddonsSignage.utils;


import android.content.Context;
import android.util.Log;

import com.ApplicationAddonsSignage.activities.LoginActivity;
import com.google.gson.JsonObject;
import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;

import org.json.JSONObject;

public class SignalRActivate
{
    private static final String TAG = SignalRActivate.class.getSimpleName();

    HubConnection hubConnection;
    String signalid="";
    Context context;

    public SignalRActivate(String url, Context ctx)
    {
        this.context=ctx;
        this.hubConnection = HubConnectionBuilder.create(url).build();
        handleIncomingMethods();
        start();
    }

    public void handleIncomingMethods()
    {
        String deviceid =Utilities.getDeviceID(context);

        this.hubConnection.on("WelcomeMethodName", (data) -> { // OK
            if(!data.isEmpty()) {
                signalid = data;
                this.hubConnection.invoke("GetFCMIdFromClientActivate",deviceid, signalid);
            }

            Log.d(TAG, data);

        }, String.class);

        this.hubConnection.on("playerActivateAsync", (data) -> { // OK
            if (data != null) {
                String sd=data.toString();

                try {
                    JSONObject jsonObj = new JSONObject(sd);
                    String datatype = jsonObj.getString("type");
                    String type = jsonObj.getString("playType");

                    if (datatype.equalsIgnoreCase("Activate")) {
                      //  Utilities.showToast(context,"Stop");
                        LoginActivity.getInstance().loginqrsuccess();
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
        this.hubConnection.stop();
    }
}