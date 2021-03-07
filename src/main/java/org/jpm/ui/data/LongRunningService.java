package org.jpm.ui.data;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

@Service
public class LongRunningService {

    @Async
    public ListenableFuture<Void> longRunningTask() {
        System.out.println("Execute method with configured executor - " + Thread.currentThread().getName());
        try {
            Thread.sleep(6000);
            System.out.println("Finished method with configured executor - " + Thread.currentThread().getName());
        } catch (InterruptedException e) {
            return AsyncResult.forExecutionException(new RuntimeException("Error"));
        }

        return AsyncResult.forValue(null);
    }

}
