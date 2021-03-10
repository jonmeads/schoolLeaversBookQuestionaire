package org.jpm.services;

import org.jpm.config.AppConstants;
import org.jpm.exceptions.ServiceException;
import org.jpm.models.BabyDetails;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import java.io.Serializable;

@Service
public class BabyFormDetailsService extends DetailsServiceAbstract implements Serializable {

    public BabyFormDetailsService() {
    }

    @Async
    public ListenableFuture<Void> store(BabyDetails babyDetails) throws ServiceException {

        String formOutputLocation = getSaveLocation(AppConstants.OUTPUT_LOCATION_BABY);

        saveFormDataToLocation(babyDetails.displayData(), "babyName", formOutputLocation);
        saveImageToLocation(babyDetails.getBabyPicture(), "babyPhoto", formOutputLocation);

        return AsyncResult.forValue(null);
    }

}