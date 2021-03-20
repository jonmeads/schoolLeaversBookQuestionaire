package org.jpm.services;

import org.jpm.config.AppConstants;
import org.jpm.exceptions.ServiceException;
import org.jpm.models.FormDetails;
import org.jpm.models.Leaver;
import org.jpm.services.dao.JdbcLeaversDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import java.io.Serializable;
import java.util.logging.Logger;

@Service
public class FormDetailsService extends DetailsServiceAbstract implements Serializable {

    private final static Logger LOGGER = Logger.getLogger(FormDetailsService.class.getName());

    private JdbcLeaversDao jdbcLeaversDao;

    public FormDetailsService(@Autowired JdbcLeaversDao jdbcLeaversDao) {
        this.jdbcLeaversDao = jdbcLeaversDao;
    }

    @Async
    public ListenableFuture<Void> store(FormDetails formDetails, Leaver leaver) throws ServiceException {

        LOGGER.info("Starting save of questionaire data");
        try {
            if(leaver.getName() == null) {
                leaver.setName(formDetails.getFullname());
            }

            String formOutputLocation = getSaveLocation(AppConstants.OUTPUT_LOCATION_FORM, leaver.getSession());

            saveFormDataToLocation(formDetails.displayData(), "answersData", formOutputLocation);
            saveImageToLocation(formDetails.getStartPrepPicture(), "startPrep", formOutputLocation);
            saveImageToLocation(formDetails.getEndOfPrepPicture(), "endPrep", formOutputLocation);
            saveImageToLocation(formDetails.getHavingFunPicture(), "funPic", formOutputLocation);

            jdbcLeaversDao.saveForm(leaver.getSession(), formDetails.getFullname());
            leaver.setForm(1);

        } catch (Exception e) {
            LOGGER.severe("Failure saving questionaire data " + e);
            throw new ServiceException("Failed to parse form data to file" + e.toString());
        }
        return AsyncResult.forValue(null);
    }

}
