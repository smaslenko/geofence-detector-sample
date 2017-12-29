package com.stone.geofence.detector.di.component;

import com.stone.geofence.detector.di.module.AppModule;
import com.stone.geofence.detector.di.module.RepositoryModule;
import com.stone.geofence.detector.provider.GeofenceTransitionProviderService;
import com.stone.geofence.detector.ui.MainActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, RepositoryModule.class})
public interface RepositoryComponent {

    void inject(MainActivity activity);
    void inject(GeofenceTransitionProviderService service);
}
