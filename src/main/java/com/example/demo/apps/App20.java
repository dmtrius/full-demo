package com.example.demo.apps;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Slf4j
public class App20 {
    void main() {
//        testObservable();
        testFutures();
    }

    @SneakyThrows
    private static void testFutures() {
        // Create an ExecutorService with a single thread
        try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
            // Submit a task to the ExecutorService
            Future<Integer> future = executorService.submit(() -> {
                TimeUnit.SECONDS.sleep(1); // Simulate a time-consuming operation
                return 9; // The result of the computation
            });
            // Continue with other tasks while the above task is running
            log.info("working...");
            Thread.currentThread().join(2000);
            // Check if the task is done
            if (future.isDone()) {
                try {
                    // Retrieve the result when the task is complete
                    Integer result = future.get();
                    log.info("Task completed. Result: {}", result);
                } catch (InterruptedException | ExecutionException e) {
                    log.error(e.getMessage());
                }
            } else {
                log.info("Task is not yet complete. Continuing with other tasks...");
            }

            // Shutdown the ExecutorService when done
            executorService.shutdown();
        }
    }

    private static void testObservable() {
        // Create an Observable that emits a sequence of some great authors
        Observable<String> observable
                = Observable.create(emitter -> {
            emitter.onNext("Charles Dickens");
            emitter.onNext("Jonathan Swift");
            emitter.onNext("Chinua Achebe");
            emitter.onNext("William Shakespear");
            emitter.onNext("Thomas Paine");
            emitter.onNext("George Orwell");
            emitter.onComplete(); // Indicates that the observable has completed
        });

        // Create an Observer to subscribe to the Observable
        Observer<String> observer = new Observer<>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                log.info("Subscribed to the Observable.");
            }

            @Override
            public void onNext(@NonNull String value) {
                log.info("Received: {}", value);
            }

            @Override
            public void onError(Throwable e) {
                log.error("Error: {}", e.getMessage());
            }

            @Override
            public void onComplete() {
                log.info("Observable completed.");
            }
        };

        // Subscribe the Observer to the Observable
        observable.subscribe(observer);
    }
}
