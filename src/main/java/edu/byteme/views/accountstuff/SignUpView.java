package edu.byteme.views.accountstuff;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.Theme;
import edu.byteme.security.SecurityService;

@Route(value = "signup")
@AnonymousAllowed
@CssImport("./themes/my-app/signin-styles.css")
public class SignUpView extends VerticalLayout {

    private final SecurityService securityService;

    public SignUpView(SecurityService securityService) {
        this.securityService = securityService;

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        addClassName("signup-view");

        // Titel
        H1 title = new H1("ByteMe Ordering System");
        title.addClassName("form-title");

        // Pflichtfelder
        TextField username = new TextField("Username *");
        EmailField email = new EmailField("Email *");
        PasswordField password = new PasswordField("Password *");
        TextField firstName = new TextField("First Name *");
        TextField lastName = new TextField("Last Name *");

        username.addClassName("form-item");
        email.addClassName("form-item");
        password.addClassName("form-item");
        firstName.addClassName("form-item");
        lastName.addClassName("form-item");

        VerticalLayout leftColumn = new VerticalLayout(username, email, password, firstName, lastName);
        leftColumn.addClassName("form-column");
        leftColumn.setPadding(false);
        leftColumn.setSpacing(false);

        // Optionale Felder
        TextField postalCode = new TextField("Postal Code (Optional)");
        TextField street = new TextField("Street (Optional)");
        NumberField houseNumber = new NumberField("House Number (Optional)");
        houseNumber.setStep(1);
        TextField phone = new TextField("Phone Number (Optional)");

        postalCode.addClassName("form-item");
        street.addClassName("form-item");
        houseNumber.addClassName("form-item");
        phone.addClassName("form-item");

        VerticalLayout rightColumn = new VerticalLayout(postalCode, street, houseNumber, phone);
        rightColumn.addClassName("form-column");
        rightColumn.setPadding(false);
        rightColumn.setSpacing(false);

        // Formular-Layout
        HorizontalLayout formLayout = new HorizontalLayout(leftColumn, rightColumn);
        formLayout.addClassName("form-layout");

        // Button
        Button signupButton = new Button("Sign Up", event -> {
            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
                Notification.show("Please fill in all required fields!");
                return;
            }

            String result = securityService.signup(
                    username.getValue(),
                    email.getValue(),
                    password.getValue(),
                    firstName.getValue(),
                    lastName.getValue(),
                    postalCode.isEmpty() ? null : postalCode.getValue(),
                    street.isEmpty() ? null : street.getValue(),
                    houseNumber.isEmpty() ? null : houseNumber.getValue().intValue(),
                    phone.isEmpty() ? null : phone.getValue()
            );

            Notification.show(result);

            if (result.equalsIgnoreCase("Registration successful.")) {
                username.clear();
                email.clear();
                password.clear();
                firstName.clear();
                lastName.clear();
                postalCode.clear();
                street.clear();
                houseNumber.clear();
                phone.clear();

                getUI().ifPresent(ui -> ui.navigate("login?success=true"));
            }
        });
        signupButton.addClassName("signup-button");

        // Wrapper für Button
        HorizontalLayout buttonWrapper = new HorizontalLayout(signupButton);
        buttonWrapper.setWidthFull();
        buttonWrapper.setJustifyContentMode(JustifyContentMode.CENTER);

        // Card Layout
        VerticalLayout cardLayout = new VerticalLayout(title, formLayout, buttonWrapper);
        cardLayout.addClassName("signup-card");

        add(cardLayout);
    }
}
