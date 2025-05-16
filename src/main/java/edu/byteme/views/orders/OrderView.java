package edu.byteme.views.orders;
import java.lang.reflect.Parameter;
import java.util.List;

import com.vaadin.flow.component.button.*;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import edu.byteme.data.entities.Order;
import edu.byteme.views.MainLayout;
import edu.byteme.views.menu.MenuListView;
import edu.byteme.data.entities.MenuItem;

/**
 * Class OrderView displays details about a single Order
 * @author  Patricia
 * @author Mark Böhme
 * @author Adrian tiberiu petre
 * @author anton Wörndle
 * @author Tinsae Ghilay
 */


@PageTitle("Order")
@Route(value = "order", layout = MainLayout.class)
public class OrderView extends VerticalLayout {
    

    private Order order;

    /*
     * empty constructor
     */
    public OrderView(){
        setSizeFull();
    }

    /*
     * Draws the required Views conditionally 
     * depending on availability of MenuItems in an order
     */
    private void drawViews() {
        
        if(order == null || order.getMenuItems().isEmpty()){ // order is empty

            // if we dont have order set or order has no menu items added( I am thinking it might happen?)
            // we show the message that the order is empty
            showEmptyMessage();
        }else{ // we have orders
            // we show menu items list
            List<MenuItem> goodies = order.getMenuItems();
            MenuListView goodiesList = new MenuListView(goodies);
            goodiesList.setMenuItemEvent(goodie ->{
                // we will display menu item details here
                Dialog dialog = new Dialog();
                configureDialog(dialog,goodie);
                add(dialog);
                dialog.open();
            });

        }
    }

    /*
     * Configures a dialog to show MenuItem details
     */
    private void configureDialog(Dialog dialog,MenuItem item) {

        // Name of menu item as title for the dialo
        dialog.setHeaderTitle(item.getName());

        // rest of the details as body text
        StringBuilder bodyTexStringBuilder = new StringBuilder();
        bodyTexStringBuilder.append("Description :\t")
            .append(item.getDescription()).append("\n")
            .append("Price :\t")
            .append(item.getPrice()).append("\n")
            .append("Available? :\t").append(item.isAvailable()?"Yes":"No");
        Paragraph body = new Paragraph(bodyTexStringBuilder.toString());

        // create and attach the body text 
        // -> copied as is from https://vaadin.com/docs/latest/components/dialog
        VerticalLayout dialogLayout = new VerticalLayout(body);
        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "18rem").set("max-width", "100%");

        dialog.add(dialogLayout);
        
        // button to close the dialog
        Button ok = new Button("OK", e -> dialog.close());
        ok.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        // attach it to dialog's bottom footer
        dialog.getFooter().add(ok);
    }

    // displays infor if order is not properly set up
    // if for example order has no menu items
    // or order is not selected or added
    private void showEmptyMessage() {
        VerticalLayout emptyLayout = new VerticalLayout();
        emptyLayout.setAlignItems(Alignment.CENTER);
        emptyLayout.setHorizontalComponentAlignment(Alignment.CENTER);
        H2 title = new H2("Empty order");
        Paragraph msg = new Paragraph("The selected order has no menu contents");
        emptyLayout.add(title,msg);
        add(emptyLayout);
    }

    public OrderView(Order order){
        this();
        setOrder(order);
    }




    private void invalidate(){
        removeAll();
        drawViews();
    }


    public void setOrder(Order order){
        this.order = order;
        invalidate();
    }
}
