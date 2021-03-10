package org.jpm.config;

import java.io.File;

public class AppConstants {

    public static final String AUTH_USER = getAuthUser();
    public static final String AUTH_PASS = getAuthPass();
    public static final String OUTPUT_LOCATION = getOutputLocation();

    public static final String OUTPUT_LOCATION_PHOTOS = getPhotoOutputLocation();
    public static final String OUTPUT_LOCATION_BABY = getBabyOutputLocation();
    public static final String OUTPUT_LOCATION_FORM = getFormOutputLocation();

    public static final String SUPPORT_CONTACT = getSupportContact();

    public static final String TEXT_FILE_EXT = ".txt";


    protected static String getAuthUser() {
        return getProperty("AUTH_USER", "bob");
    }

    protected static String getAuthPass() {
        return getProperty("AUTH_PASS", "passwd");
    }

    protected static String getOutputLocation() {
        return getProperty("OUTPUT_DIR", "D:\\DATA\\PHOTOS");
    }

    protected static String getSupportContact() {
        return getProperty("SUPPORT_CONTACT", "Bob the builder!");
    }

    public static String getPhotoOutputLocation() {
        return getOutputLocation() + File.separatorChar + "photos";
    }

    public static String getBabyOutputLocation() {
        return getOutputLocation() + File.separatorChar + "baby";
    }

    public static String getFormOutputLocation() {
        return getOutputLocation() + File.separatorChar + "forms";
    }

    protected static String getProperty(String token, String defaultValue) {
        String value = System.getenv(token);

        if (value == null || value.isEmpty()) {
            value = System.getProperty("AUTH_PASS");
        }

        if (value == null || value.isEmpty()) {
            value = defaultValue;
        }
        return value;
    }
}
