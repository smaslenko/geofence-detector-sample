package com.stone.geofence.detector.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.stone.geofence.detector.db.model.GeofenceData;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface GeofenceDao {

    //region Thermostats
    @Insert(onConflict = REPLACE)
    void saveGeofence(GeofenceData geofenceData);

    @Query("SELECT * FROM GeofenceData WHERE id = :id")
    LiveData<GeofenceData> loadGeofence(int id);

    @Query("SELECT * FROM GeofenceData")
    LiveData<List<GeofenceData>> loadAll();
    //endregion
}