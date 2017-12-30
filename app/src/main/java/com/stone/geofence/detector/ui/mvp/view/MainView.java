package com.stone.geofence.detector.ui.mvp.view;

import com.stone.geofence.detector.ui.mvp.model.MainModel;

public interface MainView extends BaseView<MainModel> {

    void updateGeofenceStatus(String name, String status);
}
