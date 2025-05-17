package edu.byteme.views.admin;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import edu.byteme.data.entities.MenuItem;
import edu.byteme.data.entities.Order;
import edu.byteme.data.entities.OrderStatus;
import edu.byteme.events.OrderBroadcaster;
import edu.byteme.services.MenuService;
import edu.byteme.services.OrderService;
import edu.byteme.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;
import java.time.format.DateTimeFormatter;
import java.util.List;

@PageTitle("Admin")
@Route(value = "admin", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@CssImport("./themes/my-app/menu-view.css")
public class AdminDashboardView extends VerticalLayout {

    /* ───────────────────────── fields ───────────────────────── */

    private final MenuService menuService;
    private final OrderService orderService;
    private final VerticalLayout central = new VerticalLayout();
    private Registration orderSub;

    /* ───────────────────────── ctor ───────────────────────── */

    public AdminDashboardView(MenuService menuService, OrderService orderService) {
        this.menuService  = menuService;
        this.orderService = orderService;

        setSizeFull();
        addClassName("menu-view");

        buildTabs();
        central.setSizeFull();
        add(central);
        showMenu();
    }

    /* ───────────────────────── tabs ───────────────────────── */

    private void buildTabs() {
        Tabs tabs = new Tabs();
        Tab menuTab   = new Tab("Menu");
        Tab ordersTab = new Tab("Orders");
        tabs.add(menuTab, ordersTab);
        tabs.setSelectedTab(menuTab);
        tabs.addSelectedChangeListener(e -> {
            central.removeAll();
            if (e.getSelectedTab() == menuTab)   showMenu();
            if (e.getSelectedTab() == ordersTab) showOrders();
        });
        add(tabs);
    }

    /* ───────────────────────── MENU ───────────────────────── */

    private void showMenu() {
        central.removeAll();
        central.add(header("Admin Dashboard — Manage Menu"));
        renderMenuList();
        buildAddItemBar();
    }

    private H2 header(String txt) {
        H2 h = new H2(txt);
        h.getStyle().set("margin", "0 1rem");
        return h;
    }

    private void renderMenuList() {
        menuService.getAllItems().forEach(mi -> central.add(menuRow(mi)));
    }

    private HorizontalLayout menuRow(MenuItem item) {
        HorizontalLayout row = new HorizontalLayout();
        row.addClassName("menu-item");
        row.setSpacing(true);
        row.setWidthFull();
        row.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        Image icon = new Image("images/take-away.png", "Menu item image");
        icon.setWidth(60, Unit.PIXELS);
        icon.setHeight(60, Unit.PIXELS);
        row.add(icon);

        VerticalLayout desc = new VerticalLayout();
        desc.setSpacing(false);
        desc.setPadding(false);
        desc.setMargin(false);
        desc.add(new H2(item.getName()), new Paragraph(item.getDescription()));
        row.add(desc);

        row.add(new Paragraph(item.getPrice() + "€"));

        Button edit = new Button("Edit", e -> showEditDialog(item));
        edit.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button del = new Button("Delete", e -> {
            menuService.deleteItem(item.getId());
            getUI().ifPresent(ui -> ui.getPage().reload());
        });
        del.addThemeVariants(ButtonVariant.LUMO_ERROR);

        row.add(edit, del);
        return row;
    }

    private void buildAddItemBar() {
        TextField name = new TextField();
        name.setPlaceholder("Name");
        name.setWidth("12rem");

        TextField description = new TextField();
        description.setPlaceholder("Description");
        description.setWidth("20rem");

        NumberField price = new NumberField();
        price.setPlaceholder("Price");
        price.setWidth("8rem");

        NumberField discount = new NumberField();
        discount.setPlaceholder("Discount");
        discount.setWidth("8rem");

        Button add = new Button("Add");
        add.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        add.addClickListener(e -> {
            if (name.isEmpty() || description.isEmpty() || price.isEmpty()) return;
            MenuItem mi = new MenuItem();
            mi.setName(name.getValue());
            mi.setDescription(description.getValue());
            mi.setPrice(price.getValue());
            mi.setDiscount(discount.isEmpty() ? 0.0 : discount.getValue());
            mi.setAvailable(true);
            menuService.saveItem(mi);
            getUI().ifPresent(ui -> ui.getPage().reload());
        });

        HorizontalLayout bar = new HorizontalLayout(name, description, price, discount, add);
        bar.setAlignItems(FlexComponent.Alignment.BASELINE);
        bar.setSpacing(true);
        bar.setPadding(true);
        central.add(bar);
    }

    private void showEditDialog(MenuItem item) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Edit Menu Item");
        dialog.setWidth("480px");

        TextField name        = new TextField("Name",        item.getName());
        TextField description = new TextField("Description", item.getDescription());
        NumberField price     = new NumberField("Price");
        price.setValue(item.getPrice());
        NumberField discount  = new NumberField("Discount");
        discount.setValue(item.getDiscount());
        Checkbox available    = new Checkbox("Available", item.isAvailable());

        Button save = new Button("Save", ev -> {
            item.setName(name.getValue());
            item.setDescription(description.getValue());
            item.setPrice(price.getValue());
            item.setDiscount(discount.getValue() == null ? 0.0 : discount.getValue());
            item.setAvailable(available.getValue());
            menuService.saveItem(item);
            dialog.close();
            getUI().ifPresent(ui -> ui.getPage().reload());
        });
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancel = new Button("Cancel", ev -> dialog.close());
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        VerticalLayout content = new VerticalLayout(
                name, description, price, discount, available,
                new HorizontalLayout(save, cancel)
        );
        dialog.add(content);
        dialog.open();
    }

    /* ───────────────────────── ORDERS ───────────────────────── */

    private void showOrders() {
        central.removeAll();
        central.add(header("Incoming Orders"));

        Grid<Order> grid = new Grid<>(Order.class, false);
        grid.setDetailsVisibleOnClick(false);                         // fixed expanded
        grid.addColumn(Order::getId)                                  .setHeader("ID").setAutoWidth(true);
        grid.addColumn(o -> o.getClient().getUserName())              .setHeader("Client");
        grid.addComponentColumn(o -> statusBox(o, grid))              .setHeader("Status").setAutoWidth(true);
        grid.addColumn(o -> o.getMenuItems().size())                  .setHeader("# Items").setAutoWidth(true);
        grid.addColumn(o -> orderService.getTotalCostOfOrder(o))      .setHeader("Total (€)").setAutoWidth(true);
        grid.setItemDetailsRenderer(new ComponentRenderer<>(this::detailsRenderer));
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.setSizeFull();

        central.add(grid);
        central.expand(grid);

        refreshOrders(grid);
        subscribe(grid);
    }

    private ComboBox<OrderStatus> statusBox(Order o, Grid<Order> grid) {
        ComboBox<OrderStatus> cb = new ComboBox<>();
        cb.setItems(OrderStatus.values());
        cb.setValue(o.getStatus());
        cb.setWidthFull();
        cb.addValueChangeListener(ev -> {
            if (!ev.isFromClient()) return;
            if (ev.getValue() == null || ev.getValue() == o.getStatus()) return;
            Order updated = orderService.updateStatus(o.getId(), ev.getValue()); // broadcast inside
            o.setStatus(updated.getStatus());
            UI.getCurrent().access(() -> {
                grid.getDataProvider().refreshItem(o);
                grid.setDetailsVisible(o, true);
            });
        });
        return cb;
    }

    private VerticalLayout detailsRenderer(Order o) {
        VerticalLayout box = new VerticalLayout();
        o.getMenuItems().forEach(mi ->
                box.add(new Paragraph(mi.getName() + " — " + mi.getPrice() + "€")));
        box.add(new Paragraph("Total: " + orderService.getTotalCostOfOrder(o) + "€"));
        box.add(new Paragraph("Status: " + o.getStatus()));
        box.add(new Paragraph("Ordered: " +
                o.getOrderDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm"))));
        return box;
    }

    private void refreshOrders(Grid<Order> g) {
        List<Order> orders = orderService.getAllOrders();
        g.setItems(orders);
        orders.forEach(o -> g.setDetailsVisible(o, true));
    }

    private void subscribe(Grid<Order> g) {
        if (orderSub != null) orderSub.remove();
        orderSub = OrderBroadcaster.register(o ->
                UI.getCurrent().access(() -> refreshOrders(g)));
        central.addDetachListener(e -> {
            if (orderSub != null) orderSub.remove();
        });
    }
}
