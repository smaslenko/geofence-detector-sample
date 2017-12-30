package com.stone.geofence.detector.repository;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.stone.geofence.detector.db.GeofenceDao;
import com.stone.geofence.detector.db.model.GeofenceData;
import com.stone.geofence.detector.receiver.GeofenceTransitionReceiver;
import com.stone.geofence.detector.receiver.WifiTransitionReceiver;
import com.stone.geofence.detector.repository.data.FenceLiveData;
import com.stone.geofence.detector.repository.data.FenceStatus;
import com.stone.geofence.detector.util.GeofenceUtil;

import java.util.List;
import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class GeofenceRepo {

    private static final String TAG = "GeofenceRepo";
    /**
     * Provides access to the Geofencing API.
     */
    private final GeofencingClient mGeofencingClient;

    /**
     * Used to attach {@link GeofenceTransitionReceiver} as transitions handler
     */
    private final PendingIntent mGeofencePendingIntent;

    /**
     * Used when requesting to add or remove geofences of interest.
     */
    private final GeofencingRequest.Builder mGeofencingRequestBuilder;

    /**
     * Used to save user entered geofences to DB.
     */
    private final GeofenceDao mGeofenceDao;

    private final Executor mExecutor;

    private final WifiTransitionReceiver.InitialWifiStateProvider mInitialWifiStateProvider;

    private FenceLiveData mFenceLiveData;

    @Inject
    GeofenceRepo(GeofenceDao dao,
                 GeofencingClient geofencingClient,
                 PendingIntent geofencePendingIntent,
                 GeofencingRequest.Builder geofencingRequestBuilder,
                 WifiTransitionReceiver.InitialWifiStateProvider initialWifiStateProvider,
                 Executor executor) {
        mGeofencingClient = geofencingClient;
        mGeofencePendingIntent = geofencePendingIntent;
        mGeofencingRequestBuilder = geofencingRequestBuilder;
        mGeofenceDao = dao;
        mInitialWifiStateProvider = initialWifiStateProvider;
        mExecutor = executor;
    }

    public void init() {
        mFenceLiveData = new FenceLiveData();
        mInitialWifiStateProvider.provide();
        loadGeofencesAndAdd();
    }

    @SuppressLint("MissingPermission")
    public void addGeofence(GeofenceData data) {
        Geofence gf = GeofenceUtil.GeofenceDataToGeofence(data);
        GeofencingRequest request = mGeofencingRequestBuilder.addGeofence(gf).build();
        mGeofencingClient.addGeofences(request, mGeofencePendingIntent)
            .addOnSuccessListener(v -> {
                Log.d(TAG, "Geofences added");
            })
            .addOnFailureListener(e -> {
                Log.d(TAG, "Geofences not added, error: " + e.toString());
            });

        saveGeofence(data);
        mFenceLiveData.updateName(data.getName());
    }

    public void updateGeoState(FenceStatus.GeoState state) {
        mFenceLiveData.updateGeoState(state);
    }

    public void updateWifiState(String wifiName, FenceStatus.WifiState state) {
        mFenceLiveData.updateWifiState(wifiName, state);
    }

    public FenceLiveData getFenceLiveData() {
        return mFenceLiveData;
    }

    private void saveGeofence(GeofenceData data) {
        mExecutor.execute(() -> {
            mGeofenceDao.saveGeofence(data);
        });
    }

    private void loadGeofencesAndAdd() {
        mExecutor.execute(() -> {
            List<GeofenceData> list = mGeofenceDao.loadAll();
            for (GeofenceData gd : list) {
                addGeofence(gd);
            }
        });
    }
}
