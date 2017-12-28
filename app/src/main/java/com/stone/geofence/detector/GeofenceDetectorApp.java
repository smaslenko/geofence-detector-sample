package com.stone.geofence.detector;

import android.app.Application;

import com.stone.geofence.detector.di.component.DaggerRepositoryComponent;
import com.stone.geofence.detector.di.component.RepositoryComponent;
import com.stone.geofence.detector.di.module.AppModule;
import com.stone.geofence.detector.di.module.RepositoryModule;

public class GeofenceDetectorApp extends Application {

    private RepositoryComponent mRepositoryComponent;


    @Override
    public void onCreate() {
        super.onCreate();

//        mNetComponent = DaggerNetComponent.builder()
//            .appModule(new AppModule(this))
//            .netModule(new NetModule(NetClientManager.BASE_URL, NetClientManager.ACCESS_TOKEN, NetClientManager.WEATHER_URL))
//            .build();

        mRepositoryComponent = DaggerRepositoryComponent.builder()
            .appModule(new AppModule(this))
            .repositoryModule(new RepositoryModule())
            .build();
    }
}
