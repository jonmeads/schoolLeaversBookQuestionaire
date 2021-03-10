package org.jpm.services;


import com.google.common.io.Files;
import org.apache.commons.lang3.RandomStringUtils;
import org.jpm.config.AppConstants;
import org.jpm.config.utils.FileUtils;
import org.jpm.exceptions.ServiceException;
import org.jpm.ui.data.PictureImage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class DetailsServiceAbstract {

    protected String getSaveLocation(String baseLocation) {
        String generatedString = RandomStringUtils.randomAlphanumeric(10);

        return baseLocation + File.separatorChar + generatedString;
    }


    protected void saveFormDataToLocation(String text, String prefix, String location) throws ServiceException {
        String newFile = "";
        try {
            File dir = new File(location);
            FileUtils.createDirectory(dir);

            File fileToSave = File.createTempFile(prefix, AppConstants.TEXT_FILE_EXT, dir);
            newFile = fileToSave.toString();

            BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave));
            writer.write(text);

            writer.close();

        } catch (Exception e) {
            throw new ServiceException("failed to write to file " + newFile);
        }
    }

    protected void saveImageToLocation(PictureImage image, String prefix, String location) throws ServiceException {

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
            throw new ServiceException("failed to write to file " + newFile);
        }
    }

}
