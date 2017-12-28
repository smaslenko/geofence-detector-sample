package com.stone.geofence.detector.util;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;

import com.stone.geofence.detector.R;

import static android.provider.Settings.Secure.LOCATION_MODE_OFF;
import static android.provider.Settings.Secure.LOCATION_MODE_SENSORS_ONLY;

public class PermissionsUtil {

    private static final String TAG = "PermissionsUtil";

    private static final String[] LOCATION_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION};

    public static final int REQUEST_PERMISSIONS_REQUEST_CODE = 39;

    /**
     * Return the current state of the permissions needed.
     */
    public static boolean checkPermissions(Activity activity) {
        int permissionState = ActivityCompat.checkSelfPermission(activity,
            Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Requesting location permission for the app
     */
    public static void requestPermissions(Activity activity) {
        boolean shouldProvideRationale =
            ActivityCompat.shouldShowRequestPermissionRationale(activity,
                Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            showSnackbar(activity, R.string.permission_rationale, android.R.string.ok,
                view -> {
                    // Request permission
                    requestPermissions(activity,
                        LOCATION_PERMISSIONS,
                        REQUEST_PERMISSIONS_REQUEST_CODE);
                });
        } else {
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            requestPermissions(activity,
                LOCATION_PERMISSIONS,
                REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private static void requestPermissions(Activity activity, String[] permissions, int requestCode) {
        Log.i(TAG, "Requesting permission");
        ActivityCompat.requestPermissions(activity,
            permissions,
            requestCode);
    }

    public static boolean checkLocationMode(Activity activity) {
        try {
            final int mode = Settings.Secure.getInt(activity.getContentResolver(), Settings.Secure.LOCATION_MODE);
            switch (mode) {
                case LOCATION_MODE_OFF:
                case LOCATION_MODE_SENSORS_ONLY:
                    showSnackbar(activity, R.string.location_mode_explanation, R.string.location_mode,
                        view -> {
                            // Build intent that displays the Location mode settings screen.
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            activity.startActivity(intent);
                        });
                    return false;
                default:
                    return true;
            }

        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, "Setting Not Found", e);
            return true;
        }
    }

    /**
     * Shows a {@link Snackbar}.
     *
     * @param mainTextStringId The id for the string resource for the Snackbar text.
     * @param actionStringId   The text of the action item.
     * @param listener         The listener associated with the Snackbar action.
     */
    public static void showSnackbar(Activity activity, final int mainTextStringId, final int actionStringId,
                                    View.OnClickListener listener) {
        Snackbar.make(
            activity.findViewById(android.R.id.content),
            activity.getString(mainTextStringId),
            Snackbar.LENGTH_INDEFINITE)
            .setAction(activity.getString(actionStringId), listener).show();
    }

}
