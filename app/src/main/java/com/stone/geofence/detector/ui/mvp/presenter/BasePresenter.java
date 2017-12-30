package com.stone.geofence.detector.ui.mvp.presenter;

import com.stone.geofence.detector.ui.mvp.model.BaseModel;
import com.stone.geofence.detector.ui.mvp.view.BaseView;

abstract class BasePresenter<V extends BaseView<M>, M extends BaseModel> {

    final String TAG = this.getClass().getSimpleName();

    V view;

    BasePresenter(V view) {
        this.view = view;
    }

    M model() {
        return view.model();
    }

}
