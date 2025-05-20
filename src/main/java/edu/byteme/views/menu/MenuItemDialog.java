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

import java.time.format.DateTimeFormatter;

public class MenuItemDialog extends Dialog {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd MMMM, yyyy");

    public MenuItemDialog(MenuItem item) {
        // Header
        setHeaderTitle(item.getName());

        // Description, Price, Availability, CreatedAt
        Paragraph desc = new Paragraph("Description: " + item.getDescription());
        Paragraph price = new Paragraph("Price: " + item.getPrice());
        Paragraph available = new Paragraph("Still available: " +
                (item.isAvailable() ? "Yes" : "No"));
        Paragraph since = new Paragraph("Since: " +
                item.getCreatedAt().format(DATE_FORMATTER));

        // Layout für Text
        VerticalLayout textLayout = new VerticalLayout(desc, price, available, since);
        textLayout.setPadding(false);
        textLayout.setSpacing(false);
        textLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        textLayout.getStyle()
                .set("width", "18rem")
                .set("max-width", "100%");

        // Bild und Text nebeneinander
        Image img = new Image(Util.getPathFromName(item.getName()), "Menu image");
        img.setMaxHeight("18rem"); // optional: maximale Höhe festlegen
        HorizontalLayout body = new HorizontalLayout(img, textLayout);
        body.setPadding(false);
        body.setSpacing(true);

        add(body);

        // Footer mit Close-Button
        Button closeButton = new Button("Close", e -> this.close());
        getFooter().add(closeButton);

        // Automatisch öffnen, sobald die Komponente dem UI hinzugefügt wird
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