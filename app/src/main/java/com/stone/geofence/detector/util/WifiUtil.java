package com.stone.geofence.detector.util;

import android.content.Context;
import android.net.wifi.WifiInfo;
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

    public static boolean isConnected(Context context) {
        WifiManager wifiManager = getWifiManager(context);
        if (wifiManager != null && wifiManager.isWifiEnabled()) { // Wi-Fi adapter is ON
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            return wifiInfo.getNetworkId() != -1;
        } else {
            return false; // Wi-Fi adapter is OFF
        }
    }
}
