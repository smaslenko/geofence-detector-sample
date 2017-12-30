package com.stone.geofence.detector.util;

import android.content.Context;
import android.net.wifi.WifiManager;

public class WifiUtil {

    public static String getSSID(Context context) {
        WifiManager wifiMgr = getWifiManager(context);
        if (wifiMgr != null) {
            return wifiMgr.getConnectionInfo().getSSID().replaceAll("[/\"]", "");
        }
        return "";
    }

    public static WifiManager getWifiManager(Context context) {
        return (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }
}
