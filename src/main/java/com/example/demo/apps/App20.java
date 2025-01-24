package com.example.demo.apps;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

public class App20 {
    void main() {
        testObservable();
    }

    private static void testObservable() {
        // Create an Observable that emits a sequence of some great authors
        Observable<String> observable = Observable.create(emitter -> {
            emitter.onNext("Charles Dickens");
            emitter.onNext("Jonathan Swift");
            emitter.onNext("Chinua Achebe");
            emitter.onNext("William Shakespear");
            emitter.onNext("Thomas Paine");
            emitter.onComplete(); // Indicates that the observable has completed
        });

        // Create an Observer to subscribe to the Observable
        Observer<String> observer = new Observer<>() {
            @Override
            public void onSubscribe(Disposable d) {
                System.out.println("Subscribed to the Observable.");
            }

            @Override
            public void onNext(String value) {
                System.out.println("Received: " + value);
            }

            @Override
            public void onError(Throwable e) {
                System.err.println("Error: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                System.out.println("Observable completed.");
            }
        };

        // Subscribe the Observer to the Observable
        observable.subscribe(observer);
    }
}
