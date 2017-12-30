package com.stone.geofence.detector.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.stone.geofence.detector.db.model.GeofenceData;

@Database(entities = {GeofenceData.class}, version = 4, exportSchema = false)
public abstract class GeofenceDatabase extends RoomDatabase {

    public abstract GeofenceDao getHomeDao();
}
