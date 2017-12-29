package com.stone.geofence.detector.repository;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.stone.geofence.detector.db.GeofenceDao;
import com.stone.geofence.detector.db.model.GeofenceData;
import com.stone.geofence.detector.provider.GeofenceTransitionProviderService;
import com.stone.geofence.detector.util.GeofenceUtil;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class GeofenceRepo implements Repository {

    private static final String TAG = "GeofenceRepo";
    /**
     * Provides access to the Geofencing API.
     */
    private final GeofencingClient mGeofencingClient;

    /**
     * Used to attach {@link GeofenceTransitionProviderService} as transitions handler
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

    private MutableLiveData<List<FenceStatus>> mFencesStatus;
    private MutableLiveData<FenceStatus> mFencesErrorStatus;

    @Inject
    GeofenceRepo(GeofenceDao dao, GeofencingClient geofencingClient, PendingIntent geofencePendingIntent, GeofencingRequest.Builder geofencingRequestBuilder, Executor executor) {
        mGeofencingClient = geofencingClient;
        mGeofencePendingIntent = geofencePendingIntent;
        mGeofencingRequestBuilder = geofencingRequestBuilder;
        mGeofenceDao = dao;
        mExecutor = executor;
    }

    @SuppressLint("MissingPermission")
    public void addGeofence(GeofenceData data) {
        Geofence gf = GeofenceUtil.GeofenceDataToGeofence(data);
        GeofencingRequest request = mGeofencingRequestBuilder.addGeofence(gf).build();

        mGeofencingClient.addGeofences(request, mGeofencePendingIntent)
            .addOnSuccessListener(v -> {
                saveGeofence(data);
                Log.d(TAG, "Geofences added");
            })
            .addOnFailureListener(e -> {
                // Failed to add geofences
                // ...
                Log.d(TAG, "Geofences not added, error: " + e.toString());
            });
    }

    public void removeGeofence(GeofenceData data) {
        List<String> toRemove = Stream.of(data).map(GeofenceData::getName).collect(Collectors.toList());
        mGeofencingClient.removeGeofences(toRemove)
            .addOnSuccessListener(v -> {
                deleteGeofence(data);
                Log.d(TAG, "Geofences removed");
            })
            .addOnFailureListener(e -> {
                Log.d(TAG, "Geofences not removed, error: " + e.toString());
            });
    }

    public void setFencesStatus(List<FenceStatus> fencesStatus) {
        if (mFencesStatus == null) {
            mFencesStatus = new MutableLiveData<>();
        }
        mFencesStatus.postValue(fencesStatus);
    }

    public MutableLiveData<List<FenceStatus>> getFencesStatus() {
        return mFencesStatus;
    }

    public void setFencesErrorStatus(String errorText) {
        if(mFencesErrorStatus == null) {
            mFencesErrorStatus = new MutableLiveData<>();
        }
        mFencesErrorStatus.postValue(new FenceStatus(errorText, FenceStatus.State.Error));
    }

    private void saveGeofence(GeofenceData data) {
        mExecutor.execute(() -> {
            mGeofenceDao.saveGeofence(data);
        });
    }

    private void deleteGeofence(GeofenceData data) {
        mExecutor.execute(() -> {
            mGeofenceDao.deleteGeofence(data);
        });
    }

}
