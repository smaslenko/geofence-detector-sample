package com.stone.geofence.detector.repository.data;

public class FenceStatus {

    public enum GeoState {
        In,
        Out,
        Error;

        private int errorCode;

        public int getErrorCode() {
            return errorCode;
        }

        public GeoState setErrorCode(int errorCode) {
            this.errorCode = errorCode;
            return this;
        }
    }

    public enum WifiState {
        Connected,
        Disconnected,
        Error
    }

    FenceStatus() {
    }

    FenceStatus(String fenceName) {
        this.fenceName = fenceName;
    }

    private String fenceName;
    private GeoState geoState = GeoState.Out;
    private WifiState wifiState = WifiState.Disconnected;

    public String getFenceName() {
        return fenceName;
    }

    public void setFenceName(String fenceName) {
        this.fenceName = fenceName;
    }

    public GeoState getGeoState() {
        return geoState;
    }

    public void setGeoState(GeoState geoState) {
        this.geoState = geoState;
    }

    public WifiState getWifiState() {
        return wifiState;
    }

    public void setWifiState(WifiState wifiState) {
        this.wifiState = wifiState;
    }
}
