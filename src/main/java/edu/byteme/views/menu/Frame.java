package edu.byteme.views.menu;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import edu.byteme.data.entities.MenuItem;
import edu.byteme.data.entities.Order;
import edu.byteme.data.repositories.MenuRepository;
import edu.byteme.data.repositories.OrderRepository;
import edu.byteme.views.MainLayout;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

 @PageTitle("Menu")
 @Route(value = "", layout = MainLayout.class)
 @PermitAll
 public class Frame extends VerticalLayout {

     private final CartComponent cartPanel = new CartComponent();
     private final List<MenuItem> cartItems = new ArrayList<>();
     private List<Order> orderItems = new ArrayList<>();

     @Autowired
     public Frame(MenuRepository menuRepository, OrderRepository orderRepository) {
         setSizeFull();
         setPadding(false);
         setSpacing(false);

         // contentLayout makes it possible for cart and content area to be next to each other
         HorizontalLayout contentLayout = new HorizontalLayout();
         contentLayout.setSizeFull();
         contentLayout.setPadding(false);
         contentLayout.setSpacing(false);

         // Content area contains the different components (menu, orders, etc)
         VerticalLayout contentArea = new VerticalLayout();
         contentArea.setSizeFull();
         contentArea.setPadding(true);
         contentArea.setSpacing(false);
         contentArea.setHeightFull();
         contentArea.getStyle().set("overflow", "auto"); // wichtig, wenn Inhalte überlaufen

         cartPanel.setHeightFull();

         contentLayout.add(contentArea, cartPanel);
         contentLayout.expand(contentArea); // Expand content area because it should receive all the space

         // Cart panel contents
         orderItems = orderRepository.findAll();
         cartPanel.displayCart(cartItems);
         cartPanel.displayOrders(orderItems);

         cartPanel.setOnCheckoutClicked(() -> {
             System.out.println("onCheckoutClicked");
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
          Start  This here is to be changed so that content area contains different components
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

         contentArea.add(orderView);

         /*
           End
          */

         add(contentLayout);
         expand(contentLayout);
     }
 }