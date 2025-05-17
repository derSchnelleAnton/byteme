package edu.byteme.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.LumoUtility.AlignItems;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import com.vaadin.flow.theme.lumo.LumoUtility.FontWeight;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import com.vaadin.flow.theme.lumo.LumoUtility.Height;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import com.vaadin.flow.theme.lumo.LumoUtility.TextColor;
import com.vaadin.flow.theme.lumo.LumoUtility.Whitespace;

import edu.byteme.views.menu.MenuView;
import jakarta.annotation.security.PermitAll;
import org.vaadin.lineawesome.LineAwesomeIcon;

import edu.byteme.security.SecurityService;

@Layout
@AnonymousAllowed
@PermitAll
public class MainLayout extends AppLayout {

    private final SecurityService securityService;

    public static class MenuItemInfo extends ListItem {

        private final Class<? extends Component> view;

        public MenuItemInfo(String menuTitle, Component icon, Class<? extends Component> view) {
            this.view = view;
            RouterLink link = new RouterLink();
            link.addClassNames(Display.FLEX, Gap.XSMALL, Height.MEDIUM, AlignItems.CENTER, Padding.Horizontal.SMALL, TextColor.BODY);
            link.setRoute(view);

            Span text = new Span(menuTitle);
            text.addClassNames(FontWeight.MEDIUM, FontSize.MEDIUM, Whitespace.NOWRAP);

            if (icon != null) {
                link.add(icon);
            }
            link.add(text);
            add(link);
        }

        public Class<?> getView() {
            return view;
        }
    }

    public MainLayout(SecurityService securityService) {
        this.securityService = securityService;
        addToNavbar(createHeaderContent());
    }

    private Component createHeaderContent() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setPadding(true);
        header.setAlignItems(FlexComponent.Alignment.CENTER); // Vertikale Ausrichtung

        // Linke Seite des Headers (kann für ein Logo oder Navigation verwendet werden)
        Div title = new Div();
        title.setText("ByteMe" + " - Ordering System");
        title.addClassName("app-title");

        // Rechte Seite des Headers — Benutzerinfo und Login/Logout-Button
        HorizontalLayout userInfoLayout = new HorizontalLayout();
        userInfoLayout.setAlignItems(FlexComponent.Alignment.CENTER); // Vertikale Ausrichtung
        userInfoLayout.setSpacing(true);

        try {
            var user = securityService.getAuthenticatedUser();
            if (user != null) {
                // Benutzer authentifiziert
                Span greeting = new Span("Hello " + user.getUsername());
                Button logoutButton = new Button("Logout", event -> {
                    securityService.logout();
                    UI.getCurrent().getPage().reload();
                });

                // UI-sicherer Zugriff
                UI ui = UI.getCurrent();
                ui.access(() -> {
                    userInfoLayout.removeAll(); // Bereinigung
                    userInfoLayout.add(greeting, logoutButton); // Hinzufügen neuer UI-Elemente
                });
            } else {
                // Benutzer nicht authentifiziert (Login anzeigen)
                Button loginButton = new Button("Login", event -> UI.getCurrent().navigate("login"));

                UI ui = UI.getCurrent();
                ui.access(() -> {
                    userInfoLayout.removeAll();
                    userInfoLayout.add(loginButton);
                });
            }
        } catch (Exception e) {
            // Fehler behandeln
            UI.getCurrent().access(() -> {
                userInfoLayout.removeAll();
                Button loginButton = new Button("Login", event -> UI.getCurrent().navigate("login"));
                userInfoLayout.add(loginButton);
            });
        }

        // Platz zwischen Titel und Benutzerinformation
        header.add(title);
        header.add(userInfoLayout);
        header.expand(title); // Titel nimmt den verbliebenen Platz ein
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        return header;
    }


    private MenuItemInfo[] createMenuItems() {
        return new MenuItemInfo[]{
                new MenuItemInfo("Menu", LineAwesomeIcon.UTENSILS_SOLID.create(), MenuView.class)
        };
    }
}
