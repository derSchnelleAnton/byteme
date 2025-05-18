/**
 * wa7505@mci4me.at
 * Missing as of 2025 5 17
 *  - What happens at checkout, callback checkout button pressed
 */

package edu.byteme.views.menu;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import edu.byteme.data.entities.MenuItem;
import edu.byteme.data.entities.Order;
import edu.byteme.data.repositories.MenuRepository;
import edu.byteme.data.repositories.OrderRepository;
import edu.byteme.views.MainLayout;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * @deprecated
 */
@PageTitle("Menu")
@Route(value = "Hidden", layout = MainLayout.class)
@PermitAll
@AnonymousAllowed
public class MenuView extends HorizontalLayout {
    private CartComponent cartPanel = new CartComponent();
    private List<MenuItem> cartItems = new ArrayList<>();
    private List<Order> orderItems = new ArrayList<>();

    @Autowired
    public MenuView(MenuRepository menuRepository, OrderRepository orderRepository) {
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

        orderItems = orderRepository.findAll();

        // Hand over orders and shopping items to cart to be displayed
        cartPanel.displayCart(cartItems);
        cartPanel.displayOrders(orderItems);

        // Callback function that is called when the order-button is pressed
        cartPanel.setOnCheckoutClicked(() -> {
            System.out.println("onCheckoutClicked"); // CALLBACK NEREDS TO BE ADDED
        });

        // Callback function to decrease item count in cart
        cartPanel.setOnRemoveMenuItem(itemToRemove -> {
            for (int i = cartItems.size() - 1; i >= 0; i--) {
                if (cartItems.get(i).getName().equals(itemToRemove.getName())) {
                    cartItems.remove(i);
                    break;
                }
            }
            cartPanel.displayCart(cartItems);
        });

        // Callback function to increase item count in cart
        cartPanel.setOnAddMenuItem(item -> {
            cartItems.add(item);
            cartPanel.displayCart(cartItems);
        });




        // Loads items onto the menu
        List<MenuItem> menuItems = menuRepository.findByIsAvailableTrue();
        MenuListView orderView = new MenuListView(menuItems);
        menuContainer.add(orderView);
        orderView.setActionText("Add to cart");
        orderView.setMenuItemEvent(item -> {
            if (item != null) {
                if (isUserLoggedIn()) { // Login-Status prüfen
                    // Benutzer ist eingeloggt -> Artikel hinzufügen
                    cartItems.add(item);
                    cartPanel.displayCart(cartItems);
                    System.out.println("Item added to cart: " + item.getName());
                } else {
                    // Benutzer ist nicht eingeloggt -> Weiterleitung zur Login-Seite
                    System.out.println("User not logged in ...");
                    UI.getCurrent().navigate("login"); // Weiterleiten zur Login-Route
                }
            }
        });

        add(menuContainer, cartPanel);
        expand(menuContainer);
    }

    private boolean isUserLoggedIn() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() &&
                !(authentication instanceof AnonymousAuthenticationToken);
    }
}
