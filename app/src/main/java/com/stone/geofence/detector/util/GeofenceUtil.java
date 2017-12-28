package com.stone.geofence.detector.util;

import com.google.android.gms.location.Geofence;
import com.stone.geofence.detector.db.model.GeofenceData;

public class GeofenceUtil {

    public static Geofence GeofenceDataToGeofence(GeofenceData data) {
        return new Geofence.Builder()
            // Set the request ID of the geofence. This is a string to identify this
            // geofence.
            .setRequestId(String.valueOf(data.getId()))

            .setCircularRegion(
                data.getLatitude(),
                data.getLongitude(),
                data.getRadius()
            )
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
            .build();
    }
}
