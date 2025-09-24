package com.ApplicationAddOnsLband.activities;
import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.StatFs;

import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;
import androidx.appcompat.app.AlertDialog;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ApplicationAddOnsLband.R;
import com.ApplicationAddOnsLband.adapters.PlaylistAdapter;
import com.ApplicationAddOnsLband.adapters.SongAdapter;
import com.ApplicationAddOnsLband.alarm_manager.MyService;
import com.ApplicationAddOnsLband.alarm_manager.MyWorker;
import com.ApplicationAddOnsLband.alarm_manager.PlaylistWatcher;
import com.ApplicationAddOnsLband.api_manager.DownloadService;
import com.ApplicationAddOnsLband.api_manager.OkHttpUtil;
import com.ApplicationAddOnsLband.application.AlenkaMedia;
import com.ApplicationAddOnsLband.custom_views.Lvideoads;
import com.ApplicationAddOnsLband.custom_views.MyClaudVideoView;
import com.ApplicationAddOnsLband.database.AdvertisementDataSource;
import com.ApplicationAddOnsLband.database.MySQLiteHelper;
import com.ApplicationAddOnsLband.interfaces.DownloadListener;
import com.ApplicationAddOnsLband.interfaces.PlaylistLoaderListener;
import com.ApplicationAddOnsLband.mediamanager.AdvertisementsManager;
import com.ApplicationAddOnsLband.mediamanager.PlayerStatusManager;
import com.ApplicationAddOnsLband.mediamanager.PlaylistManager;
import com.ApplicationAddOnsLband.models.Advertisements;
import com.ApplicationAddOnsLband.models.PlayerStatus;
import com.ApplicationAddOnsLband.models.Playlist;
import com.ApplicationAddOnsLband.models.Songs;
import com.ApplicationAddOnsLband.utils.AlenkaMediaPreferences;
import com.ApplicationAddOnsLband.utils.ConnectivityReceiver;
import com.ApplicationAddOnsLband.utils.Constants;
import com.ApplicationAddOnsLband.utils.FileUtil;
import com.ApplicationAddOnsLband.utils.MyNotificationManager;
import com.ApplicationAddOnsLband.utils.SharedPreferenceUtil;
import com.ApplicationAddOnsLband.utils.SignalRClient;
import com.ApplicationAddOnsLband.utils.StorageUtils;
import com.ApplicationAddOnsLband.utils.UpdateWithoutRestart;
import com.ApplicationAddOnsLband.utils.Utilities;
import com.github.rongi.rotate_layout.layout.RotateLayout;
import com.hisense.hotel.HisenseManager;
import com.hisense.hotel.HotelSystemManager;
import com.hisense.hotel.IServicesReadyListener;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import eu.chainfire.libsuperuser.Shell;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by love on 30/5/17.
 */
public class HomeActivity extends Activity implements DownloadListener, OkHttpUtil.OkHttpResponse, PlaylistWatcher.PlaylistStatusListener {

    public static final String TAG = "HomeActivity";

    private final int VIDEO_VIEW_TAG = 1;
    public int y = 1;

    String gblfadevol="";
    public static int ctrupdate=0;
   // boolean playads=true;
    String titleidmatch="";
    String titleidmatchads="";

    private AdvertisementDataSource advertisementDataSource;
    String addnotid="";
    float perwidth;
    float perheight;
    float calwidth;
    float calheight;
    String gbltypy="";
    TextView txtmarquee;
    String univtoken ="";
    private static final int VIDEO_AD_VIEW_TAG = 2;

    TextView txtTokenId,txtInfo,txtwaiting;
    public File gblStorage;
    WebView webView;
    private DownloadService mDownloadService;
    private ProgressBar hzProgressBar;
    private boolean mIsBound;
    private static long currentTimeInMilli,milliSec;
    Calendar calander;
    SimpleDateFormat simpleDateFormat;
    String exttype="";
    String p="";
    private ProgressBar circularProgressBar;
    private int currentApiVersion = android.os.Build.VERSION.SDK_INT;

    private ListView lvPlaylist;
    private String gblSongid = "";
    String univKeyCode="";

    float vol,vol1;
    public int keyct=0;
    int t=0;
    RelativeLayout layout;
    LinearLayout portraitmp3layout,blackLayout;
    RotateLayout mp3layout;
    private int ctrplaylistchg=0;
    RotateLayout imglayout;
    RotateLayout videolayout,rotateweb;

    private ListView lvSongs;

    private PlaylistAdapter playlistAdapter;
    private RotateLayout layout4;
    private SongAdapter songAdapter;
    public static ImageView Imgmarker;
    private ArrayList<Playlist> arrPlaylists = new ArrayList<Playlist>();
    private ArrayList<Playlist> playlists = new ArrayList<>();
    List<Playlist> noRepeat = new ArrayList<Playlist>();
    HotelSystemManager hsm;
    private ArrayList<Playlist> arrPlaylistsstatus = new ArrayList<Playlist>();

    private ArrayList<Playlist> arrPlaylistsweb = new ArrayList<Playlist>();
    private ArrayList<Songs> arrSongsDownloadAll = new ArrayList<Songs>();

    private ArrayList<Songs> arrSongs = new ArrayList<Songs>();
    private ArrayList<Songs> arrSongsweb = new ArrayList<Songs>();
    private ArrayList<Advertisements> arrAdvsswebInstant = new ArrayList<>();
    private ArrayList<Advertisements> arrAdvweb = new ArrayList<Advertisements>();
    ArrayList<Songs> songsArrayList=new ArrayList<Songs>();
    ArrayList<Songs> songsArrayListMerge;

    ArrayList<Songs> songsArrayListStatus;

    // private ArrayList<Advertisements> arrAdvertisements = new ArrayList<Advertisements>();
    public static ArrayList<Advertisements> arrAdvertisementsSong = new ArrayList<Advertisements>();
    public static ArrayList<Advertisements> arrAdvertisementsTime = new ArrayList<Advertisements>();
    public static ArrayList<Advertisements> arrAdvertisementsMinute = new ArrayList<Advertisements>();

    private int currentlyPlayingSongAtIndex = 0;

    public static int currentlyPlayingAdAtIndex = -1;
    public static int currentlyPlayingAdAtIndexMin = -1;
    public static int currentlyPlayingAdAtIndexSong = -1;
    public static int currentlyPlayingAdAtIndexTime = -1;
    private MyClaudVideoView mPreview;
    private Lvideoads mPreviewads;
//mkmk
//    private //VideoView//123videoViewAds;
//ko
    private PlaylistWatcher alarm;


//    Handler checkForPlaylistStatus   = new Handler();

    int delay = 1000; //milliseconds

    private int currentPlaylistStatus = -2;

    private boolean doubleBackToExitPressedOnce;
    public MySQLiteHelper songsrc;

    IntentFilter intentFilter = new IntentFilter(Constants.ALARM_ACTION);
    IntentFilter intentConnectivity = new IntentFilter(Constants.CONNECTIVITY_CHANGED);

   // BroadcastReceiver broadcastReceiver;
    BroadcastReceiver networkChangeReceiver;
//558
    private int playNextSongIex = -1;
    private int playNextAdvIndex = -1;
    public String titledownload = "";
    public int temploop =1;
    public int temploopads=1;
    public String artistdownload = "";
    public boolean IsInstantPlaying=false;
    private int playlistcounter = 0;

    private String sdCardLocation = "";
    public static HomeActivity hm;
    private ArrayList<String> arrVideoFiles = new ArrayList<>();

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /********countOfTotalSongsToBeDownloaded********
     This variable is used for updating the count of
     songs which are downloaded every 15 minutes. It stores
     the total number of songs which are to be downloaded.
     /********countOfTotalSongsToBeDownloaded********/

    private int countOfTotalSongsToBeDownloaded = 0;

    private int REQUEST_CODE_STORAGE_FOLDER_SELECTOR = 43;

    /*********************Broadcast Receiver Starts**************************/


    /*********************Broadcast Receiver Ends**************************/

    /***********************Download Videos Service Methods Start****************************************/

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mDownloadService = ((DownloadService.LocalBinder) service).getService();
            mDownloadService.registerListener(HomeActivity.this);
        }

        public void onServiceDisconnected(ComponentName className) {
            mDownloadService = null;
        }
    };
    private String targetFileName;
    private String sourceFileLocation;
    private int imgcounter = 0;
    private int EXPORT_VIDEO_INDEX = 0;
    private TextView txtFileWriter;

    private ImageView Imgicon;
    private ImageView waitImgicon;
    private ImageView myImage;
    // private ImageView Imgicon1;
    private TextView txtSong;
    private TextView txtArtist;
    private TextView txttimer;
    private CountDownTimer imgCountdowntimer,initiateCountTimer,imgCountdowntimer1, imgCountdowntimer2,KeyCountdownTimer,UrlCountdowntimerAd,Squeezectdtimer,Squeezectdtimerend;
    private CountDownTimer mCountDownTimer;
    boolean isCountDownTimerRunning = false;
    private long downloadId;

  //  AlertDialog.Builder dialogBuilder = null;

   // AlertDialog dialog;

    void doBindService() {
        bindService(new Intent(HomeActivity.this,
                DownloadService.class), mConnection, 0);
        mIsBound = true;
    }

    void doUnbindService() {
        if (mIsBound) {
            if (mDownloadService != null) {
                mDownloadService.unregisterListener(this);
            }
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    @Override
    public void onUpdate(long value) {
        try {
            /*When a song is being downloaded its progress is shown here.*/
            Log.e("Download Status", "" + value);
            hzProgressBar.setVisibility(View.GONE);
            txtwaiting.setVisibility(View.GONE);
            waitImgicon.setVisibility(View.GONE);
            circularProgressBar.setProgress((int) value);
        }
        catch(Exception ex)
        {
            try {
                String classname = this.getClass().getSimpleName();
                String method = new Exception().getStackTrace()[0].getMethodName();
                caughtException(classname, method, String.valueOf(value), univtoken, "5", ex);
            }catch(Exception e)
            {
                e.getCause();
            }
        }
    }




    public void playfoundsong(boolean shouldPlay, Songs song) {
        try {
            if (shouldPlay) {
                // Utilities.showToast(HomeActivity.this,"Found Song");
                getPlaylistsForCurrentTime();
                return;
            }


            if (arrSongs.size() > 0) {

                String downloadedSongPlaylistId = song.getSpl_PlaylistId();
                String currentPlayingPlaylistId = arrSongs.get(0).getSpl_PlaylistId();

                if (downloadedSongPlaylistId.equals(arrPlaylists.get(0).getsplPlaylist_Id())) {
                    // Toast.makeText(HomeActivity.this, "Downloaded and added song" + song.getTitle(), Toast.LENGTH_SHORT).show();
                    arrSongs.add(song);
                }
            }

            if (!(DownloadService.songsToBeDownloaded.size() - 1 > DownloadService.downloadingFileAtIndex)) {

                if (arrSongs.size() > 0) arrSongs.clear();

                String schtype = SharedPreferenceUtil.getStringPreference(HomeActivity.this, AlenkaMediaPreferences.SchType);
                if (schtype.equals("Normal")) {

                    songsArrayList = new PlaylistManager(HomeActivity.this, null).getSongsForPlaylist(arrPlaylists.get(0).getsplPlaylist_Id());
                } else {
                    songsArrayList = new PlaylistManager(HomeActivity.this, null).getSongsForPlaylistRandom(arrPlaylists.get(0).getsplPlaylist_Id());

                }

                if (songsArrayList != null && songsArrayList.size() > 0) {
                    arrSongs.addAll(songsArrayList);
                }

                if (arrSongs.size() > 0) {

                    if (arrPlaylists.get(0).getIsSeparatinActive() == 1) {
                        sort(arrSongs);

                    }

                }
            }
        }
        catch(Exception ex)
        {
            try {
                String classname = this.getClass().getSimpleName();
                String method = new Exception().getStackTrace()[0].getMethodName();
                caughtException(classname, method, String.valueOf(song.getTitle_Id()), univtoken, "5", ex);
            }catch(Exception e)
            {
                e.getCause();
            }
        }
    }


    @Override
    public void downloadCompleted(boolean shouldPlay, Songs song) {
        try {

            /*A case where current time has no playlist but after one hour or so there is playlist
             * and the songs are being downloaded. After the first song finishes download shouldPlay will be true
             * and also the videoView will not be playing. Prevent video view from playing in this case.*/

            if (shouldPlay) {

                /*If video view is not playing then only we start the player*/
                // Utilities.showToast(HomeActivity.this,"Activity Restart");
                //Utilities.showToast(HomeActivity.this,"onDownload");

                if (y != 0) {
                    if (mPreview.isPlaying()) {
                        mPreview.stopPlayback();
                        mPreview.reset();
                        mPreview.clearSurfaceView();
                    }
                }
                if (imgCountdowntimer != null) {
                    imgCountdowntimer.cancel();
                }
                if (imgCountdowntimer1 != null) {
                    imgCountdowntimer1.cancel();
                }
                if (imgCountdowntimer2 != null) {
                    imgCountdowntimer2.cancel();
                }
                playlistcounter = 0;
                PlaylistWatcher.PLAY_AD_AFTER_SONGS_COUNTER = 0;
                if (y == 0) {
                    myImage.setVisibility(View.INVISIBLE);
                    myImage.setImageDrawable(null);
                }
                ctrplaylistchg = 1;
                // Utilities.showToast(HomeActivity.this,"Playlistchg");
                txtwaiting.setVisibility(View.GONE);
                hzProgressBar.setVisibility(View.GONE);
                webView.setVisibility(View.INVISIBLE);
                updateDownloadedSongsStatusOnServer();
                PlayerStatusManager playerStatusManager = new PlayerStatusManager(HomeActivity.this);
                playerStatusManager.updateDownloadedSongsPlaylistWise();
                getPlaylistsForCurrentTime();
                arrSongs.size();
                return;

            }
            updateDownloadedSongsStatusOnServer();
            PlayerStatusManager playerStatusManager = new PlayerStatusManager(HomeActivity.this);
            playerStatusManager.updateDownloadedSongsPlaylistWise();
            if (arrSongs.size() > 0) {

                String downloadedSongPlaylistId = song.getSpl_PlaylistId();
                String currentPlayingPlaylistId = arrSongs.get(0).getSpl_PlaylistId();

                String schtype = SharedPreferenceUtil.getStringPreference(HomeActivity.this, AlenkaMediaPreferences.SchType);
                if (schtype.equals("Normal")) {

                    if (downloadedSongPlaylistId.equals(arrPlaylists.get(0).getsplPlaylist_Id())) {
                        // Toast.makeText(HomeActivity.this, "Downloaded and added song" + song.getTitle(), Toast.LENGTH_SHORT).show();
                        arrSongs.add(song);
                    }

                }
                else
                {
                    for(int i=0;i<arrPlaylists.size();i++)
                    {
                        if (downloadedSongPlaylistId.equals(arrPlaylists.get(i).getsplPlaylist_Id())) {

                            arrSongs.add(song);
                        }
                    }
                }

                if (!(DownloadService.songsToBeDownloaded.size() - 1 > DownloadService.downloadingFileAtIndex)) {

                    if (arrSongs.size() > 0) arrSongs.clear();

                    String schtype1 = SharedPreferenceUtil.getStringPreference(HomeActivity.this, AlenkaMediaPreferences.SchType);
                    if (schtype1.equals("Normal")) {

                        songsArrayList = new PlaylistManager(HomeActivity.this, null).getSongsForPlaylist(arrPlaylists.get(0).getsplPlaylist_Id());
                    }
                    else
                        {
                            playlists.clear();
                            noRepeat.clear();
                            playlists.addAll(arrPlaylists);
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
                            for(int i=0; i<noRepeat.size(); i++) {
                                //songsArrayListMerge.clear();
                                songsArrayList.clear();
                                songsArrayListMerge = new PlaylistManager(HomeActivity.this, null).getSongsForPlaylistRandom(noRepeat.get(i).getsplPlaylist_Id());
                                songsArrayList.addAll(songsArrayListMerge);
                            }
                        //songsArrayList = new PlaylistManager(HomeActivity.this, null).getSongsForPlaylistRandom(arrPlaylists.get(0).getsplPlaylist_Id());

                    }


                    if (songsArrayList != null && songsArrayList.size() > 0) {

                        arrSongs.addAll(songsArrayList);
                    }

                    if (arrSongs.size() > 0) {

                        if (arrPlaylists.get(0).getIsSeparatinActive() == 1) {
                            sort(arrSongs);

                        }

                        songAdapter = new SongAdapter(HomeActivity.this, arrSongs);
                        lvSongs.setAdapter(songAdapter);
                        lvSongs.deferNotifyDataSetChanged();
                    }

                }

            }
        }catch (Exception ex)
        {
            try {
                String classname = this.getClass().getSimpleName();
                String method = new Exception().getStackTrace()[0].getMethodName();
                caughtException(classname, method, String.valueOf(song.getTitle_Id()), univtoken, "5", ex);
            }catch(Exception e)
            {
                e.getCause();
            }
        }

    }

    @Override
    public void advertisementDownloaded(Advertisements advertisements) {
        try {

            if (advertisements != null) {
                alarm.setAdvertisements();
                PlaylistWatcher.PLAY_AD_AFTER_SONGS_COUNTER = 0;
                if (advertisements.getIsSong().equals("1")) {
                    arrAdvertisementsSong.add(advertisements);
                }
                if (advertisements.getIsTime().equals("1")) {
                    arrAdvertisementsTime.add(advertisements);
                }
                if (advertisements.getIsMinute().equals("1")) {
                    arrAdvertisementsMinute.add(advertisements);
                }
                updateDownloadedSongsStatusOnServer();
                PlayerStatusManager playerStatusManager = new PlayerStatusManager(HomeActivity.this);
                playerStatusManager.updateDownloadedSongsPlaylistWise();
            }
        }
        catch(Exception ex)
        {
            try {
                String classname = this.getClass().getSimpleName();
                String method = new Exception().getStackTrace()[0].getMethodName();
                caughtException(classname, method, String.valueOf(advertisements.getAdvtID()), univtoken, "5", ex);
            }catch(Exception e)
            {
                e.getCause();
            }
        }
    }

    @Override
    public void refreshPlayerControls() {

        Handler handler = new Handler(HomeActivity.this.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {


            }
        });
    }


    @Override
    public void startedCopyingSongs(final int currentSong, final int totalSongs, final boolean isFinished) {
        try {
            Handler handler = new Handler(HomeActivity.this.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    //  freeMemory();
                    if (isFinished) {
                        txtFileWriter.setText("Copy successful");
                        alarm = new PlaylistWatcher();
                        alarm.setContext(HomeActivity.this);
                        alarm.setPlaylistStatusListener(HomeActivity.this);
                        alarm.setWatcher();
                        //  Utilities.showToast(HomeActivity.this,"copying");

                        getPlaylistsForCurrentTime();
                        getAdvertisements();

                    /*PlayerStatusManager playerStatusManager = new PlayerStatusManager(HomeActivity.this);
                    playerStatusManager.songsDownloaded = "" + songsThatHaveBeenDownloaded;
                    playerStatusManager.updateDownloadedSongsCountOnServer();*/

                        return;
                    }
                    txtFileWriter.setText("Copying song " + currentSong + " of " + totalSongs);
                }
            });
        }catch (Exception ex)
        {
            try {
                String classname = this.getClass().getSimpleName();
                String method = new Exception().getStackTrace()[0].getMethodName();
                caughtException(classname, method, String.valueOf(currentSong), univtoken, "5", ex);
            }catch(Exception e)
            {
                e.getCause();
            }
        }


        /*runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (isFinished){
                    txtFileWriter.setText("Copy successful");
                    return;
                }
                txtFileWriter.setText("Copying song " + currentSong + " of " + totalSongs);
            }
        });*/
    }

    @Override
    public void finishedDownloadingSongs(int totalSongs) {
        try {

            if (totalSongs > 0) {

                PlayerStatusManager playerStatusManager = new PlayerStatusManager(HomeActivity.this);
                playerStatusManager.songsDownloaded = "" + totalSongs;
//            playerStatusManager.updateDownloadedSongsCountOnServer();

            } else {

                //   restartPlayer();

           /* if (AlenkaMedia.getInstance().isUpdateInProgress) {

               Log.e(TAG, "New songs downloaded now restarting");

                PlaylistManager playlistManager = new PlaylistManager(HomeActivity.this, playlistLoaderListener);
                playlistManager.publishTokenForUpdatedData();

            }*/
            }
        }catch(Exception ex)
        {

            try {
                String classname = this.getClass().getSimpleName();
                String method = new Exception().getStackTrace()[0].getMethodName();
                caughtException(classname, method,String.valueOf(totalSongs), univtoken, "5", ex);
            }catch(Exception e)
            {
                e.getCause();
            }
        }

    }



    private void showDialogForExportDone() {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);

        builder.setTitle("Songs copied from external storage.");

        builder.setCancelable(false);
// Set up the buttons
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
    }

    @Override
    public void showOfflineDownloadingAlert() {

    }


    /***********************Download Videos Service Methods Ends****************************************/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        /*HisenseManager.getInstance().addServiceReadyListener(new IServicesReadyListener() {
            @Override
            public void allServicesReady() {
                 hsm=new HotelSystemManager();
                 if((arrPlaylists!=null) && (arrPlaylists.size()>0)) {
                     String p = arrPlaylists.get(0).getsplPlaylistCategory();
                     if (p.equals("1")) {
                         getHisenseVolume("0");
                         vol = 0;
                         vol1 = 0;

                     }
                     else
                     {
                         getHisenseVolume(arrPlaylists.get(0).getvolper());
                         String per = arrPlaylists.get(0).getvolper();
                         if (per.equals("0")) {
                             vol = 0;
                             vol1 = 0;
                         } else {
                             vol = 1;
                             vol1 = 1;
                         }

                     }
                 }
            }
        });
        HisenseManager.getInstance().init(HomeActivity.this);*/
        if (!MyService.isServiceRunning) {
            Intent serviceIntent = new Intent(this, MyService.class);
            ContextCompat.startForegroundService(this, serviceIntent);

        }
        else
        {
            // Utilities.showToast(HomeActivity.this,"vgvhvhhbh");
        }
        univtoken=SharedPreferenceUtil.getStringPreference(HomeActivity.this, Constants.TOKEN_ID);
        View decorView = getWindow().getDecorView();
        int uiOptions =  View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
     //    Utilities.showToast(HomeActivity.this,"HomeAcivity");
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        layout = findViewById(R.id.mainContainer);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        double perwidth1 = 10.67 * ( 120 / 2.54 );
        double perheight1 = 4.58 * ( 120 / 2.54 );
        perwidth=(float)perwidth1;              //440
        perheight=(float)perheight1;             //255
        float flwidth=(float)width;
        float flheight=(float)height;
        calwidth=flwidth-perwidth;
        calheight=flheight-perheight;
        portraitmp3layout = (LinearLayout) findViewById(R.id.mp3layout);
        blackLayout= (LinearLayout) findViewById(R.id.blacklayout);
        mp3layout = (RotateLayout) findViewById(R.id.mp3rotate);
        webView= (WebView) findViewById(R.id.webView);
        imglayout = (RotateLayout) findViewById(R.id.rotateimg);
        videolayout = (RotateLayout) findViewById(R.id.rotatevideo);
        rotateweb = (RotateLayout) findViewById(R.id.rotateweb);
        circularProgressBar = findViewById(R.id.circularProgress);
        hzProgressBar = findViewById(R.id.p_Bar2);
        lvPlaylist = findViewById(R.id.listViewPlaylists);
        lvSongs = findViewById(R.id.listViewSongs);
        Imgmarker = (ImageView) findViewById(R.id.marker);
        mPreview = findViewById(R.id.video_view);
        mPreview.setOnMediaCompletionListener(mediaPlayerCompletionListener);
        mPreviewads = findViewById(R.id.video_viewads);
        mPreviewads.setOnMediaCompletionListener(mediaPlayerCompletionListenerads);
        mPreview.setTag(VIDEO_VIEW_TAG);
        txtFileWriter = findViewById(R.id.txtWritingFile);
        txtSong = findViewById(R.id.songtitle1);
        txtArtist = findViewById(R.id.Artist);
        txttimer = findViewById(R.id.txttimer);
        myImage = (ImageView) findViewById(R.id.previmg);
        Imgicon = findViewById(R.id.imgID5);
        waitImgicon = findViewById(R.id.waitimg);
        txtTokenId = findViewById(R.id.txtTokenId);
     //   txtInfo = findViewById(R.id.txtInfo);
        txtwaiting= findViewById(R.id.txtWaitingContent);
        songsrc = new MySQLiteHelper(HomeActivity.this);
        hm = this;
        MyNotificationManager.getInstance(this);
        txtmarquee=(TextView) findViewById(R.id.marqueetxt);
        txtTokenId.setTypeface(Utilities.getApplicationTypeface(HomeActivity.this));
        txtFileWriter.setTypeface(Utilities.getApplicationTypeface(HomeActivity.this));
        String token = SharedPreferenceUtil.getStringPreference(HomeActivity.this, Constants.TOKEN_ID);
        if (token.length() > 0) {
            txtTokenId.setText("Token ID : " + token);
        } else {
            txtTokenId.setText("");
        }

        String rotation = SharedPreferenceUtil.getStringPreference(HomeActivity.this, AlenkaMediaPreferences.Rotation);
      //  rotation="90";
        if(rotation.equals(""))
        {
            rotation="0";
        }
        mp3layout.setAngle(Integer.parseInt(rotation));
        imglayout.setAngle(Integer.parseInt(rotation));
        videolayout.setAngle(Integer.parseInt(rotation));
        rotateweb.setAngle(Integer.parseInt(rotation));
        String strtuptype= SharedPreferenceUtil.getStringPreference(HomeActivity.this, AlenkaMediaPreferences.Startup);
        //TODO: Handle the new custom view crash and send crash log.
        ArrayList<Songs> songs = getSongsToBeDownloaded();
        ArrayList<Advertisements> ads = getAdvertisementsToBeDownloaded();

        if ((songs != null && songs.size() > 0) || (ads != null && ads.size() > 0)) {

            if (!AlenkaMedia.getInstance().isDownloadServiceRunning) {
              //   Utilities.showToast(HomeActivity.this,"Bind");
                doBindService();
            }
            alarm = new PlaylistWatcher();
            alarm.setContext(HomeActivity.this);
            alarm.setPlaylistStatusListener(HomeActivity.this);
            alarm.setWatcher();
            getAdvertisements();
            if (songs != null) {
                countOfTotalSongsToBeDownloaded = songs.size();
            }
            if(songs==null) {
                if (ads != null && ads.size() > 0) {
                    getPlaylistsForCurrentTime();
                }
            }


        }
        else {

            alarm = new PlaylistWatcher();
            alarm.setContext(HomeActivity.this);
            alarm.setPlaylistStatusListener(HomeActivity.this);
            if(getSongsToBeDownloaded()!=null) {
               // Utilities.showToast(HomeActivity.this, "count is====>" + getSongsToBeDownloaded().size());
            }
                alarm.setWatcher();
            getPlaylistsForCurrentTime();
            getAdvertisements();
            updateDownloadedSongsStatusOnServer();
            PlayerStatusManager playerStatusManager1 = new PlayerStatusManager(HomeActivity.this);
            playerStatusManager1.updateDownloadedSongsPlaylistWise();
        }

//
//        saveLogcatToFile(HomeActivity.this);
    }




    public static HomeActivity getInstance() {
        return hm;
    }

    public void handlemarqueesettxt(String response)
    {
        try {

            JSONObject jsonObject = new JSONObject(response);
            String data1 = jsonObject.getString("response");
            if(data1.equals("1")) {
                String data = jsonObject.getString("data");
                JSONArray arr = new JSONArray(data);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject chid = arr.getJSONObject(i);
                    addnotid = chid.getString("chkDisplaywithads");
                    String text = chid.getString("rsstext");
                    txtmarquee.setVisibility(View.VISIBLE);
                    txtmarquee.setText(text);
                    txtmarquee.setSelected(true);
                    txtmarquee.setTypeface(Utilities.getApplicationTypeface(HomeActivity.this));
                    txtmarquee.setSelected(true);
                    txtmarquee.setTextColor(Color.BLACK);

                }
            }
            else
            {
               addnotid.equals("");
                txtmarquee.setVisibility(View.GONE);

            }


        }
        catch (Exception e)
        {
            try {
                String classname = this.getClass().getSimpleName();
                String method = new Exception().getStackTrace()[0].getMethodName();
                caughtException(classname, method, response, univtoken, "5", e);
            }
            catch(Exception ex)
            {
                ex.getCause();
            }
        }


    }


    public void getmarqueetxt()
    {
        try
        {
            JSONObject json = new JSONObject();
            String token = SharedPreferenceUtil.getStringPreference(HomeActivity.this, Constants.TOKEN_ID);
            json.put("_id",token);
            json.put("clientid","2");

            new OkHttpUtil(HomeActivity.this, Constants.Get_RSS_Txt,json.toString(),
                    HomeActivity.this,false,
                    Constants.RSS_DETAILS_TAG).
                    callRequest();

        }
        catch(Exception e)
        {
            try {
                e.getCause();
                String classname = this.getClass().getSimpleName();
                String method = new Exception().getStackTrace()[0].getMethodName();
                caughtException(classname, method, "", univtoken, "5", e);
            }
            catch (Exception ex)
            {
                ex.getCause();
            }
        }

    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) { e.printStackTrace();}
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }



    public void playadvnow(String id) {
        try {
            ArrayList<Advertisements> advertisementsdownloadall = new AdvertisementsManager(HomeActivity.this).
                    getAdvertisementsThatAreDownloaded();

            if (advertisementsdownloadall != null && advertisementsdownloadall.size() > 0) {
                arrAdvweb.addAll(advertisementsdownloadall);
                for (int i = 0; i < arrAdvweb.size(); i++) {
                    String t = arrAdvweb.get(i).getAdvtID();
                    String p = arrAdvweb.get(i).getAdvFileUrl();
                    final String h = p.substring(p.length() - 3);
                    if (t.equals(id)) {
                        try {
                            if (mPreview.isPlaying()) {
                                mPreview.stopPlayback();
                                mPreview.reset();
                                mPreview.clearSurfaceView();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        currentlyPlayingAdAtIndex = i;
                        Handler mHandler = new Handler(getMainLooper());
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (h.equals("mp4")) {

                                    if ((y == 0) && (myImage != null)) {
                                        myImage.setVisibility(View.INVISIBLE);
                                        txtArtist.setVisibility(View.INVISIBLE);
                                        txtSong.setVisibility(View.INVISIBLE);
                                    }

                                    if (mPreview.isPlaying()) {
                                        mPreview.stopPlayback();
                                        mPreview.reset();
                                    }
                                    portraitmp3layout.setVisibility(View.INVISIBLE);
                                    Imgicon.setVisibility(View.INVISIBLE);
                                    mPreview.setVisibility(View.VISIBLE);
                                    mPreview.playMedia(arrAdvweb.get(currentlyPlayingAdAtIndex).getAdvtFilePath(), 0, 0);

                                } else if (h.equals("jpg")) {

                                    Handler handler = new Handler(HomeActivity.this.getMainLooper());
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            y = 0;
                                            String k = arrAdvweb.get(currentlyPlayingAdAtIndex).getAdvtFilePath();
                                            portraitmp3layout.setVisibility(View.INVISIBLE);
                                            myImage.setVisibility(View.VISIBLE);
                                            myImage.setImageURI(Uri.parse(k));
                                            mPreview.setVisibility(View.INVISIBLE);
                                            circularProgressBar.setVisibility(View.INVISIBLE);
                                            txtTokenId.setVisibility(View.INVISIBLE);
                                            Imgicon.setVisibility(View.INVISIBLE);
                                            txtArtist.setVisibility(View.INVISIBLE);
                                            txtSong.setVisibility(View.INVISIBLE);
                                        }
                                    });
                                    imgCountdowntimer = new CountDownTimer(15000, 1000) {

                                        public void onTick(long millisUntilFinished) {
                                        }

                                        public void onFinish() {

                                            if (imgCountdowntimer != null) {
                                                imgCountdowntimer.cancel();
                                            }
                                            checkimgSuccesive();
                                        }
                                    }.start();


                                } else {

                                }

                            }
                        });
                    }

                }

            }
        }catch (Exception ex)
        {
            try {
                String classname = this.getClass().getSimpleName();
                String method = new Exception().getStackTrace()[0].getMethodName();
                caughtException(classname, method, id, univtoken, "5", ex);
            }
            catch(Exception e)
            {
                e.getCause();
            }
        }


    }

    public void playplaylistfromwebnow(String id) {
        try {
            ArrayList<Playlist> playlistArrayList = new PlaylistManager(HomeActivity.this, null).getAllPlaylistInPlayingOrder();

            if (playlistArrayList != null && playlistArrayList.size() > 0) {
                arrPlaylistsweb.addAll(playlistArrayList);
                for (int i = 0; i < arrPlaylistsweb.size(); i++) {
                    String t = arrPlaylistsweb.get(i).getsplPlaylist_Id();
                    if (t.equals(id)) {
                        playSelectedPlaylist(t);
                        break;
                    }

                }

            }
        }catch(Exception ex)
        {
           try {
               String classname = this.getClass().getSimpleName();
               String method = new Exception().getStackTrace()[0].getMethodName();
               caughtException(classname, method, id, univtoken, "5", ex);
           }catch(Exception e)
           {
               e.getCause();
           }
        }
    }

    private void downloadimg(String img) {
        String url = "http://api.nusign.eu/mp3files/" + img + ".jpg";
        String pathToUsb = "";
        long bytesAvailable;
        long megAvailable;
        File[] pathsss = ContextCompat.getExternalFilesDirs(getApplicationContext(), null);

        if (pathsss.length > 1) {
            File intDrive = pathsss[0];
            pathToUsb = intDrive.getAbsolutePath();
            File usbDrive = pathsss[1];
            StatFs stat = new StatFs(pathToUsb);
            bytesAvailable = stat.getBlockSizeLong() * stat.getAvailableBlocksLong();
            megAvailable = bytesAvailable / (1024 * 1024);
            if (megAvailable > 1200) {
                pathToUsb = intDrive.getAbsolutePath();
                gblStorage = usbDrive;

            } else {
                File usbDrive1 = pathsss[1];
                pathToUsb = usbDrive1.getAbsolutePath();
                gblStorage = intDrive;
            }

        } else {
            File usbDrive2 = pathsss[0];
            gblStorage = usbDrive2;
            pathToUsb = getApplicationInfo().dataDir;
        }

        String applicationDirectory = pathToUsb;
        final String filePath = applicationDirectory + "/" + img + ".jpg";
        File imgpath = new File(filePath);
        if (imgpath.exists()) {
            Imgicon.setImageURI(Uri.parse(filePath));

            // Drawable drawable = Drawable.createFromPath(filePath);
            // Imgicon.setImageDrawable(drawable);
        } else {
            Ion.with(HomeActivity.this)
                    .load(url)
                    .progress(new ProgressCallback() {
                        @Override
                        public void onProgress(long downloaded, long total) {

                            int percentage = (int) (downloaded * 100.0 / total + 0.5);

                            if (percentage == 0) {
                                percentage = 1;
                            }

                            Log.e("Song downloaded", percentage + "%");

                            sendUpdate(percentage);
                        }
                    })
                    .write(new File(filePath)).setCallback(new FutureCallback<File>() {
                @Override
                public void onCompleted(Exception e, File result) {

                    if (e != null) {

                        Handler handler = new Handler(Looper.getMainLooper());

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                              //  Toast.makeText(HomeActivity.this, "Downloading failed for img ", Toast.LENGTH_SHORT).show();
                            }
                        }, 1000);
                        return;
                    }

                    if (result != null) {

                        if (result.exists()) {
                            Imgicon.setImageURI(Uri.parse(filePath));
                            //Imgicon.setImage
                            // Drawable drawable = Drawable.createFromPath(filePath);Drawable(drawable);


                        }

                    }
                }

            });
        }


    }


    private ActivityManager.MemoryInfo getAvailableMemory() {
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(memoryInfo);
        return memoryInfo;
    }

    public void playSelectedPlaylist(String playlistid) {
        try {
            ArrayList<Songs> arrSongsForSelectedPlaylist = new PlaylistManager(HomeActivity.this, null).
                    getSongsForPlaylist(playlistid);

            if (arrSongsForSelectedPlaylist != null && arrSongsForSelectedPlaylist.size() > 0) {

                arrSongs.clear();
                arrSongs.addAll(arrSongsForSelectedPlaylist);
                Handler mHandler = new Handler(getMainLooper());
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mPreview.setTag(VIDEO_VIEW_TAG);
                        currentlyPlayingSongAtIndex = 0;
                        songAdapter.notifyDataSetChanged();
                        if (mPreview.isPlaying()) {
                            mPreview.release();
                        }
                        mPreview.setVisibility(View.VISIBLE);

                        mPreview.playMedia(arrSongs.get(currentlyPlayingSongAtIndex).getSongPath(), vol, vol1);
                    }
                });

            }
        }catch (Exception ex)
        {
            try {
                String classname = this.getClass().getSimpleName();
                String method = new Exception().getStackTrace()[0].getMethodName();
                caughtException(classname, method, playlistid, univtoken, "5", ex);
            }
            catch(Exception e)
            {
                e.getCause();
            }
        }
    }

    @Override
    protected void onUserLeaveHint() {
        try {
            super.onUserLeaveHint();

            //mPreview.setSurfaceTexture(null);
            // mPreview.release();
            if (imgCountdowntimer != null) {
                imgCountdowntimer.cancel();
            }
            if (imgCountdowntimer1 != null) {
                imgCountdowntimer1.cancel();
            }
            if (imgCountdowntimer2 != null) {
                imgCountdowntimer2.cancel();
            }

            System.exit(2);

        }catch (Exception e)
        {
            try {
                e.getCause().toString();
                String classname = this.getClass().getSimpleName();
                String method = new Exception().getStackTrace()[0].getMethodName();
                caughtException(classname, method, "", univtoken, "5", e);
            }
            catch (Exception ex)
            {
                ex.getCause();
            }
        }

    }

    public void updateTokenpublish()

    {
        try {

            PlaylistManager playlistManager = new PlaylistManager(HomeActivity.this, playlistLoaderListener);
            playlistManager.publishTokenForUpdatedData();
        }
        catch (Exception ex)
        {
            try {
                String classname = this.getClass().getSimpleName();
                String method = new Exception().getStackTrace()[0].getMethodName();
                caughtException(classname, method, "", univtoken, "5", ex);
            }
            catch(Exception e)
            {
                e.getCause();
            }
        }
    }

    public void fillInstantadv(String songid)
    {
        try
        {
            if((arrAdvsswebInstant!=null) && (arrAdvsswebInstant.size()>0)) {
                arrAdvsswebInstant.clear();
            }
            ArrayList<Advertisements> advertisementsdownloadall = new AdvertisementsManager(HomeActivity.this).getAdvertisementsThatAreDownloaded(songid);
            if((advertisementsdownloadall!=null) && (advertisementsdownloadall.size()>0))
            {
                arrAdvsswebInstant.addAll(advertisementsdownloadall);
                setVisibilityAndPlayAdvertisement(arrAdvsswebInstant,0,"Instant");
            }

        }catch(Exception e)
        {
            e.getCause();
        }
    }

    public void instantadsinitiate(String songid,int repeat,String url,long filesize,String cat)
    {
        try
        {
            int download=0;
            if((arrAdvsswebInstant!=null) && (arrAdvsswebInstant.size()>0)) {
                arrAdvsswebInstant.clear();
            }
            ArrayList<Advertisements> advertisementsdownloadall = new AdvertisementsManager(HomeActivity.this).getAdvertisementsThatAreDownloaded(songid);
            if((advertisementsdownloadall!=null) && (advertisementsdownloadall.size()>0))
            {
                arrAdvsswebInstant.addAll(advertisementsdownloadall);
                download=1;
                setVisibilityAndPlayAdvertisement(arrAdvsswebInstant,0,"Instant");
            }
            if(download==0)
            {
                Handler handler = new Handler(HomeActivity.this.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                       try {
                           advertisementDataSource = new AdvertisementDataSource(HomeActivity.this);
                           advertisementDataSource.open();
                           advertisementDataSource.insertAdvertisement(url, songid);
                           advertisementDataSource.close();
                           startDownloadingnextSongs(url, songid, filesize, cat);
                       }
                       catch (Exception e)
                       {
                           e.getCause();
                       }

                    }
                });
            }

        }
        catch(Exception e)
        {
            e.getCause();
        }
    }

   public void stopCurrentSong(String cat)
   {

       if(cat.equals("Normal")) {
           Handler handler = new Handler(HomeActivity.this.getMainLooper());
           handler.post(new Runnable() {
               @Override
               public void run() {
                   if (mPreview.isPlaying()) {
                       mPreview.stopPlayback();
                       mPreview.reset();
                       mPreview.clearSurfaceView();
                   }
                   playNextSongIex=-1;
                   temploop=0;
                   IsInstantPlaying=false;
                   String p = arrPlaylists.get(0).getsplPlaylist_Id();
                   String h = PlaylistWatcher.currentPlaylistID;
                   if(p.equals(h)) {
                       checkimgSuccesive();
                   }
                   else {
                       getPlaylistsForCurrentTime();
                   }
                  // playCurrentContent(arrSongs,currentlyPlayingSongAtIndex);

               }
           });
       }


   }



    public void playnextsongfromweb(String songid, String url, String albumid, String artistid, final String title, final String artname,int repeat,long filesize,String cat) {
        try {
            if(cat.equals("Ads"))
            {
                gblSongid=songid;
                temploopads=repeat;
                instantadsinitiate(songid,repeat,url,filesize,cat);
                return;
            }
            gblSongid = songid;
            titledownload = title;
            artistdownload = artname;
            temploop = repeat;
            int h = 0;
            // arrSongsweb.clear();
            //  arrSongsDownloadAll.clear();

            ArrayList<Songs> arrSongsDownloadAll = new PlaylistManager(HomeActivity.this, null).getAllDownloadedSongs(songid);
            final int p = arrSongsDownloadAll.size();

            if (arrSongsDownloadAll.size() > 0) {
                arrSongsweb.addAll(arrSongsDownloadAll);

                for (int i = 0; i < arrSongsweb.size(); i++) {
                    String t = arrSongsweb.get(i).getTitle_Id();
                    if (t.equals(songid)) {
                        //Utilities.showToast(HomeActivity.this,"Song Available==>"+songid);
                        h = 1;
                        IsInstantPlaying=true;
                        playCurrentContent(arrSongsweb,i);
                      //  playNextSongIex = i;
                        break;
                    }

                }

            }
            if (h == 0) {
                songsrc.insertnewSongsfromweb(songid, url, artistid, albumid);
                Handler handler = new Handler(HomeActivity.this.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        startDownloadingnextSongs(url, songid, filesize,cat);

                    }
                });

            }
        }catch (Exception ex)
        {
            try {
                String classname = this.getClass().getSimpleName();
                String method = new Exception().getStackTrace()[0].getMethodName();
                caughtException(classname, method, songid + url + albumid + artistid + title + artname + filesize, univtoken, "5", ex);
            }
            catch (Exception e)
            {
                e.getCause();
            }
        }

    }

   public void playCurrentContent(ArrayList<Songs> arrinst,int instantcurrindex)
   {
       try {
          // playNextSongIex=instantcurrindex;
           if(mPreview.isPlaying())
           {
               String titleid=arrinst.get(instantcurrindex).getTitle_Id();

               if(titleidmatch.equals(""))
               {
                   titleidmatch=titleid;

               }
               else {
                   if (titleidmatch.equals(titleid))
                   {
                       if(temploop==0)
                       {
                           playNextSongIex=-1;
                           titleidmatch="";
                           return;
                       }
                   }
                   else
                   {
                       titleidmatch="";
                       if(titleidmatch.equals(""))
                       {
                           titleidmatch=titleid;

                       }

                   }
               }
           }
           String f = arrinst.get(instantcurrindex).getTitle_Url();
           String a = f.substring(f.length() - 3);
           if (a.equals("mp4")) {

               Handler handler = new Handler(HomeActivity.this.getMainLooper());
               handler.post(new Runnable() {
                   @Override
                   public void run() {
                       if (mPreview.isPlaying()) {
                           mPreview.stopPlayback();
                           mPreview.reset();
                           mPreview.clearSurfaceView();
                       }
                       Log.d(TAG, "123");
                       if ((y == 0) && (myImage != null)) {
                           myImage.setVisibility(View.INVISIBLE);
                           myImage.setImageDrawable(null);
                       }
                       y=1;
                       txtArtist.setVisibility(View.INVISIBLE);
                       txtSong.setVisibility(View.INVISIBLE);
                       portraitmp3layout.setVisibility(View.INVISIBLE);
                       mPreview.setVisibility(View.VISIBLE);
                       mPreview.playMedia(arrinst.get(instantcurrindex).getSongPath(), vol, vol1);
                       Imgicon.setVisibility(View.INVISIBLE);
                       if(temploop==1)
                       {
                           playNextSongIex=instantcurrindex;
                           IsInstantPlaying=true;
                       }

                       else
                       {
                           playNextSongIex=-1;
                           temploop=0;
                       }

                   }
               });

           }
       }catch(Exception e)
       {
           e.getCause();
       }

   }




    public void startDownloadingnextSongs(String urlinst, String title,long filesize,String cat) {

        try {
            String url = urlinst;
            String pathToUsb = "";
            long bytesAvailable;
            long megAvailable;
            File[] pathsss = ContextCompat.getExternalFilesDirs(getApplicationContext(), null);

            if (pathsss.length > 1) {
                File intDrive = pathsss[0];
                pathToUsb = intDrive.getAbsolutePath();
                File usbDrive = pathsss[1];
                StatFs stat = new StatFs(pathToUsb);
                bytesAvailable = stat.getBlockSizeLong() * stat.getAvailableBlocksLong();
                megAvailable = bytesAvailable / (1024 * 1024);
                if (megAvailable > 1200) {
                    pathToUsb = intDrive.getAbsolutePath();
                    gblStorage = usbDrive;

                } else {
                    File usbDrive1 = pathsss[1];
                    pathToUsb = usbDrive1.getAbsolutePath();
                    gblStorage = intDrive;
                }

            } else {
                File usbDrive2 = pathsss[0];
                //gblStorage = usbDrive2;
                pathToUsb = getApplicationInfo().dataDir;
            }

            final String ext =url.substring(url.length() - 3);
            String applicationDirectory = pathToUsb;
            final String filePath = applicationDirectory + "/" + title +"." +ext;
            File sngpath = new File(filePath);
            if((sngpath.exists()) && (sngpath.length()==filesize))
            {
                if(sngpath.length()==filesize) {
                    String k = "1";
                    if(cat.equals("Ads"))
                    {
                        advertisementDataSource = new AdvertisementDataSource(HomeActivity.this);
                        advertisementDataSource.open();
                        advertisementDataSource.UpdateAdv(gblSongid,sngpath.getPath(),k);
                        advertisementDataSource.close();
                        fillInstantadv(gblSongid);
                    }
                    else {
                        songsrc.downloadupdate(k, sngpath.getPath(), gblSongid);
                        fillalldownloadsongs(gblSongid);
                    }
                }
                else {
                    ExecutorService downloadExecutor = Executors.newSingleThreadExecutor();
                    downloadExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            //  Utilities.showToast(HomeActivity.this,"Downloading Starts");
                            Ion.with(HomeActivity.this)
                                    .load(url)
                                    .progress(new ProgressCallback() {
                                        @Override
                                        public void onProgress(long downloaded, long total) {

                                            int percentage = (int) (downloaded * 100.0 / total + 0.5);

                                            if (percentage == 0) {
                                                percentage = 1;
                                            }

                                            Log.e("Song downloaded", percentage + "%");

                                            sendUpdate(percentage);
                                        }
                                    })
                                    .write(new File(filePath)).setCallback(new FutureCallback<File>() {
                                        @Override
                                        public void onCompleted(Exception e, File result) {

                                            if (e != null) {

                                                Handler handler = new Handler(Looper.getMainLooper());

                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(HomeActivity.this, "Downloading failed for Instant" + e.getCause().toString(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }, 1000);
                                                return;
                                            }

                                            if (result != null) {

                                                if (result.exists()) {
                                                    long dwloadfile = result.length();
                                                    if (dwloadfile == filesize) {
                                                        String k = "1";
                                                        if (cat.equals("Ads")) {
                                                            try {
                                                                advertisementDataSource = new AdvertisementDataSource(HomeActivity.this);
                                                                advertisementDataSource.open();
                                                                advertisementDataSource.UpdateAdv(gblSongid, sngpath.getPath(), k);
                                                                advertisementDataSource.close();
                                                                fillInstantadv(gblSongid);
                                                            } catch (Exception ex) {
                                                                ex.getCause();
                                                            }
                                                        } else {
                                                            songsrc.downloadupdate(k, sngpath.getPath(), gblSongid);
                                                            fillalldownloadsongs(gblSongid);
                                                        }
                                                    }
                                                }
                                            }

                                        }

                                    });
                        }
                    });
                }
            }
            else {
                ExecutorService downloadExecutor = Executors.newSingleThreadExecutor();
                downloadExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        Ion.with(HomeActivity.this)
                                .load(url)
                                .progress(new ProgressCallback() {
                                    @Override
                                    public void onProgress(long downloaded, long total) {
                                        int percentage = (int) (downloaded * 100.0 / total + 0.5);
                                        if (percentage == 0) {
                                            percentage = 1;
                                        }
                                        Log.e("Song downloaded", percentage + "%");
                                        sendUpdate(percentage);
                                    }
                                })
                                .write(new File(filePath)).setCallback(new FutureCallback<File>() {
                                    @Override
                                    public void onCompleted(Exception e, File result) {

                                        if (e != null) {

                                            Handler handler = new Handler(Looper.getMainLooper());

                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(HomeActivity.this, "Downloading failed for Instant"+e.getCause().toString(), Toast.LENGTH_SHORT).show();
                                                }
                                            }, 1000);
                                            return;
                                        }

                                        if (result != null) {

                                            if (result.exists()) {
                                                long dwloadfile=result.length();
                                                if(dwloadfile==filesize) {
                                                    String k = "1";
                                                    if(cat.equals("Ads"))
                                                    {
                                                        try {
                                                            advertisementDataSource = new AdvertisementDataSource(HomeActivity.this);
                                                            advertisementDataSource.open();
                                                            advertisementDataSource.UpdateAdv(gblSongid, sngpath.getPath(), k);
                                                            advertisementDataSource.close();
                                                            fillInstantadv(gblSongid);
                                                        }catch (Exception ex)
                                                        {
                                                            ex.getCause();
                                                        }
                                                    }
                                                    else {
                                                        songsrc.downloadupdate(k, sngpath.getPath(), gblSongid);
                                                        fillalldownloadsongs(gblSongid);
                                                    }
                                                }
                                            }
                                        }

                                    }

                                });
                        }
                    });

            }

        } catch (Exception e1) {
            try {
                e1.printStackTrace();
                String classname = this.getClass().getSimpleName();
                String method = new Exception().getStackTrace()[0].getMethodName();
                caughtException(classname, method, urlinst + title + filesize, univtoken, "5", e1);
            }
            catch (Exception ex)
            {
                ex.getCause();
            }
            //Utilities.showToast(HomeActivity.this, " Error => " + e1.getMessage());

        }

    }

    public void fillalldownloadsongs(String songid)
    {
        try {

            //  arrSongsweb.clear();
            //  arrSongsDownloadAll.clear();
            //  Utilities.showToast(HomeActivity.this,"Fn Fill All Songs");
            ArrayList<Songs> arrSongsDownloadAll = new PlaylistManager(HomeActivity.this, null).getAllDownloadedSongs(songid);

            if (arrSongsDownloadAll.size() > 0) {
                arrSongsweb.addAll(arrSongsDownloadAll);

                for (int i = 0; i < arrSongsweb.size(); i++) {
                    String t = arrSongsweb.get(i).getTitle_Id();
                    if (t.equals(songid)) {
                        playCurrentContent(arrSongsweb,i);
                        //playNextSongIex = i;
                        //  Utilities.showToast(HomeActivity.this,"Time to play");
                        break;
                    }

                }
            }
        }catch (Exception ex)
        {
            try {
                String classname = this.getClass().getSimpleName();
                String method = new Exception().getStackTrace()[0].getMethodName();
                caughtException(classname, method, songid, univtoken, "5", ex);
            }
            catch(Exception e)
            {
                e.getCause();
            }
        }

    }

    public void playsongfromweb(String songid, String url, String albumid, String artistid, final String title, final String artname) {

        gblSongid = songid;
        titledownload = title;
        artistdownload = artname;
        int h = 0;
        arrSongsweb.clear();
        arrSongsDownloadAll.clear();

        ArrayList<Songs> arrSongsDownloadAll = new PlaylistManager(HomeActivity.this, null).getAllDownloadedSongs(songid);
        final int p = arrSongsDownloadAll.size();

         /*
        Handler mHandler = new Handler(getMainLooper());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                String str1 = mg;
              //  Toast.makeText(HomeActivity.this,"mg "+str1,Toast.LENGTH_SHORT).show();
            }
        });*/
        if (arrSongsDownloadAll.size() > 0) {
            arrSongsweb.addAll(arrSongsDownloadAll);

            for (int i = 0; i < arrSongsweb.size(); i++) {
                String t = arrSongsweb.get(i).getTitle_Id();
                if (t.equals(songid)) {
                    h = 1;
                    currentlyPlayingSongAtIndex = i;
                    String f = arrSongsweb.get(currentlyPlayingSongAtIndex).getTitle_Url();
                    String a = f.substring(f.length() - 3);
                    //String d=h;
                    if (a.equals("mp3")) {
                        Handler handler = new Handler(HomeActivity.this.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if(mPreview.isPlaying())
                                {
                                    mPreview.release();
                                    mPreview.setVisibility(View.INVISIBLE);
                                }
                                if (y == 0) {
                                    myImage.setVisibility(View.INVISIBLE);
                                    myImage.setImageDrawable(null);
                                }

                                circularProgressBar.setVisibility(View.INVISIBLE);
                                txtTokenId.setVisibility(View.INVISIBLE);
                                portraitmp3layout.setVisibility(View.VISIBLE);
                                txtArtist.setVisibility(View.VISIBLE);
                                txtSong.setVisibility(View.VISIBLE);
                                Imgicon.setVisibility(View.VISIBLE);

                                mPreview.playMedia(arrSongsweb.get(currentlyPlayingSongAtIndex).getSongPath(),vol,vol1);
                                txtSong.setText(title);
                                txtArtist.setText(artname);

                            }
                        });
                    }
                    else if (a.equals("mp4"))
                    {

                        Handler handler = new Handler(HomeActivity.this.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "123");
                                if ((y == 0) && (myImage != null)) {
                                    myImage.setVisibility(View.INVISIBLE);
                                    myImage.setImageDrawable(null);
                                }
                                if(mPreview.isPlaying())
                                {
                                    mPreview.release();
                                }
                                y=1;
                                mPreview.release();
                                txtArtist.setVisibility(View.INVISIBLE);
                                txtSong.setVisibility(View.INVISIBLE);
                                portraitmp3layout.setVisibility(View.INVISIBLE);
                                mPreview.setVisibility(View.VISIBLE);
                                mPreview.playMedia(arrSongsweb.get(currentlyPlayingSongAtIndex).getSongPath(),vol,vol1);
                                Imgicon.setVisibility(View.INVISIBLE);

                            }
                        });

                    }

                    else {
                        Handler handler = new Handler(HomeActivity.this.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                                if (y == 0) {
                                    myImage.setVisibility(View.INVISIBLE);
                                    myImage.setImageDrawable(null);
                                    if(imgCountdowntimer!=null) {
                                        imgCountdowntimer.cancel();
                                    }
                                    if(imgCountdowntimer1!=null) {
                                        imgCountdowntimer1.cancel();
                                    }
                                    if(imgCountdowntimer2!=null) {
                                        imgCountdowntimer2.cancel();
                                    }
                                }
                                if(mPreview.isPlaying())
                                {
                                    mPreview.stopPlayback();
                                }
                                y=1;
                                String k = arrSongsweb.get(currentlyPlayingSongAtIndex).getSongPath();
                                portraitmp3layout.setVisibility(View.INVISIBLE);
                                myImage.setVisibility(View.VISIBLE);
                                myImage.setImageURI(Uri.parse(k));
                                mPreview.setVisibility(View.INVISIBLE);
                                circularProgressBar.setVisibility(View.INVISIBLE);
                                txtTokenId.setVisibility(View.INVISIBLE);
                                Imgicon.setVisibility(View.INVISIBLE);
                                txtArtist.setVisibility(View.INVISIBLE);
                                txtSong.setVisibility(View.INVISIBLE);
                            }
                        });
                        runOnUiThread(new Runnable() {
                            public void run() {
                                imgCountdowntimer = new CountDownTimer(15000, 1000) {

                                    public void onTick(long millisUntilFinished) {
                                       // txtArtist.setText("seconds remaining: " + millisUntilFinished / 1000);

                                    }

                                    public void onFinish() {

                                        ctrplaylistchg = 0;
                                        checkimgSuccesive();
                                        imgCountdowntimer.cancel();
                                    }
                                }.start();

                            }
                        });


                    }
                }
                break;

            }

        }
        if (h == 0) {
            songsrc.insertnewSongsfromweb(songid, url, artistid, albumid);
            startDownloadingSongs(url, songid);
        }

    }

    public void startDownloadingSongs(String urlinst, String title) {

        try {

            String url = urlinst;
            String pathToUsb = "";
            long bytesAvailable;
            long megAvailable;
            File[] pathsss = ContextCompat.getExternalFilesDirs(getApplicationContext(), null);

            if (pathsss.length > 1) {
                File intDrive = pathsss[0];
                pathToUsb = intDrive.getAbsolutePath();
                File usbDrive = pathsss[1];
                StatFs stat = new StatFs(pathToUsb);
                bytesAvailable = stat.getBlockSizeLong() * stat.getAvailableBlocksLong();
                megAvailable = bytesAvailable / (1024 * 1024);
                if (megAvailable > 1200) {
                    pathToUsb = intDrive.getAbsolutePath();
                    gblStorage = usbDrive;

                } else {
                    File usbDrive1 = pathsss[1];
                    pathToUsb = usbDrive1.getAbsolutePath();
                    gblStorage = intDrive;
                }

            } else {
                File usbDrive2 = pathsss[0];
                gblStorage = usbDrive2;
                pathToUsb = getApplicationInfo().dataDir;
            }

            final String ext =url.substring(url.length() - 3);
            String applicationDirectory = pathToUsb;
            final String filePath = applicationDirectory + "/" + title +"." +ext;
            File sngpath = new File(filePath);
            if(sngpath.exists())
            {

            }
            else {
                Ion.with(HomeActivity.this)
                        .load(url)
                        .progress(new ProgressCallback() {
                            @Override
                            public void onProgress(long downloaded, long total) {

                                int percentage = (int) (downloaded * 100.0 / total + 0.5);

                                if (percentage == 0) {
                                    percentage = 1;
                                }

                                Log.e("Song downloaded", percentage + "%");

                                sendUpdate(percentage);
                            }
                        })
                        .write(new File(filePath)).setCallback(new FutureCallback<File>() {
                    @Override
                    public void onCompleted(Exception e, File result) {

                        if (e != null) {

                            Handler handler = new Handler(Looper.getMainLooper());

                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    // Toast.makeText(HomeActivity.this, "Downloading failed for img ", Toast.LENGTH_SHORT).show();
                                }
                            }, 1000);
                            return;
                        }

                        if (result != null) {

                            if (result.exists()) {

                                String k = "1";
                                songsrc.downloadupdate(k, sngpath.getPath(), gblSongid);

                                try {

                                    if (ext.equals("mp3")) {


                                        if (mPreview.isPlaying()) {
                                            mPreview.release();
                                            mPreview.setVisibility(View.INVISIBLE);

                                        }
                                        if (y == 0) {
                                            myImage.setVisibility(View.INVISIBLE);
                                            myImage.setImageDrawable(null);
                                        }
                                        mPreview.release();
                                        portraitmp3layout.setVisibility(View.VISIBLE);
                                        // portraitmp3layout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                        txtArtist.setVisibility(View.VISIBLE);
                                        txtSong.setVisibility(View.VISIBLE);
                                        Imgicon.setVisibility(View.VISIBLE);
                                        txtSong.setText(titledownload);
                                        mPreview.playMedia(sngpath.getPath(), vol, vol1);
                                        txtArtist.setText(artistdownload);
                                    } else if (ext.equals("mp4")) {
                                        Handler handler = new Handler(HomeActivity.this.getMainLooper());
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (mPreview.isPlaying()) {
                                                    mPreview.release();

                                                }
                                                if ((y == 0) && (myImage != null)) {
                                                    myImage.setVisibility(View.INVISIBLE);
                                                    myImage.setImageDrawable(null);
                                                }
                                                y = 1;
                                                mPreview.release();
                                                portraitmp3layout.setVisibility(View.INVISIBLE);
                                                mPreview.setVisibility(View.VISIBLE);
                                                mPreview.playMedia(sngpath.getPath(), vol, vol1);
                                                txtArtist.setVisibility(View.INVISIBLE);
                                                txtSong.setVisibility(View.INVISIBLE);
                                                Imgicon.setVisibility(View.INVISIBLE);
                                            }
                                        });

                                    } else {
                                        Handler handler = new Handler(HomeActivity.this.getMainLooper());
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {

                                                if (y == 0) {
                                                    myImage.setVisibility(View.INVISIBLE);
                                                    myImage.setImageDrawable(null);
                                                    if (imgCountdowntimer != null) {
                                                        imgCountdowntimer.cancel();
                                                    }
                                                    if (imgCountdowntimer1 != null) {
                                                        imgCountdowntimer1.cancel();
                                                    }
                                                    if (imgCountdowntimer2 != null) {
                                                        imgCountdowntimer2.cancel();
                                                    }
                                                }
                                                if (mPreview.isPlaying()) {
                                                    mPreview.stopPlayback();
                                                }
                                                y = 1;
                                                String k = sngpath.getPath();
                                                portraitmp3layout.setVisibility(View.INVISIBLE);
                                                myImage.setVisibility(View.VISIBLE);
                                                myImage.setImageURI(Uri.parse(k));
                                                mPreview.setVisibility(View.INVISIBLE);
                                                circularProgressBar.setVisibility(View.INVISIBLE);
                                                txtTokenId.setVisibility(View.INVISIBLE);
                                                Imgicon.setVisibility(View.INVISIBLE);
                                                txtArtist.setVisibility(View.INVISIBLE);
                                                txtSong.setVisibility(View.INVISIBLE);
                                            }
                                        });
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                imgCountdowntimer = new CountDownTimer(15000, 1000) {

                                                    public void onTick(long millisUntilFinished) {
                                                        // txtArtist.setText("seconds remaining: " + millisUntilFinished / 1000);

                                                    }

                                                    public void onFinish() {

                                                        ctrplaylistchg = 0;
                                                        checkimgSuccesive();
                                                        imgCountdowntimer.cancel();
                                                    }
                                                }.start();


                                            }
                                        });


                                    }
                                } catch (Exception eb) {
                                    eb.getCause().toString();
                                    eb.getCause();

                                }

                            }

                        }
                    }

                });
            }

        } catch (Exception e1) {

            e1.printStackTrace();
           // Utilities.showToast(HomeActivity.this, " Error => " + e1.getMessage());

        }

    }

    public void sendUpdate(long value) {
        for (int i = mListeners.size() - 1; i >= 0; i--) {
            mListeners.get(i).onUpdate(value);
        }
    }

    private final ArrayList<DownloadListener> mListeners
            = new ArrayList<DownloadListener>();





    private void sendLastCrashLog() {

        try{
            String json =  SharedPreferenceUtil.getStringPreference(HomeActivity.this, Constants.CRASH_MESSAGE);

            if (json != null ){
                if(json=="")
                {
                    return;
                }

                JSONObject jsonObject = new JSONObject(json);
                new OkHttpUtil(HomeActivity.this, Constants.UPDATE_CRASH_LOG, jsonObject.toString(), new OkHttpUtil.OkHttpResponse() {
                    @Override
                    public void onResponse(String response, int tag) {

                        try {

                            JSONObject jsonObject = new JSONObject(response);

                            if (jsonObject.has("Response")){

                                String status = jsonObject.getString("Response");

                                if (status.equalsIgnoreCase("1")){

                                    SharedPreferenceUtil.removeStringPreference(HomeActivity.this,Constants.CRASH_MESSAGE);


                                }
                            }
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Exception e, int tag)
                    {
                        e.printStackTrace();
                    }
                },false,Constants.UPDATE_CRASH_LOG_TAG).execute();
            }

        }catch (Exception e){
            try {
                String classname = this.getClass().getSimpleName();
                String method = new Exception().getStackTrace()[0].getMethodName();
                caughtException(classname, method, "", univtoken, "5", e);
                e.printStackTrace();
            }
            catch (Exception ex)
            {
                ex.getCause();
            }
        }
    }

    private ArrayList<Songs> getSongsToBeDownloaded(){
        try {

            ArrayList<Playlist> playlists = new PlaylistManager(HomeActivity.this, null).getPlaylistFromLocallyToBedDownload();
            ArrayList<Songs> songsToBeDownloaded = null;

            if (playlists != null && playlists.size() > 0) {

                PlaylistManager songsLoader = new PlaylistManager(HomeActivity.this, null);
                songsToBeDownloaded = new ArrayList<>();

                for (Playlist playlist : playlists) {
                    playlists.size();
                    ArrayList<Songs> songs = songsLoader.getSongsThatAreNotDownloaded(playlist.getsplPlaylist_Id());

                    if (songs != null && songs.size() > 0) {

//                    if (playlist.getIsSeparatinActive() == 0){
                        // sort(songs);
//                    }
                        songsToBeDownloaded.addAll(songs);
                    } else {
                        ArrayList<Songs> UnschdSonglist = songsLoader.getUnschdSongs();
                        if ((UnschdSonglist != null) && (UnschdSonglist.size() > 0)) {
                            songsToBeDownloaded.addAll(UnschdSonglist);

                        }
                    }

                }

                songsLoader = null;

                if (songsToBeDownloaded.size() > 0) {
                    return songsToBeDownloaded;
                }
            }

        }catch(Exception ex)
        {
            try {
                String classname = this.getClass().getSimpleName();
                String method = new Exception().getStackTrace()[0].getMethodName();
                caughtException(classname, method, "", univtoken, "5", ex);
            }
            catch (Exception e)
            {
                e.getCause();
            }
        }
        return null;
    }

    private ArrayList<Advertisements> getAdvertisementsToBeDownloaded(){
        return new AdvertisementsManager(this).
                getAdvertisementsToBeDownloaded();
    }

    private void getAdvertisements() {
        try {
            arrAdvertisementsSong.clear();
            arrAdvertisementsMinute.clear();
            arrAdvertisementsTime.clear();
            ArrayList<Advertisements> advertisements = new AdvertisementsManager(HomeActivity.this).
                    getAdvertisementsThatAreDownloaded();

            if (advertisements != null && advertisements.size() > 0) {

                for (int i = 0; i < advertisements.size(); i++) {
                    if (advertisements.get(i).getIsSong().equals("1")) {
                        arrAdvertisementsSong.add(advertisements.get(i));
                    }
                    if (advertisements.get(i).getIsTime().equals("1")) {
                        arrAdvertisementsTime.add(advertisements.get(i));
                    }
                    if (advertisements.get(i).getIsMinute().equals("1")) {
                        arrAdvertisementsMinute.add(advertisements.get(i));
                    }
                }
            }
        }catch (Exception ex)
        {
            try {
                String classname = this.getClass().getSimpleName();
                String method = new Exception().getStackTrace()[0].getMethodName();
                caughtException(classname, method, "", univtoken, "5", ex);
            }
            catch(Exception e)
            {
                e.getCause();
            }
        }

    }

    Lvideoads.OnMediaCompletionListener mediaPlayerCompletionListenerads = new Lvideoads.OnMediaCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            try {
                closeAdvertisementtime(gbltypy,gblfadevol,exttype);
            }
            catch (Exception ex)
            {
                try {
                    String classname = this.getClass().getSimpleName();
                    String method = new Exception().getStackTrace()[0].getMethodName();
                    caughtException(classname, method, String.valueOf(mediaPlayer), univtoken, "5", ex);
                }catch (Exception e)
                {
                    e.getCause();
                }
            }

           // mPreview.playMedia(Uri.parse(url),1,1);

        }
    };


    MyClaudVideoView.OnMediaCompletionListener mediaPlayerCompletionListener = new MyClaudVideoView.OnMediaCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            try {

                if(imgCountdowntimer!=null) {
                    imgCountdowntimer.cancel();
                }
                if(imgCountdowntimer1!=null) {
                    imgCountdowntimer1.cancel();
                }
                if(imgCountdowntimer2!=null) {
                    imgCountdowntimer2.cancel();
                }

                if(mPreview.isPlaying())
                {
                    return;
                }

                /*If song played was at last index then restart the playlist.*/

                if(playNextSongIex!=-1)
                {
                    IsInstantPlaying=true;
                    playnextSong();
                    return;
                }
                else {
                    IsInstantPlaying=false;
                      //  playads=true;
                        if (arrSongs.size() - 1 > currentlyPlayingSongAtIndex) {
                            currentlyPlayingSongAtIndex++;

                        } else {
                            currentlyPlayingSongAtIndex = 0;
                        }
                    // Utilities.showToast(HomeActivity.this,"OnComplete");

                }

                if((arrAdvertisementsSong!=null) && (arrAdvertisementsSong.size()>0)) {

                    PlaylistWatcher.PLAY_AD_AFTER_SONGS_COUNTER++;

                    if (PlaylistWatcher.PLAY_AD_AFTER_SONGS_COUNTER == PlaylistWatcher.PLAY_AD_AFTER_SONGS) {

                        setVisibilityAndPlayAdvertisement(arrAdvertisementsSong, currentlyPlayingAdAtIndexSong, "Song");

                    }

                }

                mPreview.setTag(VIDEO_VIEW_TAG);
                Thread newThread = new Thread(() -> {
                    insertsongStatus(currentlyPlayingSongAtIndex);

                });
                newThread.start();
                String f = arrSongs.get(currentlyPlayingSongAtIndex).getTitle_Url();
                String mediatype = arrSongs.get(currentlyPlayingSongAtIndex).getmediatype();
                int reftime = arrSongs.get(currentlyPlayingSongAtIndex).getreftime();
                String h = f.substring(f.length() - 3);
                //String d=h;

                if(mediatype.equals("Url")) {
                    mPreview.setVisibility(View.INVISIBLE);
                    txtTokenId.setVisibility(View.INVISIBLE);
                    myImage.setVisibility(View.INVISIBLE);
                    Imgicon.setVisibility(View.INVISIBLE);
                    txtArtist.setVisibility(View.INVISIBLE);
                    txtSong.setVisibility(View.INVISIBLE);
                    circularProgressBar.setVisibility(View.INVISIBLE);
                    portraitmp3layout.setVisibility(View.INVISIBLE);
                    webView.getSettings().setLoadsImagesAutomatically(true);
                    webView.getSettings().setJavaScriptEnabled(true);
                    webView.getSettings().setDomStorageEnabled(true);
                    webView.setVisibility(View.VISIBLE);
                 //   webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
                 //   webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
                    webView.getSettings().setCacheMode( WebSettings.LOAD_CACHE_ELSE_NETWORK);
                    String geturl=arrSongs.get(currentlyPlayingSongAtIndex).getTitle_Url();
                    Uri uri = Uri.parse(geturl);
                    String ct = uri.getQueryParameter("ct");
                    if ((ct!=null) && (ct.equals("00:00")) )
                    {

                        String modurl=geturl.replace("00:00",Utilities.currentTimeHHMM());
                        webView.loadUrl(modurl);
                    }
                    else {

                        webView.loadUrl(arrSongs.get(currentlyPlayingSongAtIndex).getTitle_Url());
                    }
                    webView.setWebViewClient(new WebViewClient() {

                        public void onPageFinished(WebView view, String url) {
                            if(url.equals("about:blank")) {

                            }
                            else
                            {
                                mCountDownTimer = new CountDownTimer(500, 500) {

                                    public void onTick(long millisUntilFinished) {

                                    }

                                    public void onFinish() {
                                        webView.setVisibility(View.VISIBLE);
                                        mCountDownTimer.cancel();

                                    }
                                }.start();

                            }
                        }
                    });
                    int timeinterval=(arrSongs.get(currentlyPlayingSongAtIndex).gettimeinterval())*1000;
                    imgCountdowntimer2 = new CountDownTimer(timeinterval, 1000) {

                        public void onTick(long millisUntilFinished) {
                           /* if((timeinterval/1000) > reftime) {
                                long p = (millisUntilFinished / 1000);
                                if (p == reftime) {
                                      webView.reload();
                                }
                            }*/

                            //  txttimer.setText(" " + millisUntilFinished / 1000);
                        }

                        public void onFinish() {
                            webView.loadUrl("about:blank");
                            webView.setVisibility(View.INVISIBLE);
                            imgCountdowntimer2.cancel();
                            // imgCountdowntimer.onFinish();
                            ctrplaylistchg = 0;
                            if (mPreview.isPlaying()) {
                                return;
                            }

                            checkimgSuccesive();

                        }
                    }.start();
                }

                else if(h.equals("jpg")||h.equals("jpeg")||h.equals("png"))
                {
                    Handler handler = new Handler(HomeActivity.this.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //123videoView.reset();
                            y=0;
                            //hidenavigation();
                            if(mPreview.isPlaying())
                            {
                                mPreview.stopPlayback();
                            }

                            String k = arrSongs.get(currentlyPlayingSongAtIndex).getSongPath();
                            portraitmp3layout.setVisibility(View.INVISIBLE);
                            myImage.setVisibility(View.VISIBLE);
                            myImage.setImageURI(Uri.parse(k));
                            mPreview.setVisibility(View.INVISIBLE);
                            webView.setVisibility(View.INVISIBLE);
                            circularProgressBar.setVisibility(View.INVISIBLE);
                            txtTokenId.setVisibility(View.INVISIBLE);
                            Imgicon.setVisibility(View.INVISIBLE);
                            txtArtist.setVisibility(View.INVISIBLE);
                            txtSong.setVisibility(View.INVISIBLE);
                            //123videoView.start();

                        }
                    });
                    int timeinterval=(arrSongs.get(currentlyPlayingSongAtIndex).gettimeinterval())*1000;
                    imgCountdowntimer= new CountDownTimer(timeinterval, 1000) {

                        public void onTick(long millisUntilFinished) {

                         //  txttimer.setText(" " + millisUntilFinished / 1000);

                        }


                        public void onFinish() {
                            imgCountdowntimer.cancel();
                            onCompletion(mediaPlayer);
                        }

                    }.start();
                }
                else if(h.equals("mp4")) {

                    Handler handler = new Handler(HomeActivity.this.getMainLooper());

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                      //   Toast.makeText(HomeActivity.this,String.valueOf(arrSongs.size()),Toast.LENGTH_LONG).show();
                            //videoView.setVisibility(View.VISIBLE);
                            if ((y == 0) && (myImage!=null)) {
                                myImage.setVisibility(View.INVISIBLE);
                                txtArtist.setVisibility(View.INVISIBLE);
                                txtSong.setVisibility(View.INVISIBLE);
                            }
                            y=1;
                            mPreview.release();
                            portraitmp3layout.setVisibility(View.INVISIBLE);
                            webView.setVisibility(View.INVISIBLE);
                            mPreview.setVisibility(View.VISIBLE);
                            if(gblfadevol.equals("1")) {
                                mPreview.playMedia(arrSongs.get(currentlyPlayingSongAtIndex).getSongPath(), 0, 0);
                            }
                            else {
                                mPreview.playMedia(arrSongs.get(currentlyPlayingSongAtIndex).getSongPath(), vol, vol1);

                            }
                            Imgicon.setVisibility(View.INVISIBLE);
                        //    setvolumeplayer();
                        }
                    });
                }
                else
                {
                    Handler handler = new Handler(HomeActivity.this.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (y == 0) {

                                myImage.setVisibility(View.INVISIBLE);
                            }
                            y=1;
                            mPreview.playMedia(arrSongs.get(currentlyPlayingSongAtIndex).getSongPath(),vol,vol1);
                            mPreview.setVisibility(View.INVISIBLE);
                            circularProgressBar.setVisibility(View.INVISIBLE);
                            webView.setVisibility(View.INVISIBLE);
                            txtTokenId.setVisibility(View.INVISIBLE);
                            Imgicon.setVisibility(View.VISIBLE);
                            txtSong.setText(arrSongs.get(currentlyPlayingSongAtIndex).getTitle());
                            txtArtist.setText(arrSongs.get(currentlyPlayingSongAtIndex).getAr_Name());
                            portraitmp3layout.setVisibility(View.VISIBLE);
                            txtArtist.setVisibility(View.VISIBLE);
                            txtSong.setVisibility(View.VISIBLE);
                        }
                    });

                }

            } catch(Exception e) {
                try {
                    e.toString();
                    String classname = this.getClass().getSimpleName();
                    String methodname = new Throwable().getStackTrace()[0].getMethodName();
                    caughtException(classname, methodname, String.valueOf(mediaPlayer), univtoken, "5", e);
                }
                catch (Exception ex)
                {
                    e.getCause();
                }

            }
        }
    };

    public void caughtException(String classname,String methodname,String param,String tokenid,String Severityid,final Throwable ex) {

        try {
            Log.d(TAG, "called for " + ex.getClass());

            String stackTrace = Log.getStackTraceString(ex);
            final JSONObject jsonObject = new JSONObject();

            try {
                Calendar calendar;
                calendar =Calendar.getInstance();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MMM/yyyy hh:mm:ss aa", Locale.US);
                String crash_date_time = simpleDateFormat.format(calendar.getTime());

                jsonObject.put("CrashDateTime",crash_date_time);
                jsonObject.put("TokenId", SharedPreferenceUtil.getStringPreference(HomeActivity.this,Constants.TOKEN_ID));
                jsonObject.put("CrashLog",stackTrace);


               /* jsonObject.put("MethodName",methodname);
                jsonObject.put("ClassName",classname);
                jsonObject.put("ParametersUsed",param);
                jsonObject.put("SeverityId","5");
                jsonObject.put("TokenId", SharedPreferenceUtil.getStringPreference(HomeActivity.this,Constants.TOKEN_ID));
                jsonObject.put("ApplicationId","4");
                jsonObject.put("ErrorMessage",stackTrace);*/
                SharedPreferenceUtil.setStringPreference(HomeActivity.this,Constants.CRASH_MESSAGE,jsonObject.toString());
                sendLastCrashLog();

            }catch (Exception e){
                e.printStackTrace();
                Log.e(TAG, "uncaughtException: "+e.getMessage());
            }



        } catch (Exception e) {
            Log.e(TAG, "Exception Logger failed!", e);
        }
    }


    public long syscurrenttime()
    {

        String timeStamppraystart = new SimpleDateFormat("hh:mm aa", Locale.US).format(Calendar.getInstance().getTime());
        String strDate ="01-Jan-1900";
        String play_Pry_Time = strDate + " " + timeStamppraystart;
        String formated_time = Utilities.changeDateFormatForPrayer(play_Pry_Time);
        long timeStampprayerStart = Utilities.getTimeInMilliSecForPrayer(formated_time);
        return timeStampprayerStart;
    }
    public void playnextSong()
    {
        try {

            String f = arrSongsweb.get(playNextSongIex).getTitle_Url();
            String h = f.substring(f.length() - 3);
            if (h.equals("jpg") || h.equals("jpeg") || h.equals("png")) {
                Handler handler = new Handler(HomeActivity.this.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        y = 0;
                        if (mPreview.isPlaying()) {
                            mPreview.stopPlayback();
                        }

                        String k = arrSongsweb.get(playNextSongIex).getSongPath();
                        portraitmp3layout.setVisibility(View.INVISIBLE);
                        myImage.setVisibility(View.VISIBLE);
                        myImage.setImageURI(Uri.parse(k));
                        mPreview.setVisibility(View.INVISIBLE);
                        circularProgressBar.setVisibility(View.INVISIBLE);
                        txtTokenId.setVisibility(View.INVISIBLE);
                        Imgicon.setVisibility(View.INVISIBLE);
                        txtArtist.setVisibility(View.INVISIBLE);
                        txtSong.setVisibility(View.INVISIBLE);
                        //123videoView.start();
                        if (temploop == 1) {
                            // Utilities.showToast(HomeActivity.this,"Repeat");

                        } else {
                            playNextSongIex = -1;
                        }


                    }
                });

                //  int timeinterval = (arrSongsweb.get(playNextSongIex).gettimeinterval()) * 1000;
                imgCountdowntimer = new CountDownTimer(10000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        // mTaextField.setText("seconds remaining: " + millisUntilFinished / 1000);

                    }

                    public void onFinish() {
                        imgCountdowntimer.cancel();
                        checkimgSuccesive();
                    }
                }.start();

            } else if (h.equals("mp4")) {

                Handler handler = new Handler(HomeActivity.this.getMainLooper());

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if ((y == 0) && (myImage != null)) {
                            myImage.setVisibility(View.INVISIBLE);
                            txtArtist.setVisibility(View.INVISIBLE);
                            txtSong.setVisibility(View.INVISIBLE);
                        }
                        y = 1;
                        mPreview.release();
                        portraitmp3layout.setVisibility(View.INVISIBLE);
                        mPreview.setVisibility(View.VISIBLE);
                        mPreview.playMedia(arrSongsweb.get(playNextSongIex).getSongPath(), vol, vol1);
                        Imgicon.setVisibility(View.INVISIBLE);
                        if (temploop == 1) {
                            //Utilities.showToast(HomeActivity.this,"Repeat");

                        } else {
                            playNextSongIex = -1;
                        }

                    }
                });
            } else {
                Handler handler = new Handler(HomeActivity.this.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (y == 0) {
                            myImage.setVisibility(View.INVISIBLE);
                        }
                        y = 1;
                        mPreview.playMedia(arrSongsweb.get(playNextSongIex).getSongPath(), vol, vol1);
                        mPreview.setVisibility(View.INVISIBLE);
                        circularProgressBar.setVisibility(View.INVISIBLE);
                        txtTokenId.setVisibility(View.INVISIBLE);
                        Imgicon.setVisibility(View.VISIBLE);
                        txtSong.setText(arrSongsweb.get(playNextSongIex).getTitle());
                        txtArtist.setText(arrSongsweb.get(playNextSongIex).getAr_Name());
                        portraitmp3layout.setVisibility(View.VISIBLE);
                        txtArtist.setVisibility(View.VISIBLE);
                        txtSong.setVisibility(View.VISIBLE);
                        if (temploop == 1) {
                            //  Utilities.showToast(HomeActivity.this,"Repeat");
                        } else {
                            playNextSongIex = -1;
                        }

                    }
                });

            }
        }catch (Exception ex)
        {
            try
            {
            String classname=this.getClass().getSimpleName();
            String method=new Exception().getStackTrace()[0].getMethodName();
            caughtException(classname,method,"",univtoken,"5",ex);
            }
            catch (Exception e)
            {
                e.getCause();
            }
        }

    }

    private void checkimgSuccesive()
    {

        try {

            if(imgCountdowntimer!=null) {
                imgCountdowntimer.cancel();
            }
            if(imgCountdowntimer1!=null) {
                imgCountdowntimer1.cancel();
            }
            if(imgCountdowntimer2!=null) {
                imgCountdowntimer2.cancel();
            }
            if(ctrplaylistchg==1)
            {
                return;
            }


            if(playNextSongIex!=-1)
            {
                IsInstantPlaying=true;
                playnextSong();
                return;
            }

         //   playads=true;

            if(y!=0) {
                if (mPreview.isPlaying()) {
                    mPreview.stopPlayback();
                }
            }

            if (arrSongs.size() - 1 > currentlyPlayingSongAtIndex) {
                IsInstantPlaying=false;
                currentlyPlayingSongAtIndex++;
            } else {
                currentlyPlayingSongAtIndex = 0;
            }
            if((arrAdvertisementsSong!=null) && (arrAdvertisementsSong.size()>0)) {
                PlaylistWatcher.PLAY_AD_AFTER_SONGS_COUNTER++;

                if (PlaylistWatcher.PLAY_AD_AFTER_SONGS_COUNTER == PlaylistWatcher.PLAY_AD_AFTER_SONGS) {

                    setVisibilityAndPlayAdvertisement(arrAdvertisementsSong, currentlyPlayingAdAtIndexSong, "Song");

                }
            }
            mPreview.setTag(VIDEO_VIEW_TAG);
            Thread newThread = new Thread(() -> {
                insertsongStatus(currentlyPlayingSongAtIndex);

            });
            newThread.start();
            String f = arrSongs.get(currentlyPlayingSongAtIndex).getTitle_Url();
            String mediatype = arrSongs.get(currentlyPlayingSongAtIndex).getmediatype();
            int reftime = arrSongs.get(currentlyPlayingSongAtIndex).getreftime();

            String h = f.substring(f.length() - 3);


            /*If song played was at last index then restart the playlist.*/

            if(mediatype.equals("Url")) {
                mPreview.setVisibility(View.INVISIBLE);
                txtTokenId.setVisibility(View.INVISIBLE);
                myImage.setVisibility(View.INVISIBLE);
                Imgicon.setVisibility(View.INVISIBLE);
                txtArtist.setVisibility(View.INVISIBLE);
                txtSong.setVisibility(View.INVISIBLE);
                webView.getSettings().setDomStorageEnabled(true);
                circularProgressBar.setVisibility(View.INVISIBLE);
                portraitmp3layout.setVisibility(View.INVISIBLE);
                webView.getSettings().setLoadsImagesAutomatically(true);
               // webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
               // webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
                webView.getSettings().setJavaScriptEnabled(true);
                webView.setVisibility(View.VISIBLE);
                webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
                String geturl=arrSongs.get(currentlyPlayingSongAtIndex).getTitle_Url();
                Uri uri = Uri.parse(geturl);
                String ct = uri.getQueryParameter("ct");
                if ((ct!=null) && (ct.equals("00:00")) )
                {

                    String modurl=geturl.replace("00:00",Utilities.currentTimeHHMM());
                    webView.loadUrl(modurl);
                }
                else {

                    webView.loadUrl(arrSongs.get(currentlyPlayingSongAtIndex).getTitle_Url());
                }
                webView.setWebViewClient(new WebViewClient() {

                    public void onPageFinished(WebView view, String url) {
                        if(url.equals("about:blank")) {

                        }
                        else
                        {
                            mCountDownTimer = new CountDownTimer(500, 500) {

                                public void onTick(long millisUntilFinished) {

                                }

                                public void onFinish() {
                                    webView.setVisibility(View.VISIBLE);
                                    mCountDownTimer.cancel();

                                }
                            }.start();

                        }
                    }
                });
                int timeinterval=(arrSongs.get(currentlyPlayingSongAtIndex).gettimeinterval())*1000;
                imgCountdowntimer2 = new CountDownTimer(timeinterval, 1000) {

                    public void onTick(long millisUntilFinished) {
                     /*   if((timeinterval/1000) > reftime) {
                            long p = (millisUntilFinished / 1000);
                            if (p == reftime) {
                                 webView.reload();
                            }
                        }*/
                        //  txttimer.setText(" " + millisUntilFinished / 1000);
                    }

                    public void onFinish() {
                        webView.loadUrl("about:blank");
                        webView.setVisibility(View.INVISIBLE);
                        imgCountdowntimer2.cancel();
                        // imgCountdowntimer.onFinish();
                        ctrplaylistchg = 0;
                        if(y!=0) {
                            if (mPreview.isPlaying()) {
                                return;
                            }
                        }
                        webView.loadUrl("about:blank");
                        checkimgSuccesive();

                    }
                }.start();
            }

            else if (h.equals("jpg") || h.equals("jpeg") || h.equals("png")) {

                    Handler handler = new Handler(HomeActivity.this.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //123videoView.reset();

                            y = 0;
                         //   Utilities.showToast(HomeActivity.this, "Chkimg");
                            portraitmp3layout.setVisibility(View.INVISIBLE);
                            String k = arrSongs.get(currentlyPlayingSongAtIndex).getSongPath();
                            myImage.setVisibility(View.VISIBLE);
                            Imgicon.setVisibility(View.INVISIBLE);
                            mPreview.setVisibility(View.INVISIBLE);
                            webView.setVisibility(View.INVISIBLE);
                            myImage.setImageURI(Uri.parse(k));
                            //123videoView.setVisibility(View.INVISIBLE);
                            circularProgressBar.setVisibility(View.INVISIBLE);
                            txtTokenId.setVisibility(View.INVISIBLE);
                            txtArtist.setVisibility(View.INVISIBLE);
                            txtSong.setVisibility(View.INVISIBLE);
                            //123videoView.start();

                        }
                    });
                    int timeinterval=(arrSongs.get(currentlyPlayingSongAtIndex).gettimeinterval())*1000;
                    imgCountdowntimer1 = new CountDownTimer(timeinterval, 1000) {

                        public void onTick(long millisUntilFinished) {
                           // txttimer.setText(" " + millisUntilFinished / 1000);
                        }

                        public void onFinish() {
                            imgCountdowntimer1.cancel();
                            ctrplaylistchg=0;
                            checkimgSuccesive();
                        }
                    }.start();

                } else if (h.equals("mp4")) {

                    Handler handler = new Handler(HomeActivity.this.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if ((y == 0) && (myImage!=null)) {
                                myImage.setVisibility(View.INVISIBLE);
                                txtArtist.setVisibility(View.INVISIBLE);
                                txtSong.setVisibility(View.INVISIBLE);
                            }

                            y=1;
                            mPreview.release();
                            Imgicon.setVisibility(View.INVISIBLE);
                            webView.setVisibility(View.INVISIBLE);
                            portraitmp3layout.setVisibility(View.INVISIBLE);
                            mPreview.setVisibility(View.VISIBLE);
                            mPreview.playMedia(arrSongs.get(currentlyPlayingSongAtIndex).getSongPath(),vol,vol1);

                        }
                    });
                } else {
                    Handler handler = new Handler(HomeActivity.this.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (y == 0) {
                                myImage.setVisibility(View.INVISIBLE);

                            }
                            y=1;
                            mPreview.release();
                            mPreview.playMedia(arrSongs.get(currentlyPlayingSongAtIndex).getSongPath(),vol,vol1);
                            mPreview.setVisibility(View.INVISIBLE);
                            webView.setVisibility(View.INVISIBLE);
                            circularProgressBar.setVisibility(View.INVISIBLE);
                            txtTokenId.setVisibility(View.INVISIBLE);
                            portraitmp3layout.setVisibility(View.VISIBLE);
                            txtArtist.setVisibility(View.VISIBLE);
                            txtSong.setVisibility(View.VISIBLE);
                            Imgicon.setVisibility(View.VISIBLE);
                            txtSong.setText(arrSongs.get(currentlyPlayingSongAtIndex).getTitle());
                            txtArtist.setText(arrSongs.get(currentlyPlayingSongAtIndex).getAr_Name());

                        }
                    });

                }

        }
        catch(Exception e) {
            try {
                e.getCause();
                String classname = this.getClass().getSimpleName();
                String method = new Exception().getStackTrace()[0].getMethodName();
                caughtException(classname, method, "", univtoken, "5", e);
            }
            catch(Exception ex)
            {
                e.getCause();
            }


        }


    }


    public void  getUpadteswithoutRestart(int publish)
    {
        try {
            new UpdateWithoutRestart(HomeActivity.this,publish,playlistLoaderListener).getPlaylistsFromServer();
        }catch(Exception ex)
        {
            try {
                String classname = this.getClass().getSimpleName();
                String method = new Exception().getStackTrace()[0].getMethodName();
                caughtException(classname, method, "", univtoken, "5", ex);
            }catch(Exception e)
            {
                e.getCause();
            }
        }

    }

    public void updatesimplemented(final int publish)
    {
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (publish == 1) {
                        ctrupdate = 1;
                        if(arrPlaylists!=null && arrPlaylists.size()>0) {
                            p = arrPlaylists.get(0).getsplPlaylist_Id();
                        }
                        else {
                            p="";
                        }
                        new CountDownTimer(20000, 1000) {
                            public void onTick(long millisUntilFinished) {
                                // mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
                            }

                            public void onFinish() {
                                if ((arrPlaylists != null) && (arrPlaylists.size() > 0)) {
                                    getAdvertisements();
                                    alarm = new PlaylistWatcher();
                                    alarm.setContext(HomeActivity.this);
                                    ArrayList<Advertisements> ads = getAdvertisementsToBeDownloaded();
                                    if((ads != null) && (ads.size() > 0)) {
                                        if (!Utilities.isMyServiceRunning(DownloadService.class, HomeActivity.this)){
                                            startService(new Intent(HomeActivity.this, DownloadService.class).putExtra(Constants.TAG_START_DOWNLOAD_SERVICE,true));
                                            doBindService();
                                        }
                                    }
                                    String h = PlaylistWatcher.currentPlaylistID;
                                    if (!p.equals(h)) {
                                        return;
                                    }
                                    if (p.equals(h)) {
                                        if (y != 0) {
                                            //   Utilities.showToast(HomeActivity.this,"Return1");
                                            if (ctrplaylistchg != 1) {
                                                if (mPreview != null) {
                                                    if (mPreview.isPlaying()) {
                                                        mPreview.stopPlayback();
                                                        mPreview.reset();
                                                        mPreview.clearSurfaceView();
                                                    }
                                                }
                                            }
                                        }

                                        if (imgCountdowntimer != null) {
                                            imgCountdowntimer.cancel();
                                        }
                                        if (imgCountdowntimer1 != null) {
                                            imgCountdowntimer1.cancel();
                                        }
                                        if (imgCountdowntimer2 != null) {
                                            imgCountdowntimer2.cancel();
                                        }
                                        if (y == 0) {
                                            myImage.setVisibility(View.INVISIBLE);
                                            myImage.setImageDrawable(null);
                                        }
                                        ctrplaylistchg = 1;
                                        playlistcounter = 1;
                                        mPreview.setVisibility(View.INVISIBLE);
                                        webView.setVisibility(View.INVISIBLE);
                                        txtTokenId.setVisibility(View.VISIBLE);
                                        getPlaylistsForCurrentTime();
                                        PlaylistWatcher.PLAY_AD_AFTER_SONGS_COUNTER=0;
                                        PlaylistWatcher.ADVERTISEMENT_TIME_COUNTER=0;
                                    }

                                }

                            }

                        }.start();


                    } else {

                        ctrupdate = 1;
                    }
                }

            });
        }catch(Exception ex)
        {
            try {
                String classname = this.getClass().getSimpleName();
                String method = new Exception().getStackTrace()[0].getMethodName();
                caughtException(classname, method, "", univtoken, "5", ex);
            }catch(Exception e)
            {
                e.getCause();
            }
        }


    }


    public void closeAdvertisementtime(String type,String adformat,String ext)
    {
        try {
            Handler mHandler = new Handler(getMainLooper());
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (adformat.equals("1")) {
                        if (vol == 0) {
                            mPreview.setFadeVoume(0.0f, 0.0f);
                        } else {
                            mPreview.setFadeVoume(0.9f, 0.9f);
                        }
                    }
                    ObjectAnimator animation = ObjectAnimator.ofFloat(mPreview, "translationX", 0f);
                    animation.setDuration(2000);
                    animation.start();
                    ObjectAnimator animation1 = ObjectAnimator.ofFloat(mPreview, "translationY", 0f);
                    animation1.setDuration(2000);
                    animation1.start();
                    exttype="";
                    if (vol == 0) {
                        mPreview.setFadeVoume(0.0f, 0.0f);
                    } else {
                        mPreview.setFadeVoume(0.9f, 0.9f);
                    }
                    gblfadevol = "";
                    Squeezectdtimerend = new CountDownTimer(3000, 1000) {

                        public void onTick(long millisUntilFinished) {
                        }

                        public void onFinish() {
                            myImage.setVisibility(View.GONE);
                            if(mPreviewads.isPlaying())
                            {

                            }
                            else
                            {
                                mPreviewads.setVisibility(View.GONE);

                            }
                            if (type.equals("Song")) {

                                PlaylistWatcher.PLAY_AD_AFTER_SONGS_COUNTER = 0;

                            }
                            if (type.equals("Minute")) {
                                PlaylistWatcher.ADVERTISEMENT_TIME_COUNTER = 0;

                            }
                            if (addnotid.equals("1") || addnotid.equals("0")) {
                                txtmarquee.setVisibility(View.VISIBLE);
                            }
                            Squeezectdtimerend.cancel();

                        }
                    }.start();

                }


            });
        }
        catch (Exception e)
        {
            try {
                e.getCause();
                String classname = this.getClass().getSimpleName();
                String method = new Exception().getStackTrace()[0].getMethodName();
                caughtException(classname, method, type, univtoken, "5", e);
            }
            catch (Exception ex)
            {
                ex.getCause();
            }
        }
    }

    private void setVisibilityAndPlayAdvertisement(ArrayList<Advertisements> arrAdvertisements,int gblindex,String type)
    {
        try {
          /*  if(!playads)
            {
                PlaylistWatcher.PLAY_AD_AFTER_SONGS_COUNTER=0;
                PlaylistWatcher.ADVERTISEMENT_TIME_COUNTER=0;
                return;
            }*/

            if(type.equals("Instant"))
            {
                currentlyPlayingAdAtIndex = gblindex;
                PlaylistWatcher.PLAY_AD_AFTER_SONGS_COUNTER = 0;
                PlaylistWatcher.ADVERTISEMENT_TIME_COUNTER = 0;
                String advid=arrAdvertisements.get(currentlyPlayingAdAtIndex).getAdvtID();
                if(titleidmatchads.equals(""))
                {
                    titleidmatchads=advid;

                }
                else
                {
                    if (titleidmatchads.equals(advid))
                    {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(temploopads==0)
                                {
                                    if(mPreviewads.isPlaying()) {
                                        mPreviewads.setVisibility(View.VISIBLE);
                                        mPreviewads.setonloop(false);
                                    }

                                }
                                else
                                {
                                    if(mPreviewads.isPlaying()) {
                                        mPreviewads.setVisibility(View.VISIBLE);
                                        mPreviewads.setonloop(true);
                                    }

                                }
                            }
                        });
                        if(mPreviewads.isPlaying())
                        {
                            return;
                        }
                    }
                    else
                    {
                        titleidmatchads="";
                        if(titleidmatchads.equals(""))
                        {
                            titleidmatchads=advid;

                        }                         //   Utilities.showToast(HomeActivity.this,"return8");

                    }
                }

            }
            else {
                if(mPreviewads.isPlaying())
                {
                    if (type.equals("Song")) {
                        PlaylistWatcher.PLAY_AD_AFTER_SONGS_COUNTER = 0;
                    }
                    if (type.equals("Minute")) {
                        PlaylistWatcher.ADVERTISEMENT_TIME_COUNTER = 0;
                    }
                    return;
                }
                if (addnotid.equals("0") || (addnotid.equals(""))) {
                    txtmarquee.setVisibility(View.GONE);


                }
              //  Utilities.showToast(HomeActivity.this,"Adds");

                currentlyPlayingAdAtIndex = gblindex;

                if (currentlyPlayingAdAtIndex < 0) { // If as is playing for the first time.
                    currentlyPlayingAdAtIndex = 0;

                } else if (currentlyPlayingAdAtIndex == arrAdvertisements.size() - 1) { // If ad playing is at the last index
                    currentlyPlayingAdAtIndex = 0;
                } else { // If ad is between 0 and index of ads array.
                    currentlyPlayingAdAtIndex++;
                }
                if (type.equals("Song")) {
                    currentlyPlayingAdAtIndexSong = currentlyPlayingAdAtIndex;
                } else if (type.equals("Minute")) {
                    currentlyPlayingAdAtIndexMin = currentlyPlayingAdAtIndex;
                } else {
                    currentlyPlayingAdAtIndexTime = currentlyPlayingAdAtIndex;
                }
            }
            insertAdvertisementStatus(arrAdvertisements, currentlyPlayingAdAtIndex);
            String f = arrAdvertisements.get(currentlyPlayingAdAtIndex).getAdvFileUrl();
            String h = f.substring(f.length() - 3);
            exttype=h;
            gbltypy = type;
            // insertAdvertisementStatus(arrAdvertisements,currentlyPlayingAdAtIndex);
            if (h.equals("jpg")) {

                Handler handler = new Handler(HomeActivity.this.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        y = 0;
                        String k = arrAdvertisements.get(currentlyPlayingAdAtIndex).getAdvtFilePath();
                        portraitmp3layout.setVisibility(View.INVISIBLE);
                        myImage.setVisibility(View.VISIBLE);
                        myImage.setImageURI(Uri.parse(k));
                        webView.setVisibility(View.INVISIBLE);
                        circularProgressBar.setVisibility(View.INVISIBLE);
                        txtTokenId.setVisibility(View.INVISIBLE);
                        Imgicon.setVisibility(View.INVISIBLE);
                        txtArtist.setVisibility(View.INVISIBLE);
                        txtSong.setVisibility(View.INVISIBLE);
                        ObjectAnimator animation = ObjectAnimator.ofFloat(mPreview, "translationX", perwidth);
                        animation.setDuration(2000);
                        animation.start();
                        ObjectAnimator animation1 = ObjectAnimator.ofFloat(mPreview, "translationY", -perheight);
                        animation1.setDuration(2000);
                        animation1.start();

                        // animatescale();
                        mPreview.bringToFront();


                    }
                });
                String time = arrAdvertisements.get(currentlyPlayingAdAtIndex).getimagetime();
                int timeinterval = Integer.parseInt(time) * 1000;

                Squeezectdtimer = new CountDownTimer(timeinterval, 1000) {

                    public void onTick(long millisUntilFinished) {

                        //  txttimer.setText(" " + millisUntilFinished / 1000);

                    }


                    public void onFinish() {
                        Squeezectdtimer.cancel();
                        closeAdvertisementtime(type,"other",exttype);

                    }
                }.start();
            } else if (h.equals("mp3")) {
                Handler handler = new Handler(HomeActivity.this.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if ((y == 0) && (myImage != null)) {
                         //   myImage.setVisibility(View.INVISIBLE);
                        }
                        if (mPreviewads.isPlaying()) {
                            mPreviewads.stopPlayback();
                            mPreviewads.reset();
                            mPreviewads.clearSurfaceView();
                            ObjectAnimator animation = ObjectAnimator.ofFloat(mPreview, "translationX", 0f);
                            animation.setDuration(2000);
                            animation.start();
                            ObjectAnimator animation1 = ObjectAnimator.ofFloat(mPreview, "translationY", 0f);
                            animation1.setDuration(2000);
                            animation1.start();
                        }
                        vol=1;
                        vol1=1;
                        //myImage.setVisibility(View.VISIBLE);
                        mPreviewads.playMedia(arrAdvertisements.get(currentlyPlayingAdAtIndex).getAdvtFilePath(), vol, vol1);
                        gblfadevol = "1";
                        fadeINVolume();
                        circularProgressBar.setVisibility(View.INVISIBLE);
                        txtTokenId.setVisibility(View.INVISIBLE);
                        webView.setVisibility(View.INVISIBLE);
                        webView.loadUrl("about:blank");
                        if(type.equals("Instant") && (temploopads==1))
                        {
                            mPreviewads.setonloop(true);
                            // PlaylistWatcher.ADVERTISEMENT_TIME_COUNTER = 0;

                        }
                        if(type.equals("Instant") && (temploopads==0))
                        {
                            mPreviewads.setonloop(false);
                        }
                       // txtSong.setText(arrAdvertisements.get(currentlyPlayingAdAtIndex).getAdvtName());
                      //  portraitmp3layout.setVisibility(View.VISIBLE);
                       // mPreview.setVisibility(View.INVISIBLE);
                        txtArtist.setVisibility(View.INVISIBLE);
                      //  txtSong.setVisibility(View.VISIBLE);
                    }
                });
            } else if (h.equals("mp4")) {
                try {

                    String sound=arrAdvertisements.get(currentlyPlayingAdAtIndex).getSoundType();
                    //   Toast.makeText(HomeActivity.this, "Play add now", Toast.LENGTH_LONG).show();
                    Handler handler = new Handler(HomeActivity.this.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if ((y == 0) && (myImage != null)) {
                                myImage.setVisibility(View.INVISIBLE);
                            }

                            if (mPreviewads.isPlaying()) {
                                mPreviewads.stopPlayback();
                                mPreviewads.reset();
                                mPreviewads.clearSurfaceView();
                                gblfadevol="";
                                if(vol==1)
                                {
                                    vol=1;
                                    vol1=1;
                                    if(mPreview!=null)
                                    {
                                       mPreview.setFadeVoume(1,1);
                                    }
                                }
                            }
                            // mPreview.release();
                            Log.d(TAG, "Here");
                            txtTokenId.setVisibility(View.INVISIBLE);
                            Imgicon.setVisibility(View.INVISIBLE);
                            portraitmp3layout.setVisibility(View.INVISIBLE);
                            mPreviewads.setVisibility(View.VISIBLE);
                            mPreviewads.playMedia(arrAdvertisements.get(currentlyPlayingAdAtIndex).getAdvtFilePath(), vol, vol1);
                            if(type.equals("Instant") && (temploopads==1))
                            {
                                mPreviewads.setonloop(true);
                               // PlaylistWatcher.ADVERTISEMENT_TIME_COUNTER = 0;

                            }
                            if(type.equals("Instant") && (temploopads==0))
                            {
                                mPreviewads.setonloop(false);
                            }
                            txtArtist.setVisibility(View.INVISIBLE);
                            webView.setVisibility(View.INVISIBLE);
                            webView.loadUrl("about:blank");
                            circularProgressBar.setVisibility(View.INVISIBLE);
                            txtSong.setVisibility(View.INVISIBLE);
                            if(vol==1) {
                               if (sound.equals("1")) {
                                   gblfadevol = "1";
                                   fadeINVolume();
                               }
                           }
                            ObjectAnimator animation = ObjectAnimator.ofFloat(mPreview, "translationX", perwidth);
                            animation.setDuration(2000);
                            animation.start();
                            ObjectAnimator animation1 = ObjectAnimator.ofFloat(mPreview, "translationY", -perheight);
                            animation1.setDuration(2000);
                            animation1.start();
                           // videoresizeview(perwidth,-perheight);
                            mPreview.bringToFront();
                            mPreviewads.setVisibility(View.VISIBLE);

                        }
                    });
                } catch (Exception e) {
                    e.getCause().toString();
                    e.getCause();
                }


                //123videoView.setVisibility(View.VISIBLE);
                //123videoView.setVideoPath(arrAdvertisements.get(currentlyPlayingAdAtIndex).getAdvtFilePath());
            } else {
                mPreview.setVisibility(View.INVISIBLE);
                txtTokenId.setVisibility(View.INVISIBLE);
                myImage.setVisibility(View.INVISIBLE);
                Imgicon.setVisibility(View.INVISIBLE);
                txtArtist.setVisibility(View.INVISIBLE);
                txtSong.setVisibility(View.INVISIBLE);
                circularProgressBar.setVisibility(View.INVISIBLE);
                portraitmp3layout.setVisibility(View.INVISIBLE);
                webView.getSettings().setLoadsImagesAutomatically(true);
                webView.getSettings().setJavaScriptEnabled(true);
                webView.getSettings().setDomStorageEnabled(true);
                webView.setVisibility(View.VISIBLE);
                webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
                webView.loadUrl(arrAdvertisements.get(currentlyPlayingAdAtIndex).getAdvFileUrl());
                // int timeinterval=(arrAdvertisements.get(currentlyPlayingAdAtIndex).gettimeinterval())*1000;
                UrlCountdowntimerAd = new CountDownTimer(20000, 1000) {

                    public void onTick(long millisUntilFinished) {

                    }

                    public void onFinish() {
                        UrlCountdowntimerAd.cancel();
                        webView.loadUrl("about:blank");
                        checkimgSuccesive();

                    }
                }.start();


            }
        }catch(Exception ex)
        {
            try {


                String classname = this.getClass().getSimpleName();
                String method = new Exception().getStackTrace()[0].getMethodName();
                caughtException(classname, method, type + gblindex, univtoken, "5", ex);
            }catch (Exception e)
            {
                e.getCause();
            }
        }
    }



    MediaPlayer.OnPreparedListener videoViewPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {


        }


    };

  public void animatescale()
  {
      mPreview.setPivotX(1900);
      mPreview.setPivotY(-50);
      ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(mPreview, "scaleX",0.75f );
      ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(mPreview, "scaleY",0.75f);
      scaleDownX.setDuration(2000);
      scaleDownY.setDuration(2000);
      AnimatorSet scaleDown = new AnimatorSet();
      scaleDown.play(scaleDownX).with(scaleDownY);
      scaleDown.start();


  }

    public void getPlaylistsForCurrentTime()
        {
          try {

              if (arrPlaylists.size() > 0) arrPlaylists.clear();

              ArrayList<Playlist> playlistArrayList = new PlaylistManager(HomeActivity.this, null).getPlaylistForCurrentTimeOnly();

              if ((playlistArrayList.size() > 0) && (playlistArrayList != null)) {
                  // Utilities.showToast(HomeActivity.this,"Playing2410");

                  arrPlaylists.addAll(playlistArrayList);


                /*  AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                  int maxVolume = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                  float percent = 0.95f;
                  int seventyVolume = (int) (maxVolume*percent);
                  audio.setStreamVolume(AudioManager.STREAM_MUSIC, seventyVolume, 0);*/

                  String p = arrPlaylists.get(0).getsplPlaylistCategory();
                  if (p.equals("1")) {
                        Utilities.showToast(HomeActivity.this,"Mute");
                      //AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
                      //audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                      vol = 0;
                      vol1 = 0;


                  } else {

                      String volper = arrPlaylists.get(0).getvolper();
                      if (volper.equals("0")) {
                          vol = 0;
                          vol1 = 0;
                          //Utilities.showToast(HomeActivity.this,"Volume+++>"+vol);

                      } else {
                          if(hsm==null) {
                              AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                              int maxVolume = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                              float percent = Float.parseFloat(volper) / 100;
                              int seventyVolume = (int) (maxVolume * percent);
                              audio.setStreamVolume(AudioManager.STREAM_MUSIC, seventyVolume, 0);
                              // Utilities.showToast(HomeActivity.this,"Volume is"+seventyVolume);

                              vol = 1;
                              vol1 = 1;
                          }
                          else
                          {
                              if(hsm!=null) {
                                getHisenseVolume(volper);
                              }
                              vol=1;
                              vol1=1;

                          }
                      }
                  }
              } else {
                  mPreview.setVisibility(View.INVISIBLE);
                  txtTokenId.setVisibility(View.INVISIBLE);
                  blackLayout.setVisibility(View.VISIBLE);
                  //  Utilities.showToast(HomeActivity.this,"no Playlist");
              }


              if (arrPlaylists.size() > 0) {
                  blackLayout.setVisibility(View.INVISIBLE);
                  mPreview.setVisibility(View.VISIBLE);
                  txtTokenId.setVisibility(View.INVISIBLE);
                  /*If current time has a playlist then get the playlist for future times also*/

    //            playlistAdapter = new PlaylistAdapter(HomeActivity.this, arrPlaylists,false);
    //            lvPlaylist.setAdapter(playlistAdapter);

                  getSongsForPlaylist(arrPlaylists.get(0));

                  //   Utilities.showToast(HomeActivity.this,"Playing2407");
              } else {
                  mPreview.setVisibility(View.INVISIBLE);
                  txtTokenId.setVisibility(View.INVISIBLE);
                  blackLayout.setVisibility(View.VISIBLE);

                  //  Utilities.showToast(HomeActivity.this,"Playing2417");

              }
          }
          catch (Exception ex)
          {
              try {
                  ex.getCause();
                  String classname = this.getClass().getSimpleName();
                  String method = new Exception().getStackTrace()[0].getMethodName();
                  caughtException(classname, method, "", univtoken, "5", ex);
              }catch(Exception e)
              {
                  e.getCause();
              }
          }

    }

    private void fadeINVolume()
    {

        final Handler h = new Handler();
        h.postDelayed(new Runnable() {
            private float time = 4000;
            private Float maxVol=1f;
            private Float redpar=0.15f;
            private int t=0;

            @Override
            public void run() {
                if(PlaylistWatcher.PLAY_AD_AFTER_SONGS_COUNTER>0)
                {
                 //   mPreview.setFadeVoume(0.1f,0.1f);
                  //  return;
                }

                time=time-100;
                maxVol=maxVol-redpar;
                if(maxVol > 0) {

                    if (mPreview != null) {
                        mPreview.setFadeVoume(maxVol, maxVol);
                    }
                }
                else
                {
                    if(mPreview!=null) {
                        if(t==0) {
                            t=1;
                            mPreview.setFadeVoume(0.0f, 0.0f);
                        }

                    }

                }

                if (time > 0)
                    h.postDelayed(this, 300);  // 1 second delay (takes millis)
            }
        }, 100);


    }

    private void getHisenseVolume(String volper)
    {
        int volume = Integer.parseInt(volper);
     //   Utilities.showToast(HomeActivity.this,"Volume+++>"+volume);
        hsm.setMaxVolume(volume);
       // hsm.setCurrentVolumeMin()
    }


    private void getSongsForPlaylist(Playlist playlist){
      try {

          /*If there is not valid playlist set then set the current playlist.*/
          if (AlenkaMedia.currentPlaylistId.equals("")) {
              AlenkaMedia.currentPlaylistId = playlist.getsplPlaylist_Id();

          }

          /*If the AlenkaMedia.currentPlaylistId is not equal to current playing playlist then set
           * the current playlist as AlenkaMedia.currentPlaylistId*/

          if (!AlenkaMedia.currentPlaylistId.equals(playlist.getsplPlaylist_Id())) {
              AlenkaMedia.currentPlaylistId = playlist.getsplPlaylist_Id();

          }


          currentlyPlayingSongAtIndex = 0;

          if (arrSongs.size() > 0) arrSongs.clear();

          String schtype = SharedPreferenceUtil.getStringPreference(HomeActivity.this, AlenkaMediaPreferences.SchType);

          if (schtype.equals("Normal")) {

              songsArrayList = new PlaylistManager(HomeActivity.this, null).getSongsForPlaylist(playlist.getsplPlaylist_Id());
          }
          else {
                //testing
              playlists.clear();
              noRepeat.clear();
              playlists.addAll(arrPlaylists);
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

              for(int i=0; i<noRepeat.size(); i++) {
                  //songsArrayListMerge.clear();
                  songsArrayListMerge = new PlaylistManager(HomeActivity.this, null).getSongsForPlaylistRandom(noRepeat.get(i).getsplPlaylist_Id());
                  songsArrayList.addAll(songsArrayListMerge);
              }

          }


          // ArrayList<Songs> songsArrayList = new PlaylistManager(HomeActivity.this,null).getSongsForPlaylist(playlist.getsplPlaylist_Id());

          if ((songsArrayList != null) && (songsArrayList.size() > 0)) {
              arrSongs.addAll(songsArrayList);

          }

          if (arrSongs.size() > 0) {

              //   Utilities.showToast(HomeActivity.this,"line 2460");

              if (playlist.getIsSeparatinActive() == 1) {
                  sort(arrSongs);

              }

              songAdapter = new SongAdapter(HomeActivity.this, arrSongs);
              lvSongs.setAdapter(songAdapter);
              ArrayList<Songs> songNotDownloaded = new PlaylistManager(HomeActivity.this, null).
                      getSongsThatAreNotDownloaded(playlist.getsplPlaylist_Id());

              if (songNotDownloaded.size() > 0) {

                  if (!Utilities.isMyServiceRunning(DownloadService.class, HomeActivity.this)) {
                      startService(new Intent(HomeActivity.this, DownloadService.class).putExtra(Constants.TAG_START_DOWNLOAD_SERVICE, true));
                      doBindService();
                  }

              }
          } else {

              ArrayList<Songs> songNotDownloaded = new PlaylistManager(HomeActivity.this, null).
                      getSongsThatAreNotDownloaded(playlist.getsplPlaylist_Id());

              if (songNotDownloaded.size() > 0) {

                  if (!Utilities.isMyServiceRunning(DownloadService.class, HomeActivity.this)) {
                      startService(new Intent(HomeActivity.this, DownloadService.class).putExtra(Constants.TAG_START_DOWNLOAD_SERVICE, true));
                      doBindService();
                  }

              } else {
                  mPreview.setVisibility(View.INVISIBLE);
                  //   Utilities.showToast(HomeActivity.this,"Playing2487");

              }
              // If there are no songs to be played then hide the player and show logo.

          }


          if (arrSongs.size() > 0) {
              //123videoView.setVisibility(View.VISIBLE);
              //123videoView.setTag(VIDEO_VIEW_TAG);

              try {
                  String f = arrSongs.get(currentlyPlayingSongAtIndex).getTitle_Url();
                  String mediatype = arrSongs.get(currentlyPlayingSongAtIndex).getmediatype();
                  int reftime = arrSongs.get(currentlyPlayingSongAtIndex).getreftime();

                  String h = f.substring(f.length() - 3);
                  Thread newThread = new Thread(() -> {
                      insertsongStatus(currentlyPlayingSongAtIndex);

                  });
                  newThread.start();
                  if (mediatype.equals("Url")) {
                      mPreview.setVisibility(View.INVISIBLE);
                      txtTokenId.setVisibility(View.INVISIBLE);
                      myImage.setVisibility(View.INVISIBLE);
                      Imgicon.setVisibility(View.INVISIBLE);
                      txtArtist.setVisibility(View.INVISIBLE);
                      txtSong.setVisibility(View.INVISIBLE);
                      circularProgressBar.setVisibility(View.INVISIBLE);
                      portraitmp3layout.setVisibility(View.INVISIBLE);
                      webView.getSettings().setLoadsImagesAutomatically(true);
                      webView.getSettings().setJavaScriptEnabled(true);
                      // webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
                      // webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
                      webView.getSettings().setUseWideViewPort(true);
                      webView.getSettings().setDomStorageEnabled(true);
                      webView.setVisibility(View.VISIBLE);
                      //webView.getSettings().se
                      // tMediaPlaybackRequiresUserGesture(false);
                      // CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
                      //  webView.setWebChromeClient(new WebChromeClient());
                      //  webView.getSettings().setAppCachePath(getApplicationContext().getFilesDir().getAbsolutePath() + "/cache");
                      // webView.getSettings().setAppCacheEnabled(true);
                      //   webView.getSettings().setAllowFileAccess(true);
                      webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
                      String geturl = arrSongs.get(currentlyPlayingSongAtIndex).getTitle_Url();
                      Uri uri = Uri.parse(geturl);
                      String ct = uri.getQueryParameter("ct");
                      if ((ct != null) && (ct.equals("00:00"))) {
                          if (Utilities.isConnected()) {
                              webView.clearCache(true);
                          }
                          String modurl = geturl.replace("00:00", Utilities.currentTimeHHMM());
                          webView.loadUrl(modurl);

                      } else {
                          if (Utilities.isConnected()) {
                              webView.clearCache(true);
                          }
                          webView.loadUrl(arrSongs.get(currentlyPlayingSongAtIndex).getTitle_Url());
                      }
                      webView.setWebViewClient(new WebViewClient() {

                          public void onPageFinished(WebView view, String url) {
                              if (url.equals("about:blank")) {

                              } else {
                                  mCountDownTimer = new CountDownTimer(500, 500) {

                                      public void onTick(long millisUntilFinished) {

                                      }

                                      public void onFinish() {
                                          webView.setVisibility(View.VISIBLE);
                                          mCountDownTimer.cancel();

                                      }
                                  }.start();

                              }
                          }
                      });

                      int timeinterval = (arrSongs.get(currentlyPlayingSongAtIndex).gettimeinterval()) * 1000;
                      imgCountdowntimer2 = new CountDownTimer(timeinterval, 1000) {
                          //  int chkcount=0;

                          public void onTick(long millisUntilFinished) {
                        /*   if((timeinterval > reftime)){
                               chkcount++;
                               if(chkcount==reftime)
                               {
                                   if ((ct!=null) && (ct.equals("00:00")) )
                                   {
                                       webView.loadUrl(arrSongs.get(currentlyPlayingSongAtIndex).getTitle_Url()+Utilities.currentTimeHHMM());

                                   }
                                   else {
                                       webView.loadUrl(arrSongs.get(currentlyPlayingSongAtIndex).getTitle_Url());
                                   }

                                   chkcount=0;
                               }

                           }*/

                              //  txttimer.setText(" " + millisUntilFinished / 1000);
                          }

                          public void onFinish() {
                              webView.loadUrl("about:blank");
                              webView.setVisibility(View.INVISIBLE);
                              imgCountdowntimer2.cancel();
                              // imgCountdowntimer.onFinish();
                              ctrplaylistchg = 0;
                              if (y != 0) {
                                  if (mPreview.isPlaying()) {
                                      return;
                                  }
                              }

                              checkimgSuccesive();

                          }
                      }.start();


                      //return;

                  } else if (h.equals("jpg") || h.equals("jpeg") || h.equals("png")) {
                      Handler handler = new Handler(HomeActivity.this.getMainLooper());
                      handler.post(new Runnable() {
                          @Override
                          public void run() {


                              imgcounter = 1;
                              y = 0;
                              if (y != 0) {
                                  if (mPreview.isPlaying()) {
                                      return;
                                  }
                              }
                              // Utilities.showToast(HomeActivity.this,"Firsttime");

                              String p = arrSongs.get(currentlyPlayingSongAtIndex).getSongPath();

                              if (ctrplaylistchg == 1) {
                                  mPreview.setVisibility(View.INVISIBLE);
                                  mPreview.getSurfaceTexture();
                              } else {
                                  mPreview.setVisibility(View.VISIBLE);
                                  mPreview.getSurfaceTexture();
                              }
                              txtTokenId.setVisibility(View.INVISIBLE);
                              circularProgressBar.setVisibility(View.INVISIBLE);
                              myImage.setVisibility(View.VISIBLE);
                              myImage.setImageURI(Uri.parse(p));
                              webView.setVisibility(View.INVISIBLE);
                              Imgicon.setVisibility(View.INVISIBLE);
                              txtArtist.setVisibility(View.INVISIBLE);
                              txtSong.setVisibility(View.INVISIBLE);
                              portraitmp3layout.setVisibility(View.INVISIBLE);
                              //123videoView.start();

                          }
                      });
                      int timeinterval = (arrSongs.get(currentlyPlayingSongAtIndex).gettimeinterval()) * 1000;
                      imgCountdowntimer2 = new CountDownTimer(timeinterval, 1000) {

                          public void onTick(long millisUntilFinished) {
                              //  txttimer.setText(" " + millisUntilFinished / 1000);
                          }

                          public void onFinish() {
                              imgCountdowntimer2.cancel();
                              // imgCountdowntimer.onFinish();
                              ctrplaylistchg = 0;
                              if (y != 0) {
                                  if (mPreview.isPlaying()) {
                                      return;
                                  }
                              }
                              checkimgSuccesive();

                          }
                      }.start();
                  } else if (h.equals("mp4")) {
                      Handler handler = new Handler(HomeActivity.this.getMainLooper());
                      handler.post(new Runnable() {
                          @Override

                          public void run() {
                              //   Utilities.showToast(HomeActivity.this,"Playing2607");

                              imgcounter = 1;

                              if ((y == 0) && (myImage != null)) {
                                  myImage.setVisibility(View.INVISIBLE);
                              }
                              y = 1;
                              if (ctrplaylistchg != 1) {
                                  if (mPreview.isPlaying()) {
                                      //    Utilities.showToast(HomeActivity.this,"Playing2307");

                                      // Utilities.showToast(HomeActivity.this,"Return");
                                      return;
                                  }
                                  //   Utilities.showToast(HomeActivity.this,"Playing2207");

                                  mPreview.release();
                              }

                              Log.d(TAG, "Here");
                              Imgicon.setVisibility(View.INVISIBLE);
                              portraitmp3layout.setVisibility(View.INVISIBLE);
                              txtArtist.setVisibility(View.INVISIBLE);
                              txtSong.setVisibility(View.INVISIBLE);
                              webView.setVisibility(View.INVISIBLE);
                              circularProgressBar.setVisibility(View.INVISIBLE);
                              txtTokenId.setVisibility(View.INVISIBLE);
                              String path = arrSongs.get(currentlyPlayingSongAtIndex).getSongPath();
                              mPreview.setVisibility(View.VISIBLE);
                              //  Utilities.showToast(HomeActivity.this,"Play Now");
                              mPreview.playMedia(path, vol, vol1);
                              //setvolumeplayer();
                              ctrplaylistchg = 0;
                          }
                      });
                  } else {

                      Handler handler = new Handler(HomeActivity.this.getMainLooper());
                      handler.post(new Runnable() {
                          @Override
                          public void run() {
                              if (y == 0) {
                                  myImage.setVisibility(View.INVISIBLE);
                              }
                              y = 1;
                              if (ctrplaylistchg != 1) {
                                  if (mPreview.isPlaying()) {
                                      //   Utilities.showToast(HomeActivity.this,"Return");
                                      return;
                                  }
                                  mPreview.release();
                              }
                              imgcounter = 1;
                              circularProgressBar.setVisibility(View.INVISIBLE);
                              txtTokenId.setVisibility(View.INVISIBLE);
                              mPreview.setVisibility(View.VISIBLE);
                              Imgicon.setVisibility(View.VISIBLE);
                              webView.setVisibility(View.INVISIBLE);
                              txtSong.setText(arrSongs.get(currentlyPlayingSongAtIndex).getTitle());
                              txtArtist.setText(arrSongs.get(currentlyPlayingSongAtIndex).getAr_Name());
                              portraitmp3layout.setVisibility(View.VISIBLE);
                              String path = arrSongs.get(currentlyPlayingSongAtIndex).getSongPath();
                              mPreview.playMedia(path, vol, vol1);
                              txtArtist.setVisibility(View.VISIBLE);
                              txtSong.setVisibility(View.VISIBLE);
                              ctrplaylistchg = 0;
                          }
                      });

                  }
              } catch (Exception e) {
                  try {
                      e.getCause();
                      String classname = this.getClass().getSimpleName();
                      String method = new Exception().getStackTrace()[0].getMethodName();
                      caughtException(classname, method, "", univtoken, "5", e);
                   //   Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                  }
                  catch (Exception ex)
                  {
                      ex.getCause();
                  }
                  //  sendLastCrashLog();
              }
          } else {
              //123videoView.setVisibility(View.INVISIBLE);
          }
      }catch (Exception ex)
      {
          try {
              String classname = this.getClass().getSimpleName();
              String method = new Exception().getStackTrace()[0].getMethodName();
              caughtException(classname, method, "", univtoken, "5", ex);
          }catch(Exception ex1)
          {
              ex1.getCause();
          }
      }



    }



    @Override
    protected void onStart() {
      try {
          super.onStart();
          sendDatabaseToServer();
          getmarqueetxt();
          PlayerStatusManager playerStatusManager = new PlayerStatusManager(HomeActivity.this);
          playerStatusManager.sendHeartBeatStatusOnServer();
          startServiceViaWorker();
          String inditype = SharedPreferenceUtil.getStringPreference(HomeActivity.this, AlenkaMediaPreferences.Indicatorimg);
          if (inditype.equals("1")) {
              Imgmarker.setVisibility(View.VISIBLE);
          } else {
              Imgmarker.setVisibility(View.INVISIBLE);

          }
          String imgtype = SharedPreferenceUtil.getStringPreference(HomeActivity.this, AlenkaMediaPreferences.Imgtype);

          if (!imgtype.equals("0")) {
              //  downloadimg(imgtype);
          }
          shouldUpdateHeartbeat();
          new GetPublicIP().execute();

      }
      catch (Exception ex)
      {
          try {
              String classname = this.getClass().getSimpleName();
              String method = new Exception().getStackTrace()[0].getMethodName();
              caughtException(classname, method, "", univtoken, "5", ex);
          }
          catch(Exception e)
          {
              e.getCause();
          }
      }


    }


    private void sendDatabaseToServer() {

        new SendDatabaseToServerAsyncTask().execute();
    }

    class SendDatabaseToServerAsyncTask extends AsyncTask<String, Void ,String>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

           // Utilities.showToast(HomeActivity.this,"Start sending database");
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
          //  Utilities.showToast(HomeActivity.this,"Finished sending database");

        }

        @Override
        protected String doInBackground(String... strings) {

            final String error;

            String path = HomeActivity.this.getApplicationInfo().dataDir
                    + File.separator + Constants.ROOT_FOLDER
                    + File.separator + MySQLiteHelper.DATABASE_NAME;

            if (path != null) {

                File dbFile = new File(path);

                if (dbFile.exists()) {

                    MediaType mediaType = MediaType.parse("multipart/form-data;");

                    String token = SharedPreferenceUtil.getStringPreference(HomeActivity.this, Constants.TOKEN_ID);

                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM).addFormDataPart("",token + "-" + dbFile.getName(),RequestBody.create(mediaType, dbFile)                                    )
                            .build();

                   String lcd= "https://api.lcdmedia-audio.com/ReceiveUpload.aspx";
                   String smc="https://applicationaddons.com/ReceiveUpload.aspx";
                    Request request = new Request.Builder()
                            .url(lcd)
                            .post(requestBody)
                            .build();

                    OkHttpClient client =new OkHttpClient();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {

                            String responseString = response.body().toString();

                            if (responseString != null) {

                            }
                            response.close();
                        }
                    });
                }
            }

            return null;
        }


    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG,"Starting timer");
        startRepeatingTimer(null);
        sendLastCrashLog();

        networkChangeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    if (ConnectivityReceiver.isConnected()) {
                        try {
                            String strlcd="https://noti.lcdmedia-audio.com/pushNotification";
                            String strsmc="https://api.applicationaddons.com/pushNotification";

                            SignalRClient sigR = new SignalRClient(strlcd, HomeActivity.this);
                        } catch (Exception e) {
                            e.getCause();
                        }
                        if (!AlenkaMedia.getInstance().isDownloadServiceRunning) {

                            ArrayList<Songs> songs = getSongsToBeDownloaded();
                            ArrayList<Advertisements> ads = getAdvertisementsToBeDownloaded();

                            if (songs != null && songs.size() > 0 ||
                                    ads != null && ads.size() > 0) {

                                HomeActivity.this.startService(new Intent(HomeActivity.this, DownloadService.class).putExtra(Constants.TAG_START_DOWNLOAD_SERVICE, true));
                                doBindService();
                            }
                        }

                    } else {
                        if (AlenkaMedia.getInstance().isDownloadServiceRunning) {
                            HomeActivity.this.stopService(new Intent(HomeActivity.this, DownloadService.class));
                        }
                    }
                }catch (Exception ex)
                {
                    try {
                        String classname = this.getClass().getSimpleName();
                        String method = new Exception().getStackTrace()[0].getMethodName();
                        caughtException(classname, method, "", univtoken, "5", ex);
                    }
                    catch(Exception e)
                    {
                        e.getCause();
                    }
                }

            }
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            registerReceiver(networkChangeReceiver, intentConnectivity,RECEIVER_EXPORTED);
        }
        else {
            registerReceiver(networkChangeReceiver, intentConnectivity);
        }

//        checkForPlaylistStatus.postDelayed(handlePlaylistStatus,delay);
        updatePlayerLoginStatus();

    }

    public String getModel()
    {

        String name=Build.MODEL;
        return name;
    }

    /*
    *TODO: On back press button code if user press back button the application will not destroy or stop
    * */
    @Override

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        try {

            if (Integer.parseInt(String.valueOf(Build.VERSION.SDK_INT)) > 5
                    && keyCode == KeyEvent.KEYCODE_BACK
                    && event.getRepeatCount() == 0) {
                Log.d("CDA", "onKeyDown Called");
                onBackPressed();
                return true;
            }
            return super.onKeyDown(keyCode, event);
        }catch (Exception ex)
        {
            try {
                String classname = this.getClass().getSimpleName();
                String method = new Exception().getStackTrace()[0].getMethodName();
                caughtException(classname, method, String.valueOf(keyCode), univtoken, "5", ex);
            }
            catch (Exception e)
            {
                e.getCause();
            }
        }
        return false;
    }

    @Override
    public void shouldUpdateTimeOnServer() {
       // Utilities.showToast(HomeActivity.this,"Api Hit for Updates");

        try {
            if (Utilities.isConnected()) {
                updatePlayerSongsStatus();
              checkForUpdateData();
            } else {
               // Utilities.showToast(HomeActivity.this, "offline");

            }
        }catch (Exception ex)

        {
            try {
                ex.getCause();
                String classname = this.getClass().getSimpleName();
                String method = new Exception().getStackTrace()[0].getMethodName();
                caughtException(classname, method, "", univtoken, "5", ex);
            }
            catch(Exception e)
            {
                e.getCause();
            }
        }

    }



    @Override
    public void checkForPendingDownloads() {
      checkForUnfinishedDownloads();
    }




    @Override
    public void playAdvertisement(ArrayList<Advertisements> arrAdvertisements,String type) {
        try {

            // int p=arrAdvertisements.size();
            if ((arrAdvertisements == null) || (arrAdvertisements.size() == 0)) {
                // Utilities.showToast(HomeActivity.this,"No ads");
                PlaylistWatcher.ADVERTISEMENT_TIME_COUNTER = 0;
                PlaylistWatcher.PLAY_AD_AFTER_SONGS_COUNTER = 0;
                return;
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (arrAdvertisements.size() > 0){

                        if(type.equals("1")) {
                            //  Utilities.showToast(HomeActivity.this,"Playing");

                            setVisibilityAndPlayAdvertisement(arrAdvertisements, currentlyPlayingAdAtIndexMin, "Minute");
                        }
                        else
                        {
                            if(type.equals("3"))
                            {
                                setVisibilityAndPlayAdvertisement(arrAdvertisements, currentlyPlayingAdAtIndexTime,"FixedTime");
                            }
                        }
                    }
                }
            });


        }catch (Exception ex)
        {
            try {
                String classname = this.getClass().getSimpleName();
                String method = new Exception().getStackTrace()[0].getMethodName();
                caughtException(classname, method, type, univtoken, "5", ex);
            }
            catch(Exception e)
            {
                e.getCause();
            }
        }

    }


    public void videoresizeview(float perwidth,float perheight)
    {
        if(mPreview!=null)
        {
            int parw=(int) Math.floor(perwidth);
            int parh=(int) Math.floor(perheight);
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int widthPixels = displayMetrics.widthPixels;
            int heightPixels = displayMetrics.heightPixels;
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mPreview.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            layoutParams.width=1000;
            layoutParams.height=500;
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            mPreview.setLayoutParams(layoutParams);

            //mPreview.invalidate();
            //mPreview.refreshDrawableState();

        }
    }


    public void clearmemory()
    {
        if(imgCountdowntimer!=null) {
            imgCountdowntimer.cancel();
        }
        if(imgCountdowntimer1!=null) {
            imgCountdowntimer1.cancel();
        }
        if(imgCountdowntimer2!=null) {
            imgCountdowntimer2.cancel();
        }
        if(y!=0) {
            if (mPreview.isPlaying()) {
                mPreview.stopPlayback();
            }
        }
        mPreview.release();
        System.exit(2);
        return;
    }

    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();

            if(imgCountdowntimer!=null) {
                imgCountdowntimer.cancel();
            }
            if(imgCountdowntimer1!=null) {
                imgCountdowntimer1.cancel();
            }
            if(imgCountdowntimer2!=null) {
                imgCountdowntimer2.cancel();
            }
            PlayerStatusManager playerStatusManager = new PlayerStatusManager(HomeActivity.this);
            playerStatusManager.sendHeartBeatStatusOnServer();

            PlaylistWatcher.PLAY_AD_AFTER_SONGS_COUNTER = 0;
           // DownloadService.downloadingFileAtIndex=0;
            mPreview.release();
                    // mPreview.setSurfaceTexture(null);

            /*123if (videoView.isPlaying()){
                //123videoView.stopPlayback();
                if(mDownloadService!=null)
               {
                    mDownloadService.downloadid();
                }
            }*/

            updateLogoutStatus();
            System.exit(2);
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);

    }

    Runnable handlePlaylistStatus = new Runnable() {
        @Override
        public void run() {

            if (currentPlaylistStatus != AlenkaMedia.playlistStatus){
                currentPlaylistStatus = AlenkaMedia.playlistStatus;
                onPlaylistTimeChanged(currentPlaylistStatus);
            }

//            checkForPlaylistStatus.postDelayed(this, delay);
        }
    };

    @Override
    protected void onStop() {
        try {

            super.onStop();
            Log.e(TAG, "Stopping timer");
            cancelRepeatingTimer(null);
            stopService(new Intent(HomeActivity.this, DownloadService.class));
            PlayerStatusManager playerStatusManager = new PlayerStatusManager(HomeActivity.this);
            playerStatusManager.sendHeartBeatStatusOnServer();
        }
        catch (Exception ex)
        {
            try {
                String classname = this.getClass().getSimpleName();
                String method = new Exception().getStackTrace()[0].getMethodName();
                caughtException(classname, method, "", univtoken, "5", ex);
            }
            catch(Exception e)
            {
                e.getCause();
            }
        }


//        checkForPlaylistStatus.removeCallbacks(handlePlaylistStatus);
    }

    @Override
    protected void onDestroy() {
        try {
            super.onDestroy();
            doUnbindService();
            stopService(new Intent(HomeActivity.this, DownloadService.class));
            if(hsm!=null)
            {
               hsm=null;
                hsm.onDestory();
            }
            PlayerStatusManager playerStatusManager = new PlayerStatusManager(HomeActivity.this);
            playerStatusManager.sendHeartBeatStatusOnServer();
        }
        catch (Exception ex)
        {
            try {
                String classname = this.getClass().getSimpleName();
                String method = new Exception().getStackTrace()[0].getMethodName();
                caughtException(classname, method, "", univtoken, "5", ex);
            }
            catch(Exception e)
            {
                e.getCause();
            }
        }


    }

    @Override
    protected void onPause() {
        try {

            //unregisterReceiver(broadcastReceiver);
            unregisterReceiver(networkChangeReceiver);
            super.onPause();
        }
        catch(Exception ex)
        {
            try {
                String classname = this.getClass().getSimpleName();
                String method = new Exception().getStackTrace()[0].getMethodName();
                caughtException(classname, method, "", univtoken, "5", ex);
            }
            catch(Exception e)
            {
                e.getCause();
            }
        }

    }

    /*************************PlaylistWatcher Methods Starts****************************/

    public void startRepeatingTimer(View view) {
        Context context = HomeActivity.this;
        if(alarm != null){
            alarm.setWatcher();
        }else{
//            Toast.makeText(context, "Alarm is null", Toast.LENGTH_SHORT).show();
        }
    }

    public void cancelRepeatingTimer(View view){
        Context context = this.getApplicationContext();
        if(alarm != null){
            alarm.cancelWatcher();
        }else{
//            Toast.makeText(context, "Alarm is null", Toast.LENGTH_SHORT).show();
        }
    }

    public void onPlaylistTimeChanged(int playlistCode) {

        switch (playlistCode){
            case PlaylistWatcher.NO_PLAYLIST:{

                /*123if (videoView.isPlaying()){
                    //123videoView.stopPlayback();
                    //123videoView.setVisibility(View.GONE);
                }*/
            }break;

            case PlaylistWatcher.PLAYLIST_PRESENT:{
              //  getPlaylistsForCurrentTime();
            }break;
        }
    }



    /*This method inserts the status of song as played in database.*/

    public void insertsongStatus(final int index){
        try {

            String artist_id = arrSongs.get(index).getArtist_ID();
            String title_id = arrSongs.get(index).getTitle_Id();
            String spl_plalist_id = arrSongs.get(index).getSpl_PlaylistId();

            PlayerStatusManager playerStatusManager = new PlayerStatusManager(HomeActivity.this);
            playerStatusManager.artist_id = artist_id;
            playerStatusManager.title_id = title_id;
            playerStatusManager.spl_plalist_id = spl_plalist_id;
            playerStatusManager.insertSongPlayedStatus();


        }catch(Exception ex)
        {
            try {
                String classname = this.getClass().getSimpleName();
                String method = new Exception().getStackTrace()[0].getMethodName();
                caughtException(classname, method, String.valueOf(index), univtoken, "5", ex);
            }
            catch (Exception e)
            {
                e.getCause();
            }
        }
    }

    public static class GetPublicIP extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... strings) {
            String publicIP = "";
            try  {
                java.util.Scanner s = new java.util.Scanner(
                        new java.net.URL(
                                "https://api.ipify.org")
                                .openStream(), "UTF-8")
                        .useDelimiter("\\A");
                publicIP = s.next();
                System.out.println("My current IP address is " + publicIP);
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }

            return publicIP;
        }

        @Override
        protected void onPostExecute(String publicIp) {
            super.onPostExecute(publicIp);
            PlayerStatusManager psm=new PlayerStatusManager(HomeActivity.getInstance());
            psm.getPublicIp(publicIp);
            Log.e("PublicIP", publicIp+"");

            int totalSongs = new PlaylistManager(HomeActivity.getInstance(), null).getTotalDownloadedSongs();
            if(totalSongs>0)
            {
                psm.songsDownloaded=" "+totalSongs;
                psm.updateDownloadedSongsCountOnServer();
            }


        }


    }

    public void insertAdvertisementStatus(ArrayList<Advertisements> arrAdvertisements,final int index){
        try {

            String currentDate = Utilities.currentDate();
            String currenttime = Utilities.currentTime();

            PlayerStatus playerStatus = new PlayerStatus();
            playerStatus.setAdvPlayedDate(currentDate);
            playerStatus.setAdvPlayedTime(currenttime);
            playerStatus.setAdvIdStatus(arrAdvertisements.get(index).getAdvtID());
            playerStatus.setPlayerStatusAll("adv");

            PlayerStatusManager playerStatusManager = new PlayerStatusManager(HomeActivity.this);
            playerStatusManager.insertAdvPlayerStatus(playerStatus);
        }catch(Exception ex)
        {
            try {
                String classname = this.getClass().getSimpleName();
                String method = new Exception().getStackTrace()[0].getMethodName();
                caughtException(classname, method, String.valueOf(index), univtoken, "5", ex);
            }
            catch(Exception e)
            {
                e.getCause();
            }
        }

    }


    private void updatePlayerLoginStatus(){
        try {

            PlayerStatusManager playerStatusManager = new PlayerStatusManager(HomeActivity.this);
            playerStatusManager.updateLoginStatus();
            //playerStatusManager.updateHeartBeatStatus();
            playerStatusManager.updateDataOnServer();
        }catch (Exception ex)
        {
            try {
                String classname = this.getClass().getSimpleName();
                String method = new Exception().getStackTrace()[0].getMethodName();
                caughtException(classname, method, "", univtoken, "5", ex);
            }
            catch(Exception e)
            {
                e.getCause();
            }
        }

    }

    private void updatePlayerSongsStatus(){
        try {

            PlayerStatusManager playerStatusManager = new PlayerStatusManager(HomeActivity.this);
            playerStatusManager.sendPlayedSongsStatusOnServer();
        }catch(Exception ex)
        {
            try {
                String classname = this.getClass().getSimpleName();
                String method = new Exception().getStackTrace()[0].getMethodName();
                caughtException(classname, method, "", univtoken, "5", ex);
            }
            catch(Exception e)
            {
                e.getCause();
            }
        }
    }

    private void updateLogoutStatus(){
        try {

            PlayerStatusManager playerStatusManager = new PlayerStatusManager(HomeActivity.this);
            playerStatusManager.updateLogoutStatus();
        }catch(Exception ex)
        {
            try {
                String classname = this.getClass().getSimpleName();
                String method = new Exception().getStackTrace()[0].getMethodName();
                caughtException(classname, method, "", univtoken, "5", ex);
            }
            catch(Exception e)
            {
                e.getCause();
            }
        }

    }

    @Override
    public void onResponse(String response, int tag) {
        if (response == null || response.equals("") || response.length() < 1){
            return;
        }
        switch(tag) {

            case Constants.RSS_DETAILS_TAG: {
                handlemarqueesettxt(response);
            }
            break;
        }

    }

    @Override
    public void onError(Exception e, int tag) {

    }


    public static void saveLogcatToFile(Context context) {

        String fileName = "logcat_"+System.currentTimeMillis()+".txt";
        File outputFile = new File(Environment.getExternalStorageDirectory(),fileName);
        try {
            @SuppressWarnings("unused")
            Process process = Runtime.getRuntime().exec("logcat -e "+outputFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPlaylistStatusChanged(int status) {
        try {
            if(IsInstantPlaying==true)
            {
                return;
            }
            if ((arrPlaylists != null) && (arrPlaylists.size() > 0)) {
                String p = arrPlaylists.get(0).getsplPlaylist_Id();
                String h = PlaylistWatcher.currentPlaylistID;
                if (h.equals("")) {
                    ArrayList<Playlist> playlistArrayList = new PlaylistManager(HomeActivity.this, null).getPlaylistForCurrentTimeOnly();
                    if ((playlistArrayList != null) && (playlistArrayList.size() > 0)) {
                        // Utilities.showToast(HomeActivity.this,"Return");
                        return;
                    }
                }
                if (p.equals(h)) {
                    return;
                }
            }

            switch (status) {

                case PlaylistWatcher.NO_PLAYLIST: {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (y != 0) {
                                //   Utilities.showToast(HomeActivity.this,"Return1");
                                if (ctrplaylistchg != 1) {
                                    if (mPreview != null) {
                                        if (mPreview.isPlaying()) {
                                            mPreview.stopPlayback();
                                            mPreview.reset();
                                            mPreview.clearSurfaceView();
                                        }
                                    }
                                }
                            }

                            if (imgCountdowntimer != null) {
                                imgCountdowntimer.cancel();
                            }
                            if (imgCountdowntimer1 != null) {
                                imgCountdowntimer1.cancel();
                            }
                            if (imgCountdowntimer2 != null) {
                                imgCountdowntimer2.cancel();
                            }
                            if (y == 0) {
                                myImage.setVisibility(View.INVISIBLE);
                                myImage.setImageDrawable(null);
                            }
                            ctrplaylistchg = 1;
                            playlistcounter = 1;
                            mPreview.setVisibility(View.INVISIBLE);
                            txtTokenId.setVisibility(View.INVISIBLE);
                            blackLayout.setVisibility(View.VISIBLE);
                            webView.setVisibility(View.INVISIBLE);
                            getPlaylistsForCurrentTime();
                            //  Utilities.showToast(HomeActivity.this,"No playlist");
                            // hzProgressBar.setVisibility(View.VISIBLE);
                            //txtwaiting.setVisibility(View.VISIBLE);
                            //waitImgicon.setVisibility(View.VISIBLE);
                            //hzProgressBar.setIndeterminateTintList(ColorStateList.valueOf(Color.WHITE));
                        }
                    });

                }
                break;

                case PlaylistWatcher.PLAYLIST_PRESENT: {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //  Utilities.showToast(HomeActivity.this,"Return1");
                            if (imgCountdowntimer != null) {
                                imgCountdowntimer.cancel();
                            }
                            if (imgCountdowntimer1 != null) {
                                imgCountdowntimer1.cancel();
                            }
                            if (imgCountdowntimer2 != null) {
                                imgCountdowntimer2.cancel();
                            }
                            playlistcounter = 0;
                            PlaylistWatcher.PLAY_AD_AFTER_SONGS_COUNTER = 0;
                            ctrplaylistchg = 1;
                            //  Utilities.showToast(HomeActivity.this,"Playlist Present");
                            getPlaylistsForCurrentTime();
                            txtwaiting.setVisibility(View.GONE);
                            hzProgressBar.setVisibility(View.GONE);
                            waitImgicon.setVisibility(View.GONE);
                        }
                    });

                }
                break;

                case PlaylistWatcher.PLAYLIST_CHANGE: {
                    //   Utilities.showToast(HomeActivity.this,"chg");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (arrPlaylistsstatus.size() > 0) arrPlaylistsstatus.clear();
                            ArrayList<Playlist> playlistArrayList = new PlaylistManager(HomeActivity.this, null).getPlaylistForCurrentTimeOnly();

                            if ((playlistArrayList.size() > 0) && (playlistArrayList != null)) {
                                arrPlaylistsstatus.addAll(playlistArrayList);

                            }
                            songsArrayListStatus = new PlaylistManager(HomeActivity.this, null).getSongsForPlaylist(arrPlaylistsstatus.get(0).getsplPlaylist_Id());
                            if ((songsArrayListStatus != null) && (songsArrayListStatus.size() > 0)) {
                                if (y != 0) {
                                    if (mPreview.isPlaying()) {
                                        mPreview.stopPlayback();
                                        mPreview.reset();
                                        mPreview.clearSurfaceView() ;
                                    }
                                }
                                if (imgCountdowntimer != null) {
                                    imgCountdowntimer.cancel();
                                }
                                if (imgCountdowntimer1 != null) {
                                    imgCountdowntimer1.cancel();
                                }
                                if (imgCountdowntimer2 != null) {
                                    imgCountdowntimer2.cancel();
                                }
                                playlistcounter = 0;
                                PlaylistWatcher.PLAY_AD_AFTER_SONGS_COUNTER = 0;
                                if (y == 0) {
                                    myImage.setVisibility(View.INVISIBLE);
                                    myImage.setImageDrawable(null);
                                }
                                ctrplaylistchg = 1;
                                // Utilities.showToast(HomeActivity.this,"Playlistchg");
                                txtwaiting.setVisibility(View.GONE);
                                hzProgressBar.setVisibility(View.GONE);
                                webView.setVisibility(View.INVISIBLE);
                                getPlaylistsForCurrentTime();
                            } else {
                                if (!Utilities.isMyServiceRunning(DownloadService.class, HomeActivity.this)) {
                                    startService(new Intent(HomeActivity.this, DownloadService.class).putExtra(Constants.TAG_START_DOWNLOAD_SERVICE, true));
                                    doBindService();
                                }
                            }
                        }
                    });


                }
                break;
            }
        }catch (Exception ex)
        {
            try {
                String classname = this.getClass().getSimpleName();
                String method = new Exception().getStackTrace()[0].getMethodName();
                caughtException(classname, method, String.valueOf(status), univtoken, "5", ex);
            }
            catch(Exception e)
            {
                e.getCause();
            }
        }

    }

    private void sort(ArrayList<Songs> songsArrayList){

        try {
            Collections.shuffle(songsArrayList);

        } catch (Exception e){
            try {
                Log.e("Sort exception", "");
                e.printStackTrace();
                String classname = this.getClass().getSimpleName();
                String method = new Exception().getStackTrace()[0].getMethodName();
                caughtException(classname, method, String.valueOf(songsArrayList), univtoken, "5", e);
            }
            catch(Exception ex)
            {
                ex.getCause();
            }
        }
    }
    /*************************PlaylistWatcher Methods Ends*/

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void scanForFiles(){

        String path = HomeActivity.this.getApplicationInfo().dataDir;

        File file = new File(path);

     /*   Map<String, File> externalLocations = ExternalStorage.getAllStorageLocations();
        File sdCard = externalLocations.get(ExternalStorage.SD_CARD);

        if (sdCard != null){
            sdCardLocation = sdCard.getAbsolutePath();
        }
        File externalSdCard = externalLocations.get(ExternalStorage.EXTERNAL_SD_CARD);
*/
       /* HashSet<String> externalLocations = ExternalStorage.getExternalMounts();

        if (externalLocations.size() > 0){

            Object[] aa = externalLocations.toArray();
            sdCardLocation = (String) aa[0];
        }*/

        List<StorageUtils.StorageInfo> storageInfoList = StorageUtils.getStorageList();

       File[] files = HomeActivity.this.getExternalMediaDirs();

       String[] sdsd = files[1].getAbsolutePath().split("/");

        if (sdsd.length > 3){
            String zeroComponent = sdsd[0];

            if (zeroComponent.equals(" ") || zeroComponent.equals("")){
                zeroComponent = "/";
            }
            String firstComponent = sdsd[1];
            String secondComponent = sdsd[2];

            String finalPath = zeroComponent + firstComponent + File.separator + secondComponent;

            sdCardLocation = finalPath;
        }

       /* if (files.length > 1){

            String storage = files[1].getAbsolutePath();
            sdCardLocation = storage;
        }*/

        if (arrVideoFiles.size() > 0){

            targetFileName = arrVideoFiles.get(13);

//            targetFileName = firstVideoFileLocation.substring(firstVideoFileLocation.lastIndexOf("/")+1);

            try {

                startActivityForResult(new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE), 42);

               /* File target = new File(sdCardLocation.concat(File.separator + targetFileName));
                File source = new File(firstVideoFileLocation);
                copyDirectory(source,target);*/

            }catch (Exception e){
                Log.e("File Copy", "Failed");
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        try {

            if (resultCode != RESULT_OK)
                return;

            if (requestCode == REQUEST_CODE_STORAGE_FOLDER_SELECTOR) {

                Uri treeUri = resultData.getData();


                String path = FileUtil.getFullPathFromTreeUri(treeUri, HomeActivity.this);

                if (path != null) {

                    File sourceDirectory = new File(path);

                    if (sourceDirectory != null) {

                        //  Toast.makeText(HomeActivity.this, "" + path, Toast.LENGTH_SHORT).show();

                        DocumentFile pickedDir = DocumentFile.fromTreeUri(this, treeUri);

                        AlenkaMedia.globalDocumentFile = sourceDirectory;

                        String pickedD = pickedDir.toString();

                        if (!AlenkaMedia.getInstance().isDownloadServiceRunning) {

                            startService(new Intent(this, DownloadService.class).putExtra(Constants.TAG_START_DOWNLOAD_SERVICE, treeUri.toString()));
                            doBindService();
                        }
                    }
                }


            }
        }catch (Exception ex)
        {
            try {
                String classname = this.getClass().getSimpleName();
                String method = new Exception().getStackTrace()[0].getMethodName();
                caughtException(classname, method, String.valueOf(requestCode), univtoken, "5", ex);
            }
            catch(Exception e)
            {
                e.getCause();
            }
        }
    }

    void listRecursive(File fileOrDirectory) {

        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                listRecursive(child);

        Log.e("List Files: ", fileOrDirectory.getName());

        if (fileOrDirectory.getName().endsWith(".mp4")) {

            arrVideoFiles.add(fileOrDirectory.getAbsolutePath());
        }
    }

    private void updateDownloadedSongsStatusOnServer(){
        try {

            int totalSongs = new PlaylistManager(HomeActivity.this, null).getTotalDownloadedSongs();

            if (totalSongs >= 0) {

                PlayerStatusManager playerStatusManager = new PlayerStatusManager(HomeActivity.this);
                playerStatusManager.songsDownloaded = "" + totalSongs;
                playerStatusManager.updateDownloadedSongsCountOnServer();
            }
        }catch (Exception ex)
        {
            try {
                String classname = this.getClass().getSimpleName();
                String method = new Exception().getStackTrace()[0].getMethodName();
                caughtException(classname, method, "", univtoken, "5", ex);
            }catch (Exception e)
            {
                e.getCause();
            }
        }

        /*if (countOfTotalSongsToBeDownloaded > 0){

            if (getSongsToBeDownloaded() != null){

                int songsThatHaveBeenDownloaded =  countOfTotalSongsToBeDownloaded - getSongsToBeDownloaded().size();

                if (songsThatHaveBeenDownloaded > 0){

                    PlayerStatusManager playerStatusManager = new PlayerStatusManager(HomeActivity.this);
                    playerStatusManager.songsDownloaded = "" + songsThatHaveBeenDownloaded;
                    playerStatusManager.updateDownloadedSongsCountOnServer();
                }
            }
        }*/
    }




    public void playkeyeventSong(int index)

    {
        try {
            if (index == 0) {
                univKeyCode = "";
                keyct = 0;
                return;
            }
            if (y != 0) {
                if (mPreview.isPlaying()) {
                    mPreview.stopPlayback();
                }
            }
            getdataforSongEvent(index);
        }
        catch (Exception ex)
        {
            try {
                String classname = this.getClass().getSimpleName();
                String method = new Exception().getStackTrace()[0].getMethodName();
                caughtException(classname, method, String.valueOf(index), univtoken, "5", ex);
            }
            catch(Exception e)
            {
                e.getCause();
            }
        }

    }

    public void startServiceViaWorker() {
        try {
            Log.d(TAG, "startServiceViaWorker called");
            String UNIQUE_WORK_NAME = "StartMyServiceViaWorker";
            WorkManager workManager = WorkManager.getInstance(this);

            // As per Documentation: The minimum repeat interval that can be defined is 15 minutes
            // (same as the JobScheduler API), but in practice 15 doesn't work. Using 16 here
            PeriodicWorkRequest request =
                    new PeriodicWorkRequest.Builder(
                            MyWorker.class,
                            16,
                            TimeUnit.MINUTES)
                            .build();

            // to schedule a unique work, no matter how many times app is opened i.e. startServiceViaWorker gets called
            // do check for AutoStart permission
            workManager.enqueueUniquePeriodicWork(UNIQUE_WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, request);
        }
        catch (Exception ex)
        {
            try {
                String classname = this.getClass().getSimpleName();
                String method = new Exception().getStackTrace()[0].getMethodName();
                caughtException(classname, method, "", univtoken, "5", ex);
            }
            catch(Exception e)
            {
                e.getCause();
            }
        }

    }

    public void shouldUpdateHeartbeat() {
        try {
            PlayerStatusManager playerStatusManager = new PlayerStatusManager(HomeActivity.this);
            playerStatusManager.getCountHeartBeat();

        }catch(Exception e)
        {
            e.getCause();
        }

    }

    public void checkDifferenceinDays(long timedismiss,int days)
    {

        String date=SharedPreferenceUtil.getStringPreference(HomeActivity.this, AlenkaMediaPreferences.HeartDate);
        if((date==null) || (date.equalsIgnoreCase("")))
        {
            return;
        }
        else
        {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MMM/yyyy hh:mm:ss aa", Locale.US);
            Date datecal = null;
            try {
                datecal = simpleDateFormat.parse(date);
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }

            if (datecal != null){

                long differenceInDays = Utilities.getDifferenceBetweenTwoDatesInDays(new Date(),datecal);

                if (differenceInDays >= days){
                    SharedPreferenceUtil.setStringPreference(HomeActivity.this, AlenkaMediaPreferences.HeartDate,"shutdown");

                } else {
                    if(differenceInDays>=1) {
                        showDialogalert(timedismiss);
                    }

                }
            }

        }


    }

    public void showDialogalert(long timedismiss)
    {
        try {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HomeActivity.this);
            alertDialogBuilder.setTitle(getResources().getString(R.string.app_name));
            alertDialogBuilder
                    .setMessage("Please update software")
                    .setCancelable(false)
                    .setIcon(R.drawable.smcappicon)
                    .setNegativeButton("Wait",null)
                    .setPositiveButton("InternetConnection", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                            int id) {
                               // dialog.dismiss();

                            }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(final DialogInterface dialog) {
                    final Button defaultButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                    final CharSequence negativeButtonText = defaultButton.getText();
                    new CountDownTimer(timedismiss*1000, 100) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            defaultButton.setText(String.format(
                                    Locale.getDefault(), "%s (%d)",
                                    negativeButtonText,
                                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) + 1 //add one so it never displays zero
                            ));
                        }
                        @Override
                        public void onFinish() {
                            if (alertDialog.isShowing()) {
                                alertDialog.dismiss();
                            }
                        }
                    }.start();
                }
            });

            alertDialog.show();

        }
        catch(Exception e)
        {
            e.getCause();
        }

    }

    public void getdataforSongEvent(int index)
    {
        try {

            if (imgCountdowntimer != null) {
                imgCountdowntimer.cancel();
            }
            if (imgCountdowntimer1 != null) {
                imgCountdowntimer1.cancel();
            }
            if (imgCountdowntimer2 != null) {
                imgCountdowntimer2.cancel();
            }
            String f = arrSongs.get(index - 1).getTitle_Url();
            String h = f.substring(f.length() - 3);
            if (h.equals("mp3")) {
                Handler handler = new Handler(HomeActivity.this.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if ((y == 0) && (myImage != null)) {
                            myImage.setVisibility(View.INVISIBLE);
                        }
                        y = 1;
                        mPreview.release();
                        myImage.setVisibility(View.INVISIBLE);
                        mPreview.playMedia(arrSongs.get(index - 1).getSongPath(), vol, vol1);
                        circularProgressBar.setVisibility(View.INVISIBLE);
                        txtTokenId.setVisibility(View.INVISIBLE);
                        Imgicon.setVisibility(View.VISIBLE);
                        txtSong.setText(arrSongs.get(index - 1).getTitle());
                        txtArtist.setText(arrSongs.get(index - 1).getAr_Name());
                        portraitmp3layout.setVisibility(View.VISIBLE);
                        mPreview.setVisibility(View.INVISIBLE);
                        txtArtist.setVisibility(View.VISIBLE);
                        txtSong.setVisibility(View.VISIBLE);
                        univKeyCode = "";
                        keyct = 0;


                    }
                });
            } else if (h.equals("jpg") || h.equals("jpeg") || h.equals("png")) {

                Handler handler = new Handler(HomeActivity.this.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //123videoView.reset();

                        y = 0;
                        mPreview.release();
                        mPreview.setVisibility(View.INVISIBLE);
                        portraitmp3layout.setVisibility(View.INVISIBLE);
                        String k = arrSongs.get(index - 1).getSongPath();
                        myImage.setVisibility(View.VISIBLE);
                        Imgicon.setVisibility(View.INVISIBLE);
                        myImage.setImageURI(Uri.parse(k));
                        circularProgressBar.setVisibility(View.INVISIBLE);
                        txtTokenId.setVisibility(View.INVISIBLE);
                        txtArtist.setVisibility(View.INVISIBLE);
                        txtSong.setVisibility(View.INVISIBLE);
                        univKeyCode = "";
                        keyct = 0;

                    }
                });
                int timeinterval = (arrSongs.get(index - 1).gettimeinterval()) * 1000;
                imgCountdowntimer1 = new CountDownTimer(timeinterval, 1000) {

                    public void onTick(long millisUntilFinished) {
                        // mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
                    }

                    public void onFinish() {
                        imgCountdowntimer1.cancel();
                        ctrplaylistchg = 0;
                        checkimgSuccesive();

                    }
                }.start();

            } else {
                try {
                    //   Toast.makeText(HomeActivity.this, "Play add now", Toast.LENGTH_LONG).show();
                    Handler handler = new Handler(HomeActivity.this.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if ((y == 0) && (myImage != null)) {
                                myImage.setVisibility(View.INVISIBLE);
                            }

                            mPreview.release();
                            Log.d(TAG, "Here");
                            txtTokenId.setVisibility(View.INVISIBLE);
                            Imgicon.setVisibility(View.INVISIBLE);
                            portraitmp3layout.setVisibility(View.INVISIBLE);
                            txtArtist.setVisibility(View.INVISIBLE);
                            circularProgressBar.setVisibility(View.INVISIBLE);
                            txtSong.setVisibility(View.INVISIBLE);
                            mPreview.setVisibility(View.VISIBLE);
                            mPreview.playMedia(arrSongs.get(index - 1).getSongPath(), vol, vol1);
                            univKeyCode = "";
                            keyct = 0;

                        }
                    });
                } catch (Exception e) {
                    e.getCause().toString();
                }


                //123videoView.setVisibility(View.VISIBLE);
                //123videoView.setVideoPath(arrAdvertisements.get(currentlyPlayingAdAtIndex).getAdvtFilePath());
            }
        }catch (Exception ex)
        {
            try {
                String classname = this.getClass().getSimpleName();
                String method = new Exception().getStackTrace()[0].getMethodName();
                caughtException(classname, method, "", univtoken, "5", ex);
            }
            catch(Exception e)
            {
                e.getCause();
            }
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event){
        try {

            if (keyct == 0) {
                if (((event.getKeyCode() >= 7) && (event.getKeyCode() <= 16)) || ((event.getKeyCode() >= 144) && (event.getKeyCode() <= 153))) {
                    univKeyCode = univKeyCode + event.getNumber();
                }
            } else {
                //    Toast.makeText(HomeActivity.this, "Not Handled kEy Code", Toast.LENGTH_LONG).show();
            }

            if (t == 0) {
                if (!univKeyCode.equals("")) {
                    KeyCountdownTimer = new CountDownTimer(3000, 100) {

                        public void onTick(long millisUntilFinished) {
                            t = 1;
                        }

                        public void onFinish() {

                            String p = removeLeadingZeroes(univKeyCode);
                            if (Integer.valueOf(p) <= arrSongs.size()) {
                                playkeyeventSong(Integer.valueOf(p));
                                t = 0;
                                // Utilities.showToast(HomeActivity.this, univKeyCode);
                            } else {
                                //  Utilities.showToast(HomeActivity.this, "No song");
                                univKeyCode = "";
                                keyct = 0;
                                t = 0;
                            }
                            KeyCountdownTimer.cancel();

                        }
                    }.start();
                }
            }

            return true;
        }catch (Exception e)
        {
            try {
                String classname = this.getClass().getSimpleName();
                String method = new Exception().getStackTrace()[0].getMethodName();
                caughtException(classname, method, "", univtoken, "5", e);
            }
            catch(Exception ex)
            {
                ex.getCause();
            }
        }
        return false;

    }


    public static String removeLeadingZeroes(String str) {
        String strPattern = "^0+(?!$)";
        str = str.replaceAll(strPattern, "");
        return str;
    }

    private void checkForUnfinishedDownloads(){
        try {
            // Toast.makeText(HomeActivity.this, "Checking for unfinished downloads.", Toast.LENGTH_SHORT).show();

            Log.e(TAG, "Checking for unfinished downloads.");
            if (ConnectivityReceiver.isConnected()) {

                Log.e(TAG, "Internet is connected.");
                if (!AlenkaMedia.getInstance().isDownloadServiceRunning) {

                    ArrayList<Songs> songs = getSongsToBeDownloaded();
                    ArrayList<Advertisements> ads = getAdvertisementsToBeDownloaded();

                    if (songs != null && songs.size() > 0 ||
                            ads != null && ads.size() > 0) {
                        Log.e(TAG, "Starting download.");
                        //  Toast.makeText(HomeActivity.this, "Starting download for unfinished songs.", Toast.LENGTH_SHORT).show();
                        startService(new Intent(HomeActivity.this, DownloadService.class).putExtra(Constants.TAG_START_DOWNLOAD_SERVICE, true));
                        doBindService();
                    }
                }
            }
        }catch (Exception ex)
        {
            try {
                String classname = this.getClass().getSimpleName();
                String method = new Exception().getStackTrace()[0].getMethodName();
                caughtException(classname, method, "", univtoken, "5", ex);
            }
            catch(Exception e)
            {
                e.getCause();
            }
        }
    }

    private void checkForUpdateData(){
        try {

            boolean shouldUpdateData = AlenkaMedia.getInstance().isUpdateInProgress;

            if (!shouldUpdateData) {
                new PlaylistManager(HomeActivity.this, playlistLoaderListener).checkUpdatedPlaylistData();
            }
        }catch(Exception ex)
        {
            try {
                String classname = this.getClass().getSimpleName();
                String method = new Exception().getStackTrace()[0].getMethodName();
                caughtException(classname, method, "", univtoken, "5", ex);
            }
            catch(Exception e)
            {
                e.getCause();
            }
        }
    }

    PlaylistLoaderListener playlistLoaderListener = new PlaylistLoaderListener() {
        @Override
        public void startedGettingPlaylist() {

            Log.e(TAG,"Started getting playlist");
        }

        @Override
        public void finishedGettingPlaylist() {
            try {

                Log.e(TAG, "Finished getting playlist");

                ArrayList<Songs> songs = getSongsToBeDownloaded();
                ArrayList<Advertisements> ads = getAdvertisementsToBeDownloaded();

            /*
            If new songs are present. Start downloading them, else restart the player for playlist time changes sync.
             */
                if (songs != null && songs.size() > 0 ||
                        ads != null && ads.size() > 0) {

                    if (!AlenkaMedia.getInstance().isDownloadServiceRunning) {
                        startService(new Intent(HomeActivity.this, DownloadService.class).putExtra(Constants.TAG_START_DOWNLOAD_SERVICE, true));
                        doBindService();
                    }

                } else {

                    PlaylistManager playlistManager = new PlaylistManager(HomeActivity.this, playlistLoaderListener);
                    playlistManager.publishTokenForUpdatedData();
                }
            }catch (Exception ex)
            {
                try {
                    String classname = this.getClass().getSimpleName();
                    String method = new Exception().getStackTrace()[0].getMethodName();
                    caughtException(classname, method, "", univtoken, "5", ex);
                }
                catch(Exception e)
                {
                    e.getCause();
                }
            }
        }

        @Override
        public void errorInGettingPlaylist() {

        }

        @Override
        public void recordSaved(boolean isSaved) {

        }

        @Override
        public void tokenUpdatedOnServer() {
            try {

                AlenkaMedia.getInstance().isUpdateInProgress = false;

               // restartPlayer();
            }catch (Exception ex)
            {
                try {
                    String classname = this.getClass().getSimpleName();
                    String method = new Exception().getStackTrace()[0].getMethodName();
                    caughtException(classname, method, "", univtoken, "5", ex);
                }
                catch(Exception e)
                {
                    e.getCause();
                }
            }
        }
    };

    public void rebootbox()
    {
        try {
              Shell.SU.run("reboot");

        }
        catch (Exception ex) {
            ex.getCause();
        }

    }



    public void restartPlayer()
    {
        try {
            AlenkaMedia.playlistStatus = -12;
            AlenkaMedia.currentPlaylistId = "";
            if(ctrplaylistchg!=1) {
                if (y != 0) {
                    if (mPreview != null) {
                        if (mPreview.isPlaying()) {
                            mPreview.stopPlayback();
                        }
                        mPreview.release();
                    }
                }
                if (y != 0) {
                    if (mPreviewads != null) {
                        if (mPreviewads.isPlaying()) {
                            mPreviewads.stopPlayback();
                        }
                        mPreviewads.release();
                    }
                }
            }
            imgcounter = 0;
           // Utilities.showToast(HomeActivity.this,"restrtbegin");

            startActivity(new Intent(HomeActivity.this, Splash_Activity.class));
            HomeActivity.this.finish();
            System.exit(2);
        }
        catch (Exception e)
        {
            try {
                Utilities.showToast(HomeActivity.this, e.getCause().toString());
                e.getCause().toString();
                String classname = this.getClass().getSimpleName();
                String method = new Exception().getStackTrace()[0].getMethodName();
                caughtException(classname, method, "", univtoken, "5", e);
            }
            catch (Exception ex)
            {
                ex.getCause();
            }
        }

       // mPreview.setSurfaceTexture(null);

    }


}
