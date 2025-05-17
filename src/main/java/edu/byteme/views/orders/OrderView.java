/* ──────────────────────────────────────────────────────────────
   File: src/main/java/edu/byteme/views/orders/OrderView.java
   ────────────────────────────────────────────────────────────── */
package edu.byteme.views.orders;

import java.time.format.DateTimeFormatter;
import java.util.List;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
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
import com.vaadin.flow.shared.Registration;

import edu.byteme.data.entities.MenuItem;
import edu.byteme.data.entities.Order;
import edu.byteme.events.OrderBroadcaster;
import edu.byteme.services.OrderService;
import edu.byteme.views.MainLayout;
import edu.byteme.views.menu.MenuListView;
import edu.byteme.views.side_bar.SideBar;

@PageTitle("Order")
@Route(value = "order", layout = MainLayout.class)
@AnonymousAllowed
public class OrderView extends HorizontalLayout {

    private Registration orderRegistration;

    private final OrderService orderService;
    private Order order;
    private MenuListView goodiesList;
    private OrderTimeLine timeLine;

    public OrderView(OrderService orderService) {
        this.orderService = orderService;
        setSizeFull();
        addClassName("order-view");
        drawViews();
        addSidePanel();
    }

    /* ─── live sync ── */

    @Override
    protected void onAttach(AttachEvent event) {
        super.onAttach(event);
        orderRegistration = OrderBroadcaster.register(this::handleBroadcast);
    }

    @Override
    protected void onDetach(DetachEvent event) {
        if (orderRegistration != null) orderRegistration.remove();
        super.onDetach(event);
    }

    private void handleBroadcast(Order updated) {
        if (!updated.getId().equals(order.getId())) return;
        UI ui = getUI().orElse(null);
        if (ui == null) return;
        ui.access(() -> {
            order = updated;
            goodiesList.setItems(order.getMenuItems());
            timeLine.setValues(order, orderService.getTotalCostOfOrder(order) + "€");
        });
    }

    /* ─── layout helpers ── */

    private void addSidePanel() {
        Div cartPanel = new Div();
        cartPanel.addClassName("cart-panel");

        SideBar bar = new SideBar(orderService);
        bar.setOnOrderSelectedListener(e -> {
            goodiesList.setItems(e.getMenuItems());
            timeLine.setValues(e, orderService.getTotalCostOfOrder(e) + "€");
        });
        cartPanel.add(bar);
        add(cartPanel);
    }

    private void drawViews() {
        if (order == null || order.getMenuItems().isEmpty()) {
            order = orderService.getOrdersByClientId(5).get(0);
        }
        List<MenuItem> goodies = order.getMenuItems();
        goodiesList = new MenuListView(goodies);

        goodiesList.setMenuItemEvent(goodie -> {
            Dialog dialog = new Dialog();
            configureDialog(dialog, goodie);
            add(dialog);
            dialog.open();
        });

        VerticalLayout wrapper = new VerticalLayout();
        wrapper.setHeight("100%");

        timeLine = new OrderTimeLine(order, orderService.getTotalCostOfOrder(order) + "€");
        Div footer = new Div(timeLine);
        footer.setWidthFull();

        wrapper.add(goodiesList, footer);
        add(wrapper);
    }

    private void configureDialog(Dialog dialog, MenuItem item) {
        dialog.setHeaderTitle(item.getName());
        Paragraph desc      = new Paragraph("Description : " + item.getDescription());
        Paragraph price     = new Paragraph("Price : " + item.getPrice());
        Paragraph available = new Paragraph("Still available : " + (item.isAvailable() ? "Yes" : "No"));
        Paragraph since     = new Paragraph("Since : " + item.getCreatedAt().format(DateTimeFormatter.ofPattern("dd MMMM, yyyy")));
        VerticalLayout dialogLayout = new VerticalLayout(desc, price, available, since);
        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "18rem").set("max-width", "100%");

        HorizontalLayout bodyLayout = new HorizontalLayout();
        bodyLayout.setPadding(false);
        Image menuImage = new Image("images/take-away.png", "Menu image");
        bodyLayout.add(menuImage, dialogLayout);
        dialog.add(bodyLayout);

        Button close = new Button("Close", ev -> dialog.close());
        dialog.getFooter().add(close);
    }
}
