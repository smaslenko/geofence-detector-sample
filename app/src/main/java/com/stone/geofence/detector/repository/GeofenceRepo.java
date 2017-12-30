package com.stone.geofence.detector.repository;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
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

    private final Context mContext;

    /**
     * Provides access to the Geofencing API.
     */
    private final GeofencingClient mGeofencingClient;

    /**
     * Used to attach {@link GeofenceTransitionReceiver} as transitions handler
     */
    private PendingIntent mGeofencePendingIntent;

    /**
     * Used to save user entered geofences to DB.
     */
    private final GeofenceDao mGeofenceDao;

    private final Executor mExecutor;

    private final GeofenceTransitionReceiver.InitialLocationProvider mInitialLocationProvider;
    private final WifiTransitionReceiver.InitialWifiStateProvider mInitialWifiStateProvider;

    private FenceLiveData mFenceLiveData;
    private MutableLiveData<GeofenceData> mStoredData;

    @Inject
    GeofenceRepo(
        Context context,
        GeofenceDao dao,
        GeofencingClient geofencingClient,
        GeofenceTransitionReceiver.InitialLocationProvider initialLocationProvider,
        WifiTransitionReceiver.InitialWifiStateProvider initialWifiStateProvider,
        Executor executor) {
        mContext = context;
        mGeofencingClient = geofencingClient;
        mGeofenceDao = dao;
        mInitialLocationProvider = initialLocationProvider;
        mInitialWifiStateProvider = initialWifiStateProvider;
        mExecutor = executor;
        mGeofencePendingIntent = getGeoPendingIntent();
    }

    public void init() {
        mFenceLiveData = new FenceLiveData();
        mStoredData = new MutableLiveData<>();
        loadStoredGeofencesAndAdd();
    }

    @SuppressLint("MissingPermission")
    public void addGeofence(GeofenceData data) {
        Geofence gf = GeofenceUtil.GeofenceDataToGeofence(data);
        GeofencingRequest request = getRequestBuilder().addGeofence(gf).build();
        mGeofencePendingIntent = getGeoPendingIntent();
        mGeofencingClient.addGeofences(request, mGeofencePendingIntent)
            .addOnSuccessListener(v1 -> {
                Log.d(TAG, "Geofences added. ID=" + gf.getRequestId());
            })
            .addOnFailureListener(e -> {
                Log.d(TAG, "Geofences NOT added, error: " + e.toString());
            });

        saveGeofence(data);
        mFenceLiveData.updateName(data.getName());
        mInitialLocationProvider.provide(data.getLatitude(), data.getLongitude(), data.getRadius());
        mInitialWifiStateProvider.provide();
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

    public LiveData<GeofenceData> getStoredData() {
        return mStoredData;
    }

    private void saveGeofence(GeofenceData data) {
        mExecutor.execute(() -> {
            mGeofenceDao.saveGeofence(data);
        });
    }

    private void loadStoredGeofencesAndAdd() {
        mExecutor.execute(() -> {
            List<GeofenceData> all = mGeofenceDao.loadAll();
            if (all.isEmpty()) return;
            GeofenceData data = all.get(0);
            mStoredData.postValue(data);
            addGeofence(data);
        });
    }

    private GeofencingRequest.Builder getRequestBuilder() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER | GeofencingRequest.INITIAL_TRIGGER_EXIT);
        return builder;
    }

    private PendingIntent getGeoPendingIntent() {
        Intent intent = new Intent(mContext.getApplicationContext(), GeofenceTransitionReceiver.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        return PendingIntent.getService(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
