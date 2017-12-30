package com.stone.geofence.detector.receiver;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.stone.geofence.detector.GeofenceDetectorApp;
import com.stone.geofence.detector.R;
import com.stone.geofence.detector.repository.GeofenceRepo;
import com.stone.geofence.detector.repository.data.FenceStatus;
import com.stone.geofence.detector.util.GeofenceUtil;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

public class GeofenceTransitionReceiver extends IntentService {

    private static final String TAG = "GeofenceTransitionReceiver";

    public final static String EXTRA_KEY_TRANSITION = "GeofenceTransitionReceiver.transition";

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
        final int geofenceTransition = intent.hasExtra(EXTRA_KEY_TRANSITION) ?
            intent.getIntExtra(EXTRA_KEY_TRANSITION, -1) :
            geofencingEvent.getGeofenceTransition();

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

    /**
     * Helper class that checks initial location state and broadcasting it to receiver
     */
    @Singleton
    public static class InitialLocationProvider {
        Application application;
        FusedLocationProviderClient fusedLocationClient;

        @Inject
        InitialLocationProvider(Application application, FusedLocationProviderClient fusedLocationClient) {
            this.application = application;
            this.fusedLocationClient = fusedLocationClient;
        }

        @SuppressLint("MissingPermission")
        public void provide(double latitude, double longitude, float radius) {

            fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        float[] distance = new float[2];
                        Location.distanceBetween(location.getLatitude(), location.getLongitude(),
                            latitude, longitude, distance);
                        int transition = distance[0] > radius ? Geofence.GEOFENCE_TRANSITION_EXIT : Geofence.GEOFENCE_TRANSITION_ENTER;
                        Intent intent = new Intent(application.getApplicationContext(), GeofenceTransitionReceiver.class);
                        intent.putExtra(EXTRA_KEY_TRANSITION, transition);
                        application.getApplicationContext().startService(intent);
                    }
                });
        }
    }
}
