package com.ApplicationAddOnsLband.activities;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ApplicationAddOnsLband.R;
import com.ApplicationAddOnsLband.api_manager.OkHttpUtil;
import com.ApplicationAddOnsLband.utils.AlenkaMediaPreferences;
import com.ApplicationAddOnsLband.utils.Constants;
import com.ApplicationAddOnsLband.utils.SharedPreferenceUtil;
import com.ApplicationAddOnsLband.utils.Utilities;
import com.github.rahatarmanahmed.cpv.CircularProgressView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class SettingsActivity extends Activity implements  OkHttpUtil.OkHttpResponse {
    public static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 5469;
    private RadioGroup radioGroup,radioGroup_Strt;
    ArrayList<String> permissions = new ArrayList<String>();
    CheckBox ch,ch1;
    CircularProgressView progressView1;
    TextView txtload;
    private ArrayList<String> arrdeviceid = new ArrayList<String>();
    ImageButton submit;
    Context context = SettingsActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        radioGroup = (RadioGroup) findViewById(R.id.groupradio);
        radioGroup_Strt = (RadioGroup) findViewById(R.id.groupradiostartup);
        ch=(CheckBox)findViewById(R.id.checkBoxAppsoverApps);
        txtload=(TextView)findViewById(R.id.txtloading);
        ch1=(CheckBox)findViewById(R.id.checkBoxStorageApp);
        progressView1 = (CircularProgressView) findViewById(R.id.progress_view1);
        submit = (ImageButton) findViewById(R.id.btn_Submit1);
        radioGroup.clearCheck();
        radioGroup_Strt.clearCheck();
        ch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                                          @Override
                                          public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                                              if(isChecked)
                                              {
                                                  ch.setClickable(false);
                                                  requestPermission();

                                              }

                                          }
                                      }
        );

        ch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                                           @Override
                                           public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                                               if(isChecked)
                                               {
                                                   ch1.setClickable(false);
                                                   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                                       checkPermissions();
                                                   }
                                                   else {
                                                       if (checkPermissions().size() > 0){
                                                           ActivityCompat.requestPermissions(SettingsActivity.this, permissions.toArray(new String[0]),100);
                                                       }
                                                   }

                                               }

                                           }
                                       }
        );


        radioGroup.setOnCheckedChangeListener(
                new RadioGroup
                        .OnCheckedChangeListener() {
                    @Override

                    public void onCheckedChanged(RadioGroup group,
                                                 int checkedId) {

                        // Get the selected Radio Button
                        RadioButton
                                radioButton
                                = (RadioButton) group
                                .findViewById(checkedId);
                    }
                });

        radioGroup_Strt.setOnCheckedChangeListener(
                new RadioGroup
                        .OnCheckedChangeListener() {
                    @Override

                    public void onCheckedChanged(RadioGroup group,
                                                 int checkedId) {

                        // Get the selected Radio Button
                        RadioButton
                                radioButton
                                = (RadioButton) group
                                .findViewById(checkedId);
                    }
                });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Utilities.showToast(context,"Hiot");
                int selectedId = radioGroup.getCheckedRadioButtonId();
                int selectedIdstartup = radioGroup_Strt.getCheckedRadioButtonId();
                if (selectedId == -1) {
                    SharedPreferenceUtil.setStringPreference(SettingsActivity.this, AlenkaMediaPreferences.Rotation, "0");
                    radioGroup.check(R.id.radia_0);
                    if (selectedIdstartup == -1)
                    {
                        SharedPreferenceUtil.setStringPreference(SettingsActivity.this, AlenkaMediaPreferences.Startup, "Auto");
                        radioGroup_Strt.check(R.id.radio_auto);
                    }
                    if(checkPermissions().size()>0)
                    {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

                        }
                        else {
                            Utilities.showToast(context, "please allow storage permission");
                            return;
                        }
                    }

                    checkUserRights();
                    progressView1.setVisibility(View.VISIBLE);
                    progressView1.startAnimation();
                    txtload.setVisibility(View.VISIBLE);

                }
                else {
                    int selectedIdafter = radioGroup.getCheckedRadioButtonId();

                    if (selectedIdstartup == -1)
                    {
                        SharedPreferenceUtil.setStringPreference(SettingsActivity.this, AlenkaMediaPreferences.Startup, "Auto");
                        radioGroup_Strt.check(R.id.radio_auto);

                    }

                    RadioButton radioButton
                            = (RadioButton) radioGroup
                            .findViewById(selectedIdafter);
                    String f = radioButton.getText().toString();
                    if (f.equals("0 (Landscape Normal)"))
                        f = "0";
                    else if (f.equals("90 (Portrait Right)"))
                        f = "90";
                    else if (f.equals("180 (Inverse Landscape)"))
                        f = "180";
                    else if (f.equals("270 (Portrait Left)"))
                        f = "270";
                    else
                        f = "0";
                    SharedPreferenceUtil.setStringPreference(SettingsActivity.this, AlenkaMediaPreferences.Rotation, f);
                    int selectedIdstartupafternonsel = radioGroup_Strt.getCheckedRadioButtonId();

                    RadioButton radioButtonstart
                            = (RadioButton) radioGroup_Strt
                            .findViewById(selectedIdstartupafternonsel);
                    String selstrttype = radioButtonstart.getText().toString();
                    if(selstrttype.equals("Auto"))
                    {
                        selstrttype="Auto";
                    }
                    else
                    {
                        selstrttype="Manual";
                    }
                    SharedPreferenceUtil.setStringPreference(SettingsActivity.this, AlenkaMediaPreferences.Startup, selstrttype);

                    if(checkPermissions().size()>0)
                    {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

                        }
                        else {
                            Utilities.showToast(context, "please allow storage permission");
                            return;
                        }
                    }
                    if(f.equals("90")) {
                        Toast.makeText(SettingsActivity.this, "Correct resolution is 1080 x 1920 for " + f, Toast.LENGTH_SHORT).show();
                    }

                    checkUserRights();
                    progressView1.setVisibility(View.VISIBLE);
                    progressView1.startAnimation();



                }



            }
        });

    }
    private void checkDeviceIdOnServer(){

        try {

            JSONArray jsondeviceid = new JSONArray();
            arrdeviceid.add(Utilities.getDeviceID(context));
            arrdeviceid.add(Splash_Activity.getMacAddr());
            arrdeviceid.add(Splash_Activity.getMacAddr1());
           arrdeviceid.add(Splash_Activity.getSerialNumber());
            for(int i=0;i<arrdeviceid.size();i++)
            {
                JSONObject json = new JSONObject();
                json.put("DeviceId",arrdeviceid.get(i));
                jsondeviceid.put(json);
            }


            new OkHttpUtil(context, Constants.CHECK_USER_RIGHTS,jsondeviceid.toString(),
                    SettingsActivity.this,false,
                    Constants.CHECK_USER_RIGHTS_TAG).callRequest();


        } catch (JSONException e) {
            e.printStackTrace();

        }
    }

    private void checkUserRights(){

        if (Utilities.isConnected()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    checkDeviceIdOnServer();


                }

            }, 2000);


        } else {

            String deviceID = SharedPreferenceUtil.getStringPreference(context,AlenkaMediaPreferences.DEVICE_ID);
            if (deviceID.equals("")) {
                  Utilities.showToast(context,"EmptyDevice");
                //showDialogBox(false);

            } else {

                /*Start the app in offline mode.*/
                new Handler().postDelayed(new Runnable() {
                    @Override

                    public void run() {

                        Intent intent = new Intent(SettingsActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, 2000);


            }
        }
    }


    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SYSTEM_ALERT_WINDOW)) {
        }
        ActivityCompat.requestPermissions(SettingsActivity.this, new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW},ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
    }


    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                //startActivityForResult(new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS), 0);
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions1, int[] grantResults) {
        permissions.clear();
        super.onRequestPermissionsResult(requestCode, permissions1, grantResults);
        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                checkPermission();
            }
        }

        if (requestCode == 100) {
            if (checkPermissions().size() > 0){
                ActivityCompat.requestPermissions(this, permissions.toArray(new String[0]),100);
            }
            else
            {

            }

        }

    }
    public void checkdeviceid()
    {
        Toast.makeText(SettingsActivity.this, "Permission is already granted!", Toast.LENGTH_SHORT).show();

    }

    private ArrayList<String> checkPermissions(){

        boolean hasWritePermission = (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        boolean hasReadPermission = (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasWritePermission){
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!hasReadPermission){
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        return permissions;
    }



    @Override
    public void onResponse(String response,int tag) {

        if (response == null){
              Toast.makeText(SettingsActivity.this, "Response returned null", Toast.LENGTH_SHORT).show();
            return;
        }

        switch (tag){

            case Constants.CHECK_USER_RIGHTS_TAG:{

                handleCheckDeviceIdResponse(response);
            }break;
        }

    }


    @Override
    public void onError(Exception e,int tag) {


        if (tag == Constants.CHECK_USER_RIGHTS_TAG){

            String deviceID = SharedPreferenceUtil.getStringPreference(context,AlenkaMediaPreferences.DEVICE_ID);
            if (deviceID.equals("")) {
                Utilities.showToast(context,"Device Id Empty");

            }
            else {

                Thread thread = new Thread(){
                    public void run(){
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Intent intent = new Intent(SettingsActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                };
                thread.start();


            }

//            checkDeviceIdOnServer();
        }
        e.printStackTrace();
    }


    public void handleCheckDeviceIdResponse(String response) {
        try {

            if (response.equals("[]")) {
                Intent intent = new Intent(SettingsActivity.this, HomeActivity.class);
                startActivity(intent);
                progressView1.setVisibility(View.GONE);
                progressView1.stopAnimation();
                txtload.setVisibility(View.GONE);
                finish();
                return;
            }
            String Response = new JSONArray(response).getJSONObject(0).getString("Response");

            if (Response.equals("1")) {

                Intent intent = new Intent(SettingsActivity.this, Splash_Activity.class);
                startActivity(intent);
                progressView1.setVisibility(View.GONE);
                progressView1.stopAnimation();
                txtload.setVisibility(View.GONE);
                finish();

            } else {
                if (Response.equals("0")) {
                    Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                    startActivity(intent);
                    progressView1.setVisibility(View.GONE);
                    progressView1.stopAnimation();
                    txtload.setVisibility(View.GONE);
                    finish();

                }
            }
        }catch (Exception e)
        {
            e.getCause().toString();
        }
    }

}

