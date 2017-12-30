package com.stone.geofence.detector.ui;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.stone.geofence.detector.GeofenceDetectorApp;
import com.stone.geofence.detector.R;
import com.stone.geofence.detector.db.model.GeofenceData;
import com.stone.geofence.detector.ui.mvp.model.MainModel;
import com.stone.geofence.detector.ui.mvp.presenter.IMainPresenter;
import com.stone.geofence.detector.ui.mvp.presenter.MainPresenter;
import com.stone.geofence.detector.ui.mvp.view.MainView;
import com.stone.geofence.detector.util.GeofenceUtil;
import com.stone.geofence.detector.util.PermissionsUtil;

import javax.inject.Inject;

public class MainActivity extends PermissionsAwareActivity implements MainView {

    private static final int PLACE_PICKER_REQUEST = 222;
    @Inject
    ViewModelProvider.Factory mViewModelFactory;
    private TextView mTextMessage;
    private MainModel mMainViewModel;
    private IMainPresenter mPresenter;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener =
        item -> {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    addGeofence();
                    return true;
                case R.id.navigation_dashboard:

                    return true;
            }
            return false;
        };

    //region MVP implementation
    private void initMvp() {
        mMainViewModel = ViewModelProviders.of(this, mViewModelFactory).get(MainModel.class);
        mPresenter = new MainPresenter(this);
        mMainViewModel.init();
    }

    @Override
    public void updateGeofenceStatus(String name, String status) {
        String text = String.format("%1$s, %2$s", name, status);
        mTextMessage.setText(text);
    }

    @Override
    public MainModel model() {
        return mMainViewModel;
    }

    @Override
    public LifecycleOwner lifecycleOwner() {
        return this;
    }
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GeofenceDetectorApp.getAppComponent().inject(this);
        initMvp();

        mTextMessage = findViewById(R.id.message);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_home);
    }

    void getPointClick() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(MainActivity.this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }


    void addGeofence() {

        if (!PermissionsUtil.checkPermissions(this)) {
            showToast(getString(R.string.insufficient_permissions));
            mPendingGeofenceTask = GeofenceUtil.PendingGeofenceTask.ADD;
            PermissionsUtil.requestPermissions(this);
            return;
        }

        GeofenceData gd = new GeofenceData();
        gd.setLatitude(49.8407);
        gd.setLongitude(24.0305);
        gd.setRadius(100);
        gd.setName("AndroidWifi");

        mPresenter.setGeofenceValues("AndroidWifi", 49.8407, 24.0305, 100);

//        mGeofenceRepo.addGeofence(gd);
//
//        mGeofenceRepo.getFenceLiveData().observe(this, fenceStatus -> {
//            if (fenceStatus != null) {
//                String text = String.format("%1$s, %2$s, %3$s", fenceStatus.getFenceName(), fenceStatus.getGeoState(), fenceStatus.getWifiState());
//                mTextMessage.setText(text);
//            } else {
//                mTextMessage.setText("NULL");
//            }
//        });
    }

    /**
     * Performs the geofencing task that was pending until location permission was granted.
     */
    @Override
    protected void performPendingGeofenceTask() {
        if (mPendingGeofenceTask == GeofenceUtil.PendingGeofenceTask.ADD) {
            addGeofence();
        }
    }
}
