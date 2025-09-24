package com.ApplicationAddOnsLband.interfaces;

import com.ApplicationAddOnsLband.models.Advertisements;
import com.ApplicationAddOnsLband.models.Songs;

/**
 * Created by love on 31/5/17.
 */
public interface DownloadListener {

    void onUpdate(long value);
    void downloadCompleted(boolean shouldPlay, Songs songs);
    void advertisementDownloaded(Advertisements advertisements);
    void startedCopyingSongs(int currentSong, int totalSongs, boolean isFinished);
    void showOfflineDownloadingAlert();
    void finishedDownloadingSongs(int totalSongs);
}
