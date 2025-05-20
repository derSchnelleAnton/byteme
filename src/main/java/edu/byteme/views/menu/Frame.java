package edu.byteme.views.menu;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import edu.byteme.data.entities.Client;
import edu.byteme.data.entities.MenuItem;
import edu.byteme.data.entities.Order;
import edu.byteme.data.repositories.ClientRepository;
import edu.byteme.data.repositories.MenuRepository;
import edu.byteme.services.OrderService;
import edu.byteme.util.Util;
import edu.byteme.views.MainLayout;
import edu.byteme.views.orders.OrderTimeLine;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@PageTitle("Menu")
@Route(value = "", layout = MainLayout.class)
@PermitAll
@AnonymousAllowed
public class Frame extends VerticalLayout {
    private final List<MenuItem> cartItems = new ArrayList<>(); // No reason why this is up here but don't want to break anything so it stays
    private final CartComponent cartPanel;
    private final LargeListComponent orderView;
    private final ClientRepository clientRepository;
    private final OrderService orderService;
    //private final OrderService orderService;
    private Page currentPage;
    private final MenuRepository menuRepository;
    private final Div footer;
    private final Component fab;


    @Autowired
    public Frame(MenuRepository menuRepository,
                 CartComponent cartPanel,
                 OrderService orderService,
                 ClientRepository clientRepository) {
        this.cartPanel = cartPanel;
        this.menuRepository = menuRepository;
        footer = new Div();
        footer.addClassName("footer");
        footer.setVisible(false);
        fab = addHomeRouter();
        add(fab);

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
                //switchToMenu();
                switchToPlaceOrder();
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
         * DONE
         */
        orderView = new LargeListComponent();
        switchToMenu();
        orderView.setMenuItemEvent(item -> {
            switch (currentPage){
                case MENU: {
                    cartItems.add(item);
                    cartPanel.displayCart(cartItems);
                    break;
                }case ORDERS:{
                    openDialog(item);
                }
                // place order doesnt have a button
            }
        });

        // but here we only need register as observers
        cartPanel.setOnOrderSelected(this::switchToOrders);

        /*
         * Below everything is put together
         */
        contentArea.add(orderView);
        add(contentLayout);
        expand(contentLayout);
        contentArea.add(footer);
        contentArea.expand(orderView);
        contentArea.setHorizontalComponentAlignment(Alignment.CENTER, footer);
        this.clientRepository = clientRepository;
        this.orderService = orderService;
    }

    private void switchToOrders(Order order) {
        orderView.setItems(order.getMenuItems());
        orderView.setActionText("More");
        currentPage = Page.ORDERS;
        if(!footer.isVisible()) {
            footer.setVisible(true);
        }
        footer.removeAll();
        OrderTimeLine timeline = new OrderTimeLine(order);
        footer.add(timeline);
        fab.setVisible(currentPage == Page.ORDERS);

    }

    private void switchToMenu(){
        List<MenuItem> menuItems = menuRepository.findByIsAvailableTrue();
        orderView.setItems(menuItems);
        orderView.setActionText("Add to cart");
        currentPage = Page.MENU;
        footer.removeAll();
        footer.setWidthFull();
        footer.setVisible(false);
        fab.setVisible(currentPage == Page.ORDERS);

    }

    private void switchToPlaceOrder(){
        orderView.setItems(cartItems);
        orderView.setActionText(null);
        currentPage = Page.CART;
        if(!footer.isVisible()) {
            footer.setVisible(true);
        }
        footer.removeAll();
        footer.add(createButtons());
        fab.setVisible(currentPage == Page.ORDERS);


    }


    private Component createButtons(){
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        buttonLayout.getStyle()
                .set("padding", "16px")
                .set("justify-content", "space-around");
        buttonLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        buttonLayout.setWidthFull();
        Button backButton = new Button("Back");
        backButton.addClickListener(event -> {
            switchToMenu();
        });
        Button orderButton = new Button("Order");
        orderButton.addClickListener(event -> {
            switchToMenu();
            // we get username from security context
            User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            // we use UserRepository to get Client
            Optional<Client> client = clientRepository.findByUserName(currentUser.getUsername());
            // if client is available we place order in clients name
            client.ifPresent(value -> orderService.placeOrder(cartItems, value));
            cartItems.clear();
            cartPanel.displayCart(cartItems);
            cartPanel.refreshOrders();
        });
        buttonLayout.add(backButton, orderButton);
        return buttonLayout;
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

    enum Page{
        MENU, ORDERS, CART
    }


    // to show dialog
    private void openDialog(MenuItem item) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(item.getName());

        Paragraph desc      = new Paragraph("Description : " + item.getDescription());
        Paragraph price     = new Paragraph("Price : " + item.getPrice());
        Paragraph available = new Paragraph("Still available : " + (item.isAvailable() ? "Yes" : "No"));
        Paragraph since     = new Paragraph("Since : "
                + item.getCreatedAt().format(DateTimeFormatter.ofPattern("dd MMMM, yyyy")));

        VerticalLayout dialogLayout = new VerticalLayout(desc, price, available, since);
        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "18rem").set("max-width", "100%");

        HorizontalLayout body = new HorizontalLayout();
        body.setPadding(false);
        Image img = new Image(Util.getPathFromName(item.getName()), "Menu image");
        body.add(img, dialogLayout);
        dialog.add(body);

        dialog.getFooter().add(new com.vaadin.flow.component.button.Button("Close", e -> dialog.close()));
        add(dialog);
        dialog.open();
    }

    // return button for OrdersView
    private Component addHomeRouter() {
        Button fab = new Button(LineAwesomeIcon.ARROW_LEFT_SOLID.create(), e -> {
            switchToMenu();
        });
        fab.addClassName("fab");
        return fab;
    }

}