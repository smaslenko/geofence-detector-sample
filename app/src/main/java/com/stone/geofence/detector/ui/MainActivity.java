package com.stone.geofence.detector.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;
import android.widget.TextView;

import com.stone.geofence.detector.GeofenceDetectorApp;
import com.stone.geofence.detector.R;
import com.stone.geofence.detector.db.model.GeofenceData;
import com.stone.geofence.detector.repository.GeofenceRepo;
import com.stone.geofence.detector.util.GeofenceUtil;
import com.stone.geofence.detector.util.PermissionsUtil;

import javax.inject.Inject;

public class MainActivity extends PermissionsAwareActivity {

    @Inject
    GeofenceRepo mGeofenceRepo;
    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
        = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    addGeofence();
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
//                case R.id.navigation_notifications:
//                    mTextMessage.setText(R.string.title_notifications);
//                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((GeofenceDetectorApp) getApplication()).getRepositoryComponent().inject(this);

        mTextMessage = findViewById(R.id.message);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    void addGeofence() {

        if (!PermissionsUtil.checkPermissions(this)) {
            showToast(getString(R.string.insufficient_permissions));
            mPendingGeofenceTask = GeofenceUtil.PendingGeofenceTask.ADD;
            PermissionsUtil.requestPermissions(this);
            return;
        }

        GeofenceData gd = new GeofenceData();
        gd.setId(0);
        gd.setLatitude(49.8407);
        gd.setLongitude(24.0305);
        gd.setRadius(100);
        String name = "Chuvas";
        gd.setName(name);
        gd.setWifiId(name);

        mGeofenceRepo.addGeofence(gd);
    }

    /**
     * Performs the geofencing task that was pending until location permission was granted.
     */
    @Override
    protected void performPendingGeofenceTask() {
        if (mPendingGeofenceTask == GeofenceUtil.PendingGeofenceTask.ADD) {
            addGeofence();
        } else if (mPendingGeofenceTask == GeofenceUtil.PendingGeofenceTask.REMOVE) {
//            removeGeofences();
        }
    }

}
