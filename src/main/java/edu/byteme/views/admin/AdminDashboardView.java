package edu.byteme.views.admin;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import edu.byteme.data.entities.MenuItem;
import edu.byteme.services.MenuService;
import edu.byteme.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@PageTitle("Admin")
@Route(value = "admin", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@CssImport("./themes/my-app/menu-view.css")
public class AdminDashboardView extends VerticalLayout {

    private final MenuService menuService;

    @Autowired
    public AdminDashboardView(MenuService menuService) {
        this.menuService = menuService;
        setSizeFull();
        addClassName("menu-view");
        buildTabs();
        buildHeader();
        renderMenuList();
        buildAddItemBar();
    }

    private void buildTabs() {
        Tabs tabs = new Tabs();
        Tab menuTab = new Tab("Menu");
        Tab ordersTab = new Tab("Orders");
        Tab reportsTab = new Tab("Reports");
        tabs.add(menuTab, ordersTab, reportsTab);
        tabs.setSelectedTab(menuTab);
        tabs.addSelectedChangeListener(e -> {
            if (e.getSelectedTab() == ordersTab) {
                getUI().ifPresent(ui -> ui.navigate("admin/orders"));
            } else if (e.getSelectedTab() == reportsTab) {
                getUI().ifPresent(ui -> ui.navigate("admin/reports"));
            }
        });
        HorizontalLayout wrapper = new HorizontalLayout(tabs);
        wrapper.getStyle().set("margin", "1rem");
        add(wrapper);
    }

    private void buildHeader() {
        H2 title = new H2("Admin Dashboard — Manage Menu");
        title.getStyle().set("margin", "0 1rem");
        add(title);
    }

    private void renderMenuList() {
        List<MenuItem> items = menuService.getAllItems();
        for (MenuItem item : items) {
            HorizontalLayout itemLayout = new HorizontalLayout();
            itemLayout.addClassName("menu-item");
            itemLayout.setSpacing(true);
            itemLayout.setWidthFull();
            itemLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
            itemLayout.getElement().getStyle().set("border-width", "1px");

            Image icon = new Image("icons/icon.png", "Menu item image");
            icon.setWidth(60, Unit.PIXELS);
            icon.setHeight(60, Unit.PIXELS);
            itemLayout.add(icon);

            VerticalLayout textLayout = new VerticalLayout();
            textLayout.setSpacing(false);
            textLayout.setPadding(false);
            textLayout.setMargin(false);
            H2 name = new H2(item.getName());
            Paragraph desc = new Paragraph(item.getDescription());
            textLayout.add(name, desc);
            itemLayout.add(textLayout);

            Paragraph price = new Paragraph(item.getPrice() + "$");
            itemLayout.add(price);

            Button edit = new Button("Edit");
            edit.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            edit.addClickListener(e -> showEditDialog(item));
            Button delete = new Button("Delete");
            delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
            delete.addClickListener(e -> {
                menuService.deleteItem(item.getId());
                getUI().ifPresent(ui -> ui.getPage().reload());
            });
            itemLayout.add(edit, delete);

            add(itemLayout);
        }
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
            MenuItem item = new MenuItem();
            item.setName(name.getValue());
            item.setDescription(description.getValue());
            item.setPrice(price.getValue());
            item.setDiscount(discount.getValue() != null ? discount.getValue() : 0.0);
            item.setAvailable(true);
            menuService.saveItem(item);
            getUI().ifPresent(ui -> ui.getPage().reload());
        });
        HorizontalLayout bar = new HorizontalLayout(name, description, price, discount, add);
        bar.setWidthFull();
        bar.setAlignItems(FlexComponent.Alignment.BASELINE);
        bar.getStyle().set("margin", "1rem");
        add(bar);
    }

    private void showEditDialog(MenuItem item) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Edit Menu Item");
        TextField name = new TextField("Name", item.getName());
        TextField description = new TextField("Description", item.getDescription());
        NumberField price = new NumberField("Price");
        price.setValue(item.getPrice());
        NumberField discount = new NumberField("Discount");
        discount.setValue(item.getDiscount());
        Checkbox available = new Checkbox("Available", item.isAvailable());
        Button save = new Button("Save", e -> {
            item.setName(name.getValue());
            item.setDescription(description.getValue());
            item.setPrice(price.getValue());
            item.setDiscount(discount.getValue());
            item.setAvailable(available.getValue());
            menuService.saveItem(item);
            dialog.close();
            getUI().ifPresent(ui -> ui.getPage().reload());
        });
        Button cancel = new Button("Cancel", e -> dialog.close());
        HorizontalLayout buttons = new HorizontalLayout(save, cancel);
        buttons.setSpacing(true);
        buttons.setPadding(true);
        VerticalLayout content = new VerticalLayout(name, description, price, discount, available, buttons);
        content.getStyle().set("margin", "1rem");
        dialog.add(content);
        dialog.open();
    }
}
