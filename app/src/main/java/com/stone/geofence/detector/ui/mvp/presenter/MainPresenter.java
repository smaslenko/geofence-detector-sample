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
    public void setGeofenceValues(String name, double latitude, double longitude, float radius) {
        GeofenceData data = new GeofenceData();
        data.setName(name);
        data.setLatitude(latitude);
        data.setLongitude(longitude);
        data.setRadius(radius);

        model().addGeofence(data);

        model().subscribeGeofence().observe(view.lifecycleOwner(), fenceStatus -> {
            if (fenceStatus != null) {
                String text = String.format("%1$s, %2$s, %3$s", fenceStatus.getFenceName(), fenceStatus.getGeoState(), fenceStatus.getWifiState());
                boolean connected = fenceStatus.getGeoState() == FenceStatus.GeoState.In || fenceStatus.getWifiState() == FenceStatus.WifiState.Connected;
                String statusStr = connected ? INSIDE : OUTSIDE;
                view.updateGeofenceStatus(fenceStatus.getFenceName(), statusStr);
            }
        });
    }
}
