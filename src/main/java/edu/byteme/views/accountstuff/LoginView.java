package edu.byteme.views.accountstuff;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.auth.AnonymousAllowed;


@Route(value = "login")
@PageTitle("Login | ByteMe")
@AnonymousAllowed
@CssImport("./themes/my-app/signin-styles.css")
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private LoginForm login = new LoginForm();


    public LoginView() {
        System.out.println("DEBUG: LoginView constructor called");

        addClassName("login-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        // LoginForm anpassen und festlegen
        login.setAction("/login");
        System.out.println("DEBUG: Login form action set to: " + login.getAction());

        // "Forgot password"-Link durch Anpassung des I18n-Textes verstecken -> not in MVP
        LoginI18n i18n = LoginI18n.createDefault();
        i18n.getForm().setForgotPassword(""); // Leerer Text entfernt den Link
        login.setI18n(i18n);

        login.addLoginListener(event -> {
            System.out.println("DEBUG: Login form submitted for user: " + event.getUsername());
        });

        // Custom Sign-Up Link-Button
        RouterLink signUpLink = new RouterLink("Not registered? Sign up here", SignUpView.class);
        signUpLink.addClassName("router-link-custom");

        // Layout des LoginView mit Header
        H1 header = new H1("ByteMe Ordering System");
        header.addClassName("header-text");

        VerticalLayout layout = new VerticalLayout(header, login, signUpLink);
        layout.setAlignItems(Alignment.CENTER); // Inneres Layout zentrieren
        add(layout);

        System.out.println("DEBUG: LoginView initialization complete");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        System.out.println("DEBUG: LoginView.beforeEnter called");
        
        // inform the user about an authentication error
        if(beforeEnterEvent.getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("error")) {
            login.setError(true);
            System.out.println("DEBUG: Login error parameter detected");
        }
        
        // Show success message if user just registered
        if(beforeEnterEvent.getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("success")) {
            Notification.show("Registration successful. Please login with your credentials.");
            System.out.println("DEBUG: Registration success parameter detected");
        }
        
        // Log URL parameters for debugging
        System.out.println("DEBUG: URL parameters: " + beforeEnterEvent.getLocation().getQueryParameters().getParameters());
    }
}
