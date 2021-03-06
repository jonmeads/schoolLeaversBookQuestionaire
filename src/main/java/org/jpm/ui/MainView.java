package org.jpm.ui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.page.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.jpm.ui.data.FormDetails;
import org.jpm.ui.data.FormDetailsService;
import org.jpm.ui.data.FormDetailsService.ServiceException;
import org.jpm.ui.components.PictureField;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.Route;

import java.awt.*;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;


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

        // This is a custom field we create to handle the field 'avatar' in our data. It
        // work just as any other field, e.g. the TextFields above. Instead of a String
        // value, it has an AvatarImage value.
        PictureField startPrepPictureField = new PictureField("Select start of Prep image");
        PictureField endOfPrepPictureField = new PictureField("Select end of Prep image");

        Button submitButton = new Button("Save my responses");
        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        //Span errorMessage = new Span();

        /*
         * Build the visible layout
         */

        // Create a FormLayout with all our components. The FormLayout doesn't have any
        // logic (validation, etc.), but it allows us to configure Responsiveness from
        // Java code and its defaults looks nicer than just using a VerticalLayout.
        FormLayout formLayout = new FormLayout(title, firstnameField, lastnameField, formComboBox,houseComboBox, prefectRoleField, bestMemoryField,missAboutPrepField,
                lookingForwardToField, proudestMomentField, favSubjectField, faveGameField, likeWhenOlderField, favOuterSchoolField, startPrepPictureField, endOfPrepPictureField, submitButton);

        // dont show it yet
        formLayout.setVisible(false);


        Button doneButton = new Button("Complete");
        doneButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        doneButton.addClickListener(e -> {
            done();
        });
        FormLayout formLayout2 = new FormLayout(doneButton);
        formLayout2.setVisible(false);
        formLayout2.setMaxWidth("900px");
        formLayout2.getStyle().set("margin", "0 auto");
        formLayout2.setColspan(doneButton, 2);




        // Restrict maximum width and center on page
        formLayout.setMaxWidth("900px");
        formLayout.getStyle().set("margin", "0 auto");

        // Allow the form layout to be responsive. On device widths 0-490px we have one
        // column, then we have two. Field labels are always on top of the fields.
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

        //formLayout.setColspan(startPrepPictureField, 2);
        //formLayout.setColspan(endOfPrepPictureField, 2);

        //.setColspan(errorMessage, 2);
        formLayout.setColspan(submitButton, 2);


        // Add some styles to the error message to make it pop out
        //errorMessage.getStyle().set("color", "var(--lumo-error-text-color)");
       // errorMessage.getStyle().set("padding", "15px 0");


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
        add(formLayout2);

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


        // A label where bean-level error messages go
        //binder.setStatusLabel(errorMessage);

        // And finally the submit button
        submitButton.addClickListener(e -> {
            try {
                // Create empty bean to store the details into
                FormDetails detailsBean = new FormDetails();

                // Run validators and write the values to the bean
                binder.writeBean(detailsBean);

                // Call backend to store the data
                service.store(detailsBean);

                // Show success message if everything went well


                formLayout.setVisible(false);
                showSuccess(detailsBean);
                formLayout2.setVisible(true);



            } catch (ValidationException e1) {
                showError("Please complete all the required fields, including uploading the photos");

            } catch (ServiceException e2) {

                e2.printStackTrace();
                showError("Failed to save the responses, please retry or contact Louise");
                //errorMessage.setText("Saving the data failed, please try again");
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
        notification.setDuration(3000);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

    }

    private void showError(String message) {
        Notification notification = Notification.show(message);
        notification.setDuration(5000);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }

    private void done() {
        UI.getCurrent().getPage().reload();
    }



}
