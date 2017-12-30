package com.stone.geofence.detector.ui.mvp.presenter;

public interface IMainPresenter {

    void subscribe();

    void setGeofenceValues(String name, double latitude, double longitude, float radius);

}
