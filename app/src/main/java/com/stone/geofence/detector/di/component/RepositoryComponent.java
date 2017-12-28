package com.stone.geofence.detector.di.component;

import com.stone.geofence.detector.di.module.AppModule;
import com.stone.geofence.detector.di.module.RepositoryModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, RepositoryModule.class})
public interface RepositoryComponent {

//    void inject(HomeActivity activity);
//    void inject(DeviceFragment deviceFragment);
}
