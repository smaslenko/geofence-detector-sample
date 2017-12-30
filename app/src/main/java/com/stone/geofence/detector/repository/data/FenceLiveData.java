package com.stone.geofence.detector.repository.data;

import android.arch.lifecycle.LiveData;

public class FenceLiveData extends LiveData<FenceStatus> {

    private FenceStatus mFenceStatus;

    public FenceLiveData() {
        mFenceStatus = new FenceStatus();
    }

    public void updateName(String name) {
        mFenceStatus.setFenceName(name);
    }

    public void updateGeoState(FenceStatus.GeoState state) {
        mFenceStatus.setGeoState(state);
        fireData();
    }

    public void updateWifiState(String wifiName, FenceStatus.WifiState state) {
        if (wifiName.equals(mFenceStatus.getFenceName())) {
            mFenceStatus.setWifiState(state);
        } else {
            mFenceStatus.setWifiState(FenceStatus.WifiState.Disconnected);
        }
        fireData();
    }

    private void fireData() {
        postValue(mFenceStatus);
    }
}
