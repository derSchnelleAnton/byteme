package edu.byteme.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Nav;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility.AlignItems;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import com.vaadin.flow.theme.lumo.LumoUtility.FontWeight;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import com.vaadin.flow.theme.lumo.LumoUtility.Height;
import com.vaadin.flow.theme.lumo.LumoUtility.ListStyleType;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.Overflow;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import com.vaadin.flow.theme.lumo.LumoUtility.TextColor;
import com.vaadin.flow.theme.lumo.LumoUtility.Whitespace;

import edu.byteme.views.admin.AdminDashboardView;
import jakarta.annotation.security.PermitAll;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import edu.byteme.security.SecurityService;

import java.util.ArrayList;
import java.util.List;

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
            link.addClassNames(Display.FLEX, Gap.XSMALL, Height.MEDIUM, AlignItems.CENTER,
                    Padding.Horizontal.SMALL, TextColor.BODY);
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
        header.setAlignItems(FlexComponent.Alignment.CENTER); // Vertically align items

        // Left side of the header – typically for logo or navigation
        Div title = new Div();
        title.setText("ByteMe - Ordering System");
        title.getStyle().set("color", "#006AF5")
                .set("font-weight", "bold")
                .set("font-size", "large");

        // Right side of the header – user info and login/logout button
        HorizontalLayout userInfoLayout = new HorizontalLayout();
        userInfoLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        userInfoLayout.setSpacing(true);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null) {
            System.out.println("Current roles: " + auth.getAuthorities());

            // Example role check
            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            if (isAdmin) {
                System.out.println("User is admin");
            }
        }

        try {
            var user = securityService.getAuthenticatedUser();
            if (user != null) {
                // User is authenticated
                Span greeting = new Span("Hello " + user.getUsername());
                Button logoutButton = new Button("Logout", event -> {
                    securityService.logout();
                    UI.getCurrent().getPage().reload();
                });

                UI ui = UI.getCurrent();
                ui.access(() -> {
                    userInfoLayout.removeAll(); // Clear old UI elements
                    userInfoLayout.add(greeting, logoutButton);
                });
            } else {
                // User not authenticated – show login button
                Button loginButton = new Button("Login", event -> UI.getCurrent().navigate("login"));

                UI ui = UI.getCurrent();
                ui.access(() -> {
                    userInfoLayout.removeAll();
                    userInfoLayout.add(loginButton);
                });
            }
        } catch (Exception e) {
            // Handle unexpected errors
            UI.getCurrent().access(() -> {
                userInfoLayout.removeAll();
                Button loginButton = new Button("Login", event -> UI.getCurrent().navigate("login"));
                userInfoLayout.add(loginButton);
            });
        }

        // Navigation bar (currently not populated with menu items)
        Nav nav = new Nav();
        nav.addClassNames(Display.FLEX, Overflow.AUTO, Padding.Horizontal.MEDIUM, Padding.Vertical.XSMALL);

        UnorderedList list = new UnorderedList();
        list.addClassNames(Display.FLEX, Gap.SMALL, ListStyleType.NONE, Margin.NONE, Padding.NONE);
        nav.add(list);


        for (MenuItemInfo menuItem : createMenuItems()) {
            list.add(menuItem);
        }


        header.add(title);
        header.add(nav);
        header.add(userInfoLayout);
        header.expand(title); // Let title take up remaining space
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        return header;
    }

    private MenuItemInfo[] createMenuItems() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        List<MenuItemInfo> items = new ArrayList<>();

        // You can add conditional menu items here based on user roles
        // Example:
        if (isAdmin) {
            items.add(new MenuItemInfo("Admin Dashboard", null, AdminDashboardView.class));
        }

        return items.toArray(new MenuItemInfo[0]);
    }
}