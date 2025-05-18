package edu.byteme.views.orders;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import com.vaadin.flow.router.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.vaadin.flow.component.button.*;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import edu.byteme.data.entities.Order;
import edu.byteme.services.OrderService;
import edu.byteme.views.MainLayout;
import edu.byteme.views.menu.MenuListView;
import edu.byteme.views.side_bar.SideBar;
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
@AnonymousAllowed
public class OrderView extends HorizontalLayout implements HasUrlParameter<Long> {
    

    private Order order;
    private OrderService orderService;
    private MenuListView goodiesList;
    private OrderTimeLine timeLine;

    /*
     * empty constructor
     */
    @Autowired
    public OrderView(OrderService orderService){
        this.orderService = orderService;
        setSizeFull();
        addClassName("order-view");
        drawViews();
        addSidePannel();

    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter Long orderId) {
        if (orderId != null) {
            Optional<Order> tempOrder = orderService.getOrderById(orderId.intValue());
            if (tempOrder.isPresent()) {
                this.order = tempOrder.get();
            } else {
                this.order = orderService.getOrdersByClientId(5).get(0);
            }
        } else {
            this.order = orderService.getOrdersByClientId(5).get(0);
        }
    }

    private void addSidePannel() {
        // cart side panel
        Div cartPanel = new Div();
        cartPanel.addClassName("cart-panel");
        //cartPanel.setVisible(false);
        //cartPanel.add(cartContents, cartTotal);
        SideBar bar = new SideBar(orderService);
        bar.setOnOrderSelectedListener(e -> {
            // order selected
            goodiesList.setItems(e.getMenuItems());
            timeLine.setValues(e, orderService.getTotalCostOfOrder(e)+"€");
        });
        cartPanel.add(bar);
        add(cartPanel);
    }

    /*
     * Draws the required Views conditionally 
     * depending on availability of MenuItems in an order
     */
    private void drawViews() {
        
        if(order == null || order.getMenuItems().isEmpty()){ // order is empty
            // set selected order to index 0
            this.order = orderService.getOrdersByClientId(5).get(0);
        }
        // we show menu items list
        List<MenuItem> goodies = order.getMenuItems();
        goodiesList = new MenuListView(goodies);

        goodiesList.setMenuItemEvent(goodie ->{
            // we will display menu item details here
            Dialog dialog = new Dialog();
            configureDialog(dialog,goodie);
            add(dialog);
            dialog.open();
        });
        VerticalLayout wrapper = new VerticalLayout();
        wrapper.setHeight("100%");
        timeLine = new OrderTimeLine(order, orderService.getTotalCostOfOrder(order)+"€");
        Div footer = new Div(timeLine);
        footer.setWidthFull();
        wrapper.add(goodiesList,footer);
        add(wrapper);
        

        
    }

    /*
     * Configures a dialog to show MenuItem details
     */
    private void configureDialog(Dialog dialog,MenuItem item) {

        // Name of menu item as title for the dialo
        dialog.setHeaderTitle(item.getName());

        // rest of the details
        Paragraph desc = new Paragraph("Description : "+item.getDescription());
        Paragraph price = new Paragraph("Price : "+item.getPrice());
        Paragraph available = new Paragraph("Still available : "+(item.isAvailable()?"Yes": "No"));
        Paragraph since = new Paragraph("Since : "+item.getCreatedAt().format(DateTimeFormatter.ofPattern("dd MMMM, yyyy")));

        Paragraph body = new Paragraph(desc,price,available,since);

        // create and attach the body text 
        // -> copied as is from https://vaadin.com/docs/latest/components/dialog
        // mostly ;-D
        VerticalLayout dialogLayout = new VerticalLayout(body);
        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "18rem").set("max-width", "100%");

        HorizontalLayout bodyLayout = new HorizontalLayout();
        bodyLayout.setPadding(false);
        // I am adding an image on the left
        Image menuImage = new Image("images/take-away.png", "Menu image");
        menuImage.addClassName("menu_cover");
        //menuImage.setHeight(100, Unit.PIXELS);
        //menuImage.setWidth(100, Unit.PIXELS);
        bodyLayout.add(menuImage,dialogLayout);
        dialog.add(bodyLayout);
        
        // button to close the dialog
        Button ok = new Button("OK", e -> dialog.close());
        ok.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        // attach it to dialog's bottom footer
        dialog.getFooter().add(ok);
    }


}
