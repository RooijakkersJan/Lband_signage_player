package com.ApplicationAddOnsLband.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ApplicationAddOnsLband.models.Advertisements;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by patas tech on 9/2/2016.
 */
public class AdvertisementDataSource {

    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = {MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_ADV_FILE_URL, MySQLiteHelper.COLUMN_ADV_ID,
            MySQLiteHelper.COLUMN_ADV_NAME, MySQLiteHelper.COLUMN_ADV_IS_MIN,
            MySQLiteHelper.COLUMN_ADV_IS_SONG, MySQLiteHelper.COLUMN_ADV_IS_TIME,
            MySQLiteHelper.COLUMN_ADV_PLY_TYPE,MySQLiteHelper.COLUMN_ADV_Sound_TYPE,
            MySQLiteHelper.COLUMN_ADV_SERIAL_NO,
            MySQLiteHelper.COLUMN_ADV_TOTAL_MIN, MySQLiteHelper.COLUMN_ADV_TOTAL_SONGS,
            MySQLiteHelper.COLUMN_ADV_E_DATE, MySQLiteHelper.COLUMN_ADV_S_DATE,
            MySQLiteHelper.COLUMN_ADV_S_TIME, MySQLiteHelper.COLUMN_ADV_PATH,
            MySQLiteHelper.COLUMN_SET_DOWNLOAD_STATUS,
            MySQLiteHelper.COLUMN_IMGTime,
            MySQLiteHelper.COLUMN_START_TIME_IN_MILLIS_ADV,
            MySQLiteHelper.COLUMN_END_TIME_IN_MILLIS_ADV, MySQLiteHelper.COLUMN_btStart_TIME_IN_MILLIS_ADV,

    };

    public AdvertisementDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    /**
     * Insert inti Advertisement
     */
    public Advertisements createAdvertisement(Advertisements advertisements) {

        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_ADV_FILE_URL, advertisements.getAdvFileUrl());
        values.put(MySQLiteHelper.COLUMN_ADV_ID, advertisements.getAdvtID());
        values.put(MySQLiteHelper.COLUMN_ADV_NAME, advertisements.getAdvtName());
        values.put(MySQLiteHelper.COLUMN_ADV_IS_MIN, advertisements.getIsMinute());
        values.put(MySQLiteHelper.COLUMN_ADV_IS_SONG, advertisements.getIsSong());
        values.put(MySQLiteHelper.COLUMN_ADV_IS_TIME, advertisements.getIsTime());
        values.put(MySQLiteHelper.COLUMN_ADV_PLY_TYPE, advertisements.getPlayingType());
        values.put(MySQLiteHelper.COLUMN_ADV_Sound_TYPE, advertisements.getSoundType());
        values.put(MySQLiteHelper.COLUMN_ADV_SERIAL_NO, advertisements.getSRNo());
        values.put(MySQLiteHelper.COLUMN_ADV_TOTAL_MIN, advertisements.getTotalMinutes());
        values.put(MySQLiteHelper.COLUMN_ADV_TOTAL_SONGS, advertisements.getTotalSongs());
        values.put(MySQLiteHelper.COLUMN_ADV_E_DATE, advertisements.geteDate());
        values.put(MySQLiteHelper.COLUMN_ADV_S_DATE, advertisements.getsDate());
        values.put(MySQLiteHelper.COLUMN_ADV_S_TIME, advertisements.getsTime());
        values.put(MySQLiteHelper.COLUMN_ADV_PATH, advertisements.getAdvtFilePath());
        values.put(MySQLiteHelper.COLUMN_SET_DOWNLOAD_STATUS, advertisements.getStatus_Download());
        values.put(MySQLiteHelper.COLUMN_IMGTime, advertisements.getimagetime());
        values.put(MySQLiteHelper.COLUMN_START_TIME_IN_MILLIS_ADV, advertisements.getStart_Adv_Time_Millis());
        values.put(MySQLiteHelper.COLUMN_END_TIME_IN_MILLIS_ADV, advertisements.getEnd_Adv_Time_Millis());
        values.put(MySQLiteHelper.COLUMN_btStart_TIME_IN_MILLIS_ADV, advertisements.getbtStart_Adv_Time_Millis());

        long insertId = database.insert(MySQLiteHelper.TABLE_ADVERTISEMENT, null,
                values);

        Cursor cursor = database.query(MySQLiteHelper.TABLE_ADVERTISEMENT,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Advertisements advertisement = cursorToAdvertisement(cursor);
        cursor.close();

        return advertisement;
    }

    public Advertisements insertAdvertisement(String url,String advid) {

        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_ADV_FILE_URL,url);
        values.put(MySQLiteHelper.COLUMN_ADV_ID, advid);
        values.put(MySQLiteHelper.COLUMN_ADV_NAME,"InstantAd" );
        values.put(MySQLiteHelper.COLUMN_ADV_IS_MIN,"0");
        values.put(MySQLiteHelper.COLUMN_ADV_IS_SONG,"0");
        values.put(MySQLiteHelper.COLUMN_ADV_IS_TIME, "0");
        values.put(MySQLiteHelper.COLUMN_ADV_PLY_TYPE,"instanttype" );
        values.put(MySQLiteHelper.COLUMN_ADV_Sound_TYPE,"0" );
        values.put(MySQLiteHelper.COLUMN_ADV_SERIAL_NO,"" );
        values.put(MySQLiteHelper.COLUMN_ADV_TOTAL_MIN,"");
        values.put(MySQLiteHelper.COLUMN_ADV_TOTAL_SONGS, "");
        values.put(MySQLiteHelper.COLUMN_ADV_E_DATE,"" );
        values.put(MySQLiteHelper.COLUMN_ADV_S_DATE, "");
        values.put(MySQLiteHelper.COLUMN_ADV_S_TIME,"" );
        values.put(MySQLiteHelper.COLUMN_ADV_PATH,"" );
        values.put(MySQLiteHelper.COLUMN_SET_DOWNLOAD_STATUS,"" );
        values.put(MySQLiteHelper.COLUMN_IMGTime,"10");
        values.put(MySQLiteHelper.COLUMN_START_TIME_IN_MILLIS_ADV, "");
        values.put(MySQLiteHelper.COLUMN_END_TIME_IN_MILLIS_ADV,"" );
        values.put(MySQLiteHelper.COLUMN_btStart_TIME_IN_MILLIS_ADV, "");

        long insertId = database.insert(MySQLiteHelper.TABLE_ADVERTISEMENT, null,
                values);

        Cursor cursor = database.query(MySQLiteHelper.TABLE_ADVERTISEMENT,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Advertisements advertisement = cursorToAdvertisement(cursor);
        cursor.close();

        return advertisement;
    }


    private Advertisements cursorToAdvertisement(Cursor cursor) {
        Advertisements advertisements = new Advertisements();
        advertisements.set_id(cursor.getLong(0));
        advertisements.setAdvFileUrl(cursor.getString(1));
        advertisements.setAdvtID(cursor.getString(2));
        advertisements.setAdvtName(cursor.getString(3));
        advertisements.setIsMinute(cursor.getString(4));
        advertisements.setIsSong(cursor.getString(5));
        advertisements.setIsTime(cursor.getString(6));
        advertisements.setPlayingType(cursor.getString(7));
        advertisements.setSoundType(cursor.getString(8));
        advertisements.setSRNo(cursor.getString(9));
        advertisements.setTotalMinutes(cursor.getString(10));
        advertisements.setTotalSongs(cursor.getString(11));
        advertisements.seteDate(cursor.getString(12));
        advertisements.setsDate(cursor.getString(13));
        advertisements.setsTime(cursor.getString(14));
        advertisements.setAdvtFilePath(cursor.getString(15));
        advertisements.setStatus_Download(cursor.getInt(16));
        advertisements.setimagetime(cursor.getString(17));
        advertisements.setStart_Adv_Time_Millis(cursor.getLong(18));
        advertisements.setEnd_Adv_Time_Millis(cursor.getLong(19));
        advertisements.setbtstart_Adv_Time_Millis(cursor.getLong(20));

        return advertisements;
    }

    // Here we Check the Advertisements is Already Exist or not

    public void checkifExistAdv(Advertisements advertisements) {
        Cursor cursor = database.query(MySQLiteHelper.TABLE_ADVERTISEMENT, allColumns,
                MySQLiteHelper.COLUMN_ADV_ID + "=" + "'" + advertisements.getAdvtID() + "'", null, null, null, null);
        if (cursor.getCount() == 0) {
            createAdvertisement(advertisements);
        } else {
            UpdateAdvList(advertisements);
        }
    cursor.close();
}


    public void UpdateAdv(String songid,String songpath,String download) {
//        advertisementUpdate.setStatus_Download(0);
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_ADV_PATH,songpath);
        values.put(MySQLiteHelper.COLUMN_ADV_ID, songid);
        values.put(MySQLiteHelper.COLUMN_SET_DOWNLOAD_STATUS,download);

        database.update(MySQLiteHelper.TABLE_ADVERTISEMENT,values,MySQLiteHelper.COLUMN_ADV_ID + "=" + songid,null);
    }


    private void UpdateAdvList(Advertisements advertisementUpdate) {
//        advertisementUpdate.setStatus_Download(0);
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_ADV_FILE_URL, advertisementUpdate.getAdvFileUrl());
        values.put(MySQLiteHelper.COLUMN_ADV_ID, advertisementUpdate.getAdvtID());
        values.put(MySQLiteHelper.COLUMN_ADV_NAME, advertisementUpdate.getAdvtName());
        values.put(MySQLiteHelper.COLUMN_ADV_IS_MIN, advertisementUpdate.getIsMinute());
        values.put(MySQLiteHelper.COLUMN_ADV_IS_SONG, advertisementUpdate.getIsSong());
        values.put(MySQLiteHelper.COLUMN_ADV_IS_TIME, advertisementUpdate.getIsTime());
        values.put(MySQLiteHelper.COLUMN_ADV_PLY_TYPE, advertisementUpdate.getPlayingType());
        values.put(MySQLiteHelper.COLUMN_ADV_Sound_TYPE, advertisementUpdate.getSoundType());
        values.put(MySQLiteHelper.COLUMN_ADV_SERIAL_NO, advertisementUpdate.getSRNo());
        values.put(MySQLiteHelper.COLUMN_ADV_TOTAL_MIN, advertisementUpdate.getTotalMinutes());
        values.put(MySQLiteHelper.COLUMN_ADV_TOTAL_SONGS, advertisementUpdate.getTotalSongs());
        values.put(MySQLiteHelper.COLUMN_ADV_E_DATE, advertisementUpdate.geteDate());
        values.put(MySQLiteHelper.COLUMN_ADV_S_DATE, advertisementUpdate.getsDate());
        values.put(MySQLiteHelper.COLUMN_ADV_S_TIME, advertisementUpdate.getsTime());
        values.put(MySQLiteHelper.COLUMN_START_TIME_IN_MILLIS_ADV, advertisementUpdate.getStart_Adv_Time_Millis());

//        values.put(MySQLiteHelper.COLUMN_ADV_PATH, advertisementUpdate.getAdvtFilePath());


//        values.put(MySQLiteHelper.COLUMN_SET_DOWNLOAD_STATUS, advertisementUpdate.getStatus_Download());
        values.put(MySQLiteHelper.COLUMN_END_TIME_IN_MILLIS_ADV, advertisementUpdate.getEnd_Adv_Time_Millis());
        values.put(MySQLiteHelper.COLUMN_btStart_TIME_IN_MILLIS_ADV, advertisementUpdate.getbtStart_Adv_Time_Millis());
        values.put(MySQLiteHelper.COLUMN_IMGTime, advertisementUpdate.getimagetime());

        database.update(MySQLiteHelper.TABLE_ADVERTISEMENT,values,MySQLiteHelper.COLUMN_ADV_ID + "=" + "'" + advertisementUpdate.getAdvtID() + "'",null);
    }

    public ArrayList<Advertisements> getAdvThoseAreNotDownloaded(){
    ArrayList<Advertisements> arrayList = new ArrayList<Advertisements>();
    Cursor cursor = database.query(MySQLiteHelper.TABLE_ADVERTISEMENT,allColumns, MySQLiteHelper.COLUMN_SET_DOWNLOAD_STATUS + "=" + 0,null,null,null,null);
    cursor.moveToFirst();
    while (!cursor.isAfterLast()){
        Advertisements advertisements = cursorToAdvertisement(cursor);
        arrayList.add(advertisements);
        cursor.moveToNext();
    }
    cursor.close();
        return arrayList;
}

    public void UpdateDownloadStatusAndPath(Advertisements advertisements){
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_SET_DOWNLOAD_STATUS,advertisements.getStatus_Download());
        values.put(MySQLiteHelper.COLUMN_ADV_PATH,advertisements.getAdvtFilePath());

        database.update(MySQLiteHelper.TABLE_ADVERTISEMENT,values,"_id=" +advertisements.get_id(),null);

    }

    public ArrayList<Advertisements> getAllAdv(){
        ArrayList<Advertisements> advertisementsArrayList = new ArrayList<>();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_ADVERTISEMENT,
                allColumns, MySQLiteHelper.COLUMN_SET_DOWNLOAD_STATUS + " = " + 1
                , null, null,null, MySQLiteHelper.COLUMN_START_TIME_IN_MILLIS_ADV );

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Advertisements advertisements = cursorToAdvertisement(cursor);
                String songPath = advertisements.getAdvtFilePath();
                if(songPath!=null) {
                    File file = new File(songPath);
                    if (file.exists()) {
                        advertisementsArrayList.add(advertisements);
                    }
                }
//                }else{
//                    String id = String.valueOf(advertisements.get_id());
//                    updateAdvColumnDownloadStatus(id);
//                }
                cursor.moveToNext();
            }
            cursor.close();
            return advertisementsArrayList;

    }

    public ArrayList<Advertisements> getAllAdv(String songid){
        ArrayList<Advertisements> advertisementsArrayList = new ArrayList<>();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_ADVERTISEMENT,
                allColumns, MySQLiteHelper.COLUMN_SET_DOWNLOAD_STATUS + " = " + 1
                        + " AND " +
                        MySQLiteHelper.COLUMN_ADV_ID + " = " + songid
                , null, null,null,null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Advertisements advertisements = cursorToAdvertisement(cursor);
            String songPath = advertisements.getAdvtFilePath();
            if(songPath!=null) {
                File file = new File(songPath);
                if (file.exists()) {
                    advertisementsArrayList.add(advertisements);
                }
            }
//                }else{
//                    String id = String.valueOf(advertisements.get_id());
//                    updateAdvColumnDownloadStatus(id);
//                }
            cursor.moveToNext();
        }
        cursor.close();
        return advertisementsArrayList;

    }


    public void deleteAdvIfNotInServer(){
        try {
            database.delete(MySQLiteHelper.TABLE_ADVERTISEMENT, null, null);
        }
        catch (Exception e)
        {
            e.getCause().toString();
        }
    }

    public void deleteAds(Advertisements ads, boolean deleteSourceFile) {
        try {
            if (!database.isOpen())
                open();
            String id = ads.getAdvtID();
            String type=ads.getAdvtName();
            if(type.equals("InstantAd"))
            {

            }
            else {
                database.delete(MySQLiteHelper.TABLE_ADVERTISEMENT, MySQLiteHelper.COLUMN_ADV_ID
                        + " = " + id, null);

                if (deleteSourceFile) {
                    String songpath = ads.getAdvtFilePath();
                    File file = new File(songpath);
                    if (file.exists()) {
                        file.delete();
                    }
                }
            }

        }catch (Exception e)
        {
            e.getCause();
        }

    }

    public ArrayList<Advertisements> getListNotAvailableinWebResponse(String[] schIdArray){
        ArrayList<Advertisements> advt = new ArrayList<Advertisements>();
        String query = "SELECT * FROM " + MySQLiteHelper.TABLE_ADVERTISEMENT
                + " WHERE "+ MySQLiteHelper.COLUMN_ADV_ID +" NOT IN (" + makePlaceholders(schIdArray) + ")";
        Cursor cursor = database.rawQuery(query, schIdArray);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Advertisements ads = cursorToAdvertisement(cursor);
            advt.add(ads);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return advt;
    }

    private String makePlaceholders(String[] schIdArray){
        StringBuilder sb = new StringBuilder(schIdArray.length * 2 - 1);
        sb.append("?");
        for (int j = 1; j < schIdArray.length; j++) {
            sb.append(",?");
        }
        return sb.toString();
    }

    public void deleteAdvUnUsed(){
        database.delete(MySQLiteHelper.TABLE_ADVERTISEMENT,null,null);
    }
    public ArrayList<Advertisements> getNotExistInStorage(){
        ArrayList<Advertisements> advertisementsListToDownloadNotExist = new ArrayList<>();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_ADVERTISEMENT,
                allColumns, MySQLiteHelper.COLUMN_SET_DOWNLOAD_STATUS + " = " + 1, null, null,null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Advertisements advertisements = cursorToAdvertisement(cursor);
            String songPath = advertisements.getAdvtFilePath();
            if(songPath!=null) {
                File file = new File(songPath);
                if (!file.exists()) {
                    String id = String.valueOf(advertisements.get_id());
                    updateAdvColumnDownloadStatus(id);
                    advertisementsListToDownloadNotExist.add(advertisements);
                }
            }else {
                String id = String.valueOf(advertisements.get_id());
                updateAdvColumnDownloadStatus(id);
                advertisementsListToDownloadNotExist.add(advertisements);
            }
            cursor.moveToNext();
        }
        cursor.close();
        return advertisementsListToDownloadNotExist;
    }

//TODO: update the status download or not
    public void updateAdvColumnDownloadStatus(String id) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_SET_DOWNLOAD_STATUS, "0");
        // values.put(MySQLiteHelper.COLUMN_SONG_PATH, songs.getSongPath());
        database.update(MySQLiteHelper.TABLE_ADVERTISEMENT, values, "_id="+id, null);
        // updating row
//        database.update(MySQLiteHelper.TABLE_SONGS, values, MySQLiteHelper.COLUMN_TITLE_ID + " = ?", new String[]{String.valueOf(songs.getTitle_Id())});

    }
//TODO: Getting the IS_TIME Type Advertisement here
    public ArrayList<Advertisements> getIsTimeAdv(){
        ArrayList<Advertisements> advertisementsArrayList = new ArrayList<>();
        long currenTimeInMillis = System.currentTimeMillis();
        long newTime  = currenTimeInMillis + 2000;
        long lessNewTime = currenTimeInMillis - 2000;
        String query = "select * from " + MySQLiteHelper.TABLE_ADVERTISEMENT + " where " + lessNewTime + " <= " + MySQLiteHelper.COLUMN_START_TIME_IN_MILLIS_ADV + " and " + MySQLiteHelper.COLUMN_START_TIME_IN_MILLIS_ADV + " <= " + newTime + " AND " + MySQLiteHelper.COLUMN_SET_DOWNLOAD_STATUS + " = " + 1;
        Cursor cursor = database.rawQuery(query,null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            Advertisements advertisements = cursorToAdvertisement(cursor);
            advertisementsArrayList.add(advertisements);
            cursor.moveToNext();
        }
        cursor.close();
        return  advertisementsArrayList;
    }


}
