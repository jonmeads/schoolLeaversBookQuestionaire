package org.jpm.ui.components;

import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.upload.SucceededEvent;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.server.StreamResource;
import org.jpm.ui.data.PictureImage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

/**
 * A custom Vaadin component that allows users to upload an avatar image.
 * <p>
 * Can be used with the {@link Binder}. Note the type below; this Component can
 * only modify {@link PictureImage} data.
 */
public class PictureField extends CustomField<PictureImage> {

    /**
     * We store the value here.
     */
    private PictureImage value;

    /**
     * This is where any upload content will be written to
     */
    private ByteArrayOutputStream outputStream;

    private Image currentImage;
    private Upload upload;

    public PictureField(String caption) {
        this();
        setLabel(caption);
    }

    public PictureField() {

        // <img> that shows the current avatar
        currentImage = new Image();
        currentImage.setAlt("picture image");
        currentImage.setMaxHeight("100px");
        currentImage.getStyle().set("margin-right", "15px");
        currentImage.setVisible(false); // see updateImage()

        // create the upload component and delegate actions to the receiveUpload method
        upload = new Upload(this::receiveUpload);
        upload.getStyle().set("flex-grow", "1");

        // listen to state changes
        upload.addSucceededListener(e -> uploadSuccess(e));

        upload.addFailedListener(e -> setFailed(e.getReason().getMessage()));
        upload.addFileRejectedListener(e -> setFailed(e.getErrorMessage()));

        // only allow images to be uploaded
        upload.setAcceptedFileTypes("image/*");

        // only allow single file at a time
        upload.setMaxFiles(1);

        // set max file size to 10 MB
        upload.setMaxFileSize(10 * 1024 * 1024);

        // component layouting
        Div wrapper = new Div();
        wrapper.add(currentImage, upload);
        wrapper.getStyle().set("display", "flex");
        add(wrapper);
    }

    /*
     * We need to implement this method so that this class works with the Binder.
     * This method should return the current value.
     */
    @Override
    protected PictureImage generateModelValue() {
        return value;
    }

    /*
     * We need to implement this method so that this class works with the Binder.
     * This method should store the given value and update the visuals to the new
     * value.
     */
    @Override
    protected void setPresentationValue(PictureImage newPresentationValue) {
        value = newPresentationValue;
        updateImage();
    }

    /**
     * Called when a user initializes an upload.
     * <p>
     * We prepare the bean and a destination for the binary data; Vaadin will take
     * care of the actual network operations.
     */
    private OutputStream receiveUpload(String fileName, String mimeType) {

        // clear old errors for better user experience
        setInvalid(false);

        // create new value bean to store the data
        value = new PictureImage();
        value.setName(fileName);
        value.setMime(mimeType);

        // set up receiving Stream
        outputStream = new ByteArrayOutputStream();
        return outputStream;
    }

    /**
     * Called when an upload is successfully completed.
     */
    private void uploadSuccess(SucceededEvent e) {

        // store the binary data into our bean
        value.setImage(outputStream.toByteArray());

        // fire value changes so that Binder can do its thing
        setModelValue(value, true);

        // show the new image
        updateImage();

        // clear the upload component 'finished files' list for a cleaner appearance.
        // there is yet no API for it on the server side, see
        // https://github.com/vaadin/vaadin-upload-flow/issues/96
        upload.getElement().executeJs("this.files=[]");
    }

    /**
     * Shows an error message to the user.
     */
    private void setFailed(String message) {
        setInvalid(true);
        setErrorMessage(message);
    }

    /**
     * Updates avatar image content or hide if empty
     */
    private void updateImage() {
        if (value != null && value.getImage() != null) {
            currentImage.setSrc(new StreamResource("picture", () -> new ByteArrayInputStream(value.getImage())));
            currentImage.setVisible(true);
        } else {
            currentImage.setSrc("");
            currentImage.setVisible(false);
        }
    }
}
