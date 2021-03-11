package org.jpm.services;

import org.jpm.config.AppConstants;
import org.jpm.exceptions.ServiceException;
import org.jpm.models.FormDetails;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import java.io.Serializable;
import java.util.logging.Logger;

@Service
public class FormDetailsService extends DetailsServiceAbstract implements Serializable {

    private final static Logger LOGGER = Logger.getLogger(FormDetailsService.class.getName());

    public FormDetailsService() {
    }

    @Async
    public ListenableFuture<Void> store(FormDetails formDetails) throws ServiceException {

        LOGGER.info("starting save of questionaire data");
        try {
            String formOutputLocation = getSaveLocation(AppConstants.OUTPUT_LOCATION_FORM);

            saveFormDataToLocation(formDetails.displayData(), "answersData", formOutputLocation);
            saveImageToLocation(formDetails.getStartPrepPicture(), "startPrep", formOutputLocation);
            saveImageToLocation(formDetails.getEndOfPrepPicture(), "endPrep", formOutputLocation);
            saveImageToLocation(formDetails.getHavingFunPicture(), "funPic", formOutputLocation);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            LOGGER.severe("Failure saving questionaire data " + e);
            throw new ServiceException("Failed to parse form data to file" + e.toString());
        }
        return AsyncResult.forValue(null);
    }

}
