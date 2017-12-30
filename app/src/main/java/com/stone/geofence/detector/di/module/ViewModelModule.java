package com.stone.geofence.detector.di.module;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.stone.geofence.detector.ui.mvp.model.MainModel;
import com.stone.geofence.detector.ui.mvp.model.ModelFactory;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@SuppressWarnings("WeakerAccess")
@Module
public abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(MainModel.class)
    abstract ViewModel bindHomeViewModel(MainModel homeViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(ModelFactory factory);
}
