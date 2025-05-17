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

import java.util.List;

/**
 * Class MenuListView displays MenuItems in a vertical list view
 * @author  Patricia
 * @author Mark Böhme
 * @author Adrian tiberiu petre
 * @author anton Wörndle
 * @author Tinsae Ghilay
 */


public class MenuListView  extends VerticalLayout{

    // list that holds menu items
    List<MenuItem> items;
    // default button text
    String actionText =  "More";


    /**
     * we can use empty constructor and set Menu items using the setter,
     * or we can use the constructor with menuItems as parameter which would
     * internally call this constructor and the setter
     **/
    public MenuListView(){
        setSizeFull();
        addClassName("menu-view");
        this.setPadding(true);
        // we can add the page style here
        // I am not bothering about it now.
    }



     /**
     * constructor
     * @param items menu items that belong to a given order or a basket. this view is intended to be reusable.
     *              by just passing the list of menu items as parameter
     *              invokes the empty parameter to set up the page
     * @see MenuItem
     * @see Order
     **/
    public MenuListView(List<MenuItem> items) {
        this();
        setItems(items);
    }


    /**
     * renders a list of menuItems in a vertical Layout
     */
    private void displayItems(){
        if(!items.isEmpty()){
            for(MenuItem item : items){
                displayItem(item);
            }
        }
    }

    /**
     * renders a single menuItem
     * @param item menuItem to be displayed
     * @see MenuItem
     */
    void displayItem(MenuItem item) {
        // How I think it will be aligned if we style them in css of course,
        /*
        *  |-----------==============================-----------------------------|
        *  | /``````\  | Name                       |                             |
        *  || IMAGE  | |                            |        price    ( button )  |
        *  | \______/  | description                |                             |
        *  |-----------==============================-----------------------------|
         */
        // with the above layout in mind.
        // the MenuItem view
        HorizontalLayout itemLayout = new HorizontalLayout();
        itemLayout.addClassName("menu-item");
        itemLayout.setSpacing(true);
        itemLayout.setWidthFull();
        itemLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        itemLayout.getElement().getStyle().set("border-width", "1px");
        // we start with the icon
        Image  menuProfile = renderImage("icons/icon.png");
        itemLayout.add(menuProfile);

        // vertical layout to hold Name and description as in the wireframe
        VerticalLayout textLayout = new VerticalLayout();// we may need to use some flex properties here
        // name
        H2 name = new H2(item.getName());
        // description
        Paragraph description = new Paragraph(item.getDescription());
        // and we add them to the layout
        textLayout.add(name, description);
        // and add name and description
        itemLayout.add(textLayout);

        // price and button
        Paragraph price = new Paragraph(item.getPrice()+"€");
        itemLayout.add(price);
        // button text
        Button actionButton = new Button();
        actionButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        // button click listener
        actionButton.addClickListener(e -> {
            if(this.menuItemEvent != null){
                menuItemEvent.onClick(item);
            }
        });
        itemLayout.add(actionButton);
        actionButton.setText(actionText);
        // and we add it to the parent.
        this.add(itemLayout);
    }

    /**
     *
     * @return Image View
     * @param path String path to an image resource
     */
    private Image renderImage(String path) {

        Image image = new Image(path, "Menu item image");
        image.setWidth(60, Unit.PIXELS);
        image.setHeight(60, Unit.PIXELS);
        return image;
    }


    public void setItems(List<MenuItem> items) {
        this.items = items;
        invalidate();
    }

    // clears and updates canvas. 
    // for now used to make dynamic setting of UI values possible
    // will research if there is a better way to do it, 
    // as this does for the whole canvas
    private void invalidate(){
        // we may need to clear the canvas
        this.removeAll();
        displayItems();
    }

    // set actionButton text
    public void setActionText(String newActionText) {
        this.actionText = newActionText;
        // update canvas
        invalidate();
    }

    /**
     * listener for an action button click
     * this will enable us to observe button click from any component
     * that adds this to its child components
     */
    public interface MenuItemEvent{
        void onClick(MenuItem item);
    }

    // event handler that we set for this class
    // and its setter
    MenuItemEvent menuItemEvent;
    public void setMenuItemEvent(MenuItemEvent menuItemEvent) {
        this.menuItemEvent = menuItemEvent;
    }
    
}
