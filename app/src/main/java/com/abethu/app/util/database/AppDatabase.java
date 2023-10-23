package com.abethu.app.util.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import com.abethu.app.model.Banner;
import com.abethu.app.model.Category;
import com.abethu.app.model.OfflineVideo;
import com.abethu.app.model.OfflineVideoSections;
import com.abethu.app.model.Video;
import com.abethu.app.model.VideoSection;



@Database(entities = {Banner.class, OfflineVideoSections.class, OfflineVideo.class}, version = 4, exportSchema = false)
public  abstract class AppDatabase extends RoomDatabase {

    public abstract OnDataBaseAction dataBaseAction();
    private static volatile AppDatabase appDatabase;

    @NonNull
    @Override
    protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration config) {
        return null;
    }

    @NonNull
    @Override
    protected InvalidationTracker createInvalidationTracker() {
        return null;
    }

    @Override
    public void clearAllTables() {

    }
}

