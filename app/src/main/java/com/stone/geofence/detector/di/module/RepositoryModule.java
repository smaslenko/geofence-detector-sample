package com.stone.geofence.detector.di.module;

import android.app.Application;
import android.app.PendingIntent;
import android.arch.persistence.room.Room;
import android.content.Intent;

import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.stone.geofence.detector.db.GeofenceDao;
import com.stone.geofence.detector.db.GeofenceDatabase;
import com.stone.geofence.detector.provider.GeofenceTransitionProviderService;

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
    PendingIntent provideGeofencePendingIntent(Application application) {
        Intent intent = new Intent(application, GeofenceTransitionProviderService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        return PendingIntent.getService(application, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Singleton
    @Provides
    GeofencingRequest.Builder provideGeofencingRequestBuilder() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        return builder;
    }
}
