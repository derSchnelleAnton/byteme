package edu.byteme.views.orders;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
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
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@PageTitle("Order")
@Route(value = "order", layout = MainLayout.class)
@AnonymousAllowed
public class OrderView extends HorizontalLayout {

    private final OrderService orderService;
    private Registration orderRegistration;
    private Order order;
    private MenuListView goodiesList;
    private OrderTimeLine timeLine;

    public OrderView(OrderService orderService) {
        this.orderService = orderService;
        setSizeFull();
        addClassName("order-view");
        buildContent();
        buildSideBar();
    }

    /* ───────────────────────── live push ───────────────────────── */

    @Override
    protected void onAttach(AttachEvent event) {
        orderRegistration = OrderBroadcaster.register(this::handleBroadcast);
    }

    @Override
    protected void onDetach(DetachEvent event) {
        if (orderRegistration != null) orderRegistration.remove();
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

    /* ───────────────────────── layout ───────────────────────── */

    private void buildSideBar() {
        Div side = new Div();
        side.addClassName("cart-panel");

        SideBar bar = new SideBar(orderService);
        bar.setOnOrderSelectedListener(selected -> {
            /* always fetch current state from DB to avoid stale status */
            Optional<Order> fresh = orderService.getOrderById(selected.getId());
            order = fresh.orElse(selected);

            goodiesList.setItems(order.getMenuItems());
            timeLine.setValues(order, orderService.getTotalCostOfOrder(order) + "€");
        });
        side.add(bar);
        add(side);
    }

    private void buildContent() {
        order = orderService.getAllOrders().stream().findFirst().orElse(null);
        goodiesList = new MenuListView(order.getMenuItems());
        goodiesList.setMenuItemEvent(this::openDialog);

        VerticalLayout wrapper = new VerticalLayout();
        wrapper.setHeightFull();
        timeLine = new OrderTimeLine(order, orderService.getTotalCostOfOrder(order) + "€");
        Div footer = new Div(timeLine);
        footer.setWidthFull();

        wrapper.add(goodiesList, footer);
        wrapper.expand(goodiesList);
        add(wrapper);
        expand(wrapper);
    }

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
        Image img = new Image("images/take-away.png", "Menu image");
        body.add(img, dialogLayout);
        dialog.add(body);

        dialog.getFooter().add(new com.vaadin.flow.component.button.Button("Close", e -> dialog.close()));
        add(dialog);
        dialog.open();
    }
}
