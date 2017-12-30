package com.stone.geofence.detector.di.component;

import com.stone.geofence.detector.di.module.AppModule;
import com.stone.geofence.detector.di.module.RepositoryModule;
import com.stone.geofence.detector.receiver.GeofenceTransitionReceiver;
import com.stone.geofence.detector.receiver.WifiTransitionReceiver;
import com.stone.geofence.detector.ui.MainActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, RepositoryModule.class})
public interface AppComponent {

    void inject(MainActivity activity);

    void inject(GeofenceTransitionReceiver provider);

    void inject(WifiTransitionReceiver provider);
}
