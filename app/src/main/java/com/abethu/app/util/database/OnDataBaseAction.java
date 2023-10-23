package com.abethu.app.util.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.abethu.app.model.Banner;
import com.abethu.app.model.Category;
import com.abethu.app.model.OfflineVideo;
import com.abethu.app.model.OfflineVideoSections;
import com.abethu.app.model.Video;
import com.abethu.app.model.VideoSection;

import java.util.List;

@Dao
public interface OnDataBaseAction {

//  Banner Videos
    @Query("SELECT * FROM Banner")
    List<Banner> getAllBannerVideos();

    @Query("DELETE FROM Banner")
    void truncateBannerVideos();

    @Insert
    void insertVideoIntoBanner(List<Banner> task);


//  Offline video sections
    @Query("SELECT * FROM OfflineVideoSections")
    List<OfflineVideoSections> getVideoSections();

    @Query("DELETE FROM OfflineVideoSections")
    void truncateVideoSectiondata();

    @Insert
    void insertIntoVideoSection(List<OfflineVideoSections> task);


    //  Offline video sections
    @Query("SELECT * FROM OfflineVideo")
    List<OfflineVideo> getVideoOffline();

    @Query("DELETE FROM OfflineVideo")
    void truncateOfflineVideo();

    @Insert
    void insertVideoOffline(List<OfflineVideo> task);

}
