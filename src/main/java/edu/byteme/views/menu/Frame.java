package edu.byteme.views.menu;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import edu.byteme.data.entities.Client;
import edu.byteme.data.entities.MenuItem;
import edu.byteme.data.entities.Order;
import edu.byteme.data.repositories.ClientRepository;
import edu.byteme.data.repositories.MenuRepository;
import edu.byteme.services.OrderService;
import edu.byteme.views.MainLayout;
import edu.byteme.views.orders.OrderTimeLine;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@PageTitle("Menu")
@Route(value = "", layout = MainLayout.class)
@PermitAll
@AnonymousAllowed
public class Frame extends VerticalLayout {
    private final List<MenuItem> cartItems = new ArrayList<>(); // No reason why this is up here but don't want to break anything so it stays
    private final CartComponent cartPanel;
    private final LargeListComponent content;
    private final ClientRepository clientRepository;
    private final OrderService orderService;
    private Page currentPage;
    private final MenuRepository menuRepository;
    private final Div footer;

    @Autowired
    public Frame(
            MenuRepository menuRepository,
            CartComponent cartPanel,
            OrderService orderService,
            ClientRepository clientRepository
    ) {
        this.cartPanel = cartPanel;
        this.menuRepository = menuRepository;
        this.clientRepository = clientRepository;
        this.orderService = orderService;

        // Footer
        footer = new Div();
        footer.addClassName("footer");
        footer.setVisible(true);

        // Base layout configuration
        setSizeFull();
        setPadding(false);
        setSpacing(false);

        // Content layout is the frame that contains everything
        HorizontalLayout contentLayout = new HorizontalLayout();
        contentLayout.setSizeFull();
        contentLayout.setPadding(false);
        contentLayout.setSpacing(false);

        // Content area contains the actual content
        VerticalLayout contentArea = new VerticalLayout();
        contentArea.setSizeFull();
        contentArea.setPadding(true);
        contentArea.setSpacing(false);
        contentArea.setHeightFull();
        contentArea.getStyle().set("overflow", "auto").set("padding", "1rem");


        // Shopping cart
        cartPanel.setHeightFull();
        contentLayout.add(contentArea, cartPanel);
        contentLayout.expand(contentArea);

        cartPanel.displayCart(cartItems);

        cartPanel.setOnCheckoutClicked(() -> {
            if (isUserLoggedIn()) {
                switchToPlaceOrder();
            } else {
                UI.getCurrent().navigate("login");
            }
        });

        cartPanel.setOnRemoveMenuItem(itemToRemove -> {
            for (int i = cartItems.size() - 1; i >= 0; i--) {
                if (cartItems.get(i).getName().equals(itemToRemove.getName())) {
                    cartItems.remove(i);
                    break;
                }
            }
            cartPanel.displayCart(cartItems);
        });

        cartPanel.setOnAddMenuItem(item -> {
            cartItems.add(item);
            cartPanel.displayCart(cartItems);
        });

        content = new LargeListComponent();
        switchToMenu();

        content.setMenuItemEvent(item -> {
            switch (currentPage) {
                case MENU -> {
                    cartItems.add(item);
                    cartPanel.displayCart(cartItems);
                }
                case ORDERS
                        -> new MenuItemDialog(item).show(this);
            }
        });

        // Registering watcher for orders
        cartPanel.setOnOrderSelected(this::switchToOrders);

        contentArea.add(footer, content);
        contentArea.expand(content);
        contentArea.setHorizontalComponentAlignment(Alignment.CENTER, footer);

        add(contentLayout);
        expand(contentLayout);
    }

    /**
     * Enum for all pages a non-admin user can possibly navigate to
     */
    enum Page {
        MENU, ORDERS, CART
    }

    /**
     * Switches to order view
     * @param order to be displayed
     */
    private void switchToOrders(Order order) {
        content.setItems(order.getMenuItems());
        content.setActionText("More");
        currentPage = Page.ORDERS;
        if(!footer.isVisible())
            footer.setVisible(true);
        footer.removeAll();
        cartPanel.setVisible(true);
        footer.add(getFooterButtons(Page.MENU));
        footer.add(new OrderTimeLine(order));
    }

    /**
     * Switches to menu view
     */
    private void switchToMenu() {
        List<MenuItem> menuItems = menuRepository.findByIsAvailableTrue();
        content.setItems(menuItems);
        content.setActionText("Add to cart");
        currentPage = Page.MENU;
        footer.removeAll();
        footer.setWidthFull();
        footer.setVisible(true);
        cartPanel.setVisible(true);
    }

    /**
     * Switches to place-order view
     */
    private void switchToPlaceOrder() {
        content.setItems(cartItems);
        content.setActionText(null);
        currentPage = Page.CART;
        if(!footer.isVisible())
            footer.setVisible(true);
        footer.removeAll();
        cartPanel.setVisible(false);
        footer.add(getFooterButtons(Page.ORDERS));
    }

    /**
     *
     * @return Boolean whether a user is currently logged in or not
     */
    private boolean isUserLoggedIn() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }

    /**
     *
     * @param currentPage Determines wheter order-button shows or not
     * @return Two button element (back and order)
     */
    private HorizontalLayout getFooterButtons(Page currentPage) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        buttonLayout.setWidthFull();
        buttonLayout.getStyle()
                .set("padding", "16px");

        Button backButton = new Button("Back");
        backButton.addClickListener(event -> switchToMenu());

        Button orderButton = new Button("Order");
        orderButton.addClickListener(event -> {
            switchToMenu();

            User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Optional<Client> client = clientRepository.findByUserName(currentUser.getUsername());

            client.ifPresent(value -> orderService.placeOrder(cartItems, value));

            cartItems.clear();
            cartPanel.displayCart(cartItems);
            cartPanel.refreshOrders();
        });

        Div spacer = new Div();
        spacer.setWidthFull();

        if (currentPage == Page.ORDERS) {
            buttonLayout.add(backButton, spacer, orderButton);
        } else {
            buttonLayout.add(backButton, spacer);
        }

        buttonLayout.setAlignItems(Alignment.CENTER);
        buttonLayout.expand(spacer);

        return buttonLayout;
    }
}