package com.stone.geofence.detector.ui.mvp.presenter;

import com.stone.geofence.detector.db.model.GeofenceData;
import com.stone.geofence.detector.repository.data.FenceStatus;
import com.stone.geofence.detector.ui.mvp.model.MainModel;
import com.stone.geofence.detector.ui.mvp.view.MainView;

public class MainPresenter extends BasePresenter<MainView, MainModel> implements IMainPresenter {

    private final static String INSIDE = "INSIDE";
    private final static String OUTSIDE = "OUTSIDE";

    public MainPresenter(MainView view) {
        super(view);
    }

    @Override
    public void subscribe() {
        model().subscribeStoredData().observe(view.lifecycleOwner(), geofenceData -> {
            if (geofenceData != null) {
                view.updateLatLongViews(
                    String.valueOf(geofenceData.getLatitude()),
                    String.valueOf(geofenceData.getLongitude()));
                view.updateRadiusView(String.valueOf(geofenceData.getRadius()));
                view.updateWifiView(geofenceData.getName());
            }
        });
        model().subscribeGeofence().observe(view.lifecycleOwner(), fenceStatus -> {
            String status = OUTSIDE;
            if (fenceStatus != null) {
                boolean connected = fenceStatus.getGeoState() == FenceStatus.GeoState.In || fenceStatus.getWifiState() == FenceStatus.WifiState.Connected;
                status = connected ? INSIDE : OUTSIDE;
            }
            view.updateGeofenceStatus(status);
        });
    }

    @Override
    public void setGeofenceValues(String name, double latitude, double longitude, float radius) {
        GeofenceData data = new GeofenceData();
        data.setName(name);
        data.setLatitude(latitude);
        data.setLongitude(longitude);
        data.setRadius(radius);

        model().addGeofence(data);
    }
}
