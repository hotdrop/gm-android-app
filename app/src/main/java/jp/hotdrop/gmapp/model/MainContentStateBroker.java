package jp.hotdrop.gmapp.model;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

public class MainContentStateBroker {

    private final Subject<Page, Page> sj = new SerializedSubject<>(PublishSubject.create());

    public Observable<Page> observe() {
        return sj;
    }
}
