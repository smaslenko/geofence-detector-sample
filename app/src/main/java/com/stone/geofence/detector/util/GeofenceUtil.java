package com.stone.geofence.detector.util;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.stone.geofence.detector.R;
import com.stone.geofence.detector.db.model.GeofenceData;
import com.stone.geofence.detector.repository.FenceStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GeofenceUtil {

    /**
     * Tracks whether the user requested to add or remove geofences, or to do neither.
     */
    public enum PendingGeofenceTask {
        ADD, REMOVE, NONE
    }

    public static Geofence GeofenceDataToGeofence(GeofenceData data) {
        return new Geofence.Builder()
            // Set the request ID of the geofence. This is a string to identify this
            // geofence.
            .setRequestId(String.valueOf(data.getName()))

            .setCircularRegion(
                data.getLatitude(),
                data.getLongitude(),
                data.getRadius()
            )
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
            .build();
    }

    /**
     * Gets transition details and returns them as a formatted string.
     *
     * @param geofenceTransition  The ID of the geofence transition.
     * @param triggeringGeofences The geofence(s) triggered.
     * @return The transition details formatted as String.
     */
    public static String getGeofenceTransitionDetails(
        Context context,
        int geofenceTransition,
        List<Geofence> triggeringGeofences) {

        String geofenceTransitionString = getTransitionString(context, geofenceTransition);

        // Get the Ids of each geofence that was triggered.
        ArrayList<String> triggeringGeofencesIdsList = new ArrayList<>();
        for (Geofence geofence : triggeringGeofences) {
            triggeringGeofencesIdsList.add(geofence.getRequestId());
        }
        String triggeringGeofencesIdsString = TextUtils.join(", ", triggeringGeofencesIdsList);

        return geofenceTransitionString + ": " + triggeringGeofencesIdsString;
    }

    /**
     * Gets {@link FenceStatus} list from retrieved transition.
     *
     * @param geofenceTransition  The ID of the geofence transition.
     * @param triggeringGeofences The geofence(s) triggered.
     * @return {@link FenceStatus} list
     */
    public static List<FenceStatus> getFenceStatuses(int geofenceTransition, List<Geofence> triggeringGeofences) {
        final FenceStatus.State state =
            geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ? FenceStatus.State.In : FenceStatus.State.Out;
        return triggeringGeofences.stream()
            .map(geofence -> new FenceStatus(
                geofence.getRequestId(),
                state))
            .collect(Collectors.toList());
    }

    /**
     * Maps geofence transition types to their human-readable equivalents.
     *
     * @param transitionType A transition type constant defined in Geofence
     * @return A String indicating the type of transition
     */
    public static String getTransitionString(Context context, int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return context.getString(R.string.geofence_transition_entered);
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return context.getString(R.string.geofence_transition_exited);
            default:
                return context.getString(R.string.unknown_geofence_transition);
        }
    }

    /**
     * Returns the error string for a geofencing exception.
     */
    public static String getErrorString(Context context, Exception e) {
        if (e instanceof ApiException) {
            return getErrorString(context, ((ApiException) e).getStatusCode());
        } else {
            return context.getResources().getString(R.string.unknown_geofence_error);
        }
    }

    /**
     * Returns the error string for a geofencing error code.
     */
    public static String getErrorString(Context context, int errorCode) {
        Resources mResources = context.getResources();
        switch (errorCode) {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return mResources.getString(R.string.geofence_not_available);
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return mResources.getString(R.string.geofence_too_many_geofences);
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return mResources.getString(R.string.geofence_too_many_pending_intents);
            default:
                return mResources.getString(R.string.unknown_geofence_error);
        }
    }
}
