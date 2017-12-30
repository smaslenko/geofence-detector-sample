package com.stone.geofence.detector;

import android.app.Application;

import com.stone.geofence.detector.di.component.AppComponent;
import com.stone.geofence.detector.di.component.DaggerAppComponent;
import com.stone.geofence.detector.di.module.AppModule;
import com.stone.geofence.detector.di.module.RepositoryModule;

public class GeofenceDetectorApp extends Application {

    private static AppComponent sAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        sAppComponent = DaggerAppComponent.builder()
            .appModule(new AppModule(this))
            .repositoryModule(new RepositoryModule())
            .build();
    }

    public static AppComponent getAppComponent() {
        return sAppComponent;
    }
}
