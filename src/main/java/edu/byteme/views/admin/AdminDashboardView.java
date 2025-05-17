/* ──────────────────────────────────────────────────────────────
   File: src/main/java/edu/byteme/views/admin/AdminDashboardView.java
   ────────────────────────────────────────────────────────────── */
package edu.byteme.views.admin;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.ItemClickEvent;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.combobox.ComboBox;

import edu.byteme.data.entities.MenuItem;
import edu.byteme.data.entities.Order;
import edu.byteme.data.entities.OrderStatus;
import edu.byteme.events.OrderBroadcaster;
import edu.byteme.services.MenuService;
import edu.byteme.services.OrderService;
import edu.byteme.views.MainLayout;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@PageTitle("Admin")
@Route(value = "admin", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@CssImport("./themes/my-app/menu-view.css")

public class AdminDashboardView extends VerticalLayout {

    private final MenuService menuService;
    private final OrderService orderService;

    private final VerticalLayout central = new VerticalLayout();   // switched by tabs
    private Registration orderSubscription;                        // live push

    /* ────────────────────────────────────────────────────────── */

    @Autowired
    public AdminDashboardView(MenuService menuService, OrderService orderService) {
        this.menuService = menuService;
        this.orderService = orderService;

        setSizeFull();
        addClassName("menu-view");

        buildTabs();
        central.setSizeFull();
        add(central);
        showMenu();                        // default
    }

    /* ─────────────────────── Tabs ───────────────────────────── */

    private void buildTabs() {
        Tabs tabs = new Tabs();
        Tab menuTab    = new Tab("Menu");
        Tab ordersTab  = new Tab("Orders");
        Tab reportsTab = new Tab("Reports");   // placeholder

        tabs.add(menuTab, ordersTab, reportsTab);
        tabs.setSelectedTab(menuTab);

        tabs.addSelectedChangeListener(e -> {
            central.removeAll();
            if (e.getSelectedTab() == menuTab)   showMenu();
            if (e.getSelectedTab() == ordersTab) showOrders();
            // reportsTab left empty for now
        });
        add(tabs);
    }

    /* ──────────────── MENU RENDERING ───────────────────────── */

    private void showMenu() {
        central.removeAll();
        central.add(buildHeader("Admin Dashboard — Manage Menu"));
        renderMenuList();
        buildAddItemBar();
    }

    private H2 buildHeader(String text) {
        H2 title = new H2(text);
        title.getStyle().set("margin", "0 1rem");
        return title;
    }

    private void renderMenuList() {
        List<MenuItem> items = menuService.getAllItems();
        for (MenuItem item : items) {
            central.add(createMenuRow(item));
        }
    }

    private HorizontalLayout createMenuRow(MenuItem item) {
        HorizontalLayout row = new HorizontalLayout();
        row.addClassName("menu-item");
        row.setSpacing(true);
        row.setWidthFull();
        row.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        Image icon = new Image("icons/icon.png", "Menu item image");
        icon.setWidth(60, Unit.PIXELS);
        icon.setHeight(60, Unit.PIXELS);
        row.add(icon);

        VerticalLayout text = new VerticalLayout();
        text.setSpacing(false);
        text.setPadding(false);
        text.setMargin(false);
        text.add(new H2(item.getName()), new Paragraph(item.getDescription()));
        row.add(text);

        row.add(new Paragraph(item.getPrice() + "$"));

        Button edit = new Button("Edit", e -> showEditDialog(item));
        edit.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button delete = new Button("Delete", e -> {
            menuService.deleteItem(item.getId());
            getUI().ifPresent(ui -> ui.getPage().reload());
        });
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);

        row.add(edit, delete);
        return row;
    }

    private void buildAddItemBar() {
        TextField name = new TextField();
        name.setPlaceholder("Name");

        TextField description = new TextField();
        description.setPlaceholder("Description");

        NumberField price = new NumberField();
        price.setPlaceholder("Price");

        NumberField discount = new NumberField();
        discount.setPlaceholder("Discount");

        Button add = new Button("➕", e -> {
            if (name.isEmpty() || description.isEmpty() || price.isEmpty()) return;

            MenuItem mi = new MenuItem();
            mi.setName(name.getValue());
            mi.setDescription(description.getValue());
            mi.setPrice(price.getValue());
            mi.setDiscount(discount.getValue() != null ? discount.getValue() : 0.0);
            mi.setAvailable(true);

            menuService.saveItem(mi);
            getUI().ifPresent(ui -> ui.getPage().reload());
        });

        HorizontalLayout bar = new HorizontalLayout(name, description, price, discount, add);
        bar.setWidthFull();
        bar.setAlignItems(FlexComponent.Alignment.BASELINE);
        bar.getStyle().set("margin", "1rem");
        central.add(bar);
    }

    /* ─────────────── ORDERS RENDERING ──────────────────────── */

    private void showOrders() {
        central.removeAll();
        central.add(buildHeader("Incoming Orders"));

        Grid<Order> grid = new Grid<>(Order.class, false);
        grid.setDetailsVisibleOnClick(false);

        grid.addColumn(Order::getId).setHeader("ID").setAutoWidth(true);
        grid.addColumn(o -> o.getClient().getUserName()).setHeader("Client");

        grid.addComponentColumn(o -> {
            ComboBox<OrderStatus> cb = new ComboBox<>();
            cb.setItems(OrderStatus.values());
            cb.setValue(o.getStatus());
            cb.setWidthFull();
            cb.addValueChangeListener(e -> {
                if (e.isFromClient() && e.getValue() != null && e.getValue() != o.getStatus()) {
                    orderService.setStatus(o.getId(), e.getValue());
                    o.setStatus(e.getValue());
                    grid.getDataProvider().refreshItem(o);
                    grid.setDetailsVisible(o, true);
                }
            });
            return cb;
        }).setHeader("Status").setAutoWidth(true);

        grid.addColumn(o -> o.getMenuItems().size()).setHeader("# Items").setAutoWidth(true);
        grid.addColumn(o -> orderService.getTotalCostOfOrder(o)).setHeader("Total ($)").setAutoWidth(true);

        grid.setItemDetailsRenderer(new ComponentRenderer<>(this::orderDetailRenderer));
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.setSizeFull();

        central.add(grid);
        central.expand(grid);

        refreshOrders(grid);
        subscribe(grid);
    }


    private VerticalLayout orderDetailRenderer(Order o) {
        VerticalLayout box = new VerticalLayout();
        o.getMenuItems().forEach(mi ->
                box.add(new Paragraph(mi.getName() + " — " + mi.getPrice() + "$")));
        box.add(new Paragraph("Total: " + orderService.getTotalCostOfOrder(o) + "$"));
        return box;
    }




    /* ─── live updates ── */
    private void subscribe(Grid<Order> grid) {
        if (orderSubscription != null) orderSubscription.remove();
        orderSubscription = OrderBroadcaster.register(o ->
                UI.getCurrent().access(() -> refreshOrders(grid)));
        central.addDetachListener(e -> {
            if (orderSubscription != null) orderSubscription.remove();
        });
    }

    private void refreshOrders(Grid<Order> grid) {
        List<Order> orders = orderService.getAllOrders();
        grid.setItems(orders);
        orders.forEach(o -> grid.setDetailsVisible(o, true));
    }


    /* ─────────────── EDIT DIALOG (unchanged) ───────────────── */

    private void showEditDialog(MenuItem item) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Edit Menu Item");
        dialog.setWidth("600px");
        dialog.getElement().getStyle().set("overflow", "hidden");

        TextField name = new TextField("Name", item.getName());
        name.setWidthFull();

        TextField description = new TextField("Description", item.getDescription());
        description.setWidthFull();

        NumberField price = new NumberField("Price");
        price.setValue(item.getPrice());
        price.setWidthFull();

        NumberField discount = new NumberField("Discount");
        discount.setValue(item.getDiscount());
        discount.setWidthFull();

        Checkbox available = new Checkbox("Available", item.isAvailable());

        Button save = new Button("Save", ev -> {
            item.setName(name.getValue());
            item.setDescription(description.getValue());
            item.setPrice(price.getValue());
            item.setDiscount(discount.getValue());
            item.setAvailable(available.getValue());
            menuService.saveItem(item);
            dialog.close();
            getUI().ifPresent(ui -> ui.getPage().reload());
        });

        Button cancel = new Button("Cancel", ev -> dialog.close());

        HorizontalLayout buttons = new HorizontalLayout(save, cancel);
        buttons.setSpacing(true);
        buttons.setPadding(true);

        VerticalLayout content = new VerticalLayout(name, description, price,
                discount, available, buttons);
        content.setWidthFull();
        content.getStyle().set("padding", "1rem");
        content.getStyle().set("overflow", "hidden");

        dialog.add(content);
        dialog.open();
    }
}
