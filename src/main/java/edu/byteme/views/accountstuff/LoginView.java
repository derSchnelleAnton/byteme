package edu.byteme.views.accountstuff;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import jakarta.annotation.security.PermitAll;


@Route(value = "login")
@PageTitle("Login | ByteMe")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private LoginForm login = new LoginForm();


    public LoginView() {
        System.out.println("LoginView geladen");

        addClassName("login-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        // LoginForm anpassen und festlegen
        login.setAction("/login");

        // Custom Sign-Up Link-Button
        RouterLink signUpLink = new RouterLink("Not registered? Sign up here", SignUpView.class);
        signUpLink.getStyle().set("color", "#007bff");
        signUpLink.getStyle().set("cursor", "pointer");
        signUpLink.getStyle().set("font-size", "14px");

        // Layout des LoginView optimieren
        VerticalLayout layout = new VerticalLayout(
                new H1("ByteMe Ordering System"),
                login,
                signUpLink
        );

        // Layout zentrieren
        layout.setAlignItems(Alignment.CENTER);

        // Alles hinzufügen
        add(layout);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        // inform the user about an authentication error
        if(beforeEnterEvent.getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("error")) {
            login.setError(true);
            System.out.println("Login failed!");
        }
    }
}
