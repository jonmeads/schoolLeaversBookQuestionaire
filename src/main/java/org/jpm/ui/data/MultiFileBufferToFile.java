package org.jpm.ui.data;

import com.google.common.io.Files;
import com.vaadin.flow.component.upload.MultiFileReceiver;
import com.vaadin.flow.component.upload.receivers.FileData;
import org.jpm.config.AppConstants;
import org.jpm.models.Leaver;
import org.jpm.utils.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class MultiFileBufferToFile implements MultiFileReceiver {

    private final static Logger LOGGER = Logger.getLogger(MultiFileBufferToFile.class.getName());

    private Map<String, FileData> files = new HashMap();
    private Leaver leaver;

    public MultiFileBufferToFile(Leaver leaver) {
        this.leaver = leaver;
    }

    @Override
    public OutputStream receiveUpload(String fileName, String MIMEType) {
        FileOutputStream outputBuffer = createFileOutputStream(fileName);
        this.files.put(fileName, new FileData(fileName, MIMEType, outputBuffer));
        return outputBuffer;
    }

    public Set<String> getFiles() {
        return this.files.keySet();
    }

    public FileData getFileData(String fileName) {
        return (FileData)this.files.get(fileName);
    }

    public OutputStream getOutputBuffer(String fileName) {
        return this.files.containsKey(fileName) ? (OutputStream)((FileData)this.files.get(fileName)).getOutputBuffer() : createFileOutputStream("tempfile");
    }



    protected FileOutputStream createFileOutputStream(String fileName) {
        String newFilename = "";
        try {

            String ext = Files.getFileExtension(fileName);
            ext = ext != null ? ".".concat(ext) : null;

            String session = leaver != null && leaver.getSession() != null ? leaver.getSession() : "unknown";


            String dirString = AppConstants.OUTPUT_LOCATION_PHOTOS + File.separatorChar + session;

            File dir = new File(dirString);
            FileUtils.createDirectory(dir);

            File fileToSave = File.createTempFile("PhotoUpload", ext, dir);
            newFilename = fileToSave.toString();

            LOGGER.info("Creating photo for leaver: " + leaver + " and file: " + newFilename);

            return new FileOutputStream(fileToSave);
        } catch (IOException e) {

            System.out.println("Failed to create file output stream for: '" + fileName + "', saving to '" + newFilename + "'");
            e.printStackTrace();
            return null;
        }
    }




}
