package edu.byteme.views.orders;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import edu.byteme.data.entities.MenuItem;
import edu.byteme.views.menu.MenuListView;

import java.util.List;

/**
 * PlaceOrderView zeigt eine Liste von MenuItems mit Preis,
 * basierend auf OrderView.
 */
public class PlaceOrderView extends VerticalLayout {

    public PlaceOrderView(List<MenuItem> items) {
        setSizeFull();
        setPadding(true);
        addClassName("place-order-view");

        //Reusing OrderView from Tinsae
        MenuListView menuListView = new MenuListView(items);
        menuListView.setActionText(""); // Kein Button-Text (Buttons -> ausblenden)
        menuListView.setMenuItemEvent(null); // Keine Aktionen nötig
        add(menuListView);

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        buttonLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        buttonLayout.setWidthFull();

        Button backButton = new Button("Back");
        Button orderButton = new Button("Order");

        buttonLayout.add(backButton, orderButton);
        add(buttonLayout);
    }
}
