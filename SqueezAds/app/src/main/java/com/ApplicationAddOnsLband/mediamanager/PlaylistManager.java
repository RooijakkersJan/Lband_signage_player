package com.ApplicationAddOnsLband.mediamanager;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.ApplicationAddOnsLband.activities.LoginActivity;
import com.ApplicationAddOnsLband.activities.Splash_Activity;
import com.ApplicationAddOnsLband.api_manager.OkHttpUtil;
import com.ApplicationAddOnsLband.application.AlenkaMedia;
import com.ApplicationAddOnsLband.database.AdvertisementDataSource;
import com.ApplicationAddOnsLband.database.PlaylistDataSource;
import com.ApplicationAddOnsLband.database.SongsDataSource;
import com.ApplicationAddOnsLband.interfaces.PlaylistLoaderListener;
import com.ApplicationAddOnsLband.models.Advertisements;
import com.ApplicationAddOnsLband.models.Playlist;
import com.ApplicationAddOnsLband.models.Songs;
import com.ApplicationAddOnsLband.utils.AlenkaMediaPreferences;
import com.ApplicationAddOnsLband.utils.Constants;
import com.ApplicationAddOnsLband.utils.SharedPreferenceUtil;
import com.ApplicationAddOnsLband.utils.UpdateWithoutRestart;
import com.ApplicationAddOnsLband.utils.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Created by love on 29/5/17.
 */
public class PlaylistManager implements OkHttpUtil.OkHttpResponse {

    public static final String TAG = "PlaylistManager";

    Context context;

    PlaylistDataSource playlistDataSource = null;

    PlaylistLoaderListener playlistLoaderListener;

    ArrayList<String> schIdArrayList = new ArrayList<String>();

    SongsDataSource songsDataSource = null;
    public String SongStatus="Delete";


    int currentlyDownloadingSongsFromPlaylistAtIndex = 0;

    ArrayList<String> titleId = new ArrayList<>();
    ArrayList<String> advId = new ArrayList<>();

    String splid;
    int ctrtknctnt=0;

    private ArrayList<Playlist> playlists = new ArrayList<>();
    List<Playlist> noRepeat = new ArrayList<Playlist>();
    List<String> updateadsCat = new ArrayList<>();


    private ArrayList<Playlist> allplaylists = new ArrayList<>();

    private AdvertisementDataSource advertisementDataSource;

    public PlaylistManager(Context context, PlaylistLoaderListener playlistLoaderListener){
        this.context = context;
        playlistDataSource = new PlaylistDataSource(this.context);
        songsDataSource = new SongsDataSource(this.context);
        advertisementDataSource = new AdvertisementDataSource(this.context);
        this.playlistLoaderListener = playlistLoaderListener;
    }

    public void getPlaylistsFromServer(){

        if (this.playlistLoaderListener != null){
            this.playlistLoaderListener.startedGettingPlaylist();
        }

        try{
            JSONObject json = new JSONObject();

            json.put("DfClientId", SharedPreferenceUtil.getStringPreference(context, AlenkaMediaPreferences.DFCLIENT_ID));
            json.put("TokenId", SharedPreferenceUtil.getStringPreference(context, AlenkaMediaPreferences.TOKEN_ID));
            json.put("WeekNo", Utilities.getCurrentDayNumber());

/*
            new OkHttpUtil(context, Constants.GetSplPlaylist_VIDEO,json.toString(),
                    PlaylistManager.this,false,
                    Constants.GetSplPlaylist_TAG).
                    execute();
*/

            new OkHttpUtil(context, Constants.GetSplPlaylist_VIDEO,json.toString(),
                    PlaylistManager.this,false,
                    Constants.GetSplPlaylist_TAG).
                    callRequest();


        } catch (Exception e){
            e.printStackTrace();
        }
    }



    public void getAdvertisements(){

        try{
            JSONObject json = new JSONObject();

            json.put("Cityid", SharedPreferenceUtil.getStringPreference(context, AlenkaMediaPreferences.City_ID));
            json.put("CountryId", SharedPreferenceUtil.getStringPreference(context, AlenkaMediaPreferences.Country_ID));
            json.put("CurrentDate", Utilities.currentFormattedDate());
            json.put("DfClientId", SharedPreferenceUtil.getStringPreference(context, AlenkaMediaPreferences.DFCLIENT_ID));
            json.put("StateId", SharedPreferenceUtil.getStringPreference(context, AlenkaMediaPreferences.State_Id));
            json.put("TokenId", SharedPreferenceUtil.getStringPreference(context, AlenkaMediaPreferences.TOKEN_ID));
            json.put("WeekNo", Utilities.getDayNumberForAdv());

            new OkHttpUtil(context, Constants.ADVERTISEMENTS,json.toString(),
                    PlaylistManager.this,false,
                    Constants.ADVERTISEMENTS_TAG).
                    callRequest();


        } catch (Exception e){
            e.printStackTrace();
        }


    }

    public ArrayList<Playlist> getCurrentPlaylist(){

        try {
            playlistDataSource.open();
            /*Commented method returns the playlist for current time only and not for the future times.*/
//            return playlistDataSource.getAllPlaylists();

            /*This method returns the playlist for current time and the future times.*/
            return playlistDataSource.getPlaylistsForCurrentAndComingTime();
        } catch (Exception e){
            e.printStackTrace();
        }
        finally {
            playlistDataSource.close();
        }
        return null;
    }

    /*This method returns playlists for current time only and not for future times.*/

    public ArrayList<Playlist> getPlaylistForCurrentTimeOnly(){

        try {
            playlistDataSource.open();

            return playlistDataSource.getAllPlaylists();

        } catch (Exception e){
            e.printStackTrace();
        }
        finally {
            playlistDataSource.close();
        }
        return null;
    }

    @Override
    public void onResponse(String response, int tag) {

        if (response == null){
            Toast.makeText(this.context, "Empty response", Toast.LENGTH_SHORT).show();
            if(tag == Constants.GetSplPlaylist_TAG)
            {
                if ((getSongsToBeDownloaded() != null) && (getSongsToBeDownloaded().size() > 0)){

                    if (this.playlistLoaderListener != null){
                        this.playlistLoaderListener.finishedGettingPlaylist();
                    }

                    try {
                        Activity activity = (Activity)this.context;

                        if (activity instanceof Splash_Activity || activity instanceof LoginActivity){
                            activity.finish();
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
                else
                {
                    if (this.playlistLoaderListener != null){
                        this.playlistLoaderListener.finishedGettingPlaylist();
                    }

                    try {
                        Activity activity = (Activity)this.context;

                        if (activity instanceof Splash_Activity || activity instanceof LoginActivity){
                            activity.finish();
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
            return;
        }

        switch(tag){

            case Constants.GetSplPlaylist_TAG :{
//                Toast.makeText(this.context, "GetSplPlaylist_TAG", Toast.LENGTH_SHORT).show();
                handleGettingPlaylistResponse(response);
            }break;

            case Constants.GET_SPL_PLAY_LIST_TITLES_TAG:{
//                Toast.makeText(this.context, "GET_SPL_PLAY_LIST_TITLES_TAG", Toast.LENGTH_SHORT).show();
                handleGetSongsResponse(response);
            }break;

            case Constants.GetTokenContent_TAG:{
               handleResponseforSongsNotInSchdPlaylist(response);
            }break;

            case Constants.ADVERTISEMENTS_TAG:{
//                Toast.makeText(this.context, "ADVERTISEMENTS_TAG", Toast.LENGTH_SHORT).show();
                handleResponseForAdvertisements(response);
            }break;

            case Constants.CHECK_TOKEN_PUBLISH_TAG:{
                handleResponseForTokenPublish(response);
            }break;

            case Constants.UPDATE_TOKEN_PUBLISH_TAG:{
                handleResponseForTokenUpdatedOnServer(response);
            }break;
        }

    }

    private void handleResponseForTokenUpdatedOnServer(String response) {

        try {

            JSONArray jsonArray = new JSONArray(response);

            if (jsonArray != null){

                if (jsonArray.length() > 0){

                    JSONObject jsonObject = jsonArray.getJSONObject(0);

                    String isPublishUpdate = jsonObject.getString("IsPublishUpdate");

                    if (isPublishUpdate.equals("1")){

                        if (this.playlistLoaderListener != null){
                            this.playlistLoaderListener.tokenUpdatedOnServer();
                            new UpdateWithoutRestart(context,1,playlistLoaderListener).getPlaylistsFromServer();

                        }

                    }
                }
            }



        }catch (Exception e){

            e.printStackTrace();
        }
    }

    private void handleResponseForTokenPublish(String response) {

        try {

            JSONArray jsonArray = new JSONArray(response);

            if (jsonArray != null){

                if (jsonArray.length() > 0){

                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String isPublishUpdate = jsonObject.getString("IsPublishUpdate");

                    Log.e(TAG,"IsPublishUpdate value: " + isPublishUpdate);

                    if (isPublishUpdate.equals("1")){
                        Log.e(TAG,"Get new data.");
                        AlenkaMedia.getInstance().isUpdateInProgress = true;
                       // new UpdateWithoutRestart(context,1,playlistLoaderListener).getPlaylistsFromServer();
                        return;
                    }
                }
            }




        }catch (Exception e){

            e.printStackTrace();
        }
    }

    @Override
    public void onError(Exception e, int tag) {
        if (this.playlistLoaderListener != null)
        {

            if(tag==Constants.GetSplPlaylist_TAG)
            {

                if (this.playlistLoaderListener != null){
                    this.playlistLoaderListener.finishedGettingPlaylist();
                }

                try {
                    Activity activity = (Activity)this.context;

                    if (activity instanceof Splash_Activity || activity instanceof LoginActivity){
                        activity.finish();
                    }

                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }

            if(tag==Constants.GET_SPL_PLAY_LIST_TITLES_TAG)
            {
                if(SongStatus.equalsIgnoreCase("Delete"))
                {
                    if (this.playlistLoaderListener != null){
                        this.playlistLoaderListener.finishedGettingPlaylist();
                    }

                    try {
                        Activity activity = (Activity)this.context;
                        //  SongStatus="Delete";

                        if (activity instanceof Splash_Activity || activity instanceof LoginActivity){
                            activity.finish();
                        }

                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
                else
                {

                    if(SongStatus.equalsIgnoreCase("Update"))
                    {
                        if (this.playlistLoaderListener != null){
                            SongStatus="Delete";
                            getSongsForAllPlaylists();
                        }


                    }

                }

            }

            if(tag==Constants.ADVERTISEMENTS_TAG)
            {
                if (this.playlistLoaderListener != null){
                    this.playlistLoaderListener.finishedGettingPlaylist();
                }

                try {
                    Activity activity = (Activity)this.context;
                    //  SongStatus="Delete";

                    if (activity instanceof Splash_Activity || activity instanceof LoginActivity){
                        activity.finish();
                    }

                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }

        }
    }

    private void handleGettingPlaylistResponse(String response) {

        if(response.equals("[]")) {
            if (this.playlistLoaderListener != null) {
                this.playlistLoaderListener.finishedGettingPlaylist();

            }
            Activity activity = (Activity) this.context;
            if (activity instanceof Splash_Activity || activity instanceof LoginActivity) {
                activity.finish();
                return;
            }
        }

        String str="<?xml";
        String matchstr=response.substring(0,6);
        if (matchstr.contains(str)) {
            if (this.playlistLoaderListener != null) {
                this.playlistLoaderListener.finishedGettingPlaylist();

            }
            try {
                Activity activity = (Activity) this.context;
                if (activity instanceof Splash_Activity || activity instanceof LoginActivity) {
                    activity.finish();
                    return;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        else {
            try {
              //  deleteplaylist();
              //  Utilities.showToast(context,"Playlist=====>"+response);

                playlistDataSource.open();
                schIdArrayList.clear();
                JSONObject jsonObjectRes = new JSONObject(response);
                String Response = jsonObjectRes.getString("response");
                if(Response.equals("1")) {
                    String jsonArray = jsonObjectRes.getString("data");
                    JSONArray arr = new JSONArray(jsonArray);
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject jsonObject = arr.getJSONObject(i);
                        Playlist modal = new Playlist();
                        String startTime = jsonObject.getString("StartTime");
                        String endTime = jsonObject.getString("EndTime");

                        //TODO: Here we change the format of date & Time
                        String startTime1 = Utilities.changeDateFormat(startTime);
                        String endTime1 = Utilities.changeDateFormat(endTime);

                        //TODO: Here change the Date & Time in Milliseconds

                        long startTimeInMilli = Utilities.getTimeInMilliSec(startTime1);
                        long endTimeInMilli = Utilities.getTimeInMilliSec(endTime1);
                        modal.setStart_Time_In_Milli(startTimeInMilli);
                        modal.setEnd_Time_In_Milli(endTimeInMilli);
                        modal.setStart_time(startTime);
                        modal.setEnd_time(endTime);

                        modal.setFormat_id(jsonObject.getString("FormatId"));
                        modal.setdfclient_id(jsonObject.getString("dfclientid"));
                        modal.setPlaylistCategory(jsonObject.getString("IsMute"));
                        modal.setpSc_id(jsonObject.getString("pScid"));
                        modal.setsplPlaylist_Id(jsonObject.getString("splPlaylistId"));
                        modal.setsplPlaylist_Name(jsonObject.getString("splPlaylistName"));
                        modal.setvolper(jsonObject.getString("VolumeLevel"));
                        modal.setIsSeparatinActive(jsonObject.getLong("IsSeprationActive_New"));
                        schIdArrayList.add(modal.getpSc_id());
                        playlistDataSource.checkifPlaylistExist(modal);

                    }
                }
                else
                {
                    if(Response.equals("0"))
                    {
                        if (this.playlistLoaderListener != null) {
                            deleteplaylist();
                            //this.playlistLoaderListener.finishedGettingPlaylist();

                        }
                    }
                }

            } catch (SQLException e) {
                if (this.playlistLoaderListener != null) {
                    this.playlistLoaderListener.errorInGettingPlaylist();
                }

                e.printStackTrace();
            } catch (JSONException e) {
                if (this.playlistLoaderListener != null) {
                    this.playlistLoaderListener.errorInGettingPlaylist();
                }
                e.printStackTrace();
            } finally {
                playlistDataSource.close();
                if (this.playlistLoaderListener != null) {
//                this.playlistLoaderListener.finishedGettingPlaylist();
                }
                deletExtraPlaylists();
                getSongsForAllPlaylists();
            }
        }
    }

    private ArrayList<Songs> getSongsToBeDownloaded(){

        ArrayList<Playlist> playlists = new PlaylistManager(PlaylistManager.this.context, null).getPlaylistFromLocallyToBedDownload();
        ArrayList<Songs> songsToBeDownloaded = null;

        if (playlists.size() > 0) {

            PlaylistManager songsLoader = new PlaylistManager(PlaylistManager.this.context, null);
            songsToBeDownloaded = new ArrayList<>();
            for (Playlist playlist : playlists) {

                ArrayList<Songs> songs = songsLoader.getSongsThatAreNotDownloaded(playlist.getsplPlaylist_Id());

                if (songs != null && songs.size() > 0) {

//                    if (playlist.getIsSeparatinActive() == 0){
//                    }

                    songsToBeDownloaded.addAll(songs);
                }
            }
            songsLoader = null;

            if (songsToBeDownloaded.size() > 0) {
                return songsToBeDownloaded;
            }
        }
        return null;
    }

    private void handleGetSongsResponse(String response){

        if (response.equalsIgnoreCase("[]")){

            Toast.makeText(this.context, "Empty{}[] response", Toast.LENGTH_SHORT).show();

            if ((getSongsToBeDownloaded() != null) && (getSongsToBeDownloaded().size() > 0)){

                if (this.playlistLoaderListener != null){
                    this.playlistLoaderListener.finishedGettingPlaylist();
                }

                try {
                    Activity activity = (Activity)this.context;

                    if (activity instanceof Splash_Activity || activity instanceof LoginActivity){
                        activity.finish();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }

            }
            else
            {
                if (this.playlistLoaderListener != null){
                    this.playlistLoaderListener.finishedGettingPlaylist();
                }

                try {
                    Activity activity = (Activity)this.context;

                    if (activity instanceof Splash_Activity || activity instanceof LoginActivity){
                        activity.finish();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            return;
        }

        try {

            if(SongStatus.equalsIgnoreCase("Delete"))
            {
                SongStatus = "Update";
                JSONObject jsonObjectRes = new JSONObject(response);
                String Response = jsonObjectRes.getString("response");
                if(Response.equals("1")) {
                    songsDataSource.open();
                    songsDataSource.deleteAll();
                    songsDataSource.close();
                }
            }
            titleId.clear();
            songsDataSource.open();
            JSONObject jsonObjectRes = new JSONObject(response);
            String Response = jsonObjectRes.getString("response");
            if(Response.equals("1")) {
                String jsonArray = jsonObjectRes.getString("data");
                JSONArray arr=new JSONArray(jsonArray);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject jsonObject = arr.getJSONObject(i);
                    Songs modal = new Songs();
                    modal.setAlbum_ID(jsonObject.getString("AlbumID"));
                    modal.setArtist_ID(jsonObject.getString("ArtistID"));
                    modal.setTitle(jsonObject.getString("Title"));
                    modal.setTitle_Url(jsonObject.getString("TitleUrl"));
                    modal.setAl_Name(jsonObject.getString("alName"));
                    modal.setAr_Name(jsonObject.getString("arName"));
                    modal.setSpl_PlaylistId(jsonObject.getString("splPlaylistId"));
                    modal.setT_Time(jsonObject.getString("tTime"));
                    modal.setTitle_Id(jsonObject.getString("titleId"));
                    modal.setSerialNo(jsonObject.getLong("srno"));
                    modal.setFilesize(jsonObject.getString("FileSize"));
                    modal.settimeInterval(jsonObject.getInt("TimeInterval"));
                    modal.setmediatype(jsonObject.getString("mediatype"));
                    modal.setreftime(jsonObject.getInt("urlRefershTime"));

                    titleId.add(modal.getTitle_Id());
                    splid = modal.getSpl_PlaylistId();


                    modal.setIs_Downloaded(0);
                    String existingFilePath = Utilities.getExistingFilePath(context, modal);

                    if (existingFilePath != null){
                        modal.setIs_Downloaded(1);
                        modal.setSongPath(existingFilePath);
                    } else {
                        modal.setIs_Downloaded(0);
                    }
                    // TODO: Check for song if song exist then skip else insert
                    songsDataSource.checkifSongExist(modal,this.context);
//                    modalSongList.add(modal);
                }
            }
            else {
                Toast.makeText(this.context, "No songs in playlist.", Toast.LENGTH_SHORT).show();
            }

        }catch (Exception e){
           // Toast.makeText(this.context, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }finally {

            songsDataSource.close();

            deleteExtraSongs();

            //If there are more playlists whose songs are not retreived get them

            if (currentlyDownloadingSongsFromPlaylistAtIndex < noRepeat.size() - 1){
                currentlyDownloadingSongsFromPlaylistAtIndex++;
                startDownloadingSongsForPlaylistWithPlaylistID(currentlyDownloadingSongsFromPlaylistAtIndex);
            }
            else
                {
                    getAdvertisements();
                  //  if(ctrtknctnt==0) {
                      //  getSongsNotInSchdPlaylist();
                   //     ctrtknctnt=1;
                   // }
                }

        }
    }

    private void getSongsNotInSchdPlaylist()
    {
        try{
           // Utilities.showToast(context,"Songs not in schd");
            JSONObject json = new JSONObject();
            json.put("Tokenid", SharedPreferenceUtil.getStringPreference(context, AlenkaMediaPreferences.TOKEN_ID));
            json.put("WeekId", String.valueOf(Utilities.getCurrentDayNumber()));

            new OkHttpUtil(context, Constants.GetTokenContent,json.toString(),
                    PlaylistManager.this,false,
                    Constants.GetTokenContent_TAG).
                    callRequest();


        } catch (Exception e){
            e.printStackTrace();
        }

    }


    private void handleResponseforSongsNotInSchdPlaylist(String response)
    {
            ctrtknctnt = 1;
            handleGetSongsResponse(response);


    }


    private void handleResponseForAdvertisements(String response){
        if (response.equalsIgnoreCase("[]")){
            try {
                if (this.playlistLoaderListener != null){
                    this.playlistLoaderListener.finishedGettingPlaylist();
                }

                Activity activity = (Activity)this.context;

                if (activity instanceof Splash_Activity || activity instanceof LoginActivity){
                    activity.finish();
                }

            }catch (Exception e){
                e.printStackTrace();
            }
            return;


        }
        try {
             advId.clear();
            updateadsCat.clear();
             advertisementDataSource.open();
            JSONArray jsonArray = new JSONArray(response);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject = jsonArray.getJSONObject(i);

                if (jsonObject.getString("Response").equals("1")) {

                    Advertisements modal_Adv = new Advertisements();
                    //String Response = jsonObject.getString("Response");
                    modal_Adv.setAdvFileUrl(jsonObject.getString("AdvtFilePath"));
                    modal_Adv.setAdvtID(jsonObject.getString("AdvtId"));
                    modal_Adv.setAdvtName(jsonObject.getString("AdvtName"));
                    modal_Adv.setIsMinute(jsonObject.getString("IsMinute"));
                    modal_Adv.setIsSong(jsonObject.getString("IsSong"));
                    modal_Adv.setIsTime(jsonObject.getString("IsTime"));
                    modal_Adv.setPlayingType(jsonObject.getString("PlayingType"));
                    modal_Adv.setSoundType(jsonObject.getString("SoundType"));
                    modal_Adv.setSRNo(jsonObject.getString("SrNo"));
                    modal_Adv.setTotalMinutes(jsonObject.getString("TotalSeconds"));
                    modal_Adv.setTotalSongs(jsonObject.getString("TotalSongs"));
                    modal_Adv.seteDate(jsonObject.getString("eDate"));
                    modal_Adv.setsDate(jsonObject.getString("sDate"));
                    modal_Adv.setsTime(jsonObject.getString("sTime"));
                    modal_Adv.setimagetime(jsonObject.getString("imagetime"));

                    advId.add(modal_Adv.getAdvtID());

                    String edate = jsonObject.getString("eDate");
                    String sdate = jsonObject.getString("sDate");
                    String sTime = jsonObject.getString("sTime");
                    if(sTime.equals("12:00 AM"))
                    {
                        sTime="12:01 AM";
                    }
                    String beTime=jsonObject.getString("bETime");
                    String bsTime=jsonObject.getString("bSTime");

                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat mdformat = new SimpleDateFormat("dd-MMM-yyyy");
                    String strDate ="01-Jan-1900";
                    String play_Adv_Time = strDate + " " + sTime;
                    String betstime = strDate + " " + bsTime;
                    String betetime = strDate + " " + beTime;

                    //TODO : cahnge format using Prayer Method
                    String adv_time = Utilities.changeDateFormatForPrayer(play_Adv_Time);
                    String formated_start = Utilities.changeDateFormatForPrayer(betstime);
                    String formated_timeend = Utilities.changeDateFormatForPrayer(betetime);

                    //TODO : get Time in milliseconds using Prayer Method
                    long timeInMilliadvstrt= Utilities.getTimeInMilliSecForPrayer(adv_time);
                    long timeInMillistart = Utilities.getTimeInMilliSecForPrayer(formated_start);
                    long timeInMilliend = Utilities.getTimeInMilliSecForPrayer(formated_timeend);
                    modal_Adv.setStart_Adv_Time_Millis(timeInMilliadvstrt);
                    modal_Adv.setbtstart_Adv_Time_Millis(timeInMillistart);
                    modal_Adv.setEnd_Adv_Time_Millis(timeInMilliend);

                    modal_Adv.setStatus_Download(0);
                    String playing_type = modal_Adv.getPlayingType(); // Hard Stop


                    String is_Minute = modal_Adv.getIsMinute(); //1
                    String totalMinutes = modal_Adv.getTotalMinutes(); // 5

                    String is_Song = modal_Adv.getIsSong(); // 0
                    String totalSongs = modal_Adv.getTotalSongs();

                    String isTime = modal_Adv.getIsTime();

                    if (is_Minute.equals("1")) {
                        SharedPreferenceUtil.setStringPreference(this.context, AlenkaMediaPreferences.is_Minute_Adv, "1");
                        if(!updateadsCat.contains("is_Minute")) {
                            updateadsCat.add("is_Minute");
                        }

                    } else if (is_Song.equals("1")) {
                        SharedPreferenceUtil.setStringPreference(this.context, AlenkaMediaPreferences.is_song_Adv, "1");
                        SharedPreferenceUtil.setStringPreference(this.context, AlenkaMediaPreferences.total_Songs,totalSongs);
                        if(!updateadsCat.contains("is_Song")) {
                            updateadsCat.add("is_Song");
                        }

                    } else if (isTime.equals("1")) {
                        SharedPreferenceUtil.setStringPreference(this.context, AlenkaMediaPreferences.is_Time_Adv, "1");
                        if(!updateadsCat.contains("is_Time")) {
                            updateadsCat.add("is_Time");
                        }
                    }

                    advertisementDataSource.checkifExistAdv(modal_Adv);

                }
                else
                {
                    if (jsonObject.getString("Response").equals("0")) {
                        SharedPreferenceUtil.setStringPreference(this.context, AlenkaMediaPreferences.is_song_Adv, "");
                        SharedPreferenceUtil.setStringPreference(this.context, AlenkaMediaPreferences.is_Minute_Adv, "");
                        SharedPreferenceUtil.setStringPreference(this.context, AlenkaMediaPreferences.is_Time_Adv,"");
                        advertisementDataSource.open();
                        advertisementDataSource.deleteAdvIfNotInServer();
                        advertisementDataSource.close();
                    }
                }
            }

        }catch (Exception e)
        {
            e.getCause();
        }
        finally {
            if(advId.size()>0) {
                deletExtraAds();
            }
            if(updateadsCat.size()>0)
            {
                if(!updateadsCat.contains("is_Minute"))
                {
                    SharedPreferenceUtil.setStringPreference(this.context, AlenkaMediaPreferences.is_Minute_Adv, "");
                }
                if(!updateadsCat.contains("is_Song"))
                {
                    SharedPreferenceUtil.setStringPreference(this.context, AlenkaMediaPreferences.is_song_Adv, "");
                    SharedPreferenceUtil.setStringPreference(this.context, AlenkaMediaPreferences.total_Songs,"");

                }
                if(!updateadsCat.contains("is_Time"))
                {
                    SharedPreferenceUtil.setStringPreference(this.context, AlenkaMediaPreferences.is_Time_Adv, "");
                }

            }
            advertisementDataSource.close();

            if (this.playlistLoaderListener != null) {
                this.playlistLoaderListener.finishedGettingPlaylist();
            }

            try {
                Activity activity = (Activity) this.context;

                if (activity instanceof Splash_Activity || activity instanceof LoginActivity) {
                    activity.finish();
                }

            } catch (Exception e) {
                e.printStackTrace();
                final String h = e.getMessage();
            }
        }

    }



    public ArrayList<Songs> getDownloadedSongsForPlaylistID(String playlistID) {

        ArrayList<Songs> arrayList = new ArrayList<>();

        try {
            songsDataSource.open();

            arrayList = songsDataSource.getSongsThoseAreDownloaded(playlistID);

            songsDataSource.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrayList;

    }

    public ArrayList<Playlist> getAllPlaylistInPlayingOrder() {

        ArrayList<Playlist> arrayList = null;

        try {
            playlistDataSource.open();

            arrayList = playlistDataSource.getAllPlaylistsInPlayingOrder();

            playlistDataSource.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    public ArrayList<Songs> getAllDownloadedSongs(String songid){

        try {
            songsDataSource.open();

            return songsDataSource.getAllDownloadedSongs(songid);

        }catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            songsDataSource.close();
        }
        return null;
    }

    private void deleteExtraSongs(){

        try {

            songsDataSource.open();

            ArrayList<Songs> arrayList = songsDataSource.getSongListNotAvailableinWebResponse
                    (Arrays.copyOf(titleId.toArray(), titleId.toArray().length, String[].class),splid);
            if (arrayList.size() > 0) {
                for (int k = 0; k < arrayList.size(); k++) {
                    String songpath = arrayList.get(k).getSongPath();
                    File file = new File(songpath);
//                    file.delete();
                    songsDataSource.deleteSongs(arrayList.get(k),false);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        finally {
            songsDataSource.close();
        }
    }

    public void getSongsForAllPlaylists(){

       // allplaylists.addAll(getAllPlaylistInPlayingOrder());
        playlists.clear();
        noRepeat.clear();
        playlists.addAll(getAllPlaylistInPlayingOrder());
        for (Playlist event : playlists) {
            boolean isFound = false;
            // check if the event name exists in noRepeat
            for (Playlist e : noRepeat) {
                if (e.getsplPlaylist_Id().equalsIgnoreCase(event.getsplPlaylist_Id()))
                    isFound = true;
            }
            if (!isFound) {
                noRepeat.add(event);
            }
        }

        if (noRepeat.size() > 0){
          int h=  noRepeat.size();
            startDownloadingSongsForPlaylistWithPlaylistID(currentlyDownloadingSongsFromPlaylistAtIndex);
        }
        else {

            if (this.playlistLoaderListener != null){
                this.playlistLoaderListener.finishedGettingPlaylist();
            }
            try {
                Activity activity = (Activity)this.context;
                //  SongStatus="Delete";

                if (activity instanceof Splash_Activity || activity instanceof LoginActivity){
                    activity.finish();
                }

            }catch (Exception e){
                e.printStackTrace();
            }
          //  Toast.makeText(this.context, "No songs for current time.", Toast.LENGTH_SHORT).show();
        }

    }
    private void startDownloadingSongsForPlaylistWithPlaylistID(int index){

        getSongsForPlaylistId(noRepeat.get(index).getsplPlaylist_Id());


    }

    private void getSongsForPlaylistId(String playlistId){

        try {

            JSONObject json = new JSONObject();
            json.put("splPlaylistId", playlistId);
            Log.e(TAG, "json" + json);

/*
            new OkHttpUtil(context, Constants.GET_SPL_PLAY_LIST_TITLES_VIDEO,json.toString(),
                    PlaylistManager.this,false,
                    Constants.GET_SPL_PLAY_LIST_TITLES_TAG).
                    execute();
*/

            new OkHttpUtil(context, Constants.GET_SPL_PLAY_LIST_TITLES_VIDEO,json.toString(),
                    PlaylistManager.this,false,
                    Constants.GET_SPL_PLAY_LIST_TITLES_TAG).
                    callRequest();


        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void deleteplaylist()
    {
        try {
            playlistDataSource.open();
            playlistDataSource.deletePlaylist();
            playlistDataSource.close();
        }
        catch(Exception e)
        {
            e.getCause();
        }
    }

    private void deletExtraPlaylists(){
        try{
            playlistDataSource.open();
            if(schIdArrayList.size()>0) {

                ArrayList<Playlist> arrayList = playlistDataSource.
                        getListNotAvailableinWebResponse(
                                Arrays.copyOf(schIdArrayList.toArray(),
                                        schIdArrayList.toArray().length,
                                        String[].class));

                if (arrayList.size() > 0) {

                    songsDataSource.open();
                    //TODO: check if playlist id not refer in other schid record if not exist then delete all songs else dont
                    for (int k = 0; k < arrayList.size(); k++) {
                        if (playlistDataSource.checkifPlaylistExist(arrayList.get(k).getpSc_id())) {
                            //songsDataSource.deleteSongsWithPlaylist(arrayList.get(k).getpSc_id());
                        }
                        playlistDataSource.deletePlaylist(arrayList.get(k));
                    }

                }
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            playlistDataSource.close();
            songsDataSource.close();
        }
    }

    private void deletExtraAds(){
        try{
            advertisementDataSource.open();

            ArrayList<Advertisements> arrayList = advertisementDataSource.
                    getListNotAvailableinWebResponse(
                            Arrays.copyOf(advId.toArray(),
                                    advId.toArray().length,
                                    String[].class));
            if (arrayList.size() > 0) {
                for (int k = 0; k < arrayList.size(); k++) {
                    String songpath = arrayList.get(k).getAdvtFilePath();
                   // File file = new File(songpath);
//                    file.delete();
                    advertisementDataSource.deleteAds(arrayList.get(k),true);
                }
            }





        } catch (Exception e){
            e.printStackTrace();
        } finally {
            advertisementDataSource.close();
        }
    }


    private ArrayList<Playlist> getPlaylistFromLocally() {
        ArrayList<Playlist> arrayList = null;
        ArrayList<Playlist> remaingArrayist = null;
        try {
            playlistDataSource.open();
            arrayList = playlistDataSource.getAllPlaylists();
            remaingArrayist = playlistDataSource.getRemainingAllPlaylists();

            if (arrayList.size() > 0) {

//                 TODO Add the playlists whose time to play is gon
//                  e.
            }
            arrayList.addAll(remaingArrayist);
            playlistDataSource.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    public void songDownloaded(Songs songs, PlaylistLoaderListener songsLoaderListener){
        try {
            songsDataSource.open();
            songsDataSource.updateSongsListWithDownloadstatusandPath(songs);

        }catch (Exception e){
            e.printStackTrace();
            if (songsLoaderListener != null){
                songsLoaderListener.recordSaved(false);
            }
        }finally {
            songsDataSource.close();

            if (songsLoaderListener != null){
                songsLoaderListener.recordSaved(true);
            }
        }
    }

    public ArrayList<Songs> getSongsThatAreNotDownloaded(String playlistId){
        try {
            songsDataSource.open();
            return songsDataSource.getSongsThoseAreNotDownloaded(playlistId);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            songsDataSource.close();
        }
        return null;
    }

    public ArrayList<Songs> getUnschdSongs(){
        try {
            songsDataSource.open();
            return songsDataSource.getUnschdSongsThoseAreNotDownloaded();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            songsDataSource.close();
        }
        return null;
    }



    public ArrayList<Songs> getSongsForPlaylist(String playlistId){
        try {
            songsDataSource.open();
            return songsDataSource.getAllSongss(playlistId);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            songsDataSource.close();
        }
        return null;
    }

    public ArrayList<Playlist> getAllPlaylistCatSchd(){

        try {
            playlistDataSource.open();

            return playlistDataSource.getAllDistinctPlaylists();

        } catch (Exception e){
            e.printStackTrace();
        }
        finally {
            playlistDataSource.close();
        }
        return null;
    }


    public ArrayList<Songs> getSongsForPlaylistRandom(String playlistId){
        try {
            songsDataSource.open();
            return songsDataSource.getAllSongssRandom(playlistId);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            songsDataSource.close();
        }
        return null;
    }

    public ArrayList<Songs> getDownloadedSongsForPlaylist(String playlistId){
        try {
            songsDataSource.open();
            return songsDataSource.getSongsThoseAreDownloaded(playlistId);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            songsDataSource.close();
        }
        return null;
    }

    public ArrayList<Songs> getNotDownloadedSongsForPlaylist(String playlistId){
        try {
            songsDataSource.open();
            return songsDataSource.getSongsThoseAreNotDownloaded(playlistId);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            songsDataSource.close();
        }
        return null;
    }

    /*This method provides the playlists which will play after current time.*/
    public ArrayList<Playlist> getPlaylistFromLocallyToBedDownload() {

        ArrayList<Playlist> arrayList = null;
        ArrayList<Playlist> remaingArrayist = null;
        ArrayList<Playlist> arrayListGoneTime=null;
        try {
            playlistDataSource.open();

            arrayList = playlistDataSource.getPlaylistsForCurrentAndComingTime();

            remaingArrayist = playlistDataSource.getRemainingAllPlaylists();

          //  ArrayList<Playlist> arrayListGoneTime = new ArrayList<>();
            arrayListGoneTime = playlistDataSource.getPendingPastPlaylist();

            if(arrayList.size()>0 || remaingArrayist.size()>0 || arrayListGoneTime.size()>0)
            {
                arrayList.addAll(remaingArrayist);
                arrayList.addAll(arrayListGoneTime);
            }
            arrayList.size();
            /*We are not adding the playlists for the playlists whose time has gone.*/
//            arrayList.addAll(remaingArrayist);
            playlistDataSource.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    public void checkUpdatedPlaylistData(){

        Log.e(TAG,"Checking for new data");

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Tokenid",SharedPreferenceUtil.
                    getStringPreference(PlaylistManager.this.context,Constants.TOKEN_ID));

            new OkHttpUtil(context, Constants.CHECK_TOKEN_PUBLISH,jsonObject.toString(),
                    PlaylistManager.this,false,
                    Constants.CHECK_TOKEN_PUBLISH_TAG).
                    callRequest();


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void publishTokenForUpdatedData(){

        try {
          //  Utilities.showToast(context,"Publish");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Tokenid",SharedPreferenceUtil.
                    getStringPreference(PlaylistManager.this.context,Constants.TOKEN_ID));

            new OkHttpUtil(context, Constants.UPDATE_TOKEN_PUBLISH,jsonObject.toString(),
                    PlaylistManager.this,false,
                    Constants.UPDATE_TOKEN_PUBLISH_TAG).
                    callRequest();


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public int getTotalDownloadedSongs(){

        try {
            songsDataSource.open();

            return songsDataSource.getCountForTotalSongsDownloaded();


        }catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            songsDataSource.close();
        }
        return 0;
    }
}