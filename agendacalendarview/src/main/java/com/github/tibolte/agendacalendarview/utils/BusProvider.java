package com.github.tibolte.agendacalendarview.utils;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

public class BusProvider {

    public static BusProvider mInstance;

    private final Subject<Object, Object> mBus = new SerializedSubject<>(PublishSubject.create());

    // region Constructors

    public static BusProvider getInstance() {
        if (mInstance == null) {
            mInstance = new BusProvider();
        }
        return mInstance;
    }

    // endregion

    // region Public methods

    public void send(Object object) {
        mBus.onNext(object);
    }

    public Observable<Object> toObserverable() {
        return mBus;
    }

    // endregion
}
