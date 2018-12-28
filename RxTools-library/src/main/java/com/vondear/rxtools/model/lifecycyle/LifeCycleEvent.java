package com.vondear.rxtools.model.lifecycyle;

public enum LifeCycleEvent {
    CREATE,
    START,
    RESUME,
    PAUSE,
    STOP,
    RESTART,
    DESTROY,

    // Fragment Events
    ATTACH,
    CREATE_VIEW,
    DESTROY_VIEW,
    DETACH
}