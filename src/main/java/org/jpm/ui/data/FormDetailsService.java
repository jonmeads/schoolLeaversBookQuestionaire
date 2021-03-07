package org.jpm.ui.data;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@Service
public class FormDetailsService implements Serializable {

    private String previousHandle;


    @Async
    public ListenableFuture<Void> store(FormDetails formDetails) throws ServiceException {

        // TODO save all this
        // Here you can store the object into the DB, call REST services, etc.
        try {
            Thread.sleep(5000);

        } catch (InterruptedException e) {
            return AsyncResult.forExecutionException(new RuntimeException("Error"));
        }

        // for demo purposes, always fail first try
        if (previousHandle == null || !previousHandle.equals(formDetails.getFirstname())) {
            previousHandle = formDetails.getFirstname();
            throw new ServiceException("This exception simulates an error in the backend, and is intentional. Please try to submit the form again.");
        }

        return AsyncResult.forValue(null);
    }

    /**
     * A validator method for User handles.
     *
     * @return <code>null</code> if the handle is OK to use or an error message if
     *         it is not OK.
     */
    public String validateHandle(String handle) {

        if (handle == null) {
            return "Handle can't be empty";
        }
        if (handle.length() < 4) {
            return "Handle can't be shorter than 4 characters";
        }
        List<String> reservedHandles = Arrays.asList("admin", "test", "null", "void");
        if (reservedHandles.contains(handle)) {
            return String.format("'%s' is not available as a handle", handle);
        }

        return null;
    }

    /**
     * Utility Exception class that we can use in the frontend to show that
     * something went wrong during save.
     */
    public static class ServiceException extends Exception {
        public ServiceException(String msg) {
            super(msg);
        }
    }
}
