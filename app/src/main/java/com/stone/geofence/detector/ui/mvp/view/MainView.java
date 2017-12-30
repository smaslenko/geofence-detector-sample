package com.stone.geofence.detector.ui.mvp.view;

import com.stone.geofence.detector.ui.mvp.model.MainModel;

public interface MainView extends BaseView<MainModel> {

    void updateGeofenceStatus(String status);

    void updateLatLongViews(String latitude, String longitude);

    void updateRadiusView(String radius);

    void updateWifiView(String ssid);
}
