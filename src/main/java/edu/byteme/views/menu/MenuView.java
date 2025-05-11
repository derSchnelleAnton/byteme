package edu.byteme.views.menu;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import edu.byteme.data.entities.MenuItem;
import edu.byteme.data.repositories.MenuRepository;
import edu.byteme.views.MainLayout;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import edu.byteme.components.MultiPanel;

import java.util.ArrayList;
import java.util.List;

@PageTitle("Menu")
@Route(value = "", layout = MainLayout.class)
@PermitAll
@CssImport("./themes/my-app/menu-view.css")
public class MenuView extends HorizontalLayout {

    private final MenuRepository menuRepository;
    private final Div menuContainer;
    private final List<MenuItem> cartItems = new ArrayList<>();
    private final VerticalLayout cartContents = new VerticalLayout();
    private final Paragraph cartTotal = new Paragraph();
    
    private MultiPanel multiPanel = null;

    @Autowired
    public MenuView(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
        setSizeFull();
        addClassName("menu-view");

        menuContainer = new Div();
        menuContainer.addClassName("menu-container");

        HorizontalLayout topBar = new HorizontalLayout();
        topBar.setWidthFull();
        topBar.setJustifyContentMode(JustifyContentMode.END);
        topBar.setAlignItems(Alignment.CENTER);

        // Button schaltet Sichtbarkeit des multiPanels
        Button cartToggle = new Button("🛒 Cart", click -> multiPanel.toggleVisibility());
        topBar.add(cartToggle);
        menuContainer.add(topBar);

        Div menuList = new Div();
        menuList.addClassName("menu-list");
        menuContainer.add(menuList);

        // Erzeuge neues multiPanel-Objekt
        multiPanel = new MultiPanel();

        add(menuContainer, multiPanel);
        expand(menuContainer);

        // Lade verfügbare Menüpunkte
        menuRepository.findByIsAvailableTrue().forEach(item -> {
            menuList.add(createMenuCard(item));
        });
    }

    private Div createMenuCard(MenuItem item) {
        Div card = new Div();
        card.addClassName("menu-card");

        H2 name = new H2(item.getName());
        Paragraph desc = new Paragraph(item.getDescription());

        Paragraph price = new Paragraph("€" + item.getPrice());
        Button addToCart = new Button("Add to Cart", e -> multiPanel.addItem(item));

        Div rightSide = new Div(price, addToCart);
        rightSide.addClassName("price-button");

        card.add(name, desc, rightSide);
        return card;
    }

    private void updateCart() {
        cartContents.removeAll();
        double total = 0;

        for (MenuItem item : cartItems) {
            Paragraph line = new Paragraph(item.getName() + " — €" + item.getPrice());
            cartContents.add(line);
            total += item.getPrice();
        }

        cartTotal.setText("Total: €" + String.format("%.2f", total));
    }
}
