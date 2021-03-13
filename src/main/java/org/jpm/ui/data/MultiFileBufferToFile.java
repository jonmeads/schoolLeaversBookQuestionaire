package org.jpm.ui.data;

import com.google.common.io.Files;
import com.vaadin.flow.component.upload.MultiFileReceiver;
import com.vaadin.flow.component.upload.receivers.FileData;
import org.jpm.config.AppConstants;
import org.jpm.utils.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MultiFileBufferToFile implements MultiFileReceiver {

    private Map<String, FileData> files = new HashMap();

    public MultiFileBufferToFile() {
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


            File dir = new File(AppConstants.OUTPUT_LOCATION_PHOTOS);
            FileUtils.createDirectory(dir);

            File fileToSave = File.createTempFile("PhotoUpload", ext, dir);
            newFilename = fileToSave.toString();

            return new FileOutputStream(fileToSave);
        } catch (IOException e) {

            System.out.println("Failed to create file output stream for: '" + fileName + "', saving to '" + newFilename + "'");
            e.printStackTrace();
            return null;
        }
    }




}
