package edu.byteme.views.menu;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import edu.byteme.data.entities.MenuItem;
import edu.byteme.data.repositories.MenuRepository;
import edu.byteme.views.MainLayout;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;

@PageTitle("Menu")
@Route(value = "", layout = MainLayout.class)
@PermitAll
@AnonymousAllowed
public class Frame extends VerticalLayout {
    private final List<MenuItem> cartItems = new ArrayList<>(); // No reason why this is up here but don't want to break anything so it stays
    private final CartComponent cartPanel;

    @Autowired
    public Frame(MenuRepository menuRepository, CartComponent cartPanel) {
        this.cartPanel = cartPanel;

        setSizeFull();
        setPadding(false);
        setSpacing(false);

        // contentLayout makes content display on left hand side and cart on right hand side
        HorizontalLayout contentLayout = new HorizontalLayout();
        contentLayout.setSizeFull();
        contentLayout.setPadding(false);
        contentLayout.setSpacing(false);

        VerticalLayout contentArea = new VerticalLayout();
        contentArea.setSizeFull();
        contentArea.setPadding(true);
        contentArea.setSpacing(false);
        contentArea.setHeightFull();
        contentArea.getStyle().set("overflow", "auto");

        cartPanel.setHeightFull();

        contentLayout.add(contentArea, cartPanel);
        contentLayout.expand(contentArea);

        /*
         * Below are cart callback functions that are required for the add to cart functionality
         * and proceed to order button
         */
        cartPanel.displayCart(cartItems);

        cartPanel.setOnCheckoutClicked(() -> {
            if (isUserLoggedIn()) {
                System.out.println("CHECKOUT BUTTON PRESSED - FUNCTIONALITY MISSING");
            } else {
                UI.getCurrent().navigate("login");
            }
        });

        cartPanel.setOnRemoveMenuItem(itemToRemove -> {
            for (int i = cartItems.size() - 1; i >= 0; i--) {
                if (cartItems.get(i).getName().equals(itemToRemove.getName())) {
                    cartItems.remove(i);
                    break;
                }
            }
            cartPanel.displayCart(cartItems);
        });

        cartPanel.setOnAddMenuItem(item -> {
            cartItems.add(item);
            cartPanel.displayCart(cartItems);
        });

        /*
         * Below is code for the actual content (left hand side) of the screen
         * @TINSAE
         */
        List<MenuItem> menuItems = menuRepository.findByIsAvailableTrue();
        MenuListView orderView = new MenuListView(menuItems);
        orderView.setActionText("Add to cart");
        orderView.setMenuItemEvent(item -> {
            if (item != null) {
                cartItems.add(item);
                cartPanel.displayCart(cartItems);
            }
        });


        /*
         * Below everything is put together
         */
        contentArea.add(orderView);
        add(contentLayout);
        expand(contentLayout);
    }

    /**
     *
     * @return Boolean whether a user is currently logged in or not
     */
    private boolean isUserLoggedIn() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }
}