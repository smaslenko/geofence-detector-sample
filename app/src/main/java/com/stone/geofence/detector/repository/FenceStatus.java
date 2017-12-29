package com.stone.geofence.detector.repository;

public class FenceStatus {

    public enum State {
        In,
        Out,
        Error
    }

    private String fenceName;
    private State fenceState;

    public FenceStatus(String fenceName, State fenceState) {
        this.fenceName = fenceName;
        this.fenceState = fenceState;
    }

    public String getFenceName() {
        return fenceName;
    }

    public void setFenceName(String fenceName) {
        this.fenceName = fenceName;
    }

    public State getFenceState() {
        return fenceState;
    }

    public void setFenceState(State fenceState) {
        this.fenceState = fenceState;
    }
}
