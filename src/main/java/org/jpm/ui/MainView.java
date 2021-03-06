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
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.Route;
import org.jpm.ui.components.PictureField;
import org.jpm.ui.data.FormDetails;
import org.jpm.ui.data.FormDetailsService;
import org.jpm.ui.data.FormDetailsService.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;


@Route("")
public class MainView extends VerticalLayout {

    private FormDetailsService service;
    private BeanValidationBinder<FormDetails> binder;

    public MainView(@Autowired FormDetailsService service) {

        this.service = service;

        /*
         * Main Questionaire form
         */

        H2 title = new H2("Prep 6 Questions for the Leavers Book");

        // common componets
        Label spacing = new Label("");


        // form
        TextField firstnameField = new TextField("First name");
        TextField lastnameField = new TextField("Last name");

        ComboBox<String> formComboBox = new ComboBox<>();
        formComboBox.setItems("Swords", "Kells", "Derry");
        formComboBox.setPlaceholder("select your form");

        ComboBox<String> houseComboBox = new ComboBox<>();
        houseComboBox.setItems("Fisher", "More", "Alban", "Becket");
        houseComboBox.setPlaceholder("select your house");

        TextField prefectRoleField = new TextField("Prefect Role");
        TextField bestMemoryField = new TextField("Best Memory");
        TextField missAboutPrepField = new TextField("What I will miss about Prep");
        TextField lookingForwardToField = new TextField("What I'm looking forward to at Senior School");
        TextField proudestMomentField = new TextField("My Proudest Moment");
        TextField favSubjectField = new TextField("Favourite subject");
        TextField faveGameField = new TextField("Favourite Game/Craze/Movie");
        TextField likeWhenOlderField = new TextField("What would you like to do when older?");
        TextField favOuterSchoolField = new TextField("Favourite this to do out of school?");

        PictureField startPrepPictureField = new PictureField("Select start of Prep image");
        PictureField endOfPrepPictureField = new PictureField("Select end of Prep image");

        Button submitButton = new Button("Save my responses");
        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);


        FormLayout formLayout = new FormLayout(title, firstnameField, lastnameField, formComboBox,houseComboBox, prefectRoleField,
                bestMemoryField,missAboutPrepField, lookingForwardToField, proudestMomentField, favSubjectField, faveGameField,
                likeWhenOlderField, favOuterSchoolField, startPrepPictureField, endOfPrepPictureField, spacing, submitButton);

        // dont show it yet
        formLayout.setMaxWidth("900px");
        formLayout.getStyle().set("margin", "0 auto");
        formLayout.setVisible(false);
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("490px", 2, FormLayout.ResponsiveStep.LabelsPosition.TOP));

        // These components take full width regardless if we use one column or two (it
        // just looks better that way)
        formLayout.setColspan(title, 2);
        formLayout.setColspan(bestMemoryField, 2);
        formLayout.setColspan(missAboutPrepField, 2);
        formLayout.setColspan(lookingForwardToField, 2);
        formLayout.setColspan(proudestMomentField, 2);
        formLayout.setColspan(favSubjectField, 2);
        formLayout.setColspan(faveGameField, 2);
        formLayout.setColspan(likeWhenOlderField, 2);
        formLayout.setColspan(favOuterSchoolField, 2);

        formLayout.setColspan(spacing, 2);
        formLayout.setColspan(submitButton, 2);



        // saving
        ProgressBar progressBar = new ProgressBar(0,10);
        progressBar.setValue(0);

        Button doneButton = new Button("Complete");
        doneButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        doneButton.addClickListener(e -> {
            done();
        });
        FormLayout doneLayout = new FormLayout(spacing,spacing, progressBar, doneButton);

        doneLayout.setVisible(false);
        doneLayout.setMaxWidth("900px");
        doneLayout.getStyle().set("margin", "0 auto");
        doneLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("490px", 2, FormLayout.ResponsiveStep.LabelsPosition.TOP));

        doneLayout.setColspan(spacing, 2);
        doneLayout.setColspan(progressBar, 2);
        doneLayout.setColspan(doneButton, 2);




        // login
        LoginOverlay loginComponent = getLoginOverlay();
        loginComponent.addLoginListener(e -> {
            if("bob".equals(e.getPassword()) && "bob".equals(e.getUsername())) {
                loginComponent.close();
                formLayout.setVisible(true);
            } else {
                loginComponent.setError(true);
                loginComponent.setEnabled(true);

            }
        });
        loginComponent.setOpened(true);


        // Add the form to the page
        add(loginComponent);
        add(formLayout);
        add(doneLayout);


        /*
         * Set up form functionality
         */

        /*
         * Binder is a form utility class provided by Vaadin. Here, we use a specialized
         * version to gain access to automatic Bean Validation (JSR-303). We provide our
         * data class so that the Binder can read the validation definitions on that
         * class and create appropriate validators. The BeanValidationBinder can
         * automatically validate all JSR-303 definitions, meaning we can concentrate on
         * custom things such as the passwords in this class.
         */
        binder = new BeanValidationBinder<FormDetails>(FormDetails.class);

        // Basic name fields that are required to fill in
        binder.forField(firstnameField).asRequired().bind("firstname");
        binder.forField(lastnameField).asRequired().bind("lastname");
        binder.forField(formComboBox).asRequired().bind("form");
        binder.forField(houseComboBox).asRequired().bind("house");

        // not required
        binder.forField(prefectRoleField).bind("prefectRole");

        binder.forField(bestMemoryField).asRequired().bind("bestMemory");
        binder.forField(missAboutPrepField).asRequired().bind("missAboutPrep");
        binder.forField(lookingForwardToField).asRequired().bind("lookingForwardTo");
        binder.forField(proudestMomentField).asRequired().bind("proudestMoment");
        binder.forField(favSubjectField).asRequired().bind("favSubject");
        binder.forField(faveGameField).asRequired().bind("faveGame");
        binder.forField(likeWhenOlderField).asRequired().bind("likeWhenOlder");
        binder.forField(favOuterSchoolField).asRequired().bind("favOuterSchool");
        binder.forField(startPrepPictureField).asRequired().bind("startPrepPicture");
        binder.forField(endOfPrepPictureField).asRequired().bind("endOfPrepPicture");


        // submit button listener
        submitButton.addClickListener(e -> {
            try {

                formLayout.setVisible(false);
                doneButton.setEnabled(false);
                doneLayout.setVisible(true);

                // Create empty bean to store the details into
                FormDetails detailsBean = new FormDetails();
                progressBar.setValue(progressBar.getMax() * 0.3);

                Thread.sleep(1000);

                // Run validators and write the values to the bean
                binder.writeBean(detailsBean);
                progressBar.setValue(progressBar.getMax() * 0.6);

                Thread.sleep(1000);

                // Call backend to store the data
                service.store(detailsBean);
                progressBar.setValue(progressBar.getMax() * 0.9);

                Thread.sleep(1000);

                // Show success message if everything went well
                showSuccess(detailsBean);
                progressBar.setMax(progressBar.getMax());
                doneButton.setEnabled(true);


            } catch (ValidationException e1) {

                doneLayout.setVisible(false);
                formLayout.setVisible(true);
                showError("Please complete all the required fields, including uploading the photos");

            } catch (ServiceException e2) {

                //e2.printStackTrace();
                doneLayout.setVisible(false);
                formLayout.setVisible(true);
                showError("Failed to save the responses, please retry or contact Louise");
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
        });

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

    private void showError(String message) {
        Notification notification = Notification.show(message, 5000, Notification.Position.TOP_START);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }

    private void done() {
        UI.getCurrent().getPage().reload();
    }



}
