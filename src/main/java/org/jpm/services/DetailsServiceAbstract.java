package org.jpm.services;


import com.google.common.io.Files;
import org.apache.commons.lang3.RandomStringUtils;
import org.jpm.config.AppConstants;
import org.jpm.utils.FileUtils;
import org.jpm.exceptions.ServiceException;
import org.jpm.ui.data.PictureImage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.logging.Logger;

public class DetailsServiceAbstract {

    private final static Logger LOGGER = Logger.getLogger(DetailsServiceAbstract.class.getName());

    protected String getSaveLocation(String baseLocation, String session) {
        if(session == null || session.isEmpty()) {
            session = RandomStringUtils.randomAlphanumeric(10);
        }

        return baseLocation + File.separatorChar + session;
    }


    protected void saveFormDataToLocation(String text, String prefix, String location) throws ServiceException {
        String newFile = "";
        try {
            LOGGER.info("Saving form data to location: " + location);
            File dir = new File(location);
            FileUtils.createDirectory(dir);

            File fileToSave = File.createTempFile(prefix, AppConstants.TEXT_FILE_EXT, dir);
            newFile = fileToSave.toString();

            BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave));
            writer.write(text);

            writer.close();

        } catch (Exception e) {
            LOGGER.severe("Failed to saving form data while writing to file " + newFile);
            throw new ServiceException("failed to write to file " + newFile);
        }
    }

    protected void saveImageToLocation(PictureImage image, String prefix, String location) throws ServiceException {

        LOGGER.info("Saving image data to location: " + location);

        String newFile = "";
        try {
            String ext = Files.getFileExtension(image.getName());
            ext = ext != null ? ".".concat(ext) : null;

            File dir = new File(location);
            FileUtils.createDirectory(dir);

            File fileToSave = File.createTempFile(prefix, ext, dir);
            newFile = fileToSave.toString();
            Files.write(image.getImage(), fileToSave);

        } catch (Exception e) {
            LOGGER.severe("Failed to saving image data while writing to file " + newFile);
            throw new ServiceException("failed to write to file " + newFile);
        }
    }

}
