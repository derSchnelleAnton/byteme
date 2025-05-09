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

import java.util.ArrayList;
import java.util.List;

@PageTitle("Menu")
@Route(value = "", layout = MainLayout.class)
@PermitAll
@CssImport("./themes/my-app/menu-view.css")
public class MenuView extends HorizontalLayout {

    private final MenuRepository menuRepository;
    private Div cartPanel = null;
    private final Div menuContainer;
    private final List<MenuItem> cartItems = new ArrayList<>();
    private final VerticalLayout cartContents = new VerticalLayout();
    private final Paragraph cartTotal = new Paragraph();

    @Autowired
    public MenuView(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
        setSizeFull();
        addClassName("menu-view");

        // main menu section
        menuContainer = new Div();
        menuContainer.addClassName("menu-container");

        // top bar with cart button aligned right
        HorizontalLayout topBar = new HorizontalLayout();
        topBar.setWidthFull();
        topBar.setJustifyContentMode(JustifyContentMode.END);
        topBar.setAlignItems(Alignment.CENTER);

        Button cartToggle = new Button("🛒 Cart", click -> {
            boolean show = !cartPanel.isVisible();
            cartPanel.setVisible(show);
            getElement().getClassList().set("cart-open", show);
        });
        topBar.add(cartToggle);
        menuContainer.add(topBar);

        // content inside menu
        Div menuList = new Div();
        menuList.addClassName("menu-list");
        menuContainer.add(menuList);

        // cart side panel
        cartPanel = new Div();
        cartPanel.addClassName("cart-panel");
        cartPanel.setVisible(false);
        cartPanel.add(cartContents, cartTotal);

        add(menuContainer, cartPanel);
        expand(menuContainer);

        // load items
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
        Button addToCart = new Button("Add to Cart", e -> {
            cartItems.add(item);
            updateCart();
        });

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
