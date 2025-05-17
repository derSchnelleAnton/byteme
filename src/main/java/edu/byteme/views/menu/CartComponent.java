package edu.byteme.views.menu;

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

import java.text.NumberFormat;
import java.util.*;
import java.util.function.Consumer; // For callback function


@CssImport("./themes/my-app/menu-view.css")
public class CartComponent extends Div {
    private Details cartDetails = new Details("🛒 Cart");
    private Details orderDetails = new Details("🚙 Orders");
    private Consumer<MenuItem> onRemoveMenuItem; // Callback member variable
    private Consumer<MenuItem> setOnAddMenuItem; // Callback member variable
    private Runnable onCheckoutClicked;

    public void setOnCheckoutClicked(Runnable callback) {
        this.onCheckoutClicked = callback;
    }

    public void setOnRemoveMenuItem(Consumer<MenuItem> onRemoveMenuItem) {
        this.onRemoveMenuItem = onRemoveMenuItem;
    }

    public void setOnAddMenuItem(Consumer<MenuItem> onAddMenuItem) {
        this.setOnAddMenuItem = onAddMenuItem;
    }

    public CartComponent() {
        this.addClassName("cart-component");
        add(cartDetails, orderDetails);
    }

    public void updateCart(List<MenuItem> items) {
        cartDetails.removeAll();
        double total = 0;
        Map<String, Integer> itemsMap = new HashMap<>();

        for (MenuItem item : items) {
            if (itemsMap.containsKey(item.getName())) {
                continue;
            }

            int quantity = 0;
            for (MenuItem innerItem : items) {
                if (innerItem.getName().equals(item.getName())) {
                    quantity++;
                }
            }

            itemsMap.put(item.getName(), quantity);

            Button removeButton = new Button(new Icon(VaadinIcon.MINUS), e -> {
                if (onRemoveMenuItem != null) onRemoveMenuItem.accept(item); // Trigger callback
            });

            Button addButton = new Button(new Icon(VaadinIcon.PLUS), e -> {
                if (setOnAddMenuItem != null) setOnAddMenuItem.accept(item); // Trigger callback
            });

            VerticalLayout itemCard = createMenuItemCard(
                    item.getName(),
                    item.getPrice() * quantity,
                    quantity,
                    removeButton,
                    addButton
            );

            cartDetails.addComponentAsFirst(itemCard);

            total = total + item.getPrice() * quantity;
        }

        cartDetails.addComponentAsFirst(
                createCheckoutButton(total)
        );
    }

    private VerticalLayout createMenuItemCard(String title, double price, int quantity, Button removeButton, Button addButton) {
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

        outerContainer.getStyle().set("border-bottom", "1px solid lightgray");

        return outerContainer;
    }

    private Div createCheckoutButton(double price) {
        Div wideButton = new Div();
        // flexbox und optische Styles
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
                .set("color", price > 0 ? "white" : "#8A989A");

        // Aktionstext
        Paragraph actionText = new Paragraph("Order");
        actionText.getStyle().set("margin", "0").set("color", price > 0 ? "white" : "#8A989A");

        // Preis formatieren
        NumberFormat currencyFormat = NumberFormat.getNumberInstance(Locale.US);
        currencyFormat.setMinimumFractionDigits(2);
        currencyFormat.setMaximumFractionDigits(2);
        String formattedPrice = currencyFormat.format(price);
        Paragraph priceText = new Paragraph(formattedPrice + " $");
        priceText.getStyle().set("margin", "0");

        // zuerst Preis, dann Text
        wideButton.add(actionText, priceText);

        // Click-Listener
        wideButton.addClickListener(e -> {
            if (onCheckoutClicked != null && price > 0) {
                onCheckoutClicked.run();
            }
        });

        return wideButton;
    }

    /**
     * This method is not yet implemented correctly
     */
    public void updateOrders(List<Order> items) {
        orderDetails.removeAll();
        for (Order item : items) {
            Paragraph itemLine = new Paragraph(item.getId() + " - " + item.getDeliveryDate());
            orderDetails.add(itemLine);
        }
    }

    /**
     * This method is not yet finished
     * @return new Div
     */
    private Div createOrderItemCard() {
        return new Div();
    }
}
