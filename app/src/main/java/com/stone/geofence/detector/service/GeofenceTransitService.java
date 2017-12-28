package com.stone.geofence.detector.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.stone.geofence.detector.R;
import com.stone.geofence.detector.util.GeofenceUtil;

import java.util.List;

public class GeofenceTransitService extends IntentService {

    private static final String TAG = "GeofenceTransitService";

    public GeofenceTransitService() {
        super("GeofenceTransitService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceUtil.getErrorString(this,
                geofencingEvent.getErrorCode());
            Log.e(TAG, "" + geofencingEvent.getErrorCode());
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
            geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

//            // Get the transition details as a String.
            String geofenceTransitionDetails = GeofenceUtil.getGeofenceTransitionDetails(
                this,
                geofenceTransition,
                triggeringGeofences
            );
//
//            // Send notification and log the transition details.
//            sendNotification(geofenceTransitionDetails);
            Log.i(TAG, geofenceTransitionDetails);
        } else {
            // Log the error.
            Log.e(TAG, getString(R.string.geofence_transition_invalid_type,
                geofenceTransition));
        }
    }
}
