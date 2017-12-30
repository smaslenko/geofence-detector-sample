package com.stone.geofence.detector.ui.mvp.view;

import android.arch.lifecycle.LifecycleOwner;

import com.stone.geofence.detector.ui.mvp.model.BaseModel;

public interface BaseView<M extends BaseModel> {

    M model();

    LifecycleOwner lifecycleOwner();
}
