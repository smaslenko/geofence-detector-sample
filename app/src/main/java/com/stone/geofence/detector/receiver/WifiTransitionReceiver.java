package com.stone.geofence.detector.receiver;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.stone.geofence.detector.GeofenceDetectorApp;
import com.stone.geofence.detector.repository.GeofenceRepo;
import com.stone.geofence.detector.repository.data.FenceStatus;
import com.stone.geofence.detector.util.WifiUtil;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class WifiTransitionReceiver extends BroadcastReceiver {
    public final static String INITIAL_WIFI_TRANSITION_ACTION = "com.stone.geofence.detector.receiver.INITIAL_WIFI_TRANSITION_ACTION";
    public final static String EXTRA_KEY_CONNECTED = INITIAL_WIFI_TRANSITION_ACTION + ".connected";

    @Inject
    GeofenceRepo mGeofenceRepo;

    @Override
    public void onReceive(Context context, Intent intent) {
        GeofenceDetectorApp.getAppComponent().inject(this);

        String action = intent.getAction();

        String name = WifiUtil.getSSID(context);

        FenceStatus.WifiState state = null;

        if (INITIAL_WIFI_TRANSITION_ACTION.equals(action)) {
            state = intent.getBooleanExtra(EXTRA_KEY_CONNECTED, false) ? FenceStatus.WifiState.Connected : FenceStatus.WifiState.Disconnected;
        }
        if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
            NetworkInfo netInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (ConnectivityManager.TYPE_WIFI == netInfo.getType()) {
                state = netInfo.isConnected() ? FenceStatus.WifiState.Connected : FenceStatus.WifiState.Disconnected;
            }
        }

        if (state != null) {
            mGeofenceRepo.updateWifiState(name, state);
            Log.i("WifiTransitionReceiver", "Wi-Fi status change: " + name + " - state=" + state);
        }
    }

    /**
     * Helper class that checks initial Wifi state and broadcasting it to receiver
     */
    @Singleton
    public static class InitialWifiStateProvider {
        Application application;

        @Inject
        InitialWifiStateProvider(Application application) {
            this.application = application;
        }

        public void provide() {
            WifiManager wifiManager = WifiUtil.getWifiManager(application);
            boolean connected = false;
            if (wifiManager != null) {
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                if (wifiInfo.getSupplicantState() == SupplicantState.COMPLETED) {
                    connected = true;
                }
            }
            Intent intent = new Intent(INITIAL_WIFI_TRANSITION_ACTION);
            intent.putExtra(EXTRA_KEY_CONNECTED, connected);
            application.getApplicationContext().sendBroadcast(intent);
        }
    }
}
