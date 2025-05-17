package edu.byteme.views.menu;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import edu.byteme.data.entities.MenuItem;
import edu.byteme.data.entities.Order;
import edu.byteme.data.repositories.MenuRepository;
import edu.byteme.views.MainLayout;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@PageTitle("Menu")
@Route(value = "", layout = MainLayout.class)
@PermitAll

public class MenuView extends HorizontalLayout {
    private CartComponent cartPanel = new CartComponent();
    private List<MenuItem> cartItems = new ArrayList<>();
    private List<Order> orderItems = new ArrayList<>();

    @Autowired
    public MenuView(MenuRepository menuRepository) {
        setSizeFull();
        addClassName("menu-view");

        // Menu container wraps everything
        Div menuContainer = new Div();
        menuContainer.addClassName("menu-container");

        // Top bar with cart button aligned right
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

        cartPanel.setOnCheckoutClicked(() -> {

            System.out.println("onCheckoutClicked");
            /*
            if (userIsLoggedIn()) {
                // Checkout-Prozess starten
                navigateToCheckout();
            } else {
                // Login-Dialog öffnen
                loginDialog.open();
            }*/
        });

        // Hand over orders and shopping items to cart to be displayed
        cartPanel.updateCart(cartItems);
        cartPanel.updateOrders(orderItems);

        // Callback function that removes item from cart items when button is pressed in cart component
        cartPanel.setOnRemoveMenuItem(itemToRemove -> {
            for (int i = cartItems.size() - 1; i >= 0; i--) {
                if (cartItems.get(i).getName().equals(itemToRemove.getName())) {
                    cartItems.remove(i);
                    break;
                }
            }
            cartPanel.updateCart(cartItems);
        });

        cartPanel.setOnAddMenuItem(item -> {
            cartItems.add(item);
            cartPanel.updateCart(cartItems);
        });

        // Loads items onto the menu
        List<MenuItem> menuItems = menuRepository.findByIsAvailableTrue();
        MenuListView orderView = new MenuListView(menuItems);
        menuContainer.add(orderView);
        orderView.setActionText("Add to cart");
        orderView.setMenuItemEvent(item -> {
            if (item!= null) {
                cartItems.add(item);
                cartPanel.updateCart(cartItems);
            }
        });

        add(menuContainer, cartPanel);
        expand(menuContainer);
    }
}
