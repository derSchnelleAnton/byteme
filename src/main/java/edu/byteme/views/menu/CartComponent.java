package edu.byteme.views.menu;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.UIScope;
import edu.byteme.data.entities.Client;
import edu.byteme.data.entities.MenuItem;
import edu.byteme.data.entities.Order;
import edu.byteme.data.repositories.ClientRepository;
import edu.byteme.security.SecurityService;
import edu.byteme.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer; // For callback function

@UIScope
@Component
public class CartComponent extends HorizontalLayout {
    private final Details cartDetails = new Details("Basket");
    private final Details orderDetails = new Details("Orders");

    // Callback variables
    private Consumer<MenuItem> onRemoveMenuItem;
    private Consumer<MenuItem> setOnAddMenuItem;
    private Runnable onCheckoutClicked;
    private OnOrderSelectedListener onOrderSelected;

    // Autowired services
    private final SecurityService securityService;
    private final ClientRepository clientRepository;
    private final VerticalLayout rightSide;

    @Autowired
    public CartComponent(SecurityService securityService, ClientRepository clientRepository) {
        this.securityService = securityService;
        this.clientRepository = clientRepository;

        this.getStyle()
                .set("max-width", "320px")
                .set("box-sizing", "border-box");
        this.setSpacing(false);

        // Left side contains only the expand / collapse icon
        VerticalLayout leftSide = new VerticalLayout();
        leftSide.getStyle()
                .set("width", "20px")
                .set("border-right", "1px solid lightgray")
                .set("cursor", "pointer")
                .set("box-sizing", "border-box");
        leftSide.setHeightFull();
        leftSide.setAlignItems(FlexComponent.Alignment.CENTER);

        Paragraph sidebarLabel = new Paragraph("🛒");

        leftSide.add(sidebarLabel);

        // Right side contains menu and order items
        rightSide = new VerticalLayout();
        rightSide.getStyle()
                .set("width", "300px")
                .set("background", "#fff")
                .set("box-sizing", "border-box");
        rightSide.setHeightFull();
        rightSide.getStyle().set("overflow", "auto"); // Scrollable

        // Click listener for whole left side, click displays right side and rotates icon
        leftSide.addClickListener(e -> {
            boolean wasVisible = rightSide.isVisible();

            rightSide.setVisible(!wasVisible);
            rightSide.setWidth(wasVisible ? "0px" : "300px");
        });

        rightSide.add(cartDetails);
        cartDetails.setOpened(true);

        List<Order> orders = fetchUserOrders();

        // Only display orders if there are orders, otherwise only display basket
        if (!orders.isEmpty()) {
            rightSide.add(orderDetails);
            displayOrders(orders);
            orderDetails.setOpened(true);
        }

        add(leftSide, rightSide);
    }

    /**
     *
     * @return Orders for current user
     */
    private List<Order> fetchUserOrders() {
        UserDetails userDetails = securityService.getAuthenticatedUser();
        if (userDetails != null) {
            return clientRepository.findByUserNameWithOrders(userDetails.getUsername())
                    .map(Client::getOrders)
                    .orElse(List.of());
        }
        return List.of();
    }

    /**
     * Callback method that informs frame when order button is clicked
     * @param callback Callback function
     */
    public void setOnCheckoutClicked(Runnable callback) {
        this.onCheckoutClicked = callback;
    }

    /**
     * Callback method that informs frame when order button is clicked
     * @param onOrderSelectedListener listener
     */
    public void setOnOrderSelected(OnOrderSelectedListener onOrderSelectedListener) {
        this.onOrderSelected = onOrderSelectedListener;
    }

    /**
     * Callback method that informs frame when remove item button is clicked
     * @param onRemoveMenuItem Callback function
     */
    public void setOnRemoveMenuItem(Consumer<MenuItem> onRemoveMenuItem) {
        this.onRemoveMenuItem = onRemoveMenuItem;
    }

    /**
     * Callback method that informs frame when add item button is clicked
     * @param onAddMenuItem Callback function
     */
    public void setOnAddMenuItem(Consumer<MenuItem> onAddMenuItem) {
        this.setOnAddMenuItem = onAddMenuItem;
    }

    /**
     * Creates one menu item card per menu item and adds it to the cart, also adds checkout button with total price
     * @param items List of all the menu items to be displayed in the cart
     */
    public void displayCart(List<MenuItem> items) {
        cartDetails.removeAll();

        double total = 0;

        if (!items.isEmpty()) {
            Map<String, Integer> itemsMap = new HashMap<>();

            for (MenuItem item : items) {
                // If the item is already counted, skip it
                if (itemsMap.containsKey(item.getName())) continue;

                int quantity = 0;
                for (MenuItem innerItem : items)
                    if (innerItem.getName().equals(item.getName()))
                        quantity++;

                // Add name of item to map to mark it as count
                itemsMap.put(item.getName(), quantity);

                // Callback function and remove button
                Button removeButton = new Button(new Icon(VaadinIcon.MINUS), e -> {
                    if (onRemoveMenuItem != null)
                        onRemoveMenuItem.accept(item);
                });

                // Callback function and add button
                Button addButton = new Button(new Icon(VaadinIcon.PLUS), e -> {
                    if (setOnAddMenuItem != null)
                        setOnAddMenuItem.accept(item);
                });

                VerticalLayout itemCard = getCartCard(
                        item.getName(),
                        item.getPrice() * quantity,
                        quantity,
                        removeButton,
                        addButton
                );
                // Add new card on top
                cartDetails.addComponentAsFirst(itemCard);

                total += (item.getPrice() * quantity);
            }
        }
        // Add checkout button at the very top
        cartDetails.addComponentAsFirst(getCheckoutButton(total));
    }

    /**
     *
     * @param title Title of the menu item (e.g. Pepperoni Pizza)
     * @param price Price as double
     * @param quantity Quantity of the menu item (e.g. 8 pizzas)
     * @param removeButton Button including callback logic
     * @param addButton Button including callback logic
     * @return Item card that is ready to be displayed for a menu item
     */
    private VerticalLayout getCartCard(String title, double price, int quantity, Button removeButton, Button addButton) {
        VerticalLayout outerContainer = new VerticalLayout();

        // Title
        Paragraph titleWrapper = new Paragraph(title);
        titleWrapper.getStyle().set("font-weight", "bold");

        // Price
        NumberFormat currencyFormat = NumberFormat.getNumberInstance(Locale.US);
        currencyFormat.setMinimumFractionDigits(2);
        currencyFormat.setMaximumFractionDigits(2);
        String formattedPrice = currencyFormat.format(price);
        Paragraph priceWrapper = new Paragraph(formattedPrice + " $");

        // Quantity
        Paragraph quantityWrapper = new Paragraph(String.valueOf(quantity));

        // Buttons
        addButton.addThemeVariants(ButtonVariant.LUMO_ICON);
        addButton.setAriaLabel("Add item");
        removeButton.addThemeVariants(ButtonVariant.LUMO_ICON);
        removeButton.setAriaLabel("Remove item");

        // Quantity and Buttons
        HorizontalLayout buttonControls = new HorizontalLayout(removeButton, quantityWrapper, addButton);
        buttonControls.setSpacing(true);

        // Bottom row
        HorizontalLayout bottomRow = new HorizontalLayout();
        bottomRow.setWidthFull();
        bottomRow.add(priceWrapper, buttonControls);
        bottomRow.expand(priceWrapper);
        bottomRow.setAlignItems(FlexComponent.Alignment.CENTER);

        // Put together everything
        outerContainer.add(titleWrapper, bottomRow);
        outerContainer.setPadding(false);
        outerContainer.setSpacing(false);

        // Same style as card for orders
        outerContainer.getStyle()
                .set("border-bottom", "1px solid lightgray")
                .set("background-color", "#F4F5F7")
                .set("border-radius", "4px")
                .set("padding-bottom", "4px")
                .set("padding", "8px")
                .set("margin-bottom", "8px")
                .set("width", "250px");

        return outerContainer;
    }

    /**
     *
     * @param price The total price of the order, button turns grey if 0
     * @return Checkout button that can be displayed just like a menu item
     */
    private Div getCheckoutButton(double price) {
        Div wideButton = new Div();

        // Action text
        Paragraph actionText = new Paragraph("Proceed to Order");
        actionText.getStyle()
                .set("margin", "0")
                .set("color", price > 0 ? "white" : "#8A989A");

        // Price and price format
        NumberFormat currencyFormat = NumberFormat.getNumberInstance(Locale.US);
        currencyFormat.setMinimumFractionDigits(2);
        currencyFormat.setMaximumFractionDigits(2);
        String formattedPrice = currencyFormat.format(price);
        Paragraph priceText = new Paragraph(formattedPrice + " $");
        priceText.getStyle().set("margin", "0");

        // Put everything together
        wideButton.add(actionText, priceText);

        // Click listener for when checkout button is pressed
        wideButton.addClickListener(e -> {
            if (onCheckoutClicked != null && price > 0)
                onCheckoutClicked.run();
        });

        wideButton.getStyle()
                .set("display", "flex")
                .set("justify-content", "space-between")
                .set("align-items", "center")
                .set("font-weight", "bold")
                .set("cursor", "pointer")
                .set("border-radius", "4px")
                .set("background-color", price > 0 ? "#006AF5" : "#F4F5F7")
                .set("height", "40px")
                .set("padding", "0 1rem")
                .set("color", price > 0 ? "white" : "#8A989A")
                .set("margin-bottom", "8px")
                .set("width", "220px");

        return wideButton;
    }

    /**
     *
     * Creates one order card per order and displays it in details
     * @param orders List of all orders made by the customer
     */
    public void displayOrders(List<Order> orders) {
        orderDetails.removeAll();
        for (Order order : orders)
            orderDetails.add(getOrderCard(order));
    }

    /**
     *
     * @param order The order to be displayed as card
     * @return Order card item that is ready to be displayed for an order
     */
    private VerticalLayout getOrderCard(Order order) {
        VerticalLayout outerContainer = new VerticalLayout();

        // Order date
        LocalDateTime dateTime = LocalDateTime.parse(String.valueOf(order.getOrderDate()));
        String formatted = dateTime.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
        Paragraph orderDate = new Paragraph(formatted);
        orderDate.getStyle()
                .set("font-weight", "bold")
                .set("font-style", "underline");

        // we have a method that calculates total cost of order in Service
        double priceCalculated = OrderService.getTotalCostOfOrder(order);

        // Order total
        NumberFormat currencyFormat = NumberFormat.getNumberInstance(Locale.US);
        currencyFormat.setMinimumFractionDigits(2);
        currencyFormat.setMaximumFractionDigits(2);
        String formattedPrice = currencyFormat.format(priceCalculated);
        Paragraph orderTotal = new Paragraph(formattedPrice + " $");

        // Map status enum to text
        String status = switch (String.valueOf(order.getStatus())) {
            case "PENDING" -> "New 🔥";
            case "CONFIRMED" -> "Confirmed 👍🏻";
            case "IN_PROGRESS" -> "In Progress 👩🏻‍🍳";
            case "IN_DELIVERY" -> "In Delivery 🚗";
            case "DELIVERED" -> "Delivered ✅";
            case "CANCELLED" -> "Cancelled ❌";
            default -> "Unknown";
        };
        Paragraph statusText = new Paragraph(status);

        // Bottom row
        HorizontalLayout statusAndTotal = new HorizontalLayout();
        statusAndTotal.setWidthFull();
        statusAndTotal.add(orderTotal, statusText);
        statusAndTotal.expand(orderTotal);
        statusAndTotal.setAlignItems(FlexComponent.Alignment.CENTER);

        // Put together everything
        outerContainer.add(orderDate, statusAndTotal);
        outerContainer.setPadding(false);
        outerContainer.setSpacing(false);

        // Click listener for navigation to order
        outerContainer.addClickListener(e -> {
            if(onOrderSelected != null){
                onOrderSelected.onOrderSelected(order);
            }
        });

        // Same style as card for items
        outerContainer.getStyle()
                .set("border-bottom", "1px solid lightgray")
                .set("background-color", "#F4F5F7")
                .set("border-radius", "4px")
                .set("cursor", "pointer")
                .set("padding-bottom", "4px")
                .set("padding", "8px")
                .set("margin-bottom", "8px")
                .set("width", "250px");

        return outerContainer;
    }

    /**
     * This is an interface which an observer class/component sets or implements
     * and the method here gets called by observable
     */
    public interface OnOrderSelectedListener {
        void onOrderSelected(Order order);
    }

    public void refreshOrders(){
        rightSide.remove(orderDetails);
        List<Order> orders = fetchUserOrders();
        // Only display orders if there are orders, otherwise only display basket
        if (!orders.isEmpty()) {
            rightSide.add(orderDetails);
            displayOrders(orders);
            orderDetails.setOpened(true);
        }
    }
}
