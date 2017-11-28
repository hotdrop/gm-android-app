package jp.hotdrop.gmapp.model;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

public class MainContentStateBroker {

    // SerializedSubjectで複数のスレッドから呼ばれても大丈夫
    // PublishSubjectはSubscriber側の同一メソッドにsubscribeが伝搬する。
    // なのでonNextやonErrorと言った呼び出しがそのまま伝搬して返る
    // リスナーと同じような感じで一番わかりやすい
    // 例えば 以下の通り
    /*
    private void sampleSubject() {
      Subject<String, String> subject = PublishSubject.create();
      subject.onNext("Hoge");
      subject.subscribe(System.out::println); //Hogeと出力される
    }
    */
    private final Subject<Page, Page> sj = new SerializedSubject<>(PublishSubject.create());

    // Page型専用のobserve
    public Observable<Page> observe() {
        return sj;
    }
}
