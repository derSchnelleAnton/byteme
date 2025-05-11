package edu.byteme.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import edu.byteme.data.entities.MenuItem;
import com.vaadin.flow.component.html.Image;

import java.util.ArrayList;
import java.util.List;

@CssImport("./themes/my-app/components.css")
public class MultiPanel extends Div {

    private final List<MenuItem> cartItems = new ArrayList<>();
    private final List<MenuItem> orders = new ArrayList<>();

    private final VerticalLayout cartContents = new VerticalLayout();
    private final VerticalLayout orderContents = new VerticalLayout();

    public MultiPanel() {
        addClassName("cart-panel");
        setVisible(false);

        Details cartSection = new Details("🛒 Cart", cartContents);
        Details orderSection = new Details("📦 Orders", orderContents);

        updateItems(); // Adds total button, total button is list object so that it is collapsable with the rest of the items
        updateOrders(); // Does nothing right now

        add(cartSection, orderSection);
    }

    public void addItem(MenuItem item) {
        cartItems.add(item);
        updateItems();
    }

    private void updateItems() {
        cartContents.removeAll();
        double total = 0;

        for (MenuItem item : cartItems) {
            HorizontalLayout row = renderItem(item);
            cartContents.add(row);
            total += item.getPrice();
        }

        // 2 Create whole row
        HorizontalLayout row = new HorizontalLayout(
                new Paragraph("Total " + String.format("%.2f", total) + " €"),
                new Button("Checkout", e -> {
                    System.out.println("CHECKOUT BUTTON PRESSED");
                })
        );

        cartContents.add(row);
    }

    public HorizontalLayout renderItem(MenuItem item) {
        // Level 3 - Contains name of item
        HorizontalLayout nameContainer = new HorizontalLayout();
        nameContainer.addClassName("cart-item-name-container");
        nameContainer.add(
                new Paragraph(item.getName())
        );

        // Level 3 - Contains price and remove-button
        HorizontalLayout detailContainer = new HorizontalLayout();
        detailContainer.addClassName("cart-item-detail-container");
        detailContainer.add(
                new Paragraph(String.valueOf(item.getPrice())),
                new Button("❌", e -> {
                    cartItems.remove(item);
                    updateItems();
                })
        );

        // Level 2 - Contains detailContainer and nameContainer
        VerticalLayout textContainer = new VerticalLayout();
        textContainer.addClassName("cart-item-text-container");
        textContainer.add(
                nameContainer, detailContainer
        );

        // Level 2 - Contains item picture (placeholder at this point)
        Image img = new Image("icons/icon.png", "Picture of " + item.getName());
        img.addClassName("cart-item-picture");

        VerticalLayout pictureContainer = new VerticalLayout();
        pictureContainer.addClassName("cart-item-picture-container");
        pictureContainer.add(
               img
        );

        // Level 1
        HorizontalLayout outerContainer = new HorizontalLayout();
        outerContainer.addClassName("cart-item-card");

        outerContainer.add(pictureContainer, textContainer);

        return outerContainer;
    }

    public void addOrder(MenuItem item) {
        orders.add(item);
        updateOrders();
    }

    private void updateOrders() {
        orderContents.removeAll();

        for (MenuItem item : orders) {
            Paragraph line = new Paragraph(item.getName() + " — €" + item.getPrice());
            orderContents.add(line);
        }
    }

    public void toggleVisibility() {
        boolean show = !isVisible();
        setVisible(show);
        getElement().getClassList().set("cart-open", show);
    }
}