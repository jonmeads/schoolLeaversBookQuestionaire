package org.jpm.ui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.progressbar.ProgressBarVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.Route;
import org.jpm.ui.components.PictureField;
import org.jpm.ui.data.FormDetails;
import org.jpm.ui.data.FormDetailsService;
import org.jpm.ui.data.FormDetailsService.ServiceException;
import org.jpm.ui.data.FormQuestion;
import org.jpm.ui.data.LongRunningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.concurrent.ListenableFutureCallback;


@Route("")
@Push
public class MainView extends VerticalLayout {

    private static final String MIN_WITH = "490px";
    private static final String MAX_WITH = "900px";
    private static final String MIN_BUTTON_WIDTH = "100px";

    private FormDetailsService service;
    private LongRunningService longRunningService;
    private BeanValidationBinder<FormDetails> binder;


    public MainView(@Autowired FormDetailsService service, @Autowired LongRunningService longRunningService) {

        this.service = service;
        this.longRunningService = longRunningService;
        binder = new BeanValidationBinder<>(FormDetails.class);

        // form
        Button formSubmitButton = new Button("Save");
        Button formCancel = new Button("Cancel");
        VerticalLayout formLayout = createForm(binder, formSubmitButton, formCancel);


        // menu
        Button formButton = new Button("Fill in Questionnaire");
        Button babyButton = new Button("Provide Baby Photo");
        Button quitButton = new Button("Finished");
        VerticalLayout menuLayout = createMenu(formButton,babyButton, quitButton);

        // baby
        Button babySaveButton = new Button("Save");
        Button babyCancel = new Button("Cancel");
        VerticalLayout babyLayout = createBaby(babySaveButton, babyCancel);


        // save
        ProgressBar progressBar = new ProgressBar(0,10);
        VerticalLayout saveFormLayout = createFormSave(progressBar);


        // login
        LoginOverlay loginComponent = getLoginOverlay();
        loginComponent.addLoginListener(e -> {
            if("bob".equals(e.getPassword()) && "bob".equals(e.getUsername())) {
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


        // listeners
        formButton.addClickListener(e -> {
            menuLayout.setVisible(false);
            formLayout.setVisible(true);
        });

        babyButton.addClickListener(e -> {
            menuLayout.setVisible(false);
            babyLayout.setVisible(true);
        });

        quitButton.addClickListener(e -> done());

        babyCancel.addClickListener(e -> {
            babyLayout.setVisible(false);
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

            longRunningService
                    .longRunningTask()
                    .addCallback(new ListenableFutureCallback<>() {

                @Override
                public void onSuccess(Void unused) {
                    ui.access(
                            () -> {
                                saveFormLayout.setVisible(false);
                                menuLayout.setVisible(true);
                                showSuccess("Baby data saved");
                            }
                    );
                }

                @Override
                public void onFailure(Throwable throwable) {
                    ui.access(
                            () -> {
                                saveFormLayout.setVisible(false);
                                babyLayout.setVisible(true);
                                showError("Failed to save the responses, please retry or contact Louise");
                            }
                    );
                }
            });
        });

        formSubmitButton.addClickListener(e -> {
            UI ui = UI.getCurrent();
            formLayout.setVisible(false);
            saveFormLayout.setVisible(true);

            try {
                FormDetails detailsBean = new FormDetails();
                binder.writeBean(detailsBean);
                service.store(detailsBean).addCallback(new ListenableFutureCallback<>() {

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
                                            showError("Failed to save the responses, please retry or contact Louise");
                                        }
                                );
                            }
                        });

            } catch (ValidationException | ServiceException formException) {
                saveFormLayout.setVisible(false);
                formLayout.setVisible(true);
                showError("Failed to save the responses, please retry or contact Louise");
            }

        });

    }


    private VerticalLayout createFormSave(ProgressBar progressBar) {

        VerticalLayout savingLayout = new VerticalLayout();
        savingLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        savingLayout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        H2 title = new H2("Saving data, please wait...");

        progressBar.addThemeVariants(ProgressBarVariant.LUMO_CONTRAST);
        progressBar.setMaxWidth(MIN_WITH);
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
        Notification notification = Notification.show("Data saved, thanks for providing the data for " + detailsBean.getFirstname() + " " + detailsBean.getLastname());
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
        Notification notification = Notification.show(message, 5000, Notification.Position.TOP_START);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }

    private void done() {
        UI.getCurrent().getPage().reload();
    }


    private VerticalLayout createBaby(Button babySaveButton, Button cancel) {
        VerticalLayout babyVerticalLayout = new VerticalLayout();
        babyVerticalLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        babyVerticalLayout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        H2 title = new H2("Baby Photo");

        TextField firstnameField = new TextField("First name");
        TextField lastnameField = new TextField("Last name");
        PictureField babyPicture = new PictureField("Select baby photo");

        FormLayout formLayout = new FormLayout(firstnameField, lastnameField, babyPicture);
        assignFormLayoutSettings(formLayout, true);

        formLayout.setColspan(babyPicture, 2);

        babyVerticalLayout.add(title);
        babyVerticalLayout.add(formLayout);
        babyVerticalLayout.add(babySaveButton);

        babySaveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        FormLayout buttonLayout = new FormLayout(babySaveButton, cancel);
        assignFormLayoutSettings(buttonLayout, true);
        buttonLayout.setColspan(babySaveButton, 2);
        buttonLayout.setColspan(cancel, 2);
        cancel.setMinWidth(MIN_BUTTON_WIDTH);
        babySaveButton.setMinWidth(MIN_BUTTON_WIDTH);

        babyVerticalLayout.add(buttonLayout);

        babyVerticalLayout.setVisible(false);
        return babyVerticalLayout;
    }

    private VerticalLayout createMenu(Button formButton, Button babyButton, Button quitButton) {
        VerticalLayout menuVerticalLayout = new VerticalLayout();
        menuVerticalLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        menuVerticalLayout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        H2 title = new H2("Prep 6 Leavers Menu");

        formButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        babyButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        quitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        FormLayout formLayout = new FormLayout(formButton, babyButton, quitButton);
        assignFormLayoutSettings(formLayout, true);

        formLayout.setColspan(formButton, 2);
        formLayout.setColspan(babyButton, 2);
        formLayout.setColspan(quitButton, 2);

        menuVerticalLayout.add(title);
        menuVerticalLayout.add(formLayout);

        menuVerticalLayout.setVisible(false);
        return menuVerticalLayout;

    }


    private VerticalLayout createForm(BeanValidationBinder<FormDetails> binder, Button submitButton, Button cancel) {

        VerticalLayout formVerticalLayout = new VerticalLayout();
        formVerticalLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        formVerticalLayout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        // main fields
        H2 title = new H2("Leavers Book Questionnaire");

        TextField firstnameField = new TextField("First name");
        TextField lastnameField = new TextField("Last name");

        ComboBox<String> formComboBox = new ComboBox<>();
        formComboBox.setItems("Swords", "Kells", "Derry");
        formComboBox.setPlaceholder("select your form");

        ComboBox<String> houseComboBox = new ComboBox<>();
        houseComboBox.setItems("Fisher", "More", "Alban", "Becket");
        houseComboBox.setPlaceholder("select your house");

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

        formLayout.setColspan(startPrepPictureField, 2);
        formLayout.setColspan(endOfPrepPictureField, 2);

        binder.forField(startPrepPictureField).asRequired().bind("startPrepPicture");
        binder.forField(endOfPrepPictureField).asRequired().bind("endOfPrepPicture");

        formLayout.add(startPrepPictureField);
        formLayout.add(endOfPrepPictureField);
        formLayout.add(new Label("")); // spacing

        formVerticalLayout.add(title);
        formVerticalLayout.add(formLayout);

        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        submitButton.setMinWidth(MIN_BUTTON_WIDTH);
        cancel.setMinWidth(MIN_BUTTON_WIDTH);
        FormLayout buttonLayout = new FormLayout(submitButton, cancel);
        assignFormLayoutSettings(buttonLayout, false);
        buttonLayout.setColspan(submitButton, 2);
        buttonLayout.setColspan(cancel, 2);

        formVerticalLayout.add(buttonLayout);

        formVerticalLayout.setVisible(false);

        return formVerticalLayout;
    }

    private void assignFormLayoutSettings(FormLayout layout, boolean limtSteps) {
        layout.setMaxWidth(MAX_WITH);
        layout.getStyle().set("margin", "0 auto");
        if(limtSteps) {
            layout.setResponsiveSteps(
                    new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                    new FormLayout.ResponsiveStep(MIN_WITH, 2, FormLayout.ResponsiveStep.LabelsPosition.TOP));
        } else {
            layout.setResponsiveSteps(
                    new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                    new FormLayout.ResponsiveStep(MIN_WITH, 2, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                    new FormLayout.ResponsiveStep(MAX_WITH, 4, FormLayout.ResponsiveStep.LabelsPosition.TOP));
        }
    }

}
