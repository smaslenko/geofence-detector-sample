package com.stone.geofence.detector.ui.mvp.model;

import android.arch.lifecycle.ViewModel;

import com.stone.geofence.detector.repository.GeofenceRepo;

import javax.inject.Inject;

public class BaseModel extends ViewModel {
    @Inject
    GeofenceRepo mGeofenceRepo;


}
