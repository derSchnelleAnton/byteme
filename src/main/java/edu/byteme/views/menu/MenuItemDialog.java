package edu.byteme.views.menu;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import edu.byteme.data.entities.MenuItem;
import edu.byteme.util.Util;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class MenuItemDialog extends Dialog {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd MMMM, yyyy");

    public MenuItemDialog(MenuItem item) {
        setHeaderTitle(item.getName());

        NumberFormat currencyFormat = NumberFormat.getNumberInstance(Locale.US);
        currencyFormat.setMinimumFractionDigits(2);
        currencyFormat.setMaximumFractionDigits(2);
        String formattedPrice = currencyFormat.format(item.getPrice());

        Paragraph desc = new Paragraph("Description:\n" + item.getDescription());
        Paragraph price = new Paragraph("Price:\n" + formattedPrice + "\u00A0$");
        Paragraph available = new Paragraph("Still available:\n" +
                (item.isAvailable() ? "Yes" : "No"));
        Paragraph since = new Paragraph("Since:\n" +
                item.getCreatedAt().format(DATE_FORMATTER));

        VerticalLayout textLayout = new VerticalLayout(desc, price, available, since);
        textLayout.setPadding(false);
        textLayout.setSpacing(false);
        textLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        textLayout.getStyle()
                .set("width", "18rem")
                .set("max-width", "100%");

        Image img = new Image(Util.getPathFromName(item.getName()), "Menu image");
        img.setMaxHeight("18rem"); // optional: maximale Höhe festlegen
        HorizontalLayout body = new HorizontalLayout(img, textLayout);
        body.setPadding(false);
        body.setSpacing(true);

        add(body);

        // Footer with close button
        Button closeButton = new Button("Close", e -> this.close());
        getFooter().add(closeButton);

        this.open();
    }

    /**
     * Zeigt den Dialog an.
     * Optional: falls du Kontrolle über den Zeitpunkt haben möchtest.
     */
    public void show(Component parent) {
        parent.getElement().appendChild(this.getElement());
        this.open();
    }
}