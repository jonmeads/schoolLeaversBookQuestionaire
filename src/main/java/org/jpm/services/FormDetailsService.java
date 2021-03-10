package org.jpm.services;

import org.jpm.config.AppConstants;
import org.jpm.exceptions.ServiceException;
import org.jpm.models.FormDetails;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import java.io.Serializable;

@Service
public class FormDetailsService extends DetailsServiceAbstract implements Serializable {

    public FormDetailsService() {
    }

    @Async
    public ListenableFuture<Void> store(FormDetails formDetails) throws ServiceException {

        try {
            String formOutputLocation = getSaveLocation(AppConstants.OUTPUT_LOCATION_FORM);

            saveFormDataToLocation(formDetails.displayData(), "answersData", formOutputLocation);
            saveImageToLocation(formDetails.getStartPrepPicture(), "startPrep", formOutputLocation);
            saveImageToLocation(formDetails.getEndOfPrepPicture(), "endPrep", formOutputLocation);
            saveImageToLocation(formDetails.getHavingFunPicture(), "funPic", formOutputLocation);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new ServiceException("Failed to parse form data to file" + e.toString());
        }
        return AsyncResult.forValue(null);
    }

}
