package edu.byteme.views.accountstuff;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import edu.byteme.security.SecurityService;

@Route(value = "signup")
@AnonymousAllowed
public class SignUpView extends VerticalLayout {

    private final SecurityService securityService;

    public SignUpView(SecurityService securityService) {
        this.securityService = securityService;

        System.out.println("SignUpView loaded");

        // Styling for the view
        addClassName("signup-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        // Layout for registration
        VerticalLayout layout = new VerticalLayout(
                new H1("ByteMe Ordering System - Sign Up")
        );

        // Input fields for user data
        TextField username = new TextField("Username");
        EmailField email = new EmailField("Email");
        PasswordField password = new PasswordField("Password");
        TextField firstName = new TextField("First Name");
        TextField lastName = new TextField("Last Name");

        // Button to register the user
        Button signupButton = new Button("Sign Up", event -> {
            // Check if all fields are filled
            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
                Notification.show("Please fill in all fields!");
                return;
            }

            // Call the SecurityService to register the user
            String result = securityService.signup(
                    username.getValue(),
                    email.getValue(),
                    password.getValue(),
                    firstName.getValue(),
                    lastName.getValue()
            );

            // Notify the user of the result
            Notification.show(result);

            // Clear fields if registration is successful
            if (result.equalsIgnoreCase("User registered successfully!")) {
                username.clear();
                email.clear();
                password.clear();
                firstName.clear();
                lastName.clear();
            }
        });

        // Add all components to the layout
        add(layout, username, email, password, firstName, lastName, signupButton);
    }
}
