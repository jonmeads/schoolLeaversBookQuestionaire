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
import com.vaadin.flow.component.icon.VaadinIcon;
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
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import org.apache.commons.lang3.RandomStringUtils;
import org.jpm.config.AppConstants;
import org.jpm.models.BabyDetails;
import org.jpm.models.FormDetails;
import org.jpm.models.FormQuestion;
import org.jpm.models.Leaver;
import org.jpm.services.BabyFormDetailsService;
import org.jpm.services.FormDetailsService;
import org.jpm.services.dao.JdbcLeaversDao;
import org.jpm.ui.components.PictureField;
import org.jpm.ui.data.MultiFileBufferToFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.concurrent.ListenableFutureCallback;

import javax.servlet.http.Cookie;
import java.util.Arrays;
import java.util.Optional;
import java.util.logging.Logger;


@JsModule("./js/theme-selector.js")
@Route("")
@Push
@Theme(value = Lumo.class)
@PWA(name = "School Leavers", shortName = "leavers")
public class MainView extends VerticalLayout {

    private final static Logger LOGGER = Logger.getLogger(MainView.class.getName());

    private static final String MIN_WIDTH = "490px";
    private static final String MAX_WIDTH = "900px";
    private static final String MIN_BUTTON_WIDTH = "100px";
    private static final String COOKIE_SESSION = "session_cookie";
    private static final Integer COOKIE_AGE = 60 * 60 * 24 * 7 * 52; // 1 year


    private FormDetailsService formDetailsService;
    private BabyFormDetailsService babyFormDetailsService;
    private JdbcLeaversDao jdbcLeaversDao;
    private BeanValidationBinder<FormDetails> formDetailsBeanValidationBinder;
    private BeanValidationBinder<BabyDetails> babyDetailsBeanValidationBinder;
    private Leaver leaver;


    public MainView(@Autowired FormDetailsService formDetailsService, @Autowired BabyFormDetailsService babyFormDetailsService, @Autowired JdbcLeaversDao jdbcLeaversDao) {

        this.formDetailsService = formDetailsService;
        this.babyFormDetailsService = babyFormDetailsService;
        this.jdbcLeaversDao = jdbcLeaversDao;
        formDetailsBeanValidationBinder = new BeanValidationBinder<>(FormDetails.class);
        babyDetailsBeanValidationBinder = new BeanValidationBinder<>(BabyDetails.class);
        this.leaver = getSession();

        constructUI();
    }

    private Leaver getSession() {
        Leaver leaverSession = null;
        String session;

        Cookie[] cookies = VaadinRequest.getCurrent().getCookies();
        if(cookies != null) {

            Optional<Cookie> cookie = Arrays.stream(cookies)
                    .filter(f -> COOKIE_SESSION.equals(f.getName()))
                    .findFirst();

            if (cookie.isPresent()) {
                session = cookie.get().getValue();
                leaverSession = jdbcLeaversDao.getLeaver(session);
            }

            if(leaverSession == null) {
                session = RandomStringUtils.randomAlphanumeric(10);
                leaverSession = new Leaver(session);
            }
        }
        return leaverSession;
    }


    private void saveSession(String session) {
        if(session == null || session.isEmpty()) {
            session = RandomStringUtils.randomAlphanumeric(10);
        }
        Cookie cookie = new Cookie(COOKIE_SESSION, session);
        cookie.setMaxAge(COOKIE_AGE);
        cookie.setPath("/");
        VaadinService.getCurrentResponse().addCookie(cookie);
    }

    private void setCompletedStatusForButton(Button button, Integer value) {
        if (value != null && value > 0) {
            button.setIcon(VaadinIcon.THUMBS_UP.create());
            button.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
        }
    }

    protected void constructUI() {

        // theme button
        Button toggleTheme = new Button("Toggle dark theme", click -> {
            ThemeList themeList = UI.getCurrent().getElement().getThemeList();
            if(themeList.contains(Lumo.DARK)) {
                themeList.remove(Lumo.DARK);
            } else {
                themeList.add(Lumo.DARK);
            }
        });

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
        setCompletedStatusForButton(formButton, leaver.getForm());
        setCompletedStatusForButton(babyButton, leaver.getBaby());


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
                saveSession(leaver.getSession());
                LOGGER.info("Successful login " + leaver);
                loginComponent.close();
                menuLayout.setVisible(true);
            } else {
                LOGGER.warning("Failed login attempt, using supplied values of user: " + e.getUsername() + ", passwd: " + e.getPassword());
                loginComponent.setError(true);
                loginComponent.setEnabled(true);
            }
        });
        loginComponent.setOpened(true);




//        // add theme button top right
//        HorizontalLayout horizontalLayout = new HorizontalLayout();
//        horizontalLayout.setDefaultVerticalComponentAlignment(Alignment.END);
//
//        VerticalLayout vertLayout = new VerticalLayout();
//        vertLayout.setDefaultHorizontalComponentAlignment(Alignment.END);
//
//        horizontalLayout.add(toggleTheme);
//        vertLayout.add(horizontalLayout);
//        add(vertLayout);

        // Add the layouts to the page
        add(loginComponent);
        add(menuLayout);
        add(formLayout);
        add(babyLayout);
        add(saveFormLayout);
        add(photoLayout);


        // listeners
        formButton.addClickListener(e -> {
            LOGGER.info("Opening Questionaire " + leaver);
            menuLayout.setVisible(false);
            formLayout.setVisible(true);
        });

        babyButton.addClickListener(e -> {
            LOGGER.info("Opening Baby form "  + leaver);
            menuLayout.setVisible(false);
            babyLayout.setVisible(true);
        });

        photosButton.addClickListener(e -> {
            LOGGER.info("Opening Photos "  + leaver);
            menuLayout.setVisible(false);
            photoLayout.setVisible(true);
        });

        quitButton.addClickListener(e -> done());

        babyCancel.addClickListener(e -> {
            LOGGER.info("Cancel baby "  + leaver);
            babyLayout.setVisible(false);
            menuLayout.setVisible(true);
        });

        photoCancel.addClickListener(e -> {
            LOGGER.info("Cancel photo "  + leaver);
            photoLayout.setVisible(false);
            menuLayout.setVisible(true);
        });

        formCancel.addClickListener(e -> {
            LOGGER.info("Cancel form "  + leaver);
            formLayout.setVisible(false);
            menuLayout.setVisible(true);
        });

        babySaveButton.addClickListener(e -> {
            LOGGER.info("Saving Baby Form Data "  + leaver);
            babyLayout.setVisible(false);
            saveFormLayout.setVisible(true);
            UI ui = UI.getCurrent();

            try {

                BabyDetails detailsBean = new BabyDetails();
                babyDetailsBeanValidationBinder.writeBean(detailsBean);

                babyFormDetailsService
                    .store(detailsBean, leaver)
                    .addCallback(new ListenableFutureCallback<>() {

                    @Override
                    public void onSuccess(Void unused) {
                        ui.access(
                            () -> {
                                saveFormLayout.setVisible(false);
                                menuLayout.setVisible(true);
                                setCompletedStatusForButton(babyButton, 1);
                                LOGGER.info("Baby photo data successfully saved  "  + leaver);
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
                                LOGGER.warning("Failed to save the baby photo data" + throwable);
                                showError("Failed to save the baby photo data");
                            }
                        );
                    }
                });
            } catch (ValidationException formException) {
                saveFormLayout.setVisible(false);
                babyLayout.setVisible(true);
                LOGGER.warning("Baby form validation error "  + leaver);
                showValidationError("Please confirm all required fields have been completed including uploading the pictures.");
            } catch (Exception se) {
                saveFormLayout.setVisible(false);
                babyLayout.setVisible(true);
                LOGGER.warning("Failed to save the baby photo data" + se);
                showError("Failed to save the baby photo data");
            }
        });

        formSubmitButton.addClickListener(e -> {
            LOGGER.info("Saving the questionaire data "  + leaver);
            UI ui = UI.getCurrent();
            formLayout.setVisible(false);
            saveFormLayout.setVisible(true);

            try {
                FormDetails detailsBean = new FormDetails();
                formDetailsBeanValidationBinder.writeBean(detailsBean);
                formDetailsService.store(detailsBean, leaver).addCallback(new ListenableFutureCallback<>() {

                            @Override
                            public void onSuccess(Void unused) {
                                ui.access(() -> {
                                            saveFormLayout.setVisible(false);
                                            menuLayout.setVisible(true);
                                            setCompletedStatusForButton(formButton, 1);
                                            LOGGER.info("Successfully saved the questionaire data "  + leaver);
                                            showSuccess(detailsBean);
                                        }
                                );
                            }

                            @Override
                            public void onFailure(Throwable throwable) {
                                ui.access(() -> {
                                            saveFormLayout.setVisible(false);
                                            formLayout.setVisible(true);
                                            LOGGER.warning("Failed to save the questionaire data" + throwable);
                                            showError("Failed to save the responses");
                                        }
                                );
                            }
                        });

            } catch (ValidationException formException) {
                saveFormLayout.setVisible(false);
                formLayout.setVisible(true);
                LOGGER.warning("Validation failure for questionaire data "  + leaver);
                showValidationError("Please confirm all required fields have been completed including uploading the pictures.");
            } catch (Exception se) {
                saveFormLayout.setVisible(false);
                formLayout.setVisible(true);
                LOGGER.warning("Failed to save the questionaire data" + se);
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
        Notification notification = Notification.show("Questionaire data saved, thanks for providing the data for " + detailsBean.getFullname());
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

    private void showValidationError(String message) {
        Notification notification = Notification.show(message, 5000, Notification.Position.TOP_START);
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
        Label titleLabel = new Label("Please upload additional photos that we can add to the leavers book, multiple photos can be uploaded either via the upload button or by dragging the photos onto the page, these will automatically be uploaded, no need to save. Photos are limited to a maximum size of 10MB");

        HorizontalLayout header = new HorizontalLayout();
        header.add(titleLabel);
        header.setMaxWidth(MAX_WIDTH);
        verticalLayout.add(header);


        MultiFileBufferToFile buffer = new MultiFileBufferToFile(leaver);
        Upload upload = new Upload(buffer);

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

        TextField nameField = new TextField("Full name");
        PictureField babyPicture = new PictureField("Select baby photo");

        FormLayout formLayout = new FormLayout(nameField, babyPicture);
        assignFormLayoutSettings(formLayout, true);

        formLayout.setColspan(nameField, 2);
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

        binder.forField(nameField).asRequired().bind("fullname");
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

        Label titleLabel = new Label("Thanks for logging into the leaver book site. This information will be used to make your son's profile in the leavers book. We are also doing a \"Guess the Baby\" section and as many other photo spreads as we can. Please upload any photos you have to give us a good selection to choose from for the book.");
        Label titleSubLabel = new Label("Please contact " + AppConstants.SUPPORT_CONTACT + " if you have any issues.");

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
        VerticalLayout vertHeader = new VerticalLayout();
        vertHeader.setJustifyContentMode(JustifyContentMode.CENTER);
        vertHeader.setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        vertHeader.add(titleLabel);
        vertHeader.add(new Label(" "));
        vertHeader.add(titleSubLabel);

        header.setMaxWidth(MAX_WIDTH);
        header.add(vertHeader);
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

        TextField fullnameField = new TextField("Full name");
        ComboBox<String> formComboBox = new ComboBox<>();
        formComboBox.setItems("Kells", "Derry", "Swords");
        formComboBox.setPlaceholder("Select your form");

        ComboBox<String> houseComboBox = new ComboBox<>();
        houseComboBox.setItems("Alban", "Becket", "Fisher", "More");
        houseComboBox.setPlaceholder("Select your house");

        TextField prefectRoleField = new TextField("Prefect Role");

        binder.forField(fullnameField).asRequired().bind("fullname");
        binder.forField(formComboBox).asRequired().bind("form");
        binder.forField(houseComboBox).asRequired().bind("house");
        binder.forField(prefectRoleField).bind("prefectRole"); // not required

        FormLayout formLayout = new FormLayout(fullnameField,  formComboBox,houseComboBox, prefectRoleField);
        assignFormLayoutSettings(formLayout, false);
        formLayout.setColspan(fullnameField, 2);

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
