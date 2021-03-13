package org.jpm.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileUtils {

    public static void createDirectory(File dir ) throws IOException {
        if(! dir.isDirectory()) {
            Files.createDirectories(dir.toPath());
        }
    }

}
