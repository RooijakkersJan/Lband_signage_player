package com.ApplicationAddonsSignage.utils;

import android.content.Context;
import android.widget.Toast;

import com.ApplicationAddonsSignage.activities.HomeActivity;
import com.ApplicationAddonsSignage.api_manager.OkHttpUtil;
import com.ApplicationAddonsSignage.database.InstantSongsDataSource;
import com.ApplicationAddonsSignage.models.Songs;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class InstantPlaylistActication implements OkHttpUtil.OkHttpResponse {

    Context context;
    String playlistActivateid;

    ArrayList<String> schIdArrayList = new ArrayList<String>();

    InstantSongsDataSource songsDataSource = null;
    ArrayList<String> titleId = new ArrayList<>();

    String splid;



    public InstantPlaylistActication(Context context,String playlistid){
        this.context = context;
        songsDataSource = new InstantSongsDataSource(this.context);
        playlistActivateid=playlistid;
        getSongsForPlaylistId(playlistid);

    }



    @Override
    public void onResponse(String response, int tag) {
        switch(tag) {


            case Constants.GET_SPL_PLAY_LIST_TITLES_TAG: {
//                Toast.makeText(this.context, "GET_SPL_PLAY_LIST_TITLES_TAG", Toast.LENGTH_SHORT).show();
                handleGetSongsResponse(response);
            }
        }

    }




    private void getSongsForPlaylistId(String playlistId){

        try {

            JSONObject json = new JSONObject();
            json.put("splPlaylistId", playlistId);

            new OkHttpUtil(context, Constants.GET_SPL_PLAY_LIST_TITLES_VIDEO,json.toString(),
                    InstantPlaylistActication.this,false,
                    Constants.GET_SPL_PLAY_LIST_TITLES_TAG).
                    callRequest();

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void handleGetSongsResponse(String response){

        if (response.equalsIgnoreCase("[]")){

            Toast.makeText(this.context, "Empty{}[] response", Toast.LENGTH_SHORT).show();

            return;
        }

        try {

            titleId.clear();
            songsDataSource.open();
            JSONObject jsonObjectRes = new JSONObject(response);
            String Response = jsonObjectRes.getString("response");
            if(Response.equals("1")) {
                songsDataSource.deleteAll();
                String jsonArray = jsonObjectRes.getString("data");
                JSONArray arr=new JSONArray(jsonArray);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject jsonObject = arr.getJSONObject(i);
                    Songs modal = new Songs();
                    modal.setTitle_Url(jsonObject.getString("TitleUrl"));
                    modal.setSpl_PlaylistId(jsonObject.getString("splPlaylistId"));
                    modal.setTitle(jsonObject.getString("Title"));
                    modal.setTitle_Id(jsonObject.getString("titleId"));
                    modal.setSerialNo(jsonObject.getLong("srno"));
                    modal.setFilesize(jsonObject.getString("FileSize"));
                    modal.settimeInterval(jsonObject.getInt("TimeInterval"));
                    modal.setmediatype(jsonObject.getString("mediatype"));
                    titleId.add(modal.getTitle_Id());
                    splid = modal.getSpl_PlaylistId();

                    // modal.setIs_Downloaded(0);
                    String existingFilePath =Utilities.getExistingFilePath(context, modal);

                    if (existingFilePath != null){
                        modal.setIs_Downloaded(1);
                        modal.setSongPath(existingFilePath);
                    } else {
                        modal.setIs_Downloaded(0);
                    }
                    // TODO: Check for song if song exist then skip else insert
                    songsDataSource.checkifinstantSongExist(modal,this.context);
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
            deleteExtraSongs();
            songsDataSource.close();
            ((HomeActivity) context).assignPlaylistActivation(playlistActivateid);


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



    }
















