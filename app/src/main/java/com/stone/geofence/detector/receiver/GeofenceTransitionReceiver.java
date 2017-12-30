package com.stone.geofence.detector.receiver;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.stone.geofence.detector.GeofenceDetectorApp;
import com.stone.geofence.detector.R;
import com.stone.geofence.detector.repository.GeofenceRepo;
import com.stone.geofence.detector.repository.data.FenceStatus;
import com.stone.geofence.detector.util.GeofenceUtil;

import java.util.List;

import javax.inject.Inject;

public class GeofenceTransitionReceiver extends IntentService {

    private static final String TAG = "GeofenceTransitionReceiver";

    @Inject
    GeofenceRepo mGeofenceRepo;

    public GeofenceTransitionReceiver() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        GeofenceDetectorApp.getAppComponent().inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            int errorCode = geofencingEvent.getErrorCode();
            String errorMessage = GeofenceUtil.getErrorString(this, errorCode);
            Log.e(TAG, "" + errorMessage);
            mGeofenceRepo.updateGeoState(FenceStatus.GeoState.Error.setErrorCode(errorCode));
            return;
        }

        // Get the transition type.
        final int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
            geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            // Get the transition details as a String.
            String geofenceTransitionDetails =
                GeofenceUtil.getGeofenceTransitionDetails(this, geofenceTransition, triggeringGeofences);

            Log.i(TAG, geofenceTransitionDetails);

            // Update Repo
            final FenceStatus.GeoState state =
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ? FenceStatus.GeoState.In : FenceStatus.GeoState.Out;
            mGeofenceRepo.updateGeoState(state);
        } else {
            // Log the error.
            Log.e(TAG, getString(R.string.geofence_transition_invalid_type, geofenceTransition));
        }
    }
}
