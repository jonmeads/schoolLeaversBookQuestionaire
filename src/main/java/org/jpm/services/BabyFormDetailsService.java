package org.jpm.services;

import org.jpm.config.AppConstants;
import org.jpm.exceptions.ServiceException;
import org.jpm.models.BabyDetails;
import org.jpm.services.dao.JdbcLeaversDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import java.io.Serializable;
import java.util.logging.Logger;

@Service
public class BabyFormDetailsService extends DetailsServiceAbstract implements Serializable {

    private final static Logger LOGGER = Logger.getLogger(BabyFormDetailsService.class.getName());

    private JdbcLeaversDao jdbcLeaversDao;


    public BabyFormDetailsService(@Autowired JdbcLeaversDao jdbcLeaversDao) {
        this.jdbcLeaversDao = jdbcLeaversDao;
    }

    @Async
    public ListenableFuture<Void> store(BabyDetails babyDetails, String session) throws ServiceException {

        LOGGER.info("Saving data");

        String formOutputLocation = getSaveLocation(AppConstants.OUTPUT_LOCATION_BABY, session);

        saveFormDataToLocation(babyDetails.displayData(), "babyName", formOutputLocation);
        saveImageToLocation(babyDetails.getBabyPicture(), "babyPhoto", formOutputLocation);

        jdbcLeaversDao.saveBaby(session, babyDetails.getFullname());

        return AsyncResult.forValue(null);
    }

}
