package com.stone.geofence.detector.ui;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.stone.geofence.detector.GeofenceDetectorApp;
import com.stone.geofence.detector.R;
import com.stone.geofence.detector.ui.mvp.model.MainModel;
import com.stone.geofence.detector.ui.mvp.presenter.IMainPresenter;
import com.stone.geofence.detector.ui.mvp.presenter.MainPresenter;
import com.stone.geofence.detector.ui.mvp.view.MainView;
import com.stone.geofence.detector.util.GeofenceUtil;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends PermissionsAwareActivity implements MainView {

    private static final int PLACE_PICKER_REQUEST = 222;

    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    @BindView(R.id.statusText)
    TextView mStatusText;
    @BindView(R.id.latitudeText)
    EditText mLatitudeTxt;
    @BindView(R.id.longitudeText)
    EditText mLongitudeTxt;
    @BindView(R.id.radiusText)
    EditText mRadiusTxt;
    @BindView(R.id.wifiNameText)
    EditText mWifiTxt;
    @BindView(R.id.mapButton)
    Button mMapBtn;
    @BindView(R.id.saveButton)
    Button mSaveBtn;

    private MainModel mMainViewModel;
    private IMainPresenter mPresenter;

    //region MVP implementation
    private void initMvp() {
        mMainViewModel = ViewModelProviders.of(this, mViewModelFactory).get(MainModel.class);
        mMainViewModel.init();

        mPresenter = new MainPresenter(this);
        mPresenter.subscribe();
    }

    @Override
    public void updateGeofenceStatus(String status) {
        mStatusText.setText(status);
    }

    @Override
    public void updateLatLongViews(String latitude, String longitude) {
        mLatitudeTxt.setText(latitude);
        mLongitudeTxt.setText(longitude);
    }

    @Override
    public void updateRadiusView(String radius) {
        mRadiusTxt.setText(radius);
    }

    @Override
    public void updateWifiView(String ssid) {
        mWifiTxt.setText(ssid);
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
        ButterKnife.bind(this);

        initMvp();

        mStatusText = findViewById(R.id.statusText);
    }

    @OnClick(R.id.mapButton)
    void openMap(View button) {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(MainActivity.this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.saveButton)
    void save(View button) {
        if (!checkPermissionsAndRequest()) {
            return;
        }
        mPresenter.setGeofenceValues(
            mWifiTxt.getText().toString(),
            Double.parseDouble(mLatitudeTxt.getText().toString()),
            Double.parseDouble(mLongitudeTxt.getText().toString()),
            Float.valueOf(mRadiusTxt.getText().toString()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                LatLng latLng = place.getLatLng();
                updateLatLongViews(String.valueOf(latLng.latitude), String.valueOf(latLng.longitude));
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Performs the geofencing task that was pending until location permission was granted.
     */
    @Override
    protected void performPendingGeofenceTask() {
        if (mPendingGeofenceTask == GeofenceUtil.PendingGeofenceTask.ADD) {
            save(mSaveBtn);
        }
    }
}
