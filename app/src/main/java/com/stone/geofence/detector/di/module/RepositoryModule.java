package com.stone.geofence.detector.di.module;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;
import com.stone.geofence.detector.db.GeofenceDao;
import com.stone.geofence.detector.db.GeofenceDatabase;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = ViewModelModule.class)
public class RepositoryModule {

    @Singleton
    @Provides
    Executor provideSingleThreadExecutor() {
        return Executors.newSingleThreadExecutor();
    }

    @Singleton
    @Provides
    GeofenceDatabase provideDatabase(Application application) {
        return Room.databaseBuilder(application, GeofenceDatabase.class, "geo-db")
            .fallbackToDestructiveMigration()
            .build();
    }

    @Singleton
    @Provides
    GeofenceDao provideDao(GeofenceDatabase database) {
        return database.getHomeDao();
    }

    @Singleton
    @Provides
    GeofencingClient provideGeofencingClient(Application application) {
        return LocationServices.getGeofencingClient(application);
    }

    @Singleton
    @Provides
    FusedLocationProviderClient provideFusedLocationProviderClient(Application application) {
        return LocationServices.getFusedLocationProviderClient(application);
    }
}
