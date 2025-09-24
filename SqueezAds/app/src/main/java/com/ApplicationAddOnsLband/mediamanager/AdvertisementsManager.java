package com.ApplicationAddOnsLband.mediamanager;

import android.content.Context;

import com.ApplicationAddOnsLband.database.AdvertisementDataSource;
import com.ApplicationAddOnsLband.models.Advertisements;
import com.ApplicationAddOnsLband.models.Playlist;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static com.ApplicationAddOnsLband.utils.Utilities.changeDateFormat;
import static com.ApplicationAddOnsLband.utils.Utilities.getTimeInMilliSec;

/**
 * Created by love on 12/6/17.
 */
public class AdvertisementsManager {

    private Context context;
    Calendar calander;
    SimpleDateFormat simpleDateFormat;
    private static long currentTimeInMilli;
    String time;

    private AdvertisementDataSource advertisementDataSource = null;

    public AdvertisementsManager(Context context) {

        this.context = context;

        this.advertisementDataSource = new AdvertisementDataSource(this.context);
    }
    public ArrayList<Advertisements> getAdvertisementsToBeDownloaded(){

        try {
            advertisementDataSource.open();
            return advertisementDataSource.getAdvThoseAreNotDownloaded();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            advertisementDataSource.close();
        }
        return null;
    }

    public ArrayList<Advertisements> getAdvertisementsThatAreDownloaded(String songid){

        try {
            advertisementDataSource.open();
            return advertisementDataSource.getAllAdv(songid);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            advertisementDataSource.close();
        }
        return null;
    }



    public ArrayList<Advertisements> getAdvertisementsThatAreDownloaded(){

        try {
            advertisementDataSource.open();
            return advertisementDataSource.getAllAdv();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            advertisementDataSource.close();
        }
        return null;
    }

    public void advertisementDownloaded(Advertisements advertisements){
        try {

            advertisementDataSource.open();
            advertisementDataSource.UpdateDownloadStatusAndPath(advertisements);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public ArrayList<Advertisements> getAdvertisementsForComingTime(){

        try {
            advertisementDataSource.open();
            calander = Calendar.getInstance();
            simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss aaa");
            time = simpleDateFormat.format(calander.getTime());

            String array[]=time.split("\\s+");
            time=array[1]+" "+array[2];
            ArrayList<Playlist> playlists = new ArrayList<Playlist>();

            String currentTime=changeDateFormat("1/1/1900"+" "+ time);

            // Here change the Date & Time in Milliseconds
            currentTimeInMilli=getTimeInMilliSec(currentTime);



            ArrayList<Advertisements> allAds = advertisementDataSource.getAllAdv();

            if (allAds.size() > 0){

                ArrayList<Advertisements> adsForComingTime = new ArrayList<>();

                for (Advertisements ad :allAds) {

                    /*Add only those advertisements whose end time is greater than current time.*/
                    if (ad.getStart_Adv_Time_Millis() >= currentTimeInMilli) {

                        adsForComingTime.add(ad);
                    }
                }

                return adsForComingTime;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            advertisementDataSource.close();
        }
        return null;
    }
}
