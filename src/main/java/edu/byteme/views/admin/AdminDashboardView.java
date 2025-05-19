package edu.byteme.views.admin;

import com.vaadin.flow.component.UI;
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
import com.vaadin.flow.server.Command;
import com.vaadin.flow.shared.Registration;
import edu.byteme.data.entities.MenuItem;
import edu.byteme.data.entities.Order;
import edu.byteme.data.entities.OrderStatus;
import edu.byteme.events.OrderBroadcaster;
import edu.byteme.services.MenuService;
import edu.byteme.services.OrderService;
import edu.byteme.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@PageTitle("Admin")
@Route(value = "admin", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@CssImport("./themes/my-app/menu-view.css")
public class AdminDashboardView extends VerticalLayout {

    private final MenuService menuService;
    private final OrderService orderService;
    private final VerticalLayout central = new VerticalLayout();
    private Registration orderSub;
    private Registration reportSub;

    public AdminDashboardView(MenuService menuService, OrderService orderService) {
        this.menuService = menuService;
        this.orderService = orderService;
        setSizeFull();
        addClassName("menu-view");
        buildTabs();
        central.setSizeFull();
        add(central);
        showMenu();
    }

    /* ───────────────── tabs ───────────────── */

    private void buildTabs() {
        Tabs tabs = new Tabs();
        Tab menuTab = new Tab("Menu");
        Tab ordersTab = new Tab("Orders");
        Tab reportsTab = new Tab("Reports");
        tabs.add(menuTab, ordersTab, reportsTab);
        tabs.setSelectedTab(menuTab);
        tabs.addSelectedChangeListener(e -> {
            central.removeAll();
            clearSubs();
            if (e.getSelectedTab() == menuTab) showMenu();
            if (e.getSelectedTab() == ordersTab) showOrders();
            if (e.getSelectedTab() == reportsTab) showReports();
        });
        add(tabs);
    }

    private void clearSubs() {
        if (orderSub != null) orderSub.remove();
        if (reportSub != null) reportSub.remove();
    }

    /* ───────────────── MENU ───────────────── */

    private void showMenu() {
        central.removeAll();
        central.add(header("Admin Dashboard — Manage Menu"));
        menuService.getAllItems().forEach(mi -> central.add(menuRow(mi)));
        buildAddItemBar();
    }

    private H2 header(String txt) {
        H2 h = new H2(txt);
        h.getStyle().set("margin", "0 1rem");
        return h;
    }

    private HorizontalLayout menuRow(MenuItem item) {
        HorizontalLayout row = new HorizontalLayout();
        row.addClassName("menu-item");
        row.setSpacing(true);
        row.setWidthFull();
        row.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        Image icon = new Image("images/take-away.png", "menu image");
        icon.setWidth("60px");
        icon.setHeight("60px");
        row.add(icon);

        VerticalLayout text = new VerticalLayout();
        text.setSpacing(false);
        text.setPadding(false);
        text.setMargin(false);
        text.add(new H2(item.getName()), new Paragraph(item.getDescription()));
        row.add(text);

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
        Dialog d = new Dialog();
        d.setHeaderTitle("Edit Menu Item");
        d.setWidth("480px");

        TextField n = new TextField("Name", item.getName());
        TextField desc = new TextField("Description", item.getDescription());
        NumberField p = new NumberField("Price");
        p.setValue(item.getPrice());
        NumberField disc = new NumberField("Discount");
        disc.setValue(item.getDiscount());
        Checkbox avail = new Checkbox("Available", item.isAvailable());

        Button save = new Button("Save", e -> {
            item.setName(n.getValue());
            item.setDescription(desc.getValue());
            item.setPrice(p.getValue());
            item.setDiscount(disc.getValue() == null ? 0.0 : disc.getValue());
            item.setAvailable(avail.getValue());
            menuService.saveItem(item);
            d.close();
            getUI().ifPresent(ui -> ui.getPage().reload());
        });
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancel = new Button("Cancel", e -> d.close());

        VerticalLayout ct = new VerticalLayout(n, desc, p, disc, avail, new HorizontalLayout(save, cancel));
        d.add(ct);
        d.open();
    }

    /* ───────────────── ORDERS ───────────────── */

    private void showOrders() {
        central.removeAll();
        central.add(header("Incoming Orders"));

        Grid<Order> grid = new Grid<>(Order.class, false);
        grid.setDetailsVisibleOnClick(false);
        grid.addColumn(Order::getId).setHeader("ID").setAutoWidth(true);
        grid.addColumn(o -> o.getClient().getUserName()).setHeader("Client");
        grid.addComponentColumn(o -> statusBox(o, grid)).setHeader("Status").setAutoWidth(true);
        grid.addColumn(o -> o.getMenuItems().size()).setHeader("# Items").setAutoWidth(true);
        grid.addColumn(o -> orderService.getTotalCostOfOrder(o)).setHeader("Total (€)").setAutoWidth(true);
        grid.setItemDetailsRenderer(new ComponentRenderer<>(this::detailsRenderer));
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.setSizeFull();

        central.add(grid);
        central.expand(grid);
        refreshOrders(grid);
        subscribeOrders(grid);
    }

    private ComboBox<OrderStatus> statusBox(Order o, Grid<Order> grid) {
        ComboBox<OrderStatus> cb = new ComboBox<>();
        cb.setItems(OrderStatus.values());
        cb.setValue(o.getStatus());
        cb.setWidthFull();
        cb.addValueChangeListener(ev -> {
            if (!ev.isFromClient() || ev.getValue() == null || ev.getValue() == o.getStatus()) return;
            Order updated = orderService.updateStatus(o.getId(), ev.getValue());
            o.setStatus(updated.getStatus());
            UI.getCurrent().access(() -> {
                grid.getDataProvider().refreshItem(o);
                grid.setDetailsVisible(o, true);
            });
        });
        return cb;
    }

    private VerticalLayout detailsRenderer(Order o) {
        VerticalLayout v = new VerticalLayout();
        o.getMenuItems().forEach(mi -> v.add(new Paragraph(mi.getName() + " — " + mi.getPrice() + "€")));
        v.add(new Paragraph("Total: " + orderService.getTotalCostOfOrder(o) + "€"));
        v.add(new Paragraph("Status: " + o.getStatus()));
        v.add(new Paragraph("Ordered: " + o.getOrderDate().toLocalDate()));
        return v;
    }

    private void refreshOrders(Grid<Order> g) {
        List<Order> list = orderService.getAllOrders();
        g.setItems(list);
        list.forEach(o -> g.setDetailsVisible(o, true));
    }

    private void subscribeOrders(Grid<Order> g) {
        orderSub = OrderBroadcaster.register(o ->
                UI.getCurrent().access(() -> refreshOrders(g)));
        central.addDetachListener(e -> { if (orderSub != null) orderSub.remove(); });
    }

    /* ───────────────── REPORTS ───────────────── */

    private void showReports() {
        central.removeAll();
        central.add(header("Reports"));

        ComboBox<String> period = new ComboBox<>();
        period.setItems("Daily", "Weekly", "Monthly", "Yearly");
        period.setValue("Monthly");

        Paragraph revenueP = new Paragraph();
        revenueP.getStyle().set("font-weight", "600");

        Grid<BestRow> grid = new Grid<>(BestRow.class, false);
        grid.addColumn(BestRow::name).setHeader("Item");
        grid.addColumn(BestRow::qty).setHeader("Sold");
        grid.addColumn(BestRow::income).setHeader("Revenue (€)");
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.setAllRowsVisible(true);
        grid.setWidthFull();          // use full width

        VerticalLayout block = new VerticalLayout(period, revenueP, grid);
        block.setPadding(true);
        block.setSpacing(true);
        block.setSizeFull();
        central.add(block);
        central.expand(block);

        Runnable reload = () -> refreshReports(period.getValue(), revenueP, grid);
        period.addValueChangeListener(e -> reload.run());
        reload.run();

        reportSub = OrderBroadcaster.register(o -> UI.getCurrent().access((Command) reload));
        central.addDetachListener(e -> { if (reportSub != null) reportSub.remove(); });
    }

    private record BestRow(String name, long qty, double income) {}

    private void refreshReports(String period, Paragraph rev, Grid<BestRow> grid) {
        List<Order> all = orderService.getAllOrders();
        LocalDateTime now = LocalDateTime.now();

        List<Order> filtered = switch (period) {
            case "Daily" -> all.stream().filter(o -> o.getOrderDate().toLocalDate().equals(LocalDate.now())).toList();
            case "Weekly" -> all.stream().filter(o -> o.getOrderDate().isAfter(now.minusDays(7))).toList();
            case "Monthly" -> all.stream().filter(o -> o.getOrderDate().isAfter(now.minusDays(30))).toList();
            case "Yearly" -> all.stream().filter(o -> o.getOrderDate().isAfter(now.minusDays(365))).toList();
            default -> all;
        };

        double revenue = filtered.stream().mapToDouble(OrderService::getTotalCostOfOrder).sum();
        rev.setText("Revenue (" + period + "): " + String.format("%.2f", revenue) + " €");

        Map<MenuItem, Long> counts = new HashMap<>();
        filtered.forEach(o -> o.getMenuItems().forEach(mi -> counts.merge(mi, 1L, Long::sum)));

        List<BestRow> rows = counts.entrySet().stream()
                .map(e -> new BestRow(e.getKey().getName(), e.getValue(), e.getKey().getPrice() * e.getValue()))
                .sorted(Comparator.comparingLong(BestRow::qty).reversed())
                .limit(10)
                .toList();

        grid.setItems(rows);
    }
}
