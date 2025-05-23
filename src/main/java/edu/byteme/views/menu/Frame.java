package edu.byteme.views.menu;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.shared.Registration;

import edu.byteme.data.entities.Client;
import edu.byteme.data.entities.MenuItem;
import edu.byteme.data.entities.Order;
import edu.byteme.data.repositories.ClientRepository;
import edu.byteme.data.repositories.MenuRepository;
import edu.byteme.events.OrderBroadcaster;
import edu.byteme.services.OrderService;
import edu.byteme.views.MainLayout;
import edu.byteme.views.orders.OrderTimeLine;
import jakarta.annotation.security.PermitAll;

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
    private final VerticalLayout footer;
    
    // Add fields to track the current order and timeline
    private Order currentOrder;
    private OrderTimeLine currentOrderTimeLine;
    private Registration orderUpdateRegistration;

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
        footer = new VerticalLayout();
        footer.setVisible(true);
        footer.setWidthFull();
        footer.setMaxWidth("1000px");
        footer.setAlignItems(FlexComponent.Alignment.CENTER);

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
        // Clear any existing order update registration
        if (orderUpdateRegistration != null) {
            orderUpdateRegistration.remove();
            orderUpdateRegistration = null;
        }
        
        // Store a reference to the current order
        this.currentOrder = order;
        
        content.setItems(order.getMenuItems());
        content.setActionText("More");
        currentPage = Page.ORDERS;
        if(!footer.isVisible())
            footer.setVisible(true);
        footer.removeAll();
        cartPanel.setVisible(false);
        footer.add(getFooterButtons(Page.MENU));
        
        // Create and store a reference to the order timeline
        this.currentOrderTimeLine = new OrderTimeLine(order);
        footer.add(this.currentOrderTimeLine);
        
        // Register for order updates
        orderUpdateRegistration = OrderBroadcaster.register(this::handleOrderUpdate);
        
        // Calculate total price based on the ORDER's menu items, not cart items
        double totalPriceValue = 0.0;
        for (MenuItem item : order.getMenuItems()) {
            totalPriceValue += item.getPrice();
        }

        NumberFormat currencyFormat = NumberFormat.getNumberInstance(Locale.US);
        currencyFormat.setMinimumFractionDigits(2);
        currencyFormat.setMaximumFractionDigits(2);
        String formattedPrice = currencyFormat.format(totalPriceValue);

        // Layout für Total
        HorizontalLayout totalPrice = new HorizontalLayout();
        totalPrice.setWidthFull();
        totalPrice.setJustifyContentMode(JustifyContentMode.END);
        totalPrice.setAlignItems(Alignment.CENTER);

        Paragraph totalText = new Paragraph("Total: " + formattedPrice + "\u00A0$");
        totalText.getStyle()
                .set("font-weight", "bold")
                .set("margin", "0")
                .set("color", "#006AF5");

        totalPrice.add(totalText);
        footer.add(totalPrice);
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

        if (!footer.isVisible())
            footer.setVisible(true);

        footer.removeAll();
        cartPanel.setVisible(false);
        footer.add(getFooterButtons(Page.ORDERS));

        double totalPriceValue = 0.0;
        for (MenuItem item : cartItems)
            totalPriceValue += item.getPrice();

        NumberFormat currencyFormat = NumberFormat.getNumberInstance(Locale.US);
        currencyFormat.setMinimumFractionDigits(2);
        currencyFormat.setMaximumFractionDigits(2);
        String formattedPrice = currencyFormat.format(totalPriceValue);

        // Layout für Total
        HorizontalLayout totalPrice = new HorizontalLayout();
        totalPrice.setWidthFull();
        totalPrice.setJustifyContentMode(JustifyContentMode.END); // wichtig!
        totalPrice.setAlignItems(Alignment.CENTER);

        Paragraph totalText = new Paragraph("Total: " + formattedPrice + "\u00A0$");
        totalText.getStyle()
                .set("font-weight", "bold")
                .set("margin", "0")
                .set("color", "#006AF5");

        totalPrice.add(totalText);
        footer.add(totalPrice);
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

        Button backButton = new Button("Back", new Icon(VaadinIcon.ARROW_LEFT));
        backButton.setIconAfterText(false);
        backButton.addClickListener(event -> switchToMenu());

        Button orderButton = new Button("Order", new Icon(VaadinIcon.MONEY));
        backButton.setIconAfterText(false);
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

    /**
     * Handle order updates received from the broadcaster
     * @param updatedOrder The updated order
     */
    private void handleOrderUpdate(Order updatedOrder) {
        // Only process updates for the order that's currently being viewed
        if (currentOrder != null && updatedOrder.getId().equals(currentOrder.getId())) {
            // Update our stored order reference immediately
            currentOrder = updatedOrder;
            
            // Let the timeline component handle its own update
            // It will automatically update when it receives the broadcast
            // No need to manually refresh the component here
        }
    }
    
    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
        // Clean up subscription to prevent memory leaks
        if (orderUpdateRegistration != null) {
            orderUpdateRegistration.remove();
            orderUpdateRegistration = null;
        }
    }
}