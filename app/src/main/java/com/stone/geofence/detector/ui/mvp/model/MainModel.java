package com.stone.geofence.detector.ui.mvp.model;

import android.arch.lifecycle.LiveData;

import com.stone.geofence.detector.db.model.GeofenceData;
import com.stone.geofence.detector.repository.data.FenceLiveData;

import javax.inject.Inject;

public class MainModel extends BaseModel {

    @Inject
    public MainModel() {
    }

    public void init() {
        mGeofenceRepo.init();
    }

    public void addGeofence(GeofenceData data) {
        mGeofenceRepo.addGeofence(data);
    }

    public FenceLiveData subscribeGeofence() {
        return mGeofenceRepo.getFenceLiveData();
    }
    public LiveData<GeofenceData> subscribeStoredData() {
        return mGeofenceRepo.getStoredData();
    }
}
