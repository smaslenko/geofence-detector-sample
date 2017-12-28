package com.stone.geofence.detector.repository;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.stone.geofence.detector.db.model.GeofenceData;
import com.stone.geofence.detector.util.GeofenceUtil;

import java.util.List;
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
    private GeofencingClient mGeofencingClient;

    /**
     * Used when requesting to add or remove geofences.
     */
    private PendingIntent mGeofencePendingIntent;

    private GeofencingRequest.Builder mGeofencingRequestBuilder;

    @Inject
    GeofenceRepo(GeofencingClient geofencingClient, PendingIntent geofencePendingIntent, GeofencingRequest.Builder geofencingRequestBuilder) {
        mGeofencingClient = geofencingClient;
        mGeofencePendingIntent = geofencePendingIntent;
        mGeofencingRequestBuilder = geofencingRequestBuilder;
    }

    @SuppressLint("MissingPermission")
    public void addGeofence(GeofenceData data) {
        Geofence gf = GeofenceUtil.GeofenceDataToGeofence(data);
        GeofencingRequest request = mGeofencingRequestBuilder.addGeofence(gf).build();

        mGeofencingClient.addGeofences(request, mGeofencePendingIntent)
            .addOnSuccessListener(aVoid -> {
                Log.d(TAG, "Geofences added");
            })
            .addOnFailureListener(e -> {
                // Failed to add geofences
                // ...
                Log.d(TAG, "Geofences not added, error: " + e.toString());
            });
    }

    public void removeGeofence(GeofenceData data) {
        List<String> toRemove = Stream.of(data).map(geofence -> String.valueOf(geofence.getId())).collect(Collectors.toList());
        mGeofencingClient.removeGeofences(toRemove)
            .addOnSuccessListener(aVoid -> {
                Log.d(TAG, "Geofences removed");
            })
            .addOnFailureListener(e -> {
                Log.d(TAG, "Geofences not removed, error: " + e.toString());
            });
    }



}
