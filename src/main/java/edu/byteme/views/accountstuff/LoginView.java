package edu.byteme.views.accountstuff;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.Theme;


@Route(value = "login")
@PageTitle("Login | ByteMe")
@AnonymousAllowed
@CssImport("./themes/my-app/signin-styles.css")
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private LoginForm login = new LoginForm();


    public LoginView() {
        //System.out.println("LoginView geladen");

        addClassName("login-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        // LoginForm anpassen und festlegen
        login.setAction("/login");

        // "Forgot password"-Link durch Anpassung des I18n-Textes verstecken -> not in MVP
        LoginI18n i18n = LoginI18n.createDefault();
        i18n.getForm().setForgotPassword(""); // Leerer Text entfernt den Link
        login.setI18n(i18n);

        // Custom Sign-Up Link-Button
        RouterLink signUpLink = new RouterLink("Not registered? Sign up here", SignUpView.class);
        signUpLink.addClassName("router-link-custom");

        // Layout des LoginView mit Header
        H1 header = new H1("ByteMe Ordering System");
        header.addClassName("header-text");

        VerticalLayout layout = new VerticalLayout(header, login, signUpLink);
        layout.setAlignItems(Alignment.CENTER); // Inneres Layout zentrieren
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
