package com.stone.geofence.detector.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.stone.geofence.detector.BuildConfig;
import com.stone.geofence.detector.R;
import com.stone.geofence.detector.util.GeofenceUtil;
import com.stone.geofence.detector.util.PermissionsUtil;

import static com.stone.geofence.detector.util.PermissionsUtil.REQUEST_PERMISSIONS_REQUEST_CODE;

@SuppressLint("Registered")
public abstract class PermissionsAwareActivity extends AppCompatActivity {

    private static final String TAG = "PermissionsAwareActivity";

    protected GeofenceUtil.PendingGeofenceTask mPendingGeofenceTask = GeofenceUtil.PendingGeofenceTask.NONE;

    protected abstract void performPendingGeofenceTask();

    /**
     * Shows a {@link Toast} using {@code text}.
     *
     * @param text The Snackbar text.
     */
    protected void showToast(final String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "Permission granted.");
                performPendingGeofenceTask();
            } else {
                // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                PermissionsUtil.showSnackbar(this, R.string.permission_denied_explanation, R.string.settings,
                    view -> {
                        // Build intent that displays the App settings screen.
                        Intent intent = new Intent();
                        intent.setAction(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package",
                            BuildConfig.APPLICATION_ID, null);
                        intent.setData(uri);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    });
                mPendingGeofenceTask = GeofenceUtil.PendingGeofenceTask.NONE;
            }
        }
    }
}
