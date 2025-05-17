/**
 * wa7505@mci4me.at
 * Missing as of 2025 5 17
 *  - Link to order from order element
 *  - Real price for orders
 */

package edu.byteme.views.menu;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import edu.byteme.data.entities.MenuItem;
import edu.byteme.data.entities.Order;
import edu.byteme.views.orders.OrderView;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer; // For callback function

@CssImport("./themes/my-app/menu-view.css")
public class CartComponent extends Div {
    private Details cartDetails = new Details("Cart");
    private Details orderDetails = new Details("Orders");
    private Consumer<MenuItem> onRemoveMenuItem; // Callback member variable
    private Consumer<MenuItem> setOnAddMenuItem; // Callback member variable
    private Runnable onCheckoutClicked;

    public CartComponent() {
        this.addClassName("cart-component");
        cartDetails.setOpened(true);
        orderDetails.setOpened(true);
        add(cartDetails, orderDetails);
    }

    public void setOnCheckoutClicked(Runnable callback) {
        this.onCheckoutClicked = callback;
    }

    public void setOnRemoveMenuItem(Consumer<MenuItem> onRemoveMenuItem) {
        this.onRemoveMenuItem = onRemoveMenuItem;
    }

    public void setOnAddMenuItem(Consumer<MenuItem> onAddMenuItem) {
        this.setOnAddMenuItem = onAddMenuItem;
    }

    public void displayCart(List<MenuItem> items) {
        cartDetails.removeAll();

        double total = 0;
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
        // Add checkout button at the very top
        cartDetails.addComponentAsFirst(getCheckoutButton(total));
    }

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

        outerContainer.getStyle()
                .set("border-bottom", "1px solid lightgray")
                .set("padding-bottom", "4px")
                .set("background-color", "#F4F5F7")
                .set("border-radius", "4px")
                .set("padding", "8px")
                .set("margin-bottom", "8px");

        return outerContainer;
    }

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
                .set("margin-bottom", "8px");

        return wideButton;
    }

    public void displayOrders(List<Order> items) {
        orderDetails.removeAll();
        for (Order item : items)
            orderDetails.add(getOrderCard(item));
    }

    private VerticalLayout getOrderCard(Order order) {
        VerticalLayout outerContainer = new VerticalLayout();

        // Order date
        LocalDateTime dateTime = LocalDateTime.parse(String.valueOf(order.getOrderDate()));
        String formatted = dateTime.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
        Paragraph orderDate = new Paragraph(formatted);
        orderDate.getStyle()
                .set("font-weight", "bold")
                .set("font-style", "underline");

        // Order total
        NumberFormat currencyFormat = NumberFormat.getNumberInstance(Locale.US);
        currencyFormat.setMinimumFractionDigits(2);
        currencyFormat.setMaximumFractionDigits(2);
        String formattedPrice = currencyFormat.format(9.99);
        Paragraph orderTotal = new Paragraph(formattedPrice + " $");

        // Map status enum to text
        String status = switch (String.valueOf(order.getStatus())) {
            case "NEW" -> "New 🔥";
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
            UI.getCurrent().navigate(OrderView.class); // SHOULD LINK TO ORDER VIEW OF ORDER
        });

        // Style card
        outerContainer.getStyle()
                .set("border-bottom", "1px solid lightgray")
                .set("padding-bottom", "4px")
                .set("background-color", "#F4F5F7")
                .set("border-radius", "4px")
                .set("padding", "8px")
                .set("cursor", "pointer")
                .set("margin-bottom", "8px");

        return outerContainer;
    }
}
