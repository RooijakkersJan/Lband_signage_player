package com.ApplicationAddOnsLband.alarm_manager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;

import com.ApplicationAddOnsLband.activities.HomeActivity;
import com.ApplicationAddOnsLband.activities.Splash_Activity;
import com.ApplicationAddOnsLband.application.AlenkaMedia;
import com.ApplicationAddOnsLband.mediamanager.AdvertisementsManager;
import com.ApplicationAddOnsLband.mediamanager.PlaylistManager;
import com.ApplicationAddOnsLband.models.Advertisements;
import com.ApplicationAddOnsLband.models.Playlist;
import com.ApplicationAddOnsLband.utils.AlenkaMediaPreferences;
import com.ApplicationAddOnsLband.utils.SharedPreferenceUtil;
import com.ApplicationAddOnsLband.utils.Utilities;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;

/**
 * Created by love on 3/6/17.
 */
public class PlaylistWatcher {


    /***********************************************************
     *
     * Constant for when no playlist is present at current time.
     *
     ***********************************************************/
    public static final int NO_PLAYLIST = 0;
    public int countermin=0;


    /***********************************************************
     *
     * Constant for when a playlist is present at current time.
     *
     ***********************************************************/
    public static final int PLAYLIST_PRESENT = 1;
    int p=0;

    /************************PLAYLIST_CHANGE********************
     *
     * Constant for when a playlist is present at current time and
     * the next playlist is present without any gap.
     * For example end time of Playlist A is 4:00 pm and the start
     * time of Playlist B is also 4:00 pm. In this case we stop
     * current playback and start the new one.
     *
     ***********************************************************/

    public static final int PLAYLIST_CHANGE = 2;


    /*****************currentDayOfTheWeek************************
     * This variable indicates the current day of week as an integer.
     * For ex 1 for Monday 2 for Tuesday and so on.
     ***********************************************************/
    private static int currentDayOfTheWeek = -1;
    private  int ADVERTISEMENT_TIME_Store = 1;



    /******************currentPlaylistID***********************
     * ID of the playlist currently playling.
     **********************************************************/
    public static String currentPlaylistID = "";
    public long milliSec;
    public String formattedDate="";
    public ArrayList<String> arrtotaltime = new ArrayList<String>();
    public ArrayList<Advertisements> arrAdvertisementsTimeFilter = new ArrayList<Advertisements>();
    public ArrayList<String> arrtotalsong = new ArrayList<String>();
    public ArrayList<Advertisements> arrAdvertisementssongFilter = new ArrayList<Advertisements>();
    public HomeActivity hm=new HomeActivity();




    /**********************************************************
     * Handler that checks the playlists time. This runs every
     * 10 second
     ***********************************************************/
    private Handler mHandler = null;

    /***********************************************************
     * This handler is used to run the mHandler in background.
     ************************************************************/
    private HandlerThread mHandlerThread = null;

    Context context;

    private long IsPlaylistFind=0;
   // private long playlistcounter=0;
    private String cTime="";
    String playlistId="";
    private String popuniv=" ";
    private int daysuniv=0;
    private long popdismissuniv=0;

    //public  int strcounter=0;
    private String cDate="";
    private PlaylistStatusListener playlistStatusListener;
    //public HomeActivity hm=new HomeActivity();


    private static int UPDATE_PLAYER_STATUS_TIME = 600 * 1000; //15 minutes 900

    private static long UPDATE_PLAYER_STATUS_TIMER = 0;

    private static int CHECK_PENDING_DOWNLOADS_TIME = 600 * 1000; //15 minutes 900

    private static long CHECK_PENDING_DOWNLOADS_TIMER = 0;

    private static String ADVERTISEMENT_TYPE_isMin = ""; // 1 for isMinute, 2 for isSong, 3 is for isTime
    private static String ADVERTISEMENT_TYPE_isSong = "";
    private static String ADVERTISEMENT_TYPE_isTime = "";

    private static int TIME_AFTER_ADVERTISEMENT_SHOULD_PLAY = -1;
    private ArrayList<Playlist> playlists;
    private ArrayList<Playlist> playlistsAll;

    /*******************ADVERTISEMENT_TIME_COUNTER*******************
     * ADVERTISEMENT_TIME_COUNTER is used to check the time for an ad
     * to play. Initial value is 0 and after every 10 seconds the value
     * is increased to 10 seconds(in milliseconds). When the value becomes
     * equal to TIME_AFTER_ADVERTISEMENT_SHOULD_PLAY we play the advertisement.
     ****************************************************************/
    public static long ADVERTISEMENT_TIME_COUNTER = 0;

    /*************************ADVERTISEMENT_TIME********************
     * ADVERTISEMENT_TIME indicates the number of minutes(in milliseconds)
     * after which the ad will play. Default value is 1 and actual value we
     * get from server.
     ****************************************************************/
    private static int ADVERTISEMENT_TIME = 1;

    /******************PLAY_AD_AFTER_SONGS****************************
     * This variable is used when ADVERTISEMENT_TYPE is of type 2
     * that is when advertisement is to be played after a number
     * songs have played.
     *****************************************************************/
    public static int PLAY_AD_AFTER_SONGS = -1;

    /****************PLAY_AD_AFTER_SONGS_COUNTER************************
     * This variable is used to keep track the number of songs that have been
     * played. After value of this variable reaches PLAY_AD_AFTER_SONGS we play
     * an ad and this value is reset to 0.
     *
     *******************************************************************/
    public static long PLAY_AD_AFTER_SONGS_COUNTER = 0;

    /****************PLAYLIST_TIMER_CHECK_TIMER************************
     * This is the value in seconds in which the handler runs every mentioned
     * seconds.
     *******************************************************************/
    public static long PLAYLIST_TIMER_CHECK_TIMER = 1;

    /****************ADVERTISEMENT_PLAY_TIME************************
     * When advertisement is of type isTime. This variable will keep
     * track of the time on which the next song advertisement is to be
     * played.
     ******************************************************************/
    public static String ADVERTISEMENT_PLAY_TIME = "";

    ArrayList<Advertisements> advertisements;

    private static int currentlyPlayingAdAtIndex = 0;

    private boolean isPaused = false;

    public interface PlaylistStatusListener {
        void onPlaylistStatusChanged(int status);
        void shouldUpdateTimeOnServer();
        void playAdvertisement(ArrayList<Advertisements> arrAdv,String type);
        void checkForPendingDownloads();
        void refreshPlayerControls();
    }

    public void setContext(Context context){

        this.context = context;
        setAdvertisements();
       // setPopUpRequirement();
    }

    public void setPlaylistStatusListener(PlaylistStatusListener playlistStatusListener) {
        this.playlistStatusListener = playlistStatusListener;
    }

   /* public String datatableplaylistid()
    {
        String datatableplaylistid="";
        Date date = new Date();
        long currenttimeinmilli=date.getTime();

        int datatablecount=HomeActivity.playlistdatatable.getChildCount();
        for(int i=0;i<=datatablecount;i++)
        {
            TableRow mRow = (TableRow) HomeActivity.playlistdatatable.getChildAt(i);
            long stime=Long.parseLong(mRow.getChildAt(1).toString());
            long etime=Long.parseLong(mRow.getChildAt(2).toString());
            if((stime<=currenttimeinmilli) && (etime>=currenttimeinmilli))
            {
                datatableplaylistid=mRow.getChildAt(0).toString();
                break;
            }

        }
        return datatableplaylistid;
    }*/


    private String changeDateFormat(String starttime) {
        DateFormat readFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss aa",Locale.US);
        DateFormat writeFormat = new SimpleDateFormat( "EEE MMM dd HH:mm:ss z yyyy",Locale.US);
        Date date = null;
        try {
            date = readFormat.parse(starttime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (date != null) {
            formattedDate = writeFormat.format(date);
        }
        return formattedDate;
    }

    public long getTimeInMilliSec(String starttime) {


        SimpleDateFormat sdf1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy",Locale.US);
        try {
            Date mDate = sdf1.parse(starttime);
            milliSec=mDate.getTime();
            Calendar calendar= Calendar.getInstance();
            calendar.setTime(mDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return milliSec;
    }


    Runnable runnable = new Runnable() {
        @Override
        public void run() {

            if (PlaylistWatcher.this.playlistStatusListener != null){
                PlaylistWatcher.this.playlistStatusListener.refreshPlayerControls();
            }

            if (ADVERTISEMENT_TYPE_isMin.equals("1")){ //isMinuteAdvertisement

                ADVERTISEMENT_TIME_COUNTER = ADVERTISEMENT_TIME_COUNTER + (500 * PLAYLIST_TIMER_CHECK_TIMER);
                if((arrtotaltime!=null) && (arrtotaltime.size()>0)) {
                    // Float t=("0.5")*60000;
                    ADVERTISEMENT_TIME = Integer.valueOf(arrtotaltime.get(0)) * 1000;
                    if(ADVERTISEMENT_TIME_Store==1)
                    {
                        ADVERTISEMENT_TIME_Store = ADVERTISEMENT_TIME;

                    }
                    if(ADVERTISEMENT_TIME_Store!=ADVERTISEMENT_TIME) {
                        ADVERTISEMENT_TIME_Store = ADVERTISEMENT_TIME;
                        ADVERTISEMENT_TIME_COUNTER=0;
                    }

                    if (ADVERTISEMENT_TIME_COUNTER == ADVERTISEMENT_TIME) {
                        if (PlaylistWatcher.this.playlistStatusListener != null) {
                            if((arrAdvertisementsTimeFilter!=null) && (arrAdvertisementsTimeFilter.size()>0)) {
                                HomeActivity.arrAdvertisementsMinute.clear();
                                HomeActivity.arrAdvertisementsMinute.addAll(arrAdvertisementsTimeFilter);
                                PlaylistWatcher.this.playlistStatusListener.playAdvertisement(HomeActivity.arrAdvertisementsMinute, ADVERTISEMENT_TYPE_isMin);
                            }
                        }
                    }
                }
                else {
                    ADVERTISEMENT_TIME_Store=1;
                    ADVERTISEMENT_TIME_COUNTER=0;
                }

            }

            if (ADVERTISEMENT_TYPE_isTime.equals("3")){

                String timeStamp = new SimpleDateFormat("hh:mm aa", Locale.US).format(Calendar.getInstance().getTime());
                  // Toast.makeText(PlaylistWatcher.this.context, "Play ad for isTime", Toast.LENGTH_SHORT).show();

             //   ADVERTISEMENT_PLAY_TIME="01:40 PM";
                if (timeStamp.equals(ADVERTISEMENT_PLAY_TIME)){
                      //Toast.makeText(PlaylistWatcher.this.context, "Play ad for isTime", Toast.LENGTH_SHORT).show();
                    if(p==0) {
                        if (PlaylistWatcher.this.playlistStatusListener != null) {
                            p=1;
                            PlaylistWatcher.this.playlistStatusListener.playAdvertisement(HomeActivity.arrAdvertisementsTime, ADVERTISEMENT_TYPE_isTime);


                        }
                    }

                    currentlyPlayingAdAtIndex++;

                    if (advertisements.size() - 1 >= currentlyPlayingAdAtIndex){

                        String playAdAtTime = advertisements.get(currentlyPlayingAdAtIndex).getsTime();

                        if (playAdAtTime != null){
                            ADVERTISEMENT_PLAY_TIME = playAdAtTime;
                            p=0;
                        }
                    } else {
                        ADVERTISEMENT_PLAY_TIME = "";
                        p=0;
                    }
                }
            }

            UPDATE_PLAYER_STATUS_TIMER = UPDATE_PLAYER_STATUS_TIMER + (1000 * PLAYLIST_TIMER_CHECK_TIMER);


//            Log.e("Playlist Watcher", "Timer Value " + UPDATE_PLAYER_STATUS_TIMER);

            if (UPDATE_PLAYER_STATUS_TIMER == UPDATE_PLAYER_STATUS_TIME){

                if (PlaylistWatcher.this.playlistStatusListener != null){
                    PlaylistWatcher.this.playlistStatusListener.shouldUpdateTimeOnServer();
                }

                UPDATE_PLAYER_STATUS_TIMER = 0;
            }

            CHECK_PENDING_DOWNLOADS_TIMER = CHECK_PENDING_DOWNLOADS_TIMER + 1000 * PLAYLIST_TIMER_CHECK_TIMER;

            if (CHECK_PENDING_DOWNLOADS_TIME == CHECK_PENDING_DOWNLOADS_TIMER){

                if (PlaylistWatcher.this.playlistStatusListener != null){

                    PlaylistWatcher.this.playlistStatusListener.checkForPendingDownloads();
                }

                CHECK_PENDING_DOWNLOADS_TIMER = 0;
            }


            if (currentDayOfTheWeek == -1){
                currentDayOfTheWeek = Utilities.getCurrentDayNumber();
            }

            if (currentDayOfTheWeek != Utilities.getCurrentDayNumber()){
               currentDayOfTheWeek = Utilities.getCurrentDayNumber();
                ((HomeActivity)context).getUpadteswithoutRestart(0);

            }

            String currentDate=Utilities.currentDate();
            int playlistStatus = -1;
            boolean shouldPlaylistChange = false;
            if((!cDate.equals(currentDate) || (HomeActivity.ctrupdate==1)))
            {

                if(cDate.equals("")) {

                    cDate = currentDate;
                    playlistsAll = new PlaylistManager(context, null).getAllPlaylistInPlayingOrder();
                    cTime = "";
                }
                else {

                    if (HomeActivity.ctrupdate == 1) {
                        cDate = currentDate;
                        HomeActivity.ctrupdate = 0;
                        playlistsAll = new PlaylistManager(context, null).getAllPlaylistInPlayingOrder();
                        cTime = "";
                    }
                }


            }

            String currentTime=Utilities.currentTimeHHMM();
            Calendar calander;
            SimpleDateFormat simpleDateFormat;
            String time;


            if(!cTime.equals(currentTime)) {
                countermin++;
                cTime = currentTime;
                if ((playlistsAll == null) || (playlistsAll.size() == 0)) {
                    cDate = "";
                   // Utilities.showToast(context,"whole day Free");

                } else if ((playlistsAll != null) && (playlistsAll.size() > 0)) {

                    calander = Calendar.getInstance();
                    simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss aaa", Locale.US);
                    time = simpleDateFormat.format(calander.getTime());

                    String array[]=time.split("\\s+");
                    time=array[1]+" "+array[2];

                    String plcurrentTime=changeDateFormat("1/1/1900"+" "+ time);

                    // Here change the Date & Time in Milliseconds

                    long currenttimeinmilli =getTimeInMilliSec(plcurrentTime);
                    for (int ipl = 0; ipl < playlistsAll.size(); ipl++) {
                        long h=playlistsAll.get(ipl).getStart_Time_In_Milli();
                        long k=playlistsAll.get(ipl).getStart_Time_In_Milli();

                        if ((currenttimeinmilli >= playlistsAll.get(ipl).getStart_Time_In_Milli() ) && (currenttimeinmilli < playlistsAll.get(ipl).getEnd_Time_In_Milli() )) {
                            playlistId = playlistsAll.get(ipl).getsplPlaylist_Id();
                            break;
                        }
                        else
                        {
                            //Utilities.showToast(context,"playlist ="+playlistId);
                            playlistId="";
                        }
                    }

                }
                if(cTime.equals("02:30 am") || cTime.equals("02:30 AM"))
                {
                    Intent intent = new Intent(context, Splash_Activity.class);
                intent.putExtra("Restart", true);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_NEW_TASK);

                PendingIntent pendingIntent = PendingIntent.getActivity(AlenkaMedia.getInstance().getBaseContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT|PendingIntent.FLAG_IMMUTABLE);
                AlarmManager mgr = (AlarmManager) AlenkaMedia.getInstance().getBaseContext().getSystemService(Context.ALARM_SERVICE);
                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 30000, pendingIntent);
                System.exit(2);
                }

                if(cTime.equals("04:00 am") || cTime.equals("04:00 AM"))
                {
                    ((HomeActivity) context).rebootbox();

                }


                if(cTime.equals(popuniv))
                {
                  //  ((HomeActivity) context).checkDifferenceinDays(popdismissuniv,daysuniv);

                }

                if (playlistId.equals("")) {
                    playlistStatus = NO_PLAYLIST;
                    IsPlaylistFind=0;
                    currentPlaylistID = "";
                    AlenkaMedia.playlistStatus = NO_PLAYLIST;
                    if (playlistStatusListener != null) {
                        playlistStatusListener.onPlaylistStatusChanged(NO_PLAYLIST);
                    }

                } else {

                    if (currentPlaylistID.equals("")) {
                        currentPlaylistID = playlistId;
                    }

                    if (!currentPlaylistID.equals(playlistId)) {

                        shouldPlaylistChange = true;
                        currentPlaylistID = playlistId;

                        if (playlistStatusListener != null) {
                          playlistStatusListener.onPlaylistStatusChanged(PLAYLIST_CHANGE);
                        }
                        playlistStatus = PLAYLIST_PRESENT;
                    }

                    if (IsPlaylistFind==0){
                        IsPlaylistFind=1;
                        playlistStatus = PLAYLIST_PRESENT;
                        AlenkaMedia.playlistStatus = playlistStatus;
                        if (playlistStatusListener != null) {
                           playlistStatusListener.onPlaylistStatusChanged(playlistStatus);

                        }
                    }


                    // Old work with love
                    // playlists = new PlaylistManager(context, null).getPlaylistForCurrentTimeOnly();
                }
                if (AlenkaMedia.playlistStatus == -12){
                    AlenkaMedia.playlistStatus = playlistStatus;
                }

                if(countermin==60) {
                    getFiltertimeadv();
                    countermin=0;
                }
                //getFilterSongadv();
            }



            if (!isPaused)
                mHandler.postDelayed(runnable,1000 * PLAYLIST_TIMER_CHECK_TIMER);
        }
    };

    public void setWatcher()
    {
        mHandlerThread = new HandlerThread("HandlerThread");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
        mHandler.postDelayed(runnable,25000 * PLAYLIST_TIMER_CHECK_TIMER);
    }

    public void pausePlaylistWatcher(){
        isPaused = true;
    }

    public void resumePlaylistWatcher(){
        isPaused = false;
        mHandler.postDelayed(runnable,1000 * PLAYLIST_TIMER_CHECK_TIMER);
    }

    public void cancelWatcher()
    {

        if (mHandler != null)
            mHandler.removeCallbacks(runnable);
    }

    public void setPopUpRequirement()
    {
        String activate=SharedPreferenceUtil.getStringPreference(context,
                AlenkaMediaPreferences.Popupactivation);
        if(activate.equals("1")) {
            String popuptime = SharedPreferenceUtil.getStringPreference(context,
                    AlenkaMediaPreferences.ShowPopuptime);
            String popdismissstr = SharedPreferenceUtil.getStringPreference(context,
                    AlenkaMediaPreferences.Popupdismiss);
            String checkdaysstr = SharedPreferenceUtil.getStringPreference(context,
                    AlenkaMediaPreferences.DaysPlayerExpire);
            long popdismiss = Long.parseLong(popdismissstr);
            int chkdays = Integer.parseInt(checkdaysstr);
            popdismissuniv = popdismiss;
            popuniv = popuptime;
            daysuniv = chkdays;
        }
        else
        {
            popuniv="";
        }

    }

    public void getFiltertimeadv()
    {
        arrAdvertisementsTimeFilter.clear();
        arrtotaltime.clear();
        if(HomeActivity.arrAdvertisementsMinute.size()>0) {
            for (int i = 0; i < HomeActivity.arrAdvertisementsMinute.size(); i++) {
                long starttime = HomeActivity.arrAdvertisementsMinute.get(i).getbtStart_Adv_Time_Millis();
                long endtime = HomeActivity.arrAdvertisementsMinute.get(i).getEnd_Adv_Time_Millis();
                if ((hm.syscurrenttime() >= starttime) && (hm.syscurrenttime() < endtime)) {
                    arrAdvertisementsTimeFilter.add(HomeActivity.arrAdvertisementsMinute.get(i));
                    arrtotaltime.add(HomeActivity.arrAdvertisementsMinute.get(i).getTotalMinutes());
                }
            }
            if (arrtotaltime.size() > 0) {
                HashSet<String> duptotsong = new HashSet<>(arrtotaltime);
                arrtotaltime.clear();
                arrtotaltime.addAll(duptotsong);
            }
        }
    }

    public ArrayList<Advertisements> getFilterSongadv()
    {
        arrAdvertisementssongFilter.clear();
        arrtotalsong.clear();
        if(HomeActivity.arrAdvertisementsSong.size()>0) {
            for (int i = 0; i < HomeActivity.arrAdvertisementsSong.size(); i++) {
                long starttime = HomeActivity.arrAdvertisementsSong.get(i).getbtStart_Adv_Time_Millis();
                long endtime = HomeActivity.arrAdvertisementsSong.get(i).getEnd_Adv_Time_Millis();
                if ((hm.syscurrenttime() >= starttime) && (hm.syscurrenttime() < endtime)) {
                    arrAdvertisementssongFilter.add(HomeActivity.arrAdvertisementsSong.get(i));
                    arrtotalsong.add(HomeActivity.arrAdvertisementsSong.get(i).getTotalSongs());
                }
            }
            if(arrAdvertisementssongFilter.size()>0)
            {
                HomeActivity.arrAdvertisementsSong.clear();
                HomeActivity.arrAdvertisementsSong.addAll(arrAdvertisementssongFilter);
            }
            if (arrtotalsong.size() > 0) {
                HashSet<String> duptotsong = new HashSet<>(arrtotalsong);
                arrtotalsong.clear();
                arrtotalsong.addAll(duptotsong);
                PLAY_AD_AFTER_SONGS = Integer.valueOf(arrtotalsong.get(0));
                return arrAdvertisementssongFilter;
            }
            else {
                PLAY_AD_AFTER_SONGS=-1;
                PLAY_AD_AFTER_SONGS_COUNTER=0;
            }

        }
        else {
            PLAY_AD_AFTER_SONGS=-1;
            PLAY_AD_AFTER_SONGS_COUNTER=0;
        }
        return null;
    }

    public void setAdvertisements(){

        String advertisementTypeMinute =  SharedPreferenceUtil.getStringPreference(this.context,
                AlenkaMediaPreferences.is_Minute_Adv);

        String advertisementTypeSong =  SharedPreferenceUtil.getStringPreference(this.context,
                AlenkaMediaPreferences.is_song_Adv);

        String advertisementTypeTime =  SharedPreferenceUtil.getStringPreference(this.context,
                AlenkaMediaPreferences.is_Time_Adv);


        if (advertisementTypeMinute != null && !advertisementTypeMinute.equals("")){
            ADVERTISEMENT_TYPE_isMin = "1";
           getFiltertimeadv();
        }

        if(advertisementTypeSong != null && !advertisementTypeSong.equals("")){
            ADVERTISEMENT_TYPE_isSong = "2";
           // getFilterSongadv();

            String playSongsAfterAdvertisement =  SharedPreferenceUtil.getStringPreference(this.context,
                    AlenkaMediaPreferences.total_Songs);

            PLAY_AD_AFTER_SONGS = Integer.valueOf(playSongsAfterAdvertisement);

        }

        if(advertisementTypeTime != null && !advertisementTypeTime.equals("")){
            ADVERTISEMENT_TYPE_isTime = "3";
             ADVERTISEMENT_PLAY_TIME="";
             if(advertisements!=null) {
                 advertisements.clear();
             }

            if (ADVERTISEMENT_PLAY_TIME.equals("")){

                if (advertisements == null)
                    advertisements = new AdvertisementsManager(PlaylistWatcher.this.context).
                            getAdvertisementsForComingTime();

                if ((advertisements!=null) && (advertisements.size() > 0))
                {

                    String playAdAtTime = advertisements.get(currentlyPlayingAdAtIndex).getsTime();

                    if (playAdAtTime != null){
                        ADVERTISEMENT_PLAY_TIME = playAdAtTime;
                    }
                }

            }
        }

    }
}
