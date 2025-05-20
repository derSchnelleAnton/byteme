package edu.byteme.views.menu;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import edu.byteme.data.entities.MenuItem;
import edu.byteme.data.entities.Order;
import edu.byteme.util.Util;

import java.util.List;

public class LargeListComponent extends VerticalLayout{
    private List<MenuItem> items; // list that holds menu items
    private String actionText; // default button text

    /**
     * we can use empty constructor and set Menu items using the setter,
     * or we can use the constructor with menuItems as parameter which would
     * internally call this constructor and the setter
     **/
    public LargeListComponent(){
        setSizeFull();
        addClassName("menu-view");
        this.setPadding(true);
    }

     /**
     * constructor
     * @param items menu items that belong to a given order or a basket. this view is intended to be reusable.
     *              by just passing the list of menu items as parameter
     *              invokes the empty parameter to set up the page
     * @see MenuItem
     * @see Order
     **/
    public LargeListComponent(List<MenuItem> items) {
        this();
        setItems(items);
    }

    /**
     * Renders a list of menuItems in a vertical Layout
     */
    private void displayItems() {
        if (!items.isEmpty())
            for (MenuItem item : items)
                displayItem(item);
    }

    /**
     * Renders a single menuItem
     * @param item MenuItem to be displayed
     * @see MenuItem
     */
    void displayItem(MenuItem item) {
        HorizontalLayout itemLayout = new HorizontalLayout();
        itemLayout.addClassName("menu-item");
        itemLayout.setSpacing(true);
        itemLayout.setWidthFull();
        itemLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);

        itemLayout.add(renderImage(Util.getPathFromName(item.getName())));

        VerticalLayout textLayout = new VerticalLayout();
        textLayout.add(
                new H2(item.getName()),
                new Paragraph(item.getDescription())
        );
        itemLayout.add(textLayout);

        itemLayout.add(new Paragraph(item.getPrice() + "€"));

        if (actionText != null) {
            Button actionButton = new Button();
            actionButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            actionButton.addClickListener(e -> {
                if (this.menuItemEvent != null)
                    menuItemEvent.onClick(item);
            });
            itemLayout.add(actionButton);
            actionButton.setText(actionText);
        }
        this.add(itemLayout);
    }

    /**
     * @return Image View
     * @param path String path to an image resource
     */
    private Image renderImage(String path) {
        Image image = new Image(path, "Menu item image");
        image.addClassName("menu-item-image");
        image.setWidth(120, Unit.PIXELS);
        image.setHeight(120, Unit.PIXELS);
        return image;
    }

    /**
     * Loads items into member list, then empties component and fills with loaded items
     * @param items To be displayed
     */
    public void setItems(List<MenuItem> items) {
        this.items = items;
        invalidate();
    }

    /**
     * Clears and updates component entirely
     */
    private void invalidate(){
        this.removeAll();
        displayItems();
    }

    /**
     * Sets text for button
     * @param newActionText To be displayed in the blue button
     */
    public void setActionText(String newActionText) {
        this.actionText = newActionText;
        invalidate();
    }

    /**
     * listener for an action button click
     * this will enable us to observe button click from any component
     * that adds this to its child components
     */
    public interface MenuItemEvent {
        void onClick(MenuItem item);
    }

    /**
     * Event handler for action button
     */
    MenuItemEvent menuItemEvent;
    public void setMenuItemEvent(MenuItemEvent menuItemEvent) {
        this.menuItemEvent = menuItemEvent;
    }
}
