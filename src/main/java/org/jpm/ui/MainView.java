package org.jpm.ui;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.progressbar.ProgressBarVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.Route;
import org.jpm.config.AppConstants;
import org.jpm.exceptions.ServiceException;
import org.jpm.models.BabyDetails;
import org.jpm.models.FormDetails;
import org.jpm.models.FormQuestion;
import org.jpm.services.BabyFormDetailsService;
import org.jpm.services.FormDetailsService;
import org.jpm.ui.components.PictureField;
import org.jpm.ui.data.MultiFileBufferToFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.concurrent.ListenableFutureCallback;


@JsModule("./js/theme-selector.js")
@Route("")
@Push
public class MainView extends VerticalLayout {

    private static final String MIN_WIDTH = "490px";
    private static final String MAX_WIDTH = "900px";
    private static final String MIN_BUTTON_WIDTH = "100px";


    private FormDetailsService formDetailsService;
    private BabyFormDetailsService babyFormDetailsService;
    private BeanValidationBinder<FormDetails> formDetailsBeanValidationBinder;
    private BeanValidationBinder<BabyDetails> babyDetailsBeanValidationBinder;


    public MainView(@Autowired FormDetailsService formDetailsService, @Autowired BabyFormDetailsService babyFormDetailsService) {

        this.formDetailsService = formDetailsService;
        this.babyFormDetailsService = babyFormDetailsService;
        formDetailsBeanValidationBinder = new BeanValidationBinder<>(FormDetails.class);
        babyDetailsBeanValidationBinder = new BeanValidationBinder<>(BabyDetails.class);

        constructUI();
    }


    protected void constructUI() {
        // form
        Button formSubmitButton = new Button("Save");
        Button formCancel = new Button("Cancel");
        VerticalLayout formLayout = createForm(formDetailsBeanValidationBinder, formSubmitButton, formCancel);


        // menu
        Button formButton = new Button("Fill in Questionnaire");
        Button babyButton = new Button("Provide Baby Photo");
        Button photosButton = new Button("Upload Additional Photos for the Book");
        Button quitButton = new Button("Finished");
        VerticalLayout menuLayout = createMenu(formButton,babyButton, photosButton, quitButton);

        // baby
        Button babySaveButton = new Button("Save");
        Button babyCancel = new Button("Cancel");
        VerticalLayout babyLayout = createBaby(babyDetailsBeanValidationBinder, babySaveButton, babyCancel);

        // photos
        Button photoCancel = new Button("Finished");
        VerticalLayout photoLayout = createPhoto(photoCancel);

        // save
        ProgressBar progressBar = new ProgressBar(0,10);
        VerticalLayout saveFormLayout = createFormSave(progressBar);


        // login
        LoginOverlay loginComponent = getLoginOverlay();
        loginComponent.addLoginListener(e -> {
            if(AppConstants.AUTH_PASS.equals(e.getPassword()) && AppConstants.AUTH_USER.equals(e.getUsername())) {
                loginComponent.close();
                menuLayout.setVisible(true);
            } else {
                loginComponent.setError(true);
                loginComponent.setEnabled(true);

            }
        });
        loginComponent.setOpened(true);


        // Add the layouts to the page
        add(loginComponent);
        add(menuLayout);
        add(formLayout);
        add(babyLayout);
        add(saveFormLayout);
        add(photoLayout);


        // listeners
        formButton.addClickListener(e -> {
            menuLayout.setVisible(false);
            formLayout.setVisible(true);
        });

        babyButton.addClickListener(e -> {
            menuLayout.setVisible(false);
            babyLayout.setVisible(true);
        });

        photosButton.addClickListener(e -> {
            menuLayout.setVisible(false);
            photoLayout.setVisible(true);
        });

        quitButton.addClickListener(e -> done());

        babyCancel.addClickListener(e -> {
            babyLayout.setVisible(false);
            menuLayout.setVisible(true);
        });

        photoCancel.addClickListener(e -> {
            photoLayout.setVisible(false);
            menuLayout.setVisible(true);
        });

        formCancel.addClickListener(e -> {
            formLayout.setVisible(false);
            menuLayout.setVisible(true);
        });

        babySaveButton.addClickListener(e -> {
            babyLayout.setVisible(false);
            saveFormLayout.setVisible(true);
            UI ui = UI.getCurrent();

            try {

                BabyDetails detailsBean = new BabyDetails();
                babyDetailsBeanValidationBinder.writeBean(detailsBean);

                babyFormDetailsService
                    .store(detailsBean)
                    .addCallback(new ListenableFutureCallback<>() {

                    @Override
                    public void onSuccess(Void unused) {
                        ui.access(
                            () -> {
                                saveFormLayout.setVisible(false);
                                menuLayout.setVisible(true);
                                showSuccess("Baby photo data successfully saved");
                            }
                        );
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        ui.access(
                            () -> {
                                saveFormLayout.setVisible(false);
                                babyLayout.setVisible(true);
                                showError("Failed to save the responses");
                            }
                        );
                    }
                });
            } catch (ValidationException | ServiceException formException) {
                saveFormLayout.setVisible(false);
                babyLayout.setVisible(true);
                showError("Failed to save the responses");
            }
        });

        formSubmitButton.addClickListener(e -> {
            UI ui = UI.getCurrent();
            formLayout.setVisible(false);
            saveFormLayout.setVisible(true);

            try {
                FormDetails detailsBean = new FormDetails();
                formDetailsBeanValidationBinder.writeBean(detailsBean);
                formDetailsService.store(detailsBean).addCallback(new ListenableFutureCallback<>() {

                            @Override
                            public void onSuccess(Void unused) {
                                ui.access(() -> {
                                            saveFormLayout.setVisible(false);
                                            menuLayout.setVisible(true);
                                            showSuccess(detailsBean);
                                        }
                                );
                            }

                            @Override
                            public void onFailure(Throwable throwable) {
                                ui.access(() -> {
                                            saveFormLayout.setVisible(false);
                                            formLayout.setVisible(true);
                                            showError("Failed to save the responses");
                                        }
                                );
                            }
                        });

            } catch (ValidationException | ServiceException formException) {
                saveFormLayout.setVisible(false);
                formLayout.setVisible(true);
                showError("Failed to save the responses");
            }

        });

    }


    private VerticalLayout createFormSave(ProgressBar progressBar) {

        VerticalLayout savingLayout = new VerticalLayout();
        savingLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        savingLayout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        H2 title = new H2("Saving data, please wait...");

        progressBar.addThemeVariants(ProgressBarVariant.LUMO_CONTRAST);
        progressBar.setMaxWidth(MIN_WIDTH);
        progressBar.setIndeterminate(true);

        savingLayout.add(title);
        savingLayout.add(progressBar);

        savingLayout.setVisible(false);
        return savingLayout;
    }

    private LoginOverlay getLoginOverlay() {
        LoginOverlay loginComponent = new LoginOverlay();
        loginComponent.setTitle("Prep 6");
        loginComponent.setDescription("Leavers 2021");
        loginComponent.setForgotPasswordButtonVisible(false);

        LoginI18n i18n = LoginI18n.createDefault();
        loginComponent.setI18n(i18n);

        return loginComponent;
    }

    /**
     * We call this method when form submission has succeeded
     */
    private void showSuccess(FormDetails detailsBean) {
        Notification notification = Notification.show("Questionaire data saved, thanks for providing the data for " + detailsBean.getFirstname() + " " + detailsBean.getLastname());
        notification.setDuration(5000);
        notification.setPosition(Notification.Position.TOP_START);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

    }

    private void showSuccess(String message) {
        Notification notification = Notification.show(message);
        notification.setDuration(5000);
        notification.setPosition(Notification.Position.TOP_START);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

    }

    private void showError(String message) {
        String fullMessage = message + ", please retry or contact " + AppConstants.SUPPORT_CONTACT;
        Notification notification = Notification.show(fullMessage, 5000, Notification.Position.TOP_START);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }

    private void done() {
        UI.getCurrent().getPage().reload();
    }


    private VerticalLayout createPhoto(Button photoCancel) {

        VerticalLayout verticalLayout = new VerticalLayout();

        verticalLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        verticalLayout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        verticalLayout.setSpacing(true);

        H2 title = new H2("Upload Photos");

        verticalLayout.add(title);
        Label titleLabel = new Label("Please upload additional photos that we can add to the leavers book, multiple photos can be uploaded either via the upload button or by dragging the photos onto the page. Photos are limited to a maximum size of 10MB");

        HorizontalLayout header = new HorizontalLayout();
        header.add(titleLabel);
        header.setMaxWidth(MAX_WIDTH);
        verticalLayout.add(header);


        MultiFileBufferToFile buffer = new MultiFileBufferToFile();
        Upload upload = new Upload(buffer);

        upload.setMinWidth(MIN_WIDTH);
        upload.setMaxWidth(MAX_WIDTH);

        Div output = new Div();

        upload.addSucceededListener(event -> showSuccess("Uploaded photo " + event.getFileName()));
        upload.addFailedListener(event -> showError("Upload failed: " + event.getReason()));
        upload.addFileRejectedListener(event -> showError("Upload error: " + event.getErrorMessage()));

        verticalLayout.add(upload, output);
        verticalLayout.add(new Label(" "));
        verticalLayout.add(photoCancel);

        verticalLayout.setVisible(false);

        return verticalLayout;

    }

    private VerticalLayout createBaby(BeanValidationBinder<BabyDetails> binder, Button babySaveButton, Button cancel) {
        VerticalLayout babyVerticalLayout = new VerticalLayout();
        babyVerticalLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        babyVerticalLayout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        babyVerticalLayout.setSpacing(true);

        H2 title = new H2("Baby Photo");

        Text txt = new Text("Please upload a baby photo for Guess Who!");

        TextField firstnameField = new TextField("First name");
        TextField lastnameField = new TextField("Last name");
        PictureField babyPicture = new PictureField("Select baby photo");

        FormLayout formLayout = new FormLayout(firstnameField, lastnameField, babyPicture);
        assignFormLayoutSettings(formLayout, true);

        formLayout.setColspan(babyPicture, 2);

        babyVerticalLayout.add(title);
        babyVerticalLayout.add(txt);

        babyVerticalLayout.add(formLayout);
        babyVerticalLayout.add(new Label(""));

        babySaveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        FormLayout buttonLayout = new FormLayout(babySaveButton, cancel);
        assignFormLayoutSettings(buttonLayout, true);
        buttonLayout.setColspan(babySaveButton, 2);
        buttonLayout.setColspan(cancel, 2);
        cancel.setMinWidth(MIN_BUTTON_WIDTH);
        babySaveButton.setMinWidth(MIN_BUTTON_WIDTH);

        binder.forField(firstnameField).asRequired().bind("firstname");
        binder.forField(lastnameField).asRequired().bind("lastname");
        binder.forField(babyPicture).asRequired().bind("babyPicture");

        babyVerticalLayout.add(new Label(" "));
        babyVerticalLayout.add(buttonLayout);
        babyVerticalLayout.setPadding(true);

        babyVerticalLayout.setVisible(false);
        return babyVerticalLayout;
    }

    private VerticalLayout createMenu(Button formButton, Button babyButton, Button photosButton, Button quitButton) {
        VerticalLayout menuVerticalLayout = new VerticalLayout();
        menuVerticalLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        menuVerticalLayout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        menuVerticalLayout.setSpacing(true);

        H2 title = new H2("Prep 6 Leavers Menu");

        Label titleLabel = new Label("Thanks for logging into the leaver book site, this is to capture your childs information for their profile page in the book, along with the Guess Who baby quizz, please contact: " + AppConstants.SUPPORT_CONTACT + " if you have any issues");

        formButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        babyButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        photosButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        quitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);


        FormLayout formLayout = new FormLayout(formButton, babyButton, photosButton);
        assignFormLayoutSettings(formLayout, true);

        formLayout.setColspan(formButton, 2);
        formLayout.setColspan(babyButton, 2);
        formLayout.setColspan(photosButton, 2);

        menuVerticalLayout.add(title);
        HorizontalLayout header = new HorizontalLayout();
        header.add(titleLabel);
        header.setMaxWidth(MAX_WIDTH);
        menuVerticalLayout.add(header);

        menuVerticalLayout.add(new Label(" "));
        menuVerticalLayout.add(formLayout);
        menuVerticalLayout.add(new Label(" "));
        menuVerticalLayout.add(quitButton);

        menuVerticalLayout.setVisible(false);
        return menuVerticalLayout;

    }


    private VerticalLayout createForm(BeanValidationBinder<FormDetails> binder, Button submitButton, Button cancel) {

        VerticalLayout formVerticalLayout = new VerticalLayout();
        formVerticalLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        formVerticalLayout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        formVerticalLayout.setSpacing(true);

        // main fields
        H2 title = new H2("Leavers Book Questionnaire");

        Text txt1 = new Text("Please answer the following questions, these are for you profile page in the leavers book");

        TextField firstnameField = new TextField("First name");
        TextField lastnameField = new TextField("Last name");

        ComboBox<String> formComboBox = new ComboBox<>();
        formComboBox.setItems("Kells", "Derry", "Swords");
        formComboBox.setPlaceholder("Select your form");

        ComboBox<String> houseComboBox = new ComboBox<>();
        houseComboBox.setItems("Alban", "Becket", "Fisher", "More");
        houseComboBox.setPlaceholder("Select your house");

        TextField prefectRoleField = new TextField("Prefect Role");

        binder.forField(firstnameField).asRequired().bind("firstname");
        binder.forField(lastnameField).asRequired().bind("lastname");
        binder.forField(formComboBox).asRequired().bind("form");
        binder.forField(houseComboBox).asRequired().bind("house");
        binder.forField(prefectRoleField).bind("prefectRole"); // not required

        FormLayout formLayout = new FormLayout(firstnameField, lastnameField, formComboBox,houseComboBox, prefectRoleField);
        assignFormLayoutSettings(formLayout, false);

        for(FormQuestion question : FormQuestion.values()) {
            TextField field = new TextField(question.getDescription());
            formLayout.add(field);
            formLayout.setColspan(field, 2);
            binder.forField(field).asRequired().bind(question.getPojoField());
        }

        PictureField startPrepPictureField = new PictureField("Select start of Prep image");
        PictureField endOfPrepPictureField = new PictureField("Select end of Prep image");
        PictureField havingFunPictureField = new PictureField("Select having fun image");

        formLayout.setColspan(startPrepPictureField, 3);
        formLayout.setColspan(endOfPrepPictureField, 3);
        formLayout.setColspan(havingFunPictureField, 3);

        binder.forField(startPrepPictureField).asRequired().bind("startPrepPicture");
        binder.forField(endOfPrepPictureField).asRequired().bind("endOfPrepPicture");
        binder.forField(havingFunPictureField).asRequired().bind("havingFunPicture");

        formLayout.add(startPrepPictureField);
        formLayout.add(endOfPrepPictureField);
        formLayout.add(havingFunPictureField);
        formLayout.add(new Label("")); // spacing

        formVerticalLayout.add(title);
        formVerticalLayout.add(txt1);
        formVerticalLayout.add(formLayout);

        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        submitButton.setMinWidth(MIN_BUTTON_WIDTH);
        cancel.setMinWidth(MIN_BUTTON_WIDTH);
        FormLayout buttonLayout = new FormLayout(submitButton, cancel);
        assignFormLayoutSettings(buttonLayout, false);
        buttonLayout.setColspan(submitButton, 2);
        buttonLayout.setColspan(cancel, 2);

        formVerticalLayout.add(new Label(" "));
        formVerticalLayout.add(buttonLayout);

        formVerticalLayout.setPadding(true);
        formVerticalLayout.setVisible(false);

        return formVerticalLayout;
    }

    private void assignFormLayoutSettings(FormLayout layout, boolean limtSteps) {
        layout.setMaxWidth(MAX_WIDTH);
        layout.getStyle().set("margin", "0 auto");
        if(limtSteps) {
            layout.setResponsiveSteps(
                    new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                    new FormLayout.ResponsiveStep(MIN_WIDTH, 2, FormLayout.ResponsiveStep.LabelsPosition.TOP));
        } else {
            layout.setResponsiveSteps(
                    new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                    new FormLayout.ResponsiveStep(MIN_WIDTH, 2, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                    new FormLayout.ResponsiveStep(MAX_WIDTH, 4, FormLayout.ResponsiveStep.LabelsPosition.TOP));
        }
    }



}
