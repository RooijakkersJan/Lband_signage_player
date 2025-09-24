package com.ApplicationAddOnsLband.utils;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.ApplicationAddOnsLband.activities.HomeActivity;
import com.ApplicationAddOnsLband.api_manager.OkHttpUtil;
import com.ApplicationAddOnsLband.database.PlaylistDataSource;
import com.ApplicationAddOnsLband.database.SongsDataSource;
import com.ApplicationAddOnsLband.interfaces.PlaylistLoaderListener;
import com.ApplicationAddOnsLband.mediamanager.PlaylistManager;
import com.ApplicationAddOnsLband.models.Playlist;
import com.ApplicationAddOnsLband.models.Songs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UpdateWithoutRestart implements OkHttpUtil.OkHttpResponse,PlaylistLoaderListener {

    Context context;
    int ctrvar=0;

    PlaylistDataSource playlistDataSource = null;

    PlaylistLoaderListener playlistLoaderListener;

    ArrayList<String> schIdArrayList = new ArrayList<String>();

    SongsDataSource songsDataSource = null;
    ArrayList<String> titleId = new ArrayList<>();

    String splid;

    private ArrayList<Playlist> playlists = new ArrayList<>();
    List<Playlist> noRepeat = new ArrayList<Playlist>();
    int currentlyDownloadingSongsFromPlaylistAtIndex = 0;
    int publishRe;

    public UpdateWithoutRestart(Context context, int Publish,PlaylistLoaderListener playlistLoaderListener){
        this.context = context;
        playlistDataSource = new PlaylistDataSource(this.context);
        songsDataSource = new SongsDataSource(this.context);
        publishRe=Publish;
        this.playlistLoaderListener = playlistLoaderListener;
    }



    @Override
    public void onResponse(String response, int tag) {
        switch(tag) {

            case Constants.GetSplPlaylist_TAG: {
                handleGettingPlaylistResponse(response);
            }
            break;

            case Constants.GET_SPL_PLAY_LIST_TITLES_TAG: {
//                Toast.makeText(this.context, "GET_SPL_PLAY_LIST_TITLES_TAG", Toast.LENGTH_SHORT).show();
                handleGetSongsResponse(response);
            }
        }

    }


    private ArrayList<Songs> getSongsToBeDownloaded(){

        ArrayList<Playlist> playlists = new PlaylistManager(UpdateWithoutRestart.this.context, null).getPlaylistFromLocallyToBedDownload();
        ArrayList<Songs> songsToBeDownloaded = null;

        if (playlists.size() > 0) {

            PlaylistManager songsLoader = new PlaylistManager(UpdateWithoutRestart.this.context, null);
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
                    //  this.playlistLoaderListener.finishedGettingPlaylist();
                }

                try {
                    Activity activity = (Activity)this.context;



                }catch (Exception e){
                    e.printStackTrace();
                }

            }
            else
            {
                if (this.playlistLoaderListener != null){
                    //this.playlistLoaderListener.finishedGettingPlaylist();
                }

                try {
                    Activity activity = (Activity)this.context;


                }catch (Exception e){
                    e.printStackTrace();
                }
            }
          //  ((HomeActivity)context).updatesimplemented();


            return;
        }

        try {

            titleId.clear();
            songsDataSource.open();
            //   Utilities.showToast(context,"songs=====>"+response);
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

                    // modal.setIs_Downloaded(0);
                    String existingFilePath = Utilities.getExistingFilePath(context, modal);

                    if (existingFilePath != null){
                        modal.setIs_Downloaded(1);
                        modal.setSongPath(existingFilePath);
                    } else {
                        modal.setIs_Downloaded(0);
                    }
                    ctrvar=1;
                    // TODO: Check for song if song exist then skip else insert
                    songsDataSource.checkifSongExist(modal,this.context);
                   // Utilities.showToast(context,"songs");

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

            // deleteExtraSongs();

            //If there are more playlists whose songs are not retreived get them

            if (currentlyDownloadingSongsFromPlaylistAtIndex < noRepeat.size() - 1){
                currentlyDownloadingSongsFromPlaylistAtIndex++;
                startDownloadingSongsForPlaylistWithPlaylistID(currentlyDownloadingSongsFromPlaylistAtIndex);
            }
            else
            {
                ((HomeActivity)context).getmarqueetxt();
                if(ctrvar==1) {
                    PlaylistManager adv = new PlaylistManager(UpdateWithoutRestart.this.context, null);
                    adv.getAdvertisements();
                    ((HomeActivity) context).updatesimplemented(publishRe);
                    ctrvar=0;
                }
            }

        }
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
                    songsDataSource.deleteSongs(arrayList.get(k),true);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        finally {
            songsDataSource.close();
        }
    }





    @Override
    public void onError(Exception e, int tag) {

    }

    private void handleGettingPlaylistResponse(String response){
        if(response.equals("[]")) {
            if (this.playlistLoaderListener != null) {
                //  this.playlistLoaderListener.finishedGettingPlaylist();
                ((HomeActivity)context).updatesimplemented(publishRe);

                return;

            }
        }
        try {

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
                    //Utilities.showToast(context, "Playlist");

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
                // this.playlistLoaderListener.errorInGettingPlaylist();
            }

            e.printStackTrace();
        } catch (JSONException e) {
            if (this.playlistLoaderListener != null) {
                //this.playlistLoaderListener.errorInGettingPlaylist();
            }
            e.printStackTrace();
        } finally {
            playlistDataSource.close();
            if (this.playlistLoaderListener != null) {
//                this.playlistLoaderListener.finishedGettingPlaylist();
            }
            deletExtraPlaylists();
            songsDataSource.open();
            songsDataSource.deleteAll();
            songsDataSource.close();
            getSongsForAllPlaylists();


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
        } else {

            if (this.playlistLoaderListener != null){
                //this.playlistLoaderListener.finishedGettingPlaylist();
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
            // Log.e(TAG, "json" + json);

/*
            new OkHttpUtil(context, Constants.GET_SPL_PLAY_LIST_TITLES_VIDEO,json.toString(),
                    PlaylistManager.this,false,
                    Constants.GET_SPL_PLAY_LIST_TITLES_TAG).
                    execute();
*/

            new OkHttpUtil(context, Constants.GET_SPL_PLAY_LIST_TITLES_VIDEO,json.toString(),
                    UpdateWithoutRestart.this,false,
                    Constants.GET_SPL_PLAY_LIST_TITLES_TAG).
                    callRequest();


        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void deletExtraPlaylists(){
        try{
            playlistDataSource.open();

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
                        // songsDataSource.deleteSongsWithPlaylist(arrayList.get(k).getpSc_id());
                    }
                    playlistDataSource.deletePlaylist(arrayList.get(k));
                }

            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            playlistDataSource.close();
            songsDataSource.close();
        }
    }

    public void getPlaylistsFromServer(){

        try{
            JSONObject json = new JSONObject();

            json.put("DfClientId", SharedPreferenceUtil.getStringPreference(context, AlenkaMediaPreferences.DFCLIENT_ID));
            json.put("TokenId", SharedPreferenceUtil.getStringPreference(context, AlenkaMediaPreferences.TOKEN_ID));
            json.put("WeekNo", Utilities.getCurrentDayNumber());


            new OkHttpUtil(context, Constants.GetSplPlaylist_VIDEO,json.toString(),
                    UpdateWithoutRestart.this,false,
                    Constants.GetSplPlaylist_TAG).
                    callRequest();


        } catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void startedGettingPlaylist() {

    }

    @Override
    public void finishedGettingPlaylist() {

    }

    @Override
    public void errorInGettingPlaylist() {

    }

    @Override
    public void recordSaved(boolean isSaved) {

    }

    @Override
    public void tokenUpdatedOnServer() {

    }
}

