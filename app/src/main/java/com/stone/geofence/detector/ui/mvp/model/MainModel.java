package com.stone.geofence.detector.ui.mvp.model;

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
}
