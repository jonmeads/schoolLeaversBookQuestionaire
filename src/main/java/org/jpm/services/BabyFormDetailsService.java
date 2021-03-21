package org.jpm.services;

import org.jpm.config.AppConstants;
import org.jpm.exceptions.ServiceException;
import org.jpm.models.BabyDetails;
import org.jpm.models.Leaver;
import org.jpm.services.dao.JdbcLeaversDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

@Service
public class BabyFormDetailsService extends DetailsServiceAbstract implements Serializable {

    private final static Logger LOGGER = Logger.getLogger(BabyFormDetailsService.class.getName());

    private JdbcLeaversDao jdbcLeaversDao;


    public BabyFormDetailsService(@Autowired JdbcLeaversDao jdbcLeaversDao) {
        this.jdbcLeaversDao = jdbcLeaversDao;
    }

    @Async
    public ListenableFuture<Void> store(BabyDetails babyDetails, Leaver leaver) throws ServiceException {

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        try {
            LOGGER.info("Saving data");

            if (leaver.getName() == null) {
                leaver.setName(babyDetails.getFullname());
            }

            String formOutputLocation = getSaveLocation(AppConstants.OUTPUT_LOCATION_BABY, leaver.getSession());

            saveFormDataToLocation(babyDetails.displayData(), "babyName", formOutputLocation);
            saveImageToLocation(babyDetails.getBabyPicture(), "babyPhoto", formOutputLocation);

            // save to db in background
            executorService.execute(() -> jdbcLeaversDao.saveBaby(leaver.getSession(), babyDetails.getFullname()));

            leaver.setBaby(1);
        } catch (Exception e) {
            LOGGER.severe("failed" + e);
        }

        return AsyncResult.forValue(null);
    }

}
